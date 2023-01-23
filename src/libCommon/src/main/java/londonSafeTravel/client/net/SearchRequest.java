package londonSafeTravel.client.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import londonSafeTravel.schema.GeoFactory;
import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.document.poi.PointOfInterest;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchRequest {
    private final List<PointOfInterest> pois;

    public SearchRequest(String hostname, String namePoint) throws Exception{

        namePoint = namePoint.replace(" ", "%20");
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
        Type collectionType = new TypeToken<ArrayList<PointOfInterest>>() {
        }.getType();

        pois = new Gson().fromJson(in, collectionType);
    }

    public Location getCoord(){
        return GeoFactory.fromMongo(pois.get(0).coordinates);
    }

    public List<PointOfInterest> getList(){
        return pois;
    }
}
