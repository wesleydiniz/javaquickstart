package com.ef.dao;

import com.ef.utils.IgnoreField;
import org.apache.commons.lang.WordUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 *  I choose not to use an ORM Framework because it makes things so simple and 
 *  using pure JDBC the application will have a better performance 
 * 
 * @param <T> An entity to be saved, updated or selected
 */
public abstract class BaseDAO<T> {

    private Connection connection;

    public BaseDAO() {
        initConnection();
    }

    protected abstract Class<T> getEntityClass();

    /**
     *  Start connection with database and create a connection instance to be used for all methods
     */
    public void initConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/logparser","root","root");
            this.connection = connection;
        } catch (Exception e) {
            throw new RuntimeException("Error while connection to database. Check if the schema logparser is created.");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     *  Insert a single entity to database
     * @param entity
     */
    public void insert(T entity) {
        try {
            final Statement statement = getConnection().createStatement();
            statement.executeUpdate(buildInsertStatement(entity));
        } catch (Exception e) {
            throw new RuntimeException("Error while inserting entity " + e.getMessage());
        }
    }

    /**
     * Makes a bulk insert to improve application performance
     * @param entities
     */
    public void bulkInsert(List<T> entities) {
        try {
            final Statement statement = getConnection().createStatement();
            statement.executeUpdate(buildBulkInsertStatement(entities));
        } catch (Exception e) {
            throw new RuntimeException("Error while inserting entity " + e.getMessage());
        }
    }

    /**
     *  Execute a simple query on database
     * @param query simple SELECT statement
     * @return List of entities
     */
    public List<T> find(String query) {
        try {
            final Statement statement = getConnection().createStatement();
            return convertResultSet(statement.executeQuery(query));
        } catch (Exception e) {
            throw new RuntimeException("Error fetching entities | " + e.getMessage());
        }
    }

    /**
     *  Convert a ResultSet to DAO's entity type
     * @param rs
     * @return
     * @throws Exception
     */
    private List<T> convertResultSet(ResultSet rs) throws Exception {
        List<T> result = new ArrayList<>();
        while (rs.next()) {
            T entity = getEntityClass().getConstructor().newInstance();
            Stream<Field> fields = Arrays.asList(getEntityClass().getDeclaredFields()).stream();
            fields.forEach(
                    f -> {
                        try {
                            if (!f.isAnnotationPresent(IgnoreField.class) && columnExists(rs,f.getName())) {
                                getEntityClass().getMethod("set" + WordUtils.capitalize(f.getName()),String.class).invoke(entity, rs.getString(f.getName()));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
            result.add(entity);
        }
        
        return result;
    }

    /**
     * Check if a field is compatible with the columns on the result set
     * @param rs
     * @param fieldName
     * @return
     */
    private boolean columnExists(ResultSet rs, String fieldName) {
        boolean result = true;
        try{
            rs.findColumn(fieldName);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * Build an insert statement based on entity fields
     * @param entity
     * @return Insert statement
     */
    public String buildInsertStatement(T entity) {
        final List<Field> fields = Arrays.asList(getEntityClass().getDeclaredFields());

        final StringJoiner fieldsToInsert = new StringJoiner(",");
        final StringJoiner valuesToInsert = new StringJoiner(",");
        for (Field field : fields) {
            if (field.isAnnotationPresent(IgnoreField.class)) {
                continue;
            }
            
            fieldsToInsert.add(field.getName());
            try {
                valuesToInsert.add("'"+getEntityClass().getMethod("get" + WordUtils.capitalize(field.getName())).invoke(entity).toString()+"'");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return String.format("INSERT INTO %s ( %s ) VALUES ( %s )", getEntityClass().getSimpleName().toLowerCase(), fieldsToInsert, valuesToInsert);
    }

    /**
     * Build an bulk insert statement based on entity fields
     * @param entities
     * @return Insert statement
     */
    private String buildBulkInsertStatement(List<T> entities) {
        List<Field> fields = Arrays.asList(getEntityClass().getDeclaredFields());
        StringJoiner bulkValues = new StringJoiner(",");
        String fieldsToInsert = generateFieldsToInsert();
        
        for (T entity : entities) {
            final StringJoiner valuesToInsert = new StringJoiner(",");
            for (Field field : fields) {
                try {
                    if (field.isAnnotationPresent(IgnoreField.class)) {
                        continue;
                    }
                    valuesToInsert.add("'" + getEntityClass().getMethod("get" + WordUtils.capitalize(field.getName())).invoke(entity).toString() + "'");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            bulkValues.add("("+valuesToInsert+")");
        }

        return String.format("INSERT INTO %s ( %s ) VALUES  %s ", getEntityClass().getSimpleName().toLowerCase(), fieldsToInsert, bulkValues);
    }

    /**
     * Generate the fields do use on insert statement
     * @return
     */
    private String generateFieldsToInsert() {
        final List<Field> fields = Arrays.asList(getEntityClass().getDeclaredFields());
        final StringJoiner fieldsToInsert = new StringJoiner(",");
        for (Field field : fields) {
            if (field.isAnnotationPresent(IgnoreField.class)) {
                continue;
            }
            fieldsToInsert.add(field.getName());
        }
        
        return fieldsToInsert.toString();
    }
}
