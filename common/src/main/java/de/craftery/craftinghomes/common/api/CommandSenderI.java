package de.craftery.craftinghomes.common.api;

public interface CommandSenderI {
    boolean isPlayer();
    void sendMessage(String message);

    PlayerI getPlayer();
}