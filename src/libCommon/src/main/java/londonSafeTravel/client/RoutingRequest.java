package londonSafeTravel.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import londonSafeTravel.driver.tims.RoadDisruptionUpdate;
import londonSafeTravel.schema.graph.Point;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoutingRequest {
    List<Point> route;

    public RoutingRequest(String hostname, long start, long end, String type) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(
                "http://" + hostname + "/route.json?start=" + start + "&end=" + end + "&type=" + type
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

        Type collectionType = new TypeToken<ArrayList<Point>>() {
        }.getType();

        route = new Gson().fromJson(in, collectionType);
    }

    public List<Point> getRoute() {
        return route;
    }

    public List<GeoPosition> getRouteGeo() {
        return route.stream()
                .map(point -> new GeoPosition(
                        point.location.getLatitude(), point.location.getLongitude())
                )
                .collect(Collectors.toList());
    }

}
