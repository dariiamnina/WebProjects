package spring;

import for_database.TablesHashMap;
import for_database.Reader;
import for_database.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
public class RestDbController
{
    private TablesHashMap database;
    public RestDbController()
    {
        try
        {
            database = new Reader("test.json").read();
        } catch (Exception e)
        {
            database = new TablesHashMap("test.json");
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/database/tables")
    public Operation tables() {
        return database.query("list tables");
    }

    @DeleteMapping(value = "/database/tables/delete/{tableName}")
    public Operation dropTable(@PathVariable String tableName) { return database.query(String.format("drop table %s", tableName));
    }
    @PostMapping(value = "/database/tables/create/{tableName}/{columns}")
    public Operation createTable(@PathVariable String columns, @PathVariable String tableName) {
        return database.query(String.format("create table %s (%s)", tableName, columns));
    }
    @GetMapping(value = "/database/{tableName}/select/{columns}/{condition}")
    public Operation selectCondition(@PathVariable String columns, @PathVariable String tableName, @PathVariable String condition) {
        return database.query(String.format("select %s from %s where %s", columns, tableName, condition));
    }

    @GetMapping(value = "/database/{tableName}/select/{columns}")
    public Operation select(@PathVariable String columns, @PathVariable String tableName) {
        return database.query(String.format("select %s from %s", columns, tableName));
    }

    @PostMapping(value = "/database/{tableName}/insert/{columns}/{values}")
    public Operation insert(@PathVariable String columns, @PathVariable String tableName, @PathVariable String values) {
        return database.query(String.format("insert into %s (%s) values (%s)", tableName, columns, values));
    }

    @DeleteMapping(value = "/database/{tableName}/delete/{condition}")
    public Operation delete(@PathVariable String tableName, @PathVariable String condition) {
        return database.query(String.format("delete from %s where %s", tableName, condition));
    }

    @DeleteMapping(value = "/database/{tableName}/delete_dublicates")
    public Operation deleteDuplicates(@PathVariable String tableName) {
        return database.query(String.format("delete duplicates %s", tableName));
    }
}
