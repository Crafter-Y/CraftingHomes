package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.CommandSenderI;
import de.craftery.craftinghomes.common.api.PlayerI;

import java.util.List;

public class TestConsolePlayerImplementation implements CommandSenderI {
    public String lastSendMessage = null;
    private final List<String> permissions;

    public TestConsolePlayerImplementation(List<String> permissions) {
        this.permissions = permissions;
    }

    public String popLastMessage() {
        String message = lastSendMessage;
        lastSendMessage = null;
        return message;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public void sendMessage(String message) {
        lastSendMessage = message;
    }

    @Override
    public PlayerI getPlayer() {
        throw new RuntimeException("This should never be called!");
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}
