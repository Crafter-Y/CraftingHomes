package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.CraftingLocation;
import de.craftery.craftinghomes.common.api.PlayerI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerImpl extends CommandSenderImpl implements PlayerI  {
    public PlayerImpl(CommandSender sender) {
        super(sender);
    }

    @Override
    public void teleport(CraftingLocation location) {
        World world = Bukkit.getWorld(location.getWorld());
        ((Player) this.bukkitCommandSender).teleport(new Location(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));
    }

    @Override
    public CraftingLocation getLocation() {
        Location loc = ((Player) this.bukkitCommandSender).getLocation();
        return new CraftingLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    @Override
    public String getUniqueId() {
        return ((Player) this.bukkitCommandSender).getUniqueId().toString();
    }
}