package de.craftery.craftinghomes.common.storage;

import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.api.ConfigurationI;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YmlDataStorageProvider implements DataStorageProvider {
    private final ConfigurationI configuration;

    public YmlDataStorageProvider() {
        configuration = Platform.getServer().getConfiguration("database.yml");
    }

    @Override
    public void register(AbstractDataModel model) {
        // nothing has to be done here
    }

    @Override
    public long getNextId(String qualifiedName) {
        long id = 0;

        if (configuration.exists(qualifiedName + ".sequenceIdentifier")) {
            id = configuration.getLong(qualifiedName + ".sequenceIdentifier");
        }
        configuration.set(qualifiedName + ".sequenceIdentifier", id + 1);
        configuration.saveConfig();

        return id;
    }

    @Override
    public void save(String qualifiedName, long id, Map<String, Map.Entry<FieldType, Object>> saveObject) {
        for (Map.Entry<String, Map.Entry<FieldType, Object>> field : saveObject.entrySet()) {
            String path = qualifiedName + "." + id + "." + field.getKey();
            switch (field.getValue().getKey()) {
                case STRING -> configuration.set(path, (String) field.getValue().getValue());
                case DOUBLE -> configuration.set(path, (Double) field.getValue().getValue());
                case LONG -> configuration.set(path, (Long) field.getValue().getValue());
                case FLOAT -> configuration.set(path, (Float) field.getValue().getValue());
            }
        }
        configuration.saveConfig();
    }

    @Override
    public void update(String qualifiedName, long id, Map<String, Map.Entry<FieldType, Object>> saveObject) {
        configuration.set(qualifiedName + "." + id, null);
        this.save(qualifiedName, id, saveObject);
    }

    private @Nullable Object getField(String path, FieldType type) {
        return switch (type) {
            case STRING -> configuration.getString(path);
            case DOUBLE -> configuration.getDouble(path);
            case LONG -> configuration.getLong(path);
            case FLOAT -> (float) configuration.getDouble(path);
        };
    }

    @Override
    public <T extends AbstractDataModel> List<T> getByField(String qualifiedName, Class<T> clazz, Map<String, FieldType> fields, String field, Object value) {
        if (fields.get(field) == null) {
            throw new RuntimeException("Field " + field + " does not exist in " + qualifiedName);
        }
        if (!configuration.exists(qualifiedName)) return new ArrayList<>();


        List<T> result = new ArrayList<>();
        for (String entry : configuration.getKeys(qualifiedName)) {
            if (entry.equals("sequenceIdentifier")) continue;
            String row = qualifiedName + "." + entry;

            Object contents = getField(row + "." + field, fields.get(field));
            if (contents == null) continue;
            if (!contents.equals(value)) continue;
            try {
                T instance = clazz.getDeclaredConstructor().newInstance();
                instance.setId(Long.parseLong(entry));
                for (Map.Entry<String, FieldType> requestedFields : fields.entrySet()) {
                    instance.setField(requestedFields.getKey(), getField(row + "." + requestedFields.getKey(), requestedFields.getValue()));
                }

                result.add(instance);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException("Could not create instance of " + clazz.getSimpleName(), e);
            }
        }

        return result;
    }

    @Override
    public void delete(String qualifiedName, long id) {
        configuration.set(qualifiedName + "." + id, null);
        configuration.saveConfig();
    }
}