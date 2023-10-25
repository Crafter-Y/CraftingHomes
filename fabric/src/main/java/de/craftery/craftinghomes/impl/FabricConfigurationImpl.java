package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.ConfigurationI;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

import joptsimple.internal.Strings;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class FabricConfigurationImpl implements ConfigurationI {
    private Map<String, Object> data = new HashMap<>();
    private final File configFile;

    public FabricConfigurationImpl (Path configDirectory) {
        this(configDirectory, "config.yml");
    }

    public FabricConfigurationImpl (Path configDirectory, String configName) {
        configFile = new File(configDirectory.toFile(), configName);
        Yaml yaml = new Yaml();
        try {
            data = yaml.load(new FileInputStream(configFile));
            if (data == null) data = new HashMap<>();
            return;
        } catch (FileNotFoundException ignored) {}

        URL jarFile = FabricConfigurationImpl.class.getClassLoader().getResource(configName);
        if (jarFile != null) {
            try (InputStream reader = jarFile.openStream()) {
                List<String> lines = IOUtils.readLines(reader, "UTF-8");
                data = yaml.load(Strings.join(lines, "\n"));
            } catch (IOException ignored) {}
        }
    }

    private Object get(String path) {
        String[] parts = path.split("\\.");

        Map<String, Object> currentNode = this.data;
        for (int i = 0; i < parts.length; i++) {
            if (i == parts.length - 1) {
                return currentNode.get(parts[i]);
            }

            if (currentNode.get(parts[i]) == null) return null;
            currentNode = (Map<String, Object>) currentNode.get(parts[i]);
        }
        return null;
    }


    @Override
    public void set(String path, Object value) {
        String[] parts = path.split("\\.");

        Map<String, Object> currentNode = this.data;
        for (int i = 0; i < parts.length; i++) {
            if (i == parts.length - 1) {
                if (value == null) {
                    currentNode.remove(parts[i]);
                    return;
                }
                currentNode.put(parts[i], value);
                return;
            }

            currentNode.putIfAbsent(parts[i], new HashMap<String, Object>());
            currentNode = (Map<String, Object>) currentNode.get(parts[i]);
        }
    }

    @Override
    public void delete(String path) {
        this.set(path, null);
    }

    @Override
    public Integer getInt(String path, int def) {
         return (Integer) this.get(path);
    }

    @Override
    public String getString(String path) {
        return (String) this.get(path);
    }

    @Override
    public String getString(String path, String def) {
        String value = this.getString(path);
        if (value == null) return def;
        return value;
    }

    @Override
    public Double getDouble(String path) {
        return (Double) this.get(path);
    }

    @Override
    public Long getLong(String path) {
        if (this.get(path) == null) return null;
        if (this.get(path) instanceof Integer) return ((Integer) this.get(path)).longValue();
        return (Long) this.get(path);
    }

    @Override
    public Float getFloat(String path) {
        if (this.get(path) == null) return null;
        if (this.get(path) instanceof Double) return ((Double) this.get(path)).floatValue();
        return (Float) this.get(path);
    }

    @Override
    public boolean exists(String path) {
        return this.get(path) != null;
    }

    @Override
    public Set<String> getKeys(String path) {
        Object result = this.get(path);
        if (result == null) return new HashSet<>();

        if (result instanceof Map) {
            return ((Map<String, Object>) result).keySet();
        }
        return null;
    }

    @Override
    public void saveConfig() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(configFile)){
            yaml.dump(data, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addDefault(String path, Object value) {
        data.putIfAbsent(path, value);
    }

    @Override
    public void applyDefaults() {
        this.saveConfig();
    }
}
