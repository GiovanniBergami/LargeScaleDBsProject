package londonSafeTravel.client.net;

import org.bson.Document;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

public class StatTableRequest {
    Collection<Document> results;

    public StatTableRequest(String hostname, double latTopLeft, double longTopLeft, double latBottomRight, double longBottomRight) throws Exception{
        assert (latTopLeft > latBottomRight);
        assert (longBottomRight > longBottomRight);

        HttpURLConnection con = (HttpURLConnection) new URL(
                "http://"+ hostname+ "/queryTable.json?latTopLeft="+ latTopLeft +"&longTopLeft="+longTopLeft+"&latBottomRight="+latBottomRight+"&longBottomRight="+longBottomRight
        ).openConnection();

        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestMethod("GET");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        con.connect();

        int status = con.getResponseCode();
        if(status != 200)
            throw new Exception("errore " + status);

        String rawJson = new String(con.getInputStream().readAllBytes());
        var doc = Document.parse("{ schifo: " + rawJson + "}");
        results = doc.getList("schifo", Document.class);
    }

    public Collection<Document> getResults() {
        return results;
    }
}
