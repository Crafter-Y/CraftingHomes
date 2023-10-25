package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.CommandSenderI;
import de.craftery.craftinghomes.common.api.PlayerI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
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
    @SuppressWarnings("")
    public boolean hasPermission(String permission) {
        if (!isPlayer()) return source.hasPermissionLevel(3);
        PlayerI genericPlayer = getPlayer();
        FabricPlayerImpl implPlayer = (FabricPlayerImpl) genericPlayer;
        ServerPlayerEntity player = implPlayer.getImplPlayer();

        try {
            Class.forName("LuckPermsProvider");

            LuckPerms api = LuckPermsProvider.get();
            User user = api.getPlayerAdapter(ServerPlayerEntity.class).getUser(player);

            Tristate permissionData = user.getCachedData().getPermissionData()
                    .checkPermission(permission);
            if (permissionData == Tristate.UNDEFINED) {
                return source.hasPermissionLevel(3);
            }
            return permissionData.asBoolean();
        } catch (IllegalStateException | ClassNotFoundException exception) {
            return source.hasPermissionLevel(3);
        }
    }
}
