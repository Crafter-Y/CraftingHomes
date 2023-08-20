package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.CraftingLocation;
import de.craftery.craftinghomes.common.api.PlayerI;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class FabricPlayerImpl extends FabricCommandSenderImpl implements PlayerI {
    public FabricPlayerImpl(ServerCommandSource source) {
        super(source);
    }

    @Override
    public void teleport(CraftingLocation location) {
        if (source.getPlayer() == null) throw new RuntimeException("Player should not be null at this point!");
        ServerWorld world = source.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier.tryParse(location.getWorld())));
        source.getPlayer().teleport(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public CraftingLocation getLocation() {
        if (source.getPlayer() == null) throw new RuntimeException("Player should not be null at this point!");

        String world = source.getWorld().getRegistryKey().getValue().toString();
        double x = source.getPosition().getX();
        double y = source.getPosition().getY();
        double z = source.getPosition().getZ();
        float yaw = source.getPlayer().getYaw();
        float pitch = source.getPlayer().getPitch();
        return new CraftingLocation(world, x, y, z, yaw, pitch);
    }

    @Override
    public String getUniqueId() {
        if (source.getPlayer() == null) throw new RuntimeException("Player should not be null at this point!");
        return source.getPlayer().getUuid().toString();
    }
}
