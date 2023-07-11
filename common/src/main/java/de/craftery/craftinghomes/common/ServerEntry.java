package de.craftery.craftinghomes.common;

import de.craftery.craftinghomes.common.api.ConfigurationI;

public interface ServerEntry {
    void log(String message);
    void registerCommand(AbstractCommand command);
    ConfigurationI getConfiguration();
    ConfigurationI getConfiguration(String configFileName);
}
