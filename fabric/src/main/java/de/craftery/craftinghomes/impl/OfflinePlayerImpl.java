package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.OfflinePlayerI;

public record OfflinePlayerImpl(String uniqueId, String name) implements OfflinePlayerI {
    @Override
    public String getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
