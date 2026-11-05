package de.simpletp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * Experimental 26.3 port. NOT security-tested.
 * Replaces vanilla /tp and /teleport, including for operators.
 */
public class SimpleTP implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // Fail closed: if vanilla roots cannot be removed, do not register
            // an unsafe merged /tp command.
            try {
                removeRoot(dispatcher, "tp");
                removeRoot(dispatcher, "teleport");
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot safely replace vanilla teleport commands", e);
            }

            for (String name : new String[]{"tp", "teleport"}) {
                dispatcher.register(Commands.literal(name)
                    .requires(source -> source.getEntity() instanceof ServerPlayer)
                    .then(Commands.argument("destination", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer self = ctx.getSource().getPlayerOrException();
                            ServerPlayer target = EntityArgument.getPlayer(ctx, "destination");
                            self.teleportTo(target.serverLevel(), target.getX(), target.getY(), target.getZ(),
                                Set.of(), self.getYRot(), self.getXRot(), false);
                            return 1;
                        }))
                    .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                        .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                            .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                .executes(ctx -> {
                                    ServerPlayer self = ctx.getSource().getPlayerOrException();
                                    self.teleportTo(
                                        DoubleArgumentType.getDouble(ctx, "x"),
                                        DoubleArgumentType.getDouble(ctx, "y"),
                                        DoubleArgumentType.getDouble(ctx, "z"));
                                    return 1;
                                })))));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static void removeRoot(CommandDispatcher<CommandSourceStack> dispatcher, String name)
            throws ReflectiveOperationException {
        CommandNode<CommandSourceStack> root = dispatcher.getRoot();
        for (String fieldName : new String[]{"children", "literals", "arguments"}) {
            Field field = CommandNode.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            ((Map<String, ?>) field.get(root)).remove(name);
        }
    }
}
