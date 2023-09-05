package net.treset.worldmanager.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.treset.worldmanager.WorldManagerMod;
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
            .requires(source -> source.hasPermissionLevel(2)).then(CommandManager.literal("remove")
                .then(CommandManager.argument("radius", IntegerArgumentType.integer(1))
                        .executes(this::onRemoveRadius)
                )
                .then(CommandManager.argument("pos1", Vec2ArgumentType.vec2())
                        .then(CommandManager.argument("pos2", Vec2ArgumentType.vec2())
                                .executes(this::onRemoveCoordinates)
                        )
                )
            )
            .requires(source -> source.hasPermissionLevel(2)).then(CommandManager.literal("export")
                .then(CommandManager.literal("mca-selector")
                    .executes(this::onExportMcaSelector)
                )
            )
        );
    }

    public int onKeepRadius(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) return 0;
        CommandCallback dimensionCallback = checkDimension(ctx);
        if(dimensionCallback != null) return sendFeedback(ctx, dimensionCallback);
        int radius = IntegerArgumentType.getInteger(ctx, "radius");
        CommandCallback commandCallback = WorldManagerMod.getChunkManager().add(player.getPos(), radius);
        return sendFeedback(ctx, commandCallback);
    }

    public int onKeepCoordinates(CommandContext<ServerCommandSource> ctx) {
        CommandCallback dimensionCallback = checkDimension(ctx);
        if(dimensionCallback != null) return sendFeedback(ctx, dimensionCallback);
        Vec2f pos1 = Vec2ArgumentType.getVec2(ctx, "pos1");
        Vec2f pos2 = Vec2ArgumentType.getVec2(ctx, "pos2");
        CommandCallback commandCallback = WorldManagerMod.getChunkManager().add(pos1, pos2);
        return sendFeedback(ctx, commandCallback);
    }

    public int onRemoveRadius(CommandContext<ServerCommandSource> ctx) {
        CommandCallback dimensionCallback = checkDimension(ctx);
        if(dimensionCallback != null) return sendFeedback(ctx, dimensionCallback);
        int radius = IntegerArgumentType.getInteger(ctx, "radius");
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) return 0;
        CommandCallback commandCallback = WorldManagerMod.getChunkManager().remove(player.getPos(), radius);
        return sendFeedback(ctx, commandCallback);
    }

    public int onRemoveCoordinates(CommandContext<ServerCommandSource> ctx) {
        CommandCallback dimensionCallback = checkDimension(ctx);
        if(dimensionCallback != null) return sendFeedback(ctx, dimensionCallback);
        Vec2f pos1 = Vec2ArgumentType.getVec2(ctx, "pos1");
        Vec2f pos2 = Vec2ArgumentType.getVec2(ctx, "pos2");
        CommandCallback commandCallback = WorldManagerMod.getChunkManager().remove(pos1, pos2);
        return sendFeedback(ctx, commandCallback);
    }

    private CommandCallback checkDimension(CommandContext<ServerCommandSource> ctx) {
        //TODO: save separate lists for each dimension
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) return new CommandCallback(CommandCallback.Type.FAILURE, "You must be a player to use this command.");
        if(!player.getWorld().getDimension().bedWorks()) {
            //nether or end
            return new CommandCallback(CommandCallback.Type.FAILURE, "You can only use this command in the overworld.");
        }
        return null;
    }

    private int sendFeedback(CommandContext<ServerCommandSource> ctx, CommandCallback commandCallback) {
        if(commandCallback.getType() == CommandCallback.Type.FAILURE) {
            ctx.getSource().sendError(Text.literal(commandCallback.getMessage()));
            return 0;
        }
        ctx.getSource().sendFeedback(() -> Text.literal(commandCallback.getMessage()), false);
        return 1;
    }

    private int onExportMcaSelector(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) return 0;
        CommandCallback commandCallback = WorldManagerMod.getConfig().exportMcaSelector();
        return sendFeedback(ctx, commandCallback);
    }
}
