package for_database;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;

public class Reader
{
    private String filePath;

    public Reader(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public TablesHashMap read() throws Exception {
        FileReader reader = new FileReader(filePath);
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(reader, TablesHashMap.class);
    }
}
