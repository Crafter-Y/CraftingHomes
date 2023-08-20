package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.CommandSenderI;
import de.craftery.craftinghomes.common.api.PlayerI;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class FabricCommandSenderImpl implements CommandSenderI {
    protected final ServerCommandSource source;

    public FabricCommandSenderImpl(ServerCommandSource source) {
        this.source = source;
    }

    @Override
    public boolean isPlayer() {
        return source.isExecutedByPlayer();
    }

    @Override
    public void sendMessage(String message) {
        message = message.replaceAll("&", "§");
        source.sendMessage(Text.literal(message));
    }

    @Override
    public PlayerI getPlayer() {
        return (PlayerI) this;
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermissionLevel(2);
    }
}
