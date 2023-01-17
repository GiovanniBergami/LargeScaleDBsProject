package londonSafeTravel.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import londonSafeTravel.schema.document.poi.PointOfInterest;
import londonSafeTravel.schema.graph.Disruption;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class POIRequest {

    ArrayList<PointOfInterest> pois;
    public POIRequest(String hostname, double latTopLeft, double longTopLeft, double latBottomRight, double longBottomRight) throws Exception{

        HttpURLConnection con = (HttpURLConnection) new URL(
         "http://"+ hostname+ "/queryPOI.json?latTopLeft="+ latTopLeft +"&longTopLeft="+longTopLeft+"&latBottomRight="+latBottomRight+"&longBottomRight="+longBottomRight
        ).openConnection();

        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestMethod("GET");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        con.connect();

        int status = con.getResponseCode();
        if(status != 200)
            throw new Exception("errore " + status);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuffer content = new StringBuffer();
        Type collectionType = new TypeToken<ArrayList<PointOfInterest>>() {
        }.getType();

        pois = new Gson().fromJson(in, collectionType);
    }

    public ArrayList<PointOfInterest> getPOIs() {
        return pois;
    }
}
