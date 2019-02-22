package repository;


import for_database.TablesHashMap;
import for_database.TableConfig;
import org.springframework.stereotype.Component;

public interface DatabaseRepository {
    void createEmptyDatabase(String dbName);
    TableConfig getTableConfig();
    void setEmptyTableConfig();
}