package springmvc;

import for_database.TablesHashMap;
import for_database.Reader;
import for_database.Operation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DatabaseController {
    private TablesHashMap database;

    public DatabaseController() {
        try {
            database = new Reader("test.json").read();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @GetMapping("/")
    public String queryForm(@RequestParam(name="name", required=false, defaultValue="guest") String name,
                            Model model) {
        model.addAttribute("name", name);
        return "database";
    }

    @PostMapping(value = "/database", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public Operation query(@RequestParam(name="query", required=true) String query)
    {
        System.out.println(query);
        return database.query(query);
    }
}