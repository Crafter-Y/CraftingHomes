package de.craftery.craftinghomes.impl;

public class CommandSenderImpl implements CommandSenderI {
    protected final CommandSender bukkitCommandSender;

    public CommandSenderImpl(CommandSender sender) {
        this.bukkitCommandSender = sender;
    }

    @Override
    public boolean isPlayer() {
        return bukkitCommandSender instanceof Player;
    }

    @Override
    public void sendMessage(Component message) {
        bukkitCommandSender.sendMessage(message);
    }

    @Override
    public PlayerI getPlayer() {
        return (PlayerImpl) this;
    }
}