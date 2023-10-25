package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.AbstractCommand;
import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.ServerEntry;
import de.craftery.craftinghomes.common.api.ConfigurationI;
import de.craftery.craftinghomes.common.api.OfflinePlayerI;
import de.craftery.craftinghomes.common.api.PlayerI;
import de.craftery.craftinghomes.common.gui.GuiBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestPlatform implements ServerEntry {
    private final Map<String, AbstractCommand> commands = new HashMap<>();
    private final Map<String, GuiBuilder> openGuis = new HashMap<>();
    private static final List<TestPlayerImplementation> existingPlayers = new ArrayList<>();

    public void onEnable() {
        Platform.onEnable(this);
    }

    public void onDisable() {
        Platform.shutdown();
        TestConfiguration.eraseData();
        existingPlayers.clear();
    }

    public static void registerPlayer(TestPlayerImplementation player) {
        existingPlayers.add(player);
    }

    public void clickGuiSlot(PlayerI player, int slot) {
        openGuis.get(player.getUniqueId()).getSlots().get(slot).getCallback().onClick();
    }

    public int openGuiSlotCount(PlayerI player) {
        return openGuis.get(player.getUniqueId()).getSlots().size();
    }

    public boolean hasOpenGui(PlayerI player) {
        return openGuis.containsKey(player.getUniqueId());
    }

    private AbstractCommand getCommand(String command) {
        AbstractCommand cmd = this.commands.get(command);
        if (cmd == null) {
            throw new RuntimeException("Command not found: " + command);
        }
        return cmd;
    }

    public void executeCommand(String command, TestConsolePlayerImplementation consolePlayer, String[] args) {
        getCommand(command).onCommand(consolePlayer, args);
    }

    public void executeCommand(String command, TestPlayerImplementation player, String[] args) {
        getCommand(command).onCommand(player, args);
    }

    public List<String> getCommandSuggestions(String command, TestConsolePlayerImplementation consolePlayer, String[] args) {
        return getCommand(command).onTabComplete(consolePlayer, args);
    }

    public List<String> getCommandSuggestions(String command, TestPlayerImplementation player, String[] args) {
        return getCommand(command).onTabComplete(player, args);
    }

    @Override
    public void log(String message) {
        System.out.println("LOG: '"+ message +"'");
    }

    @Override
    public void registerCommand(AbstractCommand command) {
        this.commands.put(command.getName(), command);
    }

    @Override
    public ConfigurationI getConfiguration() {
        return getConfiguration("config.yml");
    }

    @Override
    public ConfigurationI getConfiguration(String configFileName) {
        return new TestConfiguration(configFileName);
    }

    @Override
    public @Nullable OfflinePlayerI getOfflinePlayer(String name) {
        for (TestPlayerImplementation player : existingPlayers) {
            if (player.getName().equals(name)) {
                return new TestOfflinePlayer(player.getUniqueId(), player.getName());
            }
        }
        return null;
    }

    @Override
    public void openGui(PlayerI player, GuiBuilder builder) {
        openGuis.put(player.getUniqueId(), builder);
    }
}
