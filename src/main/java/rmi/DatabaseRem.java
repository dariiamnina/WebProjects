package iiop;

import for_database.Operation;
import for_database.Reader;
import for_database.TablesHashMap;

public class DatabaseRem implements IRemoteClass {

    private TablesHashMap database;

    public DatabaseRem(String databasePath) {
        try {
            database = new Reader(databasePath).read();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Operation query(String msg) {
        System.out.println("Accepted query: " + msg);
        return database.query(msg);
    }
}