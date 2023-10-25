package de.craftery.craftinghomes.impl;

import de.craftery.craftinghomes.common.api.ConfigurationI;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TestConfiguration implements ConfigurationI {
    private Map<String, Object> data = new TreeMap<>();
    private static Map<String, Map<String, Object>> allData = new TreeMap<>();
    private final String configName;

    public TestConfiguration (String configName) {
        this.configName = configName;
        if (allData.containsKey(configName)) {
            data = allData.get(configName);
            return;
        }

        if (configName.equals("config.yml")) {
            try (InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("config.yml")){
                if (inStream == null) throw new RuntimeException("config.yml could not be loaded");
                Yaml yaml = new Yaml();
                data = yaml.load(inStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void eraseData() {
        allData = new TreeMap<>();
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

            currentNode.putIfAbsent(parts[i], new TreeMap<String, Object>());
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
        allData.put(configName, data);
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
