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
import net.treset.worldmanager.manager.ChangeCallback;

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
        );
    }

    public int onKeepRadius(CommandContext<ServerCommandSource> ctx) {
        int radius = IntegerArgumentType.getInteger(ctx, "radius");
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) return 0;
        ChangeCallback changeCallback = WorldManagerMod.getChunkManager().add(player.getPos(), radius);
        return sendFeedback(ctx, changeCallback);
    }

    public int onKeepCoordinates(CommandContext<ServerCommandSource> ctx) {
        Vec2f pos1 = Vec2ArgumentType.getVec2(ctx, "pos1");
        Vec2f pos2 = Vec2ArgumentType.getVec2(ctx, "pos2");
        ChangeCallback changeCallback = WorldManagerMod.getChunkManager().add(pos1, pos2);
        return sendFeedback(ctx, changeCallback);
    }

    public int onRemoveRadius(CommandContext<ServerCommandSource> ctx) {
        int radius = IntegerArgumentType.getInteger(ctx, "radius");
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) return 0;
        ChangeCallback changeCallback = WorldManagerMod.getChunkManager().remove(player.getPos(), radius);
        return sendFeedback(ctx, changeCallback);
    }

    public int onRemoveCoordinates(CommandContext<ServerCommandSource> ctx) {
        Vec2f pos1 = Vec2ArgumentType.getVec2(ctx, "pos1");
        Vec2f pos2 = Vec2ArgumentType.getVec2(ctx, "pos2");
        ChangeCallback changeCallback = WorldManagerMod.getChunkManager().remove(pos1, pos2);
        return sendFeedback(ctx, changeCallback);
    }

    private int sendFeedback(CommandContext<ServerCommandSource> ctx, ChangeCallback changeCallback) {
        if(changeCallback.getType() == ChangeCallback.Type.FAILURE) {
            ctx.getSource().sendError(Text.literal(changeCallback.getMessage()));
            return 0;
        }
        ctx.getSource().sendFeedback(() -> Text.literal(changeCallback.getMessage()), false);
        return 1;
    }
}
