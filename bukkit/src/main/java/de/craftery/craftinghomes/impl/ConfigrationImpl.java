package de.craftery.craftinghomes.impl;

import com.google.common.base.Charsets;
import de.craftery.craftinghomes.BukkitPlatform;
import de.craftery.craftinghomes.common.api.ConfigurationI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class ConfigrationImpl implements ConfigurationI {
    private final FileConfiguration config;
    private final File configFile;

    public ConfigrationImpl(File configDirectory) {
        this(configDirectory, "config.yml");
    }

    public ConfigrationImpl (File configDirectory, String configName) {
        this.configFile = new File(configDirectory, configName);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.tryLoadDefaults(this.config, configName);
    }

    private void tryLoadDefaults(FileConfiguration config, String configName) {
        InputStream defConfigStream = BukkitPlatform.getInstance().getResource(configName);
        if (defConfigStream == null) {
            return;
        }

        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
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
        try {
            this.config.save(this.configFile);
        } catch (Exception e) {
            throw new RuntimeException("Could not save config file!", e);
        }

    }

    @Override
    public void addDefault(String path, Object value) {
        this.config.addDefault(path, value);
    }

    @Override
    public void applyDefaults() {
        this.config.options().copyDefaults(true);
        this.saveConfig();
    }
}
