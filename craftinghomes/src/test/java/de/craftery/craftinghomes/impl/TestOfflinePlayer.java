package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.OfflinePlayerI;

public class TestOfflinePlayer implements OfflinePlayerI {
    private final String uniqueId;
    private final String name;

    public TestOfflinePlayer(String uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    @Override
    public String getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
