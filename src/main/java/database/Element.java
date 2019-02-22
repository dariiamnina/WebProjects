package for_database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mentaregex.Regex;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Objects;

public class Element implements Serializable
{
    private String value;
    private String column;

    Element(String value, String column)
    {
        this.value = value;
        this.column = column;
    }

    @JsonIgnore
    public Integer getAsInteger() {
        return Integer.parseInt(value);
    }

    @JsonIgnore
    public Float getAsFloat() {
        return Float.parseFloat(value);
    }

    @JsonIgnore
    public char getAsCharacter() throws Exception
    {
        if (value.length() != 1) throw new Exception("Invalid character value");
        return value.charAt(0);
    }

    @JsonIgnore
    public String getAsString() {
        return value;
    }

    @JsonIgnore
    public LocalDate getAsDate() {
        return LocalDate.parse(value);
    }

    @JsonIgnore
    public LocalDate[] getAsDateRange() throws Exception {
        String[] match = Regex.match(value, "([^\\s]+)\\-([^\\s]+)");
        if (match == null) throw new Exception("Invalid date range value");

        LocalDate start = LocalDate.parse(match[0]);
        LocalDate end = LocalDate.parse(match[1]);

        if (start.isAfter(end)) throw new Exception("The two time stamps must be in non-decreasing order to form a range");

        return new LocalDate[]{ start, end };
    }
    public String getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }

    void validate(Table table) throws Exception {
        Column column = table.getColumn(this.column);

        if (value == null)
        {
            if (column.isNullAllowed()) return;
            throw new Exception("Null value is not allowed");
        }

        try
        {
            switch (column.getType()) {
                case INT: getAsInteger(); break;
                case FLOAT: getAsFloat(); break;
                case CHAR: getAsCharacter(); break;
                case STR: getAsString(); break;
                case DATE: getAsDate(); break;
                case DATE_RANGE: getAsDateRange(); break;
            }
        }
        catch (Exception e)
        {
            throw new Exception(String.format("Invalid element value '%s': %s", value, e.getMessage()));
        }
    }

    public boolean equals(String other)
    {
        if (other == null) return value == null;
        if (value == null) return other.equals("null");
        return value.equals(other);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element element = (Element) o;
        return Objects.equals(value, element.value) &&
                Objects.equals(column, element.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, column);
    }

}
