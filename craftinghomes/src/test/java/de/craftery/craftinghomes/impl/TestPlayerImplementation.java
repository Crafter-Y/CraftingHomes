package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.CraftingLocation;
import de.craftery.craftinghomes.common.api.PlayerI;

import java.util.List;
import java.util.UUID;

public class TestPlayerImplementation extends TestConsolePlayerImplementation implements PlayerI {
    private CraftingLocation location;
    private final String uuid = UUID.randomUUID().toString();
    private final String name;

    public TestPlayerImplementation(String name, List<String> permissions) {
        super(permissions);
        this.name = name;
        location = new CraftingLocation("world", 0.0, 0.0, 0.0, 0.0f, 0.0f);

        TestPlatform.registerPlayer(this);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public PlayerI getPlayer() {
        return this;
    }

    @Override
    public void teleport(CraftingLocation location) {
        this.location = location;
    }

    @Override
    public CraftingLocation getLocation() {
        return location;
    }

    @Override
    public String getUniqueId() {
        return uuid;
    }
}
