package de.simpletp;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SimpleTP implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // Register the same root names as vanilla, with safe self-only routes.
            // A command registration callback may merge nodes with vanilla.
            for (String name : new String[]{"tp", "teleport"}) {
                dispatcher.register(literal(name)
                    .then(argument("destination", EntityArgumentType.player())
                        .executes(ctx -> {
                            ServerPlayerEntity self = ctx.getSource().getPlayerOrThrow();
                            ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "destination");
                            self.teleport((ServerWorld) target.getWorld(), target.getX(), target.getY(), target.getZ(),
                                java.util.Collections.emptySet(), self.getYaw(), self.getPitch(), false);
                            return 1;
                        }))
                    .then(argument("x", DoubleArgumentType.doubleArg())
                        .then(argument("y", DoubleArgumentType.doubleArg())
                            .then(argument("z", DoubleArgumentType.doubleArg())
                                .executes(ctx -> {
                                    ServerPlayerEntity self = ctx.getSource().getPlayerOrThrow();
                                    double x = DoubleArgumentType.getDouble(ctx, "x");
                                    double y = DoubleArgumentType.getDouble(ctx, "y");
                                    double z = DoubleArgumentType.getDouble(ctx, "z");
                                    self.teleport((ServerWorld) self.getWorld(), x, y, z,
                                        java.util.Collections.emptySet(), self.getYaw(), self.getPitch(), false);
                                    return 1;
                                })))));
            }
        });
    }
}
