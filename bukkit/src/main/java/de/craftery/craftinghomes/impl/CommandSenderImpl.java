package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.CommandSenderI;
import de.craftery.craftinghomes.common.api.PlayerI;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandSenderImpl implements CommandSenderI {
    protected final CommandSender bukkitCommandSender;

    public CommandSenderImpl(CommandSender sender) {
        this.bukkitCommandSender = sender;
    }

    @Override
    public boolean isPlayer() {
        return this instanceof PlayerImpl;
    }

    @Override
    public void sendMessage(String message) {
        bukkitCommandSender.sendMessage(
                LegacyComponentSerializer.legacySection().deserialize(
                        ChatColor.translateAlternateColorCodes('&', message)));
    }

    @Override
    public PlayerI getPlayer() {
        return (PlayerImpl) this;
    }
}