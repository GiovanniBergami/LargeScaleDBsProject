package londonSafeTravel.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import londonSafeTravel.schema.document.HeatmapComputation;
import londonSafeTravel.schema.graph.Disruption;
import londonSafeTravel.schema.graph.Point;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HeatmapRequest {

    private final List<HeatmapComputation> heatmap;
    public HeatmapRequest(String hostname, String disruptionClass, long lenLat, long lenLon) throws Exception {
        disruptionClass=disruptionClass.replace(" ", "%20");
        HttpURLConnection con = (HttpURLConnection) new URL(
                "http://" + hostname + "/heatmap.json?class=" + disruptionClass +"&lenLat=" + lenLat
                        + "&lenLon=" + lenLon
        ).openConnection();

        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestMethod("GET");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        con.connect();

        int status = con.getResponseCode();
        if (status != 200)
            throw new Exception("errore " + status);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        Type collectionType = new TypeToken<ArrayList<HeatmapComputation>>() {
        }.getType();
        heatmap = new Gson().fromJson(in, collectionType);
    }

    public List<HeatmapComputation> heatmap() {
        return heatmap;
    }
}
