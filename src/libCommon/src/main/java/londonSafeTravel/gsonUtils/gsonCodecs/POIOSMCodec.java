package londonSafeTravel.gsonUtils.gsonCodecs;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import londonSafeTravel.schema.document.poi.PointOfInterestOSM;

import java.lang.reflect.Type;

public class POIOSMCodec implements JsonSerializer<PointOfInterestOSM> {
    @Override
    public JsonElement serialize(PointOfInterestOSM src, Type typeOfSrc, JsonSerializationContext context) {
        return new POICodec().serialize(src, typeOfSrc, context);
    }
}
