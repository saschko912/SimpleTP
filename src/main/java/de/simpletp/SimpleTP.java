package de.simpletp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.CommandNode;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class SimpleTP implements ModInitializer {

    @Override
    public void onInitialize() {

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {

                try {
                    removeRoot(dispatcher, "tp");
                    removeRoot(dispatcher, "teleport");
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(
                        "Cannot safely replace teleport commands", e
                    );
                }

                for (String name : new String[]{"tp", "teleport"}) {

                    var command = Commands.literal(name)
                        .requires(source ->
                            source.getEntity() instanceof ServerPlayer
                        );

                    // Teleport to another player (only moves yourself)
                    command.then(
                        Commands.argument(
                            "destination",
                            EntityArgument.player()
                        ).executes(ctx -> {

                            ServerPlayer self =
                                ctx.getSource().getPlayerOrException();

                            ServerPlayer target =
                                EntityArgument.getPlayer(
                                    ctx, "destination"
                                );

                            self.teleportTo(
                                (ServerLevel) target.level(),
                                target.getX(),
                                target.getY(),
                                target.getZ(),
                                Set.of(),
                                self.getYRot(),
                                self.getXRot(),
                                false
                            );

                            return 1;
                        })
                    );

                    // /tp X Y Z
                    command.then(
                        coordinates()
                    );

                    // /tp @s X Y Z
                    // @s is a literal, never an arbitrary target
                    command.then(
                        Commands.literal("@s")
                            .then(coordinates())
                    );

                    dispatcher.register(command);
                }
            }
        );
    }

    private static com.mojang.brigadier.builder
        .RequiredArgumentBuilder<CommandSourceStack, Double>
        coordinates() {

        return Commands.argument(
            "x", DoubleArgumentType.doubleArg()
        ).then(
            Commands.argument(
                "y", DoubleArgumentType.doubleArg()
            ).then(
                Commands.argument(
                    "z", DoubleArgumentType.doubleArg()
                ).executes(ctx -> {

                    ServerPlayer self =
                        ctx.getSource().getPlayerOrException();

                    double x = DoubleArgumentType.getDouble(ctx, "x");
                    double y = DoubleArgumentType.getDouble(ctx, "y");
                    double z = DoubleArgumentType.getDouble(ctx, "z");

                    self.teleportTo(x, y, z);

                    return 1;
                })
            )
        );
    }

    @SuppressWarnings("unchecked")
    private static void removeRoot(
        CommandDispatcher<CommandSourceStack> dispatcher,
        String name
    ) throws ReflectiveOperationException {

        CommandNode<CommandSourceStack> root =
            dispatcher.getRoot();

        for (String fieldName :
                new String[]{"children", "literals", "arguments"}) {

            Field field =
                CommandNode.class.getDeclaredField(fieldName);

            field.setAccessible(true);

            ((Map<String, ?>) field.get(root)).remove(name);
        }
    }
}
