package londonSafeTravel.client;

import com.google.gson.Gson;
import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.graph.Point;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchRequest {
    private Location poi;

    public SearchRequest(String hostname, String namePoint) throws Exception{

        HttpURLConnection con = (HttpURLConnection) new URL(
                "http://"+ hostname+ "/querySearchPOI.json?name="+ namePoint
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
        StringBuffer content = new StringBuffer();

        poi = new Gson().fromJson(in, Location.class);
    }

    public Location getCoord(){
        return poi;
    }
}
