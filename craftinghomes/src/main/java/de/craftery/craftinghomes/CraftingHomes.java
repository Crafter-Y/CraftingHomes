package de.craftery.craftinghomes;

import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.api.ConfigurationI;
import de.craftery.craftinghomes.common.api.CraftingLocation;
import de.craftery.craftinghomes.common.api.PlayerI;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CraftingHomes {
    public static Integer getMaxHomes() {
        return Platform.getServer().getConfiguration().getInt("maxHomes", 3);
    }

    public static List<Home> getHomes(PlayerI player) {
        ConfigurationI config = Platform.getServer().getConfiguration();

        String playerSection = "homes." + player.getUniqueId();
        if (!config.exists(playerSection)) {
            return new ArrayList<>();
        }

        List<Home> homes = new ArrayList<>();
        for (String key : config.getKeys(playerSection)) {
            String homeSection = playerSection + "." + key;
            Home home = new Home(key, new CraftingLocation(
                    config.getString(homeSection + ".world", "world"),
                    config.getDouble(homeSection + ".x"),
                    config.getDouble(homeSection + ".y"),
                    config.getDouble(homeSection + ".z"),
                    (float) config.getDouble(homeSection + ".yaw"),
                    (float) config.getDouble(homeSection + ".pitch"))
            );
            homes.add(home);
        }
        return homes;
    }

    public static void addHome(PlayerI player, Home home) {
        ConfigurationI config = Platform.getServer().getConfiguration();

        String playerSection = "homes." + player.getUniqueId();

        String homeSection = playerSection  + "." + home.name();

        config.set(homeSection + ".world", home.location().getWorld());
        config.set(homeSection + ".x", home.location().getX());
        config.set(homeSection + ".y", home.location().getY());
        config.set(homeSection + ".z", home.location().getZ());
        config.set(homeSection + ".yaw", (double) home.location().getYaw());
        config.set(homeSection + ".pitch", (double) home.location().getPitch());

        config.saveConfig();
    }

    public static @Nullable Home getHome(PlayerI player, String name) {
        ConfigurationI config = Platform.getServer().getConfiguration();

        String playerSection = "homes." + player.getUniqueId();
        if (!config.exists(playerSection)) {
            return null;
        }

        String homeSection = playerSection + "." + name;

        if (!config.exists(homeSection)) {
            return null;
        }

        return new Home(name, new CraftingLocation(
                config.getString(homeSection + ".world", "world"),
                config.getDouble(homeSection + ".x"),
                config.getDouble(homeSection + ".y"),
                config.getDouble(homeSection + ".z"),
                (float) config.getDouble(homeSection + ".yaw"),
                (float) config.getDouble(homeSection + ".pitch"))
        );
    }

    public static void deleteHome(PlayerI player, Home home) {
        ConfigurationI config = Platform.getServer().getConfiguration();

        String playerSection = "homes." + player.getUniqueId();
        if (!config.exists(playerSection)) {
            return;
        }

        config.set(playerSection + "." + home.name(), null);
        config.saveConfig();
    }
}
