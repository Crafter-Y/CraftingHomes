package de.craftery.craftinghomes.common.storage;

import java.util.List;
import java.util.Map;

public interface DataStorageProvider {
    void register(AbstractDataModel model);
    long getNextId(String qualifiedName);
    void save(String qualifiedName, long id, Map<String, Map.Entry<FieldType, Object>> saveObject);
    void update(String qualifiedName, long id, Map<String, Map.Entry<FieldType, Object>> saveObject);
    <T extends AbstractDataModel> List<T> getByField(String qualifiedName, Class<T> clazz, Map<String, FieldType> fields, String field, Object value);
    void delete(String qualifiedName, long id);
}
