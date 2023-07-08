package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.BukkitPlatform;
import de.craftery.craftinghomes.common.api.ConfigurationI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class ConfigrationImpl implements ConfigurationI {
    private final FileConfiguration config;

    public ConfigrationImpl(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public void set(String path, Object value) {
        this.config.set(path, value);
    }

    @Override
    public Integer getInt(String path, int def) {
        return this.config.getInt(path, def);
    }

    @Override
    public String getString(String path, String def) {
        return this.config.getString(path, def);
    }

    @Override
    public double getDouble(String path) {
        return this.config.getDouble(path);
    }

    @Override
    public boolean exists(String path) {
        return this.config.get(path) != null;
    }

    @Override
    public Set<String> getKeys(String path) {
        ConfigurationSection sec = this.config.getConfigurationSection(path);
        if (sec == null) return new HashSet<>();
        return sec.getKeys(false);
    }

    @Override
    public void saveConfig() {
        BukkitPlatform.saveConfiguration();
    }
}
