package repository;

import java.util.Arrays;
import java.util.List;


import for_database.Column.Type;
import for_database.TablesHashMap;
import for_database.Table;
import for_database.Column;
import for_database.TableConfig;
    public class InMemDatabaseRepository implements DatabaseRepository {

        private TablesHashMap database;
        private TableConfig config;

        public InMemDatabaseRepository() {

            String dbName = "My database";
            database = new TablesHashMap(dbName);

            String tableName = "My table";
             config = new TableConfig();
            config.addColumn("Primary key column", Column.Type.INT, true);
            config.addColumn("Integer column", Column.Type.INT, false);
            config.addColumn("Char column", Column.Type.CHAR, false);
            config.addColumn("Real column", Type.FLOAT, false);
            config.addColumn("Date column", Type.DATE, false);
            database.createTable(tableName, config);

            Table table = database.getTable(tableName);
            List<String> row1 = Arrays.asList("1", "123", "a", "0.1",
                    "http://worldartsme.com/images/123-clipart-1.jpg");
            List<String> row2 = Arrays.asList("2", "234", "b", "0.2",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/NY-234.svg/750px-NY-234.svg.png");
            List<String> row3 = Arrays.asList("3", "345", "c", "0.3",
                    "http://www.zahlenparty.de/wp-content/uploads/345.jpg");
            table.addRow(row1);
            table.addRow(row2);
            table.addRow(row3);

            database.addTable(tableName + "1", config);
            database.addTable(tableName + "2", config);
            Table table1 = database.getTable(tableName + "1");
            Table table2 = database.getTable(tableName + "2");
            table1.addRow(row1);
            table1.addRow(row2);
            table2.addRow(row1);
            table2.addRow(row3);
        }

        @Override
        public void createEmptyDatabase(String dbName) {
            database = new Database(dbName);
        }

        @Override
        public void setCurrentDatabase(Database db) {
            database = db;
        }

        @Override
        public Database getCurrentDatabase() {
            return database;
        }

        @Override
        public TableConfig getTableConfig() {
            return config;
        }

        @Override
        public void setEmptyTableConfig() {
            config = new TableConfig();
        }
    }
