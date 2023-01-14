package londonSafeTravel.schema.document;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class HeatmapComputation {
    @BsonProperty
    public double latitude;
    @BsonProperty
    public double longitude;
    @BsonProperty
    public long count;
}
