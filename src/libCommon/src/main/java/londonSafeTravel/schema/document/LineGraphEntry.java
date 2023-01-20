package londonSafeTravel.schema.document;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class LineGraphEntry {
    @BsonProperty("_id")
    public long hour;

    @BsonProperty
    public double count;
}
