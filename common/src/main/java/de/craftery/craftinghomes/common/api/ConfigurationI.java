package de.craftery.craftinghomes.common.api;

import java.util.Set;

public interface ConfigurationI {
    void set(String path, Object value);
    void delete(String path);
    Integer getInt(String path, int def);
    String getString(String path);
    String getString(String path, String def);
    Double getDouble(String path);
    Long getLong(String path);
    Float getFloat(String path);
    boolean exists(String path);
    Set<String> getKeys(String path);
    void saveConfig();
    void addDefault(String path, Object value);
    void applyDefaults();
}
