package londonSafeTravel.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import londonSafeTravel.schema.graph.Disruption;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DisruptionsRequest {
    private ArrayList<Disruption> disruptions;

    public DisruptionsRequest(String hostname) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(
                "http://" + hostname + "/disruptions.json").openConnection();

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
        Type collectionType = new TypeToken<ArrayList<Disruption>>() {
        }.getType();

        disruptions = new Gson().fromJson(in, collectionType);
    }

    public ArrayList<Disruption> getDisruptions() {
        return disruptions;
    }

}
