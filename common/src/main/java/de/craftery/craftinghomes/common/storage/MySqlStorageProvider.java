package de.craftery.craftinghomes.common.storage;

import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.api.ConfigurationI;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MySqlStorageProvider implements DataStorageProvider {
    private final Connection connection;
    private final String prefix;

    public MySqlStorageProvider() throws RuntimeException {
        ConfigurationI config = Platform.getServer().getConfiguration();

        String host = config.getString("mysql.host", "localhost");
        int port = config.getInt("mysql.port", 3306);
        String database = config.getString("mysql.database", "craftinghomes");
        String username = config.getString("mysql.username", "root");
        String password = config.getString("mysql.password", "");
        this.prefix = config.getString("mysql.prefix", "ch_");

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "sequence` (`name` VARCHAR(128) NOT NULL PRIMARY KEY, `val` BIGINT NOT NULL DEFAULT 1)").execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(AbstractDataModel model) {
        if (model.getQualifiedName().equalsIgnoreCase("sequence")) {
            throw new RuntimeException("A model cannot have the name 'sequence'. This term is reserved internally.");
        }

        StringBuilder createQuery = new StringBuilder();
        createQuery.append("CREATE TABLE IF NOT EXISTS ").append(prefix).append(model.getQualifiedName()).append(" (");
        List<String> fieldStrings = new ArrayList<>();
        fieldStrings.add("id INT NOT NULL PRIMARY KEY");
        for (Map.Entry<String, FieldType> entry : model.getFields().entrySet()) {
            StringBuilder fieldBuilder = new StringBuilder();
            fieldBuilder.append(entry.getKey()).append(" ");
            switch (entry.getValue()) {
                case STRING: {
                    fieldBuilder.append("VARCHAR(1024)");
                    break;
                }
                case LONG: {
                    fieldBuilder.append("BIGINT");
                    break;
                }
                case FLOAT: {
                    fieldBuilder.append("FLOAT");
                    break;
                }
                case DOUBLE: {
                    fieldBuilder.append("DOUBLE");
                    break;
                }
            }
            fieldStrings.add(fieldBuilder.toString());
        }
        createQuery.append(String.join(", ", fieldStrings));
        createQuery.append(")");

        try {
            connection.prepareStatement(createQuery.toString()).execute();
            connection.prepareStatement("INSERT IGNORE INTO `" + prefix + "sequence` (`name`) VALUES ('"+ model.getQualifiedName() +"')").execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public long getNextId(String qualifiedName) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT `val` FROM `"+ prefix +"sequence` WHERE `name` = '"+ qualifiedName +"' LIMIT 1;");
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("No result for '" + qualifiedName + "'");
            }
            int nextId = rs.getInt("val");
            if (nextId == 0) {
                throw new RuntimeException("Returned nextId for '" + qualifiedName + "' is zero.");
            }

            connection.prepareStatement("UPDATE `"+ prefix +"sequence` SET `val` = '"+ (nextId + 1) +"' WHERE name = '"+ qualifiedName +"'").execute();
            return nextId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(String qualifiedName, long id, Map<String, Map.Entry<FieldType, Object>> saveObject) {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO `").append(prefix).append(qualifiedName).append("` (`id`, ");

        List<String> fieldStrings = new ArrayList<>();
        for (String key : saveObject.keySet()) {
            fieldStrings.add("`" + key + "`");
        }
        query.append(String.join(", ", fieldStrings)).append(") VALUES ('").append(id).append("', ");

        List<String> valueStrings = new ArrayList<>();
        for (Map.Entry<FieldType, Object> entry : saveObject.values()) {
            valueStrings.add("'" + entry.getValue().toString() + "'");
        }
        query.append(String.join(", ", valueStrings)).append(")");

        try {
            connection.prepareStatement(query.toString()).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(String qualifiedName, long id, Map<String, Map.Entry<FieldType, Object>> saveObject) {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE `").append(prefix).append(qualifiedName).append("` SET ");

        List<String> setStrings = new ArrayList<>();
        for (Map.Entry<String, Map.Entry<FieldType, Object>> field : saveObject.entrySet()) {
            setStrings.add("`" + field.getKey() + "` = '" + field.getValue().getValue() + "'");
        }
        query.append(String.join(", ", setStrings)).append(" WHERE `id` = '").append(id).append("';");

        try {
            connection.prepareStatement(query.toString()).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private @Nullable Object getField(ResultSet rs, String field, FieldType type) {
        try{
            Object toType = null;
            switch (type) {
                case STRING: { toType = rs.getString(field); break; }
                case DOUBLE: { toType = rs.getDouble(field); break; }
                case LONG: { toType = rs.getLong(field); break; }
                case FLOAT: { toType = rs.getFloat(field); break; }
            }
            return toType;
        } catch (SQLException e) {
            throw new RuntimeException("Field " + field + " could not be found in query");
        }
    }

    @Override
    public <T extends AbstractDataModel> List<T> getByField(String qualifiedName, Class<T> clazz, Map<String, FieldType> fields, String field, Object value) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `"+ prefix + qualifiedName + "` WHERE `"+ field +"` = '"+ value +"';");
            ResultSet rs = stmt.executeQuery();


            List<T> result = new ArrayList<>();
            while (rs.next()) {
                try {
                    T instance = clazz.getDeclaredConstructor().newInstance();
                    int entryId = rs.getInt("id");
                    instance.setId((long) entryId);
                    for (Map.Entry<String, FieldType> requestedFields : fields.entrySet()) {
                        Object fieldValue = getField(rs, requestedFields.getKey(), requestedFields.getValue());
                        instance.setField(requestedFields.getKey(), fieldValue);
                    }
                    result.add(instance);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException("Could not create instance of " + clazz.getSimpleName(), e);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String qualifiedName, long id) {
        try {
            connection.prepareStatement("DELETE FROM `"+ prefix + qualifiedName +"` WHERE `id` = '"+ id +"'").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
