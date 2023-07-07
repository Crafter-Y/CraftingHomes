package de.craftery.craftinghomes.common.api;

public interface CommandSender {
    boolean isPlayer();
    void sendMessage(Component message);

    PlayerI getPlayer();
}