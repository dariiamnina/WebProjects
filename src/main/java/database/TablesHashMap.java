package for_database;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mentaregex.Regex;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Collection;

public class TablesHashMap
{
    public enum SqlQuery
    {
        delete_rows("^\\s*delete\\s+from\\s+([^\\s]+)(?:\\s+where\\s+([^\\s]+))?\\s*$"),
        insert_rows("^\\s*insert\\s+into\\s+([^\\s]+)\\s+\\(([^\\)]+)\\)\\s+values\\s*\\(([^\\)]+)\\)s*$"),
        update_rows("^\\s*update\\s+([^\\s]+)\\s+set\\s*([^\\s]+)(?:\\s+where\\s+([^\\s]+))?\\s*$"),
        select_rows("^\\s*select\\s+(.+)\\s+from\\s+([^\\s]+)(?:\\s+where\\s+([^\\s]+))?\\s*$"),
        order_rows("^\\s*select\\s+(.+)\\s+from\\s+([^\\s]+)\\s+order_by\\s+([^\\s]+)\\s*$"),
        create("^\\s*create\\s+table\\s+([^\\s]+)\\s+\\(([^\\)]+)\\)\\s*$"),
        drop("^\\s*drop\\s+table\\s+([^\\s]+)\\s*$"),
        list("^\\s*(list\\s+tables)\\s*$");
        private String regex;
        public final String SUFFIX = "/i";
        SqlQuery(String regex) {
            this.regex = regex;
        }
        public String getRegex() {
            return regex + SUFFIX;
        }
    }

    private String filePath;

    private HashMap<String, Table> tables;

    public TablesHashMap(String filePath)
    {
        this.filePath = filePath;
        tables = new HashMap<>();
    }

    public void createTable(String name, Collection<Column> columns) throws Exception
    {
        if (tables.containsKey(name)) throw new Exception(String.format("table '%s' already exists", name));
        tables.put(name, new Table(name, columns));
    }

    public void dropTable(String name) throws Exception
    {
        if (!tables.containsKey(name)) throw new Exception(String.format("table '%s' doesn't exist", name));
        tables.remove(name);
    }

    public Collection<String> getTableNames()
    {
        return new ArrayList<>(tables.keySet());
    }

    public Operation query(String queryMessage)
    {
        try {
            for (SqlQuery regex : SqlQuery.values()) {
                String[] found = Regex.match(queryMessage, regex.getRegex());
                if (found == null) continue;

                ArrayList<String> matches = Arrays.stream(found).filter(Predicate.isEqual(null).negate()).map(String::trim).collect(Collectors.toCollection(ArrayList::new));

                Operation result = new Operation(Operation.Status.OK);
                boolean modified = false;

                switch (regex) {
                    case insert_rows: {
                        Table table = tables.get(matches.get(0));

                        ArrayList<Column> columns = toColumns(parseValues(matches.get(1)), table);
                        ArrayList<String> values = parseValues(matches.get(2));

                        table.insert(IntStream.range(0, columns.size()).boxed().collect(Collectors.toMap(columns::get, values::get)));
                        modified = true;
                        break;
                    }
                    case delete_rows: {
                        tables.get(matches.get(0)).delete(parsePredicate(matches.get(1)));
                        modified = true;
                        break;
                    }
                    case update_rows: {
                        Table table = tables.get(matches.get(0));

                        ArrayList<String> assignmentExprs = parseValues(matches.get(1));
                        Map<Column, String> assignments = new HashMap<>();

                        for (String assignmentExpr : assignmentExprs) {
                            String[] pair = parseEqual(assignmentExpr);
                            assignments.put(table.getColumn(pair[0]), pair[1]);
                        }

                        table.update(assignments, parsePredicate(matches.get(2)));
                        modified = true;
                        break;
                    }
                    case select_rows: {
                        Table table = tables.get(matches.get(1));
                        String predicateExpr = matches.size() > 2 ? matches.get(2) : null;
                        Collection<Column> columns = matches.get(0).equals("*") ? table.getColumns() : toColumns(parseValues(matches.get(0)), table);

                        result.setRows(table.select(columns, parsePredicate(predicateExpr)));
                        break;
                    }
                    case order_rows: {
                        Table table = tables.get(matches.get(1));
                        String column = matches.get(2);
                        Collection<Column> columns = matches.get(0).equals("*") ? table.getColumns() : toColumns(parseValues(matches.get(0)), table);
                        result.setRows(table.select(columns, row -> true).stream().sorted(Comparator.comparing(a -> a.getElement(column).getAsString())).collect(Collectors.toList()));
                        break;
                    }
                    case create: {
                        String[] columnProps = matches.get(1).split(",");

                        ArrayList<Column> columns = new ArrayList<>();
                        for (String prop : columnProps) {
                            String[] tokens = prop.trim().split("\\s+");

                            Column column = new Column(Column.Type.valueOf(tokens[0]), tokens[1]);
                            //TODO
                            columns.add(column);
                        }
                        createTable(matches.get(0), columns);
                        modified = true;
                        break;
                    }
                    case drop: {
                        dropTable(matches.get(0));
                        modified = true;
                        break;
                    }
                    case list: {
                        result.setRows(getTableNames().stream().map(tableName -> new Row(Collections.singletonList(new Element(tableName, "TABLES")))).collect(Collectors.toCollection(ArrayList::new)));
                        break;
                    }
                }
                if (modified) save();
                return result;
            }
        } catch (Exception e) {
            return new Operation(Operation.Status.FAIL).setReport(e.getMessage());
        }

        return new Operation(Operation.Status.FAIL).setReport("syntax");
    }

    private Predicate <Row> parsePredicate(String expr) throws Exception
    {
        Predicate<Row> result = row -> true;
        if (expr == null) return result;
        for (String equalExpr : parseValues(expr))
        {
            result = result.and(toEqualPredicate(parseEqual(equalExpr)));
        }
        return result;
    }

    private String[] parseEqual(String expr) throws Exception
    {
        String[] pair = expr.split("=");
        if (pair.length != 2) throw new Exception("syntax");
        return pair;
    }

    private Predicate<Row> toEqualPredicate(String[] pair)
    {
        return row -> {
            Element element = row.getElement(pair[0]);
            if (element == null) return false;
            return element.equals(pair[1]);
        };
    }

    private ArrayList<String> parseValues(String expr) throws Exception {
        return Arrays.stream(expr.split(",")).map(String::trim).collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<Column> toColumns(Collection<String> names, Table table) throws Exception {
        ArrayList<Column> result = new ArrayList<>();
        for (String colName : names) result.add(table.getColumn(colName));
        return result;
    }

    private void save() throws IOException {
        if (filePath == null) return;

        FileWriter writer = new FileWriter(filePath);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(this, writer);
        writer.close();
    }
}
