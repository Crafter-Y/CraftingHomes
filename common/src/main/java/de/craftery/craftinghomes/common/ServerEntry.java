package de.craftery.craftinghomes.common;

import de.craftery.craftinghomes.common.api.ConfigurationI;
import de.craftery.craftinghomes.common.api.OfflinePlayerI;
import de.craftery.craftinghomes.common.api.PlayerI;
import de.craftery.craftinghomes.common.gui.GuiBuilder;
import org.jetbrains.annotations.Nullable;

public interface ServerEntry {
    void log(String message);
    void registerCommand(AbstractCommand command);
    ConfigurationI getConfiguration();
    ConfigurationI getConfiguration(String configFileName);
    @Nullable OfflinePlayerI getOfflinePlayer(String name);
    void openGui(PlayerI player, GuiBuilder builder);
}
