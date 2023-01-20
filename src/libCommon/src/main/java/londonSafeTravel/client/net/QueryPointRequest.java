package londonSafeTravel.client.net;

import londonSafeTravel.gsonUtils.GsonFactory;
import londonSafeTravel.schema.graph.Point;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QueryPointRequest {
    public Point getPoint() {
        return point;
    }

    private Point point;

    public QueryPointRequest(String hostname, double latitude, double longitude, String type) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(
                "http://" + hostname + "/query.json?latitude=" + latitude + "&longitude=" + longitude +"&type="+type
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

        point = GsonFactory.build().fromJson(in, Point.class);
    }

    GeoPosition getPosition(){
        return new GeoPosition(
                point.location.getLatitude(),
                point.location.getLongitude()
        );
    }
}
