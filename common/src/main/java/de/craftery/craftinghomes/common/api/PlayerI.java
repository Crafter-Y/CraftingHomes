package de.craftery.craftinghomes.common.api;

public interface PlayerI extends CommandSenderI {
    void teleport(CraftingLocation location);

    CraftingLocation getLocation();

    String getUniqueId();
}