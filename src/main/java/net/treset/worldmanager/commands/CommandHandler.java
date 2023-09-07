package net.treset.worldmanager.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.treset.worldmanager.WorldManagerMod;
import net.treset.worldmanager.config.Config;
import net.treset.worldmanager.manager.CommandCallback;

public class CommandHandler {
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandManager.RegistrationEnvironment environment) {
        if(!environment.dedicated) return;
        dispatcher.register(CommandManager.literal("worldmanager")
            .then(CommandManager.literal("keep")
                .then(CommandManager.argument("radius", IntegerArgumentType.integer(1))
                        .executes(this::onKeepRadius)
                )
                .then(CommandManager.argument("pos1", Vec2ArgumentType.vec2())
                    .then(CommandManager.argument("pos2", Vec2ArgumentType.vec2())
                        .executes(this::onKeepCoordinates)
                    )
                )
            )
            .then(CommandManager.literal("remove").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("radius", IntegerArgumentType.integer(1))
                        .executes(this::onRemoveRadius)
                )
                .then(CommandManager.argument("pos1", Vec2ArgumentType.vec2())
                        .then(CommandManager.argument("pos2", Vec2ArgumentType.vec2())
                                .executes(this::onRemoveCoordinates)
                        )
                )
            )
            .then(CommandManager.literal("export").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("mca-selector")
                    .executes(this::onExportMcaSelectorWithoutDimension)
                    .then(CommandManager.argument("dimensionId", IdentifierArgumentType.identifier())
                            .executes(this::onExportMcaSelectorWithDimension)
                    )
                )
            )
        );
    }

    public int onKeepRadius(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) return 0;
        String dimensionId = getDimensionId(ctx);
        if(dimensionId == null) return sendFeedback(ctx, new CommandCallback(CommandCallback.Type.FAILURE, "Failed to get dimension id."));
        int radius = IntegerArgumentType.getInteger(ctx, "radius");
        CommandCallback commandCallback = WorldManagerMod.getChunkManager().add(player.getPos(), radius, dimensionId);
        return sendFeedback(ctx, commandCallback);
    }

    public int onKeepCoordinates(CommandContext<ServerCommandSource> ctx) {
        String dimensionId = getDimensionId(ctx);
        if(dimensionId == null) return sendFeedback(ctx, new CommandCallback(CommandCallback.Type.FAILURE, "Failed to get dimension id."));
        Vec2f pos1 = Vec2ArgumentType.getVec2(ctx, "pos1");
        Vec2f pos2 = Vec2ArgumentType.getVec2(ctx, "pos2");
        CommandCallback commandCallback = WorldManagerMod.getChunkManager().add(pos1, pos2, dimensionId);
        return sendFeedback(ctx, commandCallback);
    }

    public int onRemoveRadius(CommandContext<ServerCommandSource> ctx) {
        String dimensionId = getDimensionId(ctx);
        if(dimensionId == null) return sendFeedback(ctx, new CommandCallback(CommandCallback.Type.FAILURE, "Failed to get dimension id."));
        int radius = IntegerArgumentType.getInteger(ctx, "radius");
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) return 0;
        CommandCallback commandCallback = WorldManagerMod.getChunkManager().remove(player.getPos(), radius, dimensionId);
        return sendFeedback(ctx, commandCallback);
    }

    public int onRemoveCoordinates(CommandContext<ServerCommandSource> ctx) {
        String dimensionId = getDimensionId(ctx);
        if(dimensionId == null) return sendFeedback(ctx, new CommandCallback(CommandCallback.Type.FAILURE, "Failed to get dimension id."));
        Vec2f pos1 = Vec2ArgumentType.getVec2(ctx, "pos1");
        Vec2f pos2 = Vec2ArgumentType.getVec2(ctx, "pos2");
        CommandCallback commandCallback = WorldManagerMod.getChunkManager().remove(pos1, pos2, dimensionId);
        return sendFeedback(ctx, commandCallback);
    }

    private int onExportMcaSelectorWithDimension(CommandContext<ServerCommandSource> ctx) {
        Identifier dimensionIdentifier = IdentifierArgumentType.getIdentifier(ctx, "dimensionId");
        return exportMcaSelector(dimensionIdentifier.getNamespace() + "." + dimensionIdentifier.getPath(), ctx);
    }

    private int onExportMcaSelectorWithoutDimension(CommandContext<ServerCommandSource> ctx) {
        return exportMcaSelector(getDimensionId(ctx), ctx);
    }

    private int exportMcaSelector(String dimensionId, CommandContext<ServerCommandSource> ctx) {
        if(dimensionId == null) return sendFeedback(ctx, new CommandCallback(CommandCallback.Type.FAILURE, "Failed to get dimension id."));
        Config config = WorldManagerMod.getConfig(dimensionId);
        if(config == null) return sendFeedback(ctx, new CommandCallback(CommandCallback.Type.FAILURE, "Failed to get config file."));
        CommandCallback commandCallback = config.exportMcaSelector();
        return sendFeedback(ctx, commandCallback);
    }

    private String getDimensionId(CommandContext<ServerCommandSource> ctx) {
        try {
            Identifier dimension = ctx.getSource().getWorld().getDimensionKey().getValue();
            return dimension.getNamespace() + "." + dimension.getPath();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private int sendFeedback(CommandContext<ServerCommandSource> ctx, CommandCallback commandCallback) {
        if(commandCallback.getType() == CommandCallback.Type.FAILURE) {
            ctx.getSource().sendError(Text.literal(commandCallback.getMessage()));
            return 0;
        }
        ctx.getSource().sendFeedback(() -> Text.literal(commandCallback.getMessage()), false);
        return 1;
    }

}
