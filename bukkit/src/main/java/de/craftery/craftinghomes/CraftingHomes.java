package de.craftery.craftinghomes;

import de.craftery.craftinghomes.commands.DelhomeCommand;
import de.craftery.craftinghomes.commands.HomeCommand;
import de.craftery.craftinghomes.commands.HomesCommand;
import de.craftery.craftinghomes.commands.SethomeCommand;
import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.ServerEntry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;

public final class CraftingHomes extends JavaPlugin implements ServerEntry {
    private static CraftingHomes instance;

    private final List<String> commands = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        Platform.onEnable(this);
        this.saveDefaultConfig();

        registerCommand("sethome", new SethomeCommand());
        registerCommand("home", new HomeCommand());
        registerCommand("homes", new HomesCommand());
        registerCommand("delhome", new DelhomeCommand());
    }

    @Override
    public void onDisable() {
        Platform.shutdown();

        unregisterCommands();
    }

    private <T extends CommandExecutor & TabCompleter> void registerCommand(String command, T inst) {
        this.commands.add(command);

        PluginCommand pluginCommand = this.getCommand(command);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(inst);
            pluginCommand.setTabCompleter(inst);
        }
    }

    private void unregisterCommands() {
        for (String command : this.commands) {
            PluginCommand pluginCommand = this.getCommand(command);
            if (pluginCommand != null) {
                pluginCommand.setExecutor(null);
                pluginCommand.setTabCompleter(null);
            }
        }
    }

    public static Integer getMaxHomes() {
        return instance.getConfig().getInt("maxHomes", 3);
    }

    private static ConfigurationSection getHomesSection() {
        ConfigurationSection section = instance.getConfig().getConfigurationSection("homes");
        if (section == null) {
            section = instance.getConfig().createSection("homes");
        }
        return section;
    }

    public static List<Home> getHomes(Player player) {
        ConfigurationSection playerSection = getHomesSection().getConfigurationSection(player.getUniqueId().toString());
        if (playerSection == null) {
            return new ArrayList<>();
        }

        List<Home> homes = new ArrayList<>();
        for(String key : playerSection.getKeys(false)) {
            ConfigurationSection homeSection = playerSection.getConfigurationSection(key);
            if (homeSection == null) continue;
            World world = Bukkit.getWorld(homeSection.getString("world", "world"));
            Home home = new Home(key, new Location(world, homeSection.getDouble("x"), homeSection.getDouble("y"), homeSection.getDouble("z"), (float) homeSection.getDouble("yaw"), (float) homeSection.getDouble("pitch")));
            homes.add(home);
        }
        return homes;
    }

    public static void addHome(Player player, Home home) {
        ConfigurationSection playerSection = getHomesSection().getConfigurationSection(player.getUniqueId().toString());
        if (playerSection == null) {
            playerSection = getHomesSection().createSection(player.getUniqueId().toString());
        }

        playerSection.createSection(home.getName());
        ConfigurationSection homeSection = playerSection.getConfigurationSection(home.getName());
        assert homeSection != null;

        homeSection.set("world", home.getLocation().getWorld().getName());
        homeSection.set("x", home.getLocation().getX());
        homeSection.set("y", home.getLocation().getY());
        homeSection.set("z", home.getLocation().getZ());
        homeSection.set("yaw", (double) home.getLocation().getYaw());
        homeSection.set("pitch", (double) home.getLocation().getPitch());

        instance.saveConfig();
    }

    public static @Nullable Home getHome(Player player, String name) {
        ConfigurationSection playerSection = getHomesSection().getConfigurationSection(player.getUniqueId().toString());
        if (playerSection == null) {
            return null;
        }

        ConfigurationSection homeSection = playerSection.getConfigurationSection(name);
        if (homeSection == null) return null;
        World world = Bukkit.getWorld(homeSection.getString("world", "world"));
        return new Home(name, new Location(world, homeSection.getDouble("x"), homeSection.getDouble("y"), homeSection.getDouble("z"), (float) homeSection.getDouble("yaw"), (float) homeSection.getDouble("pitch")));
    }

    public static void deleteHome(Player player, Home home) {
        ConfigurationSection playerSection = getHomesSection().getConfigurationSection(player.getUniqueId().toString());
        if (playerSection == null) {
            return;
        }

        playerSection.set(home.getName(), null);
        instance.saveConfig();
    }

    @Override
    public void log(String message) {
        instance.getLogger().log(Level.INFO, message);
    }
}
