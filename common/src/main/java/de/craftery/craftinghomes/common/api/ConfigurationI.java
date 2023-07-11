package de.craftery.craftinghomes.common.api;

import java.util.Set;

public interface ConfigurationI {
    void set(String path, Object value);
    Integer getInt(String path, int def);
    String getString(String path, String def);
    double getDouble(String path);
    boolean exists(String path);
    Set<String> getKeys(String path);
    void saveConfig();
    void addDefault(String path, Object value);
    void applyDefaults();
}
