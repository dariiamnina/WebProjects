package for_database;

import java.io.Serializable;
import java.util.Collection;

public class Operation implements Serializable
{
    public enum Status
    {
        OK,
        FAIL
    }

    private Status status;
    private String report;
    private Collection<Row> rows;

    Operation(Status status)
    {
        this.status = status;
        report = "";
        rows = null;
    }

    public Status getStatus()
    {
        return status;
    }

    public String getReport()
    {
        return report;
    }

    Operation setReport(String report)
    {
        this.report = report;
        return this;
    }

    public Collection<Row> getRows()
    {
        return rows;
    }

    Operation setRows(Collection<Row> rows)
    {
        this.rows = rows;
        return this;
    }

}