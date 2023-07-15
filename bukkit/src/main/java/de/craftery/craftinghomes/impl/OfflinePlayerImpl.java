package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.OfflinePlayerI;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerImpl implements OfflinePlayerI {
    private final OfflinePlayer offlinePlayer;

    public OfflinePlayerImpl (OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    @Override
    public String getUniqueId() {
        return offlinePlayer.getUniqueId().toString();
    }

    @Override
    public String getName() {
        return offlinePlayer.getName();
    }
}
