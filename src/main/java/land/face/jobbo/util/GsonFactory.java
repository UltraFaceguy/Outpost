package land.face.jobbo.util;

import com.tealcube.minecraft.bukkit.shade.google.common.reflect.TypeToken;
import com.tealcube.minecraft.bukkit.shade.google.gson.ExclusionStrategy;
import com.tealcube.minecraft.bukkit.shade.google.gson.FieldAttributes;
import com.tealcube.minecraft.bukkit.shade.google.gson.Gson;
import com.tealcube.minecraft.bukkit.shade.google.gson.GsonBuilder;
import com.tealcube.minecraft.bukkit.shade.google.gson.TypeAdapter;
import com.tealcube.minecraft.bukkit.shade.google.gson.annotations.Expose;
import com.tealcube.minecraft.bukkit.shade.google.gson.stream.JsonReader;
import com.tealcube.minecraft.bukkit.shade.google.gson.stream.JsonToken;
import com.tealcube.minecraft.bukkit.shade.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class GsonFactory {

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD})
  public static @interface Ignore {}

  private static Gson g = new Gson();

  private final static String CLASS_KEY = "SERIAL-ADAPTER-CLASS-KEY";

  private static Gson prettyGson;
  private static Gson compactGson;

  /**
   * Returns a Gson instance for use anywhere with new line pretty printing
   * <p>
   *    Use @GsonIgnore in order to skip serialization and deserialization
   * </p>
   * @return a Gson instance
   */
  public static Gson getPrettyGson () {
    if (prettyGson == null)
      prettyGson = new GsonBuilder().addSerializationExclusionStrategy(new ExposeExclusion())
          .addDeserializationExclusionStrategy(new ExposeExclusion())
          .registerTypeAdapter(Location.class, new LocationGsonAdapter())
          .setPrettyPrinting()
          .disableHtmlEscaping()
          .create();
    return prettyGson;
  }

  /**
   * Returns a Gson instance for use anywhere with one line strings
   * <p>
   *    Use @GsonIgnore in order to skip serialization and deserialization
   * </p>
   * @return a Gson instance
   */
  public static Gson getCompactGson () {
    if(compactGson == null)
      compactGson = new GsonBuilder().addSerializationExclusionStrategy(new ExposeExclusion())
          .addDeserializationExclusionStrategy(new ExposeExclusion())
          .registerTypeAdapter(Location.class, new LocationGsonAdapter())
          .disableHtmlEscaping()
          .create();
    return compactGson;
  }

  /**
   * Creates a new instance of Gson for use anywhere
   * <p>
   *    Use @GsonIgnore in order to skip serialization and deserialization
   * </p>
   * @return a Gson instance
   */
  public static Gson getNewGson(boolean prettyPrinting) {
    GsonBuilder builder = new GsonBuilder().addSerializationExclusionStrategy(new ExposeExclusion())
        .addDeserializationExclusionStrategy(new ExposeExclusion())
        .registerTypeAdapter(Location.class, new LocationGsonAdapter())
        .disableHtmlEscaping();
    if (prettyPrinting)
      builder.setPrettyPrinting();
    return builder.create();
  }

  private static class LocationGsonAdapter extends TypeAdapter<Location> {

    private static Type seriType = new TypeToken<Map<String, Object>>(){}.getType();

    private static String UUID = "uuid";
    private static String X = "x";
    private static String Y = "y";
    private static String Z = "z";
    private static String YAW = "yaw";
    private static String PITCH = "pitch";

    @Override
    public void write(JsonWriter jsonWriter, Location location) throws IOException {
      if(location == null) {
        jsonWriter.nullValue();
        return;
      }
      jsonWriter.value(getRaw(location));
    }

    @Override
    public Location read(JsonReader jsonReader) throws IOException {
      if(jsonReader.peek() == JsonToken.NULL) {
        jsonReader.nextNull();
        return null;
      }
      return fromRaw(jsonReader.nextString());
    }

    private String getRaw (Location location) {
      Map<String, Object> serial = new HashMap<String, Object>();
      serial.put(UUID, location.getWorld().getUID().toString());
      serial.put(X, Double.toString(location.getX()));
      serial.put(Y, Double.toString(location.getY()));
      serial.put(Z, Double.toString(location.getZ()));
      serial.put(YAW, Float.toString(location.getYaw()));
      serial.put(PITCH, Float.toString(location.getPitch()));
      return g.toJson(serial);
    }

    private Location fromRaw (String raw) {
      Map<String, Object> keys = g.fromJson(raw, seriType);
      World w = Bukkit.getWorld(java.util.UUID.fromString((String) keys.get(UUID)));
      return new Location(w, Double.parseDouble((String) keys.get(X)), Double.parseDouble((String) keys.get(Y)), Double.parseDouble((String) keys.get(Z)),
          Float.parseFloat((String) keys.get(YAW)), Float.parseFloat((String) keys.get(PITCH)));
    }
  }

  private static class ExposeExclusion implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
      final Ignore ignore = fieldAttributes.getAnnotation(Ignore.class);
      if(ignore != null)
        return true;
      final Expose expose = fieldAttributes.getAnnotation(Expose.class);
      return expose != null && (!expose.serialize() || !expose.deserialize());
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
      return false;
    }
  }
}
