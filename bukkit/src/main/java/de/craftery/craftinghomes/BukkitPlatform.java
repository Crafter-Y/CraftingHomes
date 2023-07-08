package de.craftery.craftinghomes;

import de.craftery.craftinghomes.common.AbstractCommand;
import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.ServerEntry;
import de.craftery.craftinghomes.common.api.ConfigurationI;
import de.craftery.craftinghomes.helper.CommandStub;

import de.craftery.craftinghomes.impl.ConfigrationImpl;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public final class BukkitPlatform extends JavaPlugin implements ServerEntry {
    private static BukkitPlatform instance;

    private final List<String> commands = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        Platform.onEnable(this);
        this.saveDefaultConfig();
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

    @Override
    public void log(String message) {
        instance.getLogger().log(Level.INFO, message);
    }

    @Override
    public void registerCommand(AbstractCommand command) {
        this.registerCommand(command.getName(), new CommandStub(command));
    }

    @Override
    public ConfigurationI getConfiguration() {
        return new ConfigrationImpl(this.getConfig());
    }

    public static void saveConfiguration() {
        instance.saveConfig();
    }
}
