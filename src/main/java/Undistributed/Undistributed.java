package Undistributed;

import for_database.*;

import java.util.Scanner;

public class Undistributed {

    private static TablesHashMap database;
    public static void main(String[] args)
    {
        String databasePath = "test.json";
        try
        {
            database = new Reader(databasePath).read();
        }
        catch (Exception e)
        {
            database = new TablesHashMap(databasePath);
            e.printStackTrace();
        }
        while (true)
        {
            System.out.print("$");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            Operation result = database.query(command);
            processResult(result);
        }
    }

    public static void processResult(Operation result)
    {
        if (result.getStatus() == Operation.Status.FAIL)
        {
            System.out.println("FAIL");
            System.out.println(result.getReport());
            return;
        }

        if (result.getRows() == null)
        {
            System.out.println("OK");
            return;
        }

        if (result.getRows().isEmpty())
        {
            System.out.println("Nothing was found");
            return;
        }

        for (Element element : result.getRows().iterator().next().getElements())
        {
            System.out.print(String.format("%15s", element.getColumn()));
        }
        System.out.println();
        System.out.println();

        for (Row row : result.getRows())
        {
            for (Element element : row.getElements())
            {
                System.out.print(String.format("%15s", element.getAsString()));
            }
            System.out.println();
        }
    }
}
