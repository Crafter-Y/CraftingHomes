package de.craftery.craftinghomes;

import de.craftery.craftinghomes.common.AbstractCommand;
import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.ServerEntry;
import de.craftery.craftinghomes.common.api.ConfigurationI;
import de.craftery.craftinghomes.common.api.OfflinePlayerI;
import de.craftery.craftinghomes.common.api.PlayerI;
import de.craftery.craftinghomes.common.gui.GuiBuilder;
import de.craftery.craftinghomes.common.gui.GuiClickCallback;
import de.craftery.craftinghomes.common.gui.GuiItem;
import de.craftery.craftinghomes.helper.CommandStub;
import de.craftery.craftinghomes.impl.ConfigrationImpl;
import de.craftery.craftinghomes.impl.OfflinePlayerImpl;

import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class BukkitPlatform extends JavaPlugin implements ServerEntry {
    private static BukkitPlatform instance;

    private final List<String> commands = new ArrayList<>();

    public final Map<ItemStack, GuiClickCallback> guiClickCallbacks = new HashMap<>();
    public final Set<String> protectedWindowTitles = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;
        Platform.onEnable(this);
        this.getServer().getPluginManager().registerEvents(new InventoryProtector(), this);
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
        return new ConfigrationImpl(this.getDataFolder());
    }

    @Override
    public ConfigurationI getConfiguration(String configFileName) {
        return new ConfigrationImpl(this.getDataFolder(), configFileName);
    }

    @Override
    public @Nullable OfflinePlayerI getOfflinePlayer(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(name);
        if (!offlinePlayer.hasPlayedBefore()) return null;
        return new OfflinePlayerImpl(offlinePlayer);
    }

    @Override
    public void openGui(PlayerI player, GuiBuilder builder) {
        String title = ChatColor.translateAlternateColorCodes('&', builder.getTitle());
        Inventory inv = Bukkit.createInventory(null, 9*builder.getRows(), title);
        protectedWindowTitles.add(title);
        for (Map.Entry<Integer, GuiItem> item : builder.getSlots().entrySet()) {
            ItemStack invItem = new ItemStack(Material.PAPER);
            //TODO: Use player heads for this

            ItemMeta meta = invItem.getItemMeta();
            meta.setLore(item.getValue().getLores().stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', item.getValue().getName()));
            invItem.setItemMeta(meta);
            this.guiClickCallbacks.put(invItem, item.getValue().getCallback());

            inv.setItem(item.getKey(), invItem);
        }

        Player p = Bukkit.getPlayer(UUID.fromString(player.getUniqueId()));
        if (p == null) return;
        p.openInventory(inv);
    }


    public static BukkitPlatform getInstance() {
        return instance;
    }
}
