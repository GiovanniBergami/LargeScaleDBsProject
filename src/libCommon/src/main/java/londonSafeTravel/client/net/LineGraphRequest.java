package londonSafeTravel.client.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import londonSafeTravel.schema.document.LineGraphEntry;
import londonSafeTravel.schema.document.poi.PointOfInterest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LineGraphRequest {
    private final List<LineGraphEntry> entries;

    public LineGraphRequest(String hostname, String category) throws Exception{
        category = category == null ? null : category.replace(" ", "%20");

        HttpURLConnection con = (HttpURLConnection) new URL(
                "http://"+ hostname+ "/lineGraph.json" +
                        (category == null ? "" : ("?category=" + category))
        ).openConnection();

        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestMethod("GET");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        con.connect();

        int status = con.getResponseCode();
        if(status != 200)
            throw new Exception("errore " + status);

        // continuare dopo
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        Type collectionType = new TypeToken<ArrayList<LineGraphEntry>>() {
        }.getType();

        entries = new Gson().fromJson(in, collectionType);
    }

    public List<LineGraphEntry> getEntries() {
        return entries;
    }
}
