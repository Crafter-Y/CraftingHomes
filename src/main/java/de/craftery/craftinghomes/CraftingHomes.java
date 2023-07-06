package de.craftery.craftinghomes;

import de.craftery.craftinghomes.commands.DelhomeCommand;
import de.craftery.craftinghomes.commands.HomeCommand;
import de.craftery.craftinghomes.commands.HomesCommand;
import de.craftery.craftinghomes.commands.SethomeCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;

public final class CraftingHomes extends JavaPlugin {

    private static final Map<UUID, List<Home>> homes = new HashMap<>();

    private final List<String> commands = new ArrayList<>();

    @Override
    public void onEnable() {
        this.getLogger().fine("CraftingHomes is starting up!");

        registerCommand("sethome", new SethomeCommand());
        registerCommand("home", new HomeCommand());
        registerCommand("homes", new HomesCommand());
        registerCommand("delhome", new DelhomeCommand());
    }

    @Override
    public void onDisable() {
        this.getLogger().fine("CraftingHomes is shutting down!");

        unregisterCommands();
    }

    private <T extends CommandExecutor & TabCompleter> void  registerCommand(String command, T inst) {
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

    public static List<Home> getHomes(Player player) {
        if (homes.containsKey(player.getUniqueId())) {
            return homes.get(player.getUniqueId());
        }
        return new ArrayList<>();
    }

    public static void addHome(Player player, Home home) {
        if (!homes.containsKey(player.getUniqueId())) {
            homes.put(player.getUniqueId(), new ArrayList<>());
        }
        homes.get(player.getUniqueId()).add(home);
    }

    public static @Nullable Home getHome(Player player, String name) {
        if (!homes.containsKey(player.getUniqueId())) {
            return null;
        }
        return homes.get(player.getUniqueId()).stream().filter(home -> home.getName().equals(name)).findFirst().orElse(null);
    }

    public static void deleteHome(Player player, Home home) {
        if (!homes.containsKey(player.getUniqueId())) {
            return;
        }
        homes.get(player.getUniqueId()).remove(home);
    }
}
