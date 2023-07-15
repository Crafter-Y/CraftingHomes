package de.craftery.craftinghomes.common.storage;

import de.craftery.craftinghomes.annotation.annotations.Column;
import de.craftery.craftinghomes.common.Platform;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDataModel {
    @Getter
    @Setter
    private Long id = null;
    @Getter
    private final String qualifiedName;
    @Getter
    private final Map<String, FieldType> fields = new HashMap<>();

    public AbstractDataModel() {
        this.qualifiedName = this.getClass().getSimpleName().toLowerCase();

        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Column.class) == null) continue;

            FieldType type = switch (field.getType().toString()) {
                case "class java.lang.String" -> FieldType.STRING;
                case "class java.lang.Double" -> FieldType.DOUBLE;
                case "class java.lang.Long" -> FieldType.LONG;
                case "class java.lang.Float" -> FieldType.FLOAT;
                default -> throw new RuntimeException("Unsupported field type: " + field.getType());
            };
            fields.put(field.getName(), type);
        }
    }

    private Object getField(String field) {
        try {
            Field f = this.getClass().getDeclaredField(field);
            boolean accessible = f.canAccess(this);
            f.setAccessible(true);
            Object value = f.get(this);
            f.setAccessible(accessible);

            if (value == null) {
                throw new RuntimeException("Field " + field + " is null. Fields cannot be null for now.");
            }
            return value;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not get field " + field + " from " + this.getClass().getSimpleName(), e);
        }
    }

    public void setField(String field, Object value) {
        try {
            Field f = this.getClass().getDeclaredField(field);
            boolean accessible = f.canAccess(this);
            f.setAccessible(true);
            f.set(this, value);
            f.setAccessible(accessible);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not set field " + field + " from " + this.getClass().getSimpleName(), e);
        }
    }

    public long save() {
        if (this.id != null) {
            throw new RuntimeException("This data model has already been created! Try using update() instead.");
        }

        this.id = Platform.getDataStorageProvider().getNextId(this.qualifiedName);
        Map<String, Map.Entry<FieldType, Object>> saveObject = new HashMap<>();
        for (Map.Entry<String, FieldType> entry : this.fields.entrySet()) {
            saveObject.put(entry.getKey(), Map.entry(entry.getValue(), this.getField(entry.getKey())));
        }
        Platform.getDataStorageProvider().save(this.qualifiedName, this.id, saveObject);

        return this.id;
    }

    public long update() {
        if (this.id == null) {
            throw new RuntimeException("This data model has not been created yet! Try using save() instead.");
        }

        Map<String, Map.Entry<FieldType, Object>> saveObject = new HashMap<>();
        for (Map.Entry<String, FieldType> entry : this.fields.entrySet()) {
            saveObject.put(entry.getKey(), Map.entry(entry.getValue(), this.getField(entry.getKey())));
        }
        Platform.getDataStorageProvider().update(this.qualifiedName, this.id, saveObject);

        return this.id;
    }

    public long saveOrUpdate() {
        if (this.id == null) {
            return this.save();
        } else {
            return this.update();
        }
    }

    public void delete() {
        if (this.id == null) {
            throw new RuntimeException("This data model has not been created yet! Try using save() instead.");
        }

        Platform.getDataStorageProvider().delete(this.qualifiedName, this.id);
    }

    protected static <T extends AbstractDataModel> List<T> getByField(Class<T> clazz, String field, Object value) {
        try {
            T type = clazz.getDeclaredConstructor().newInstance();

            if (!type.getFields().containsKey(field)) {
                throw new RuntimeException("Field " + field + " does not exist in " + clazz.getSimpleName());
            }
            return Platform.getDataStorageProvider().getByField(type.getQualifiedName(), clazz, type.getFields(), field, value);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Could not create instance of " + clazz.getSimpleName(), e);
        }
    }
}
