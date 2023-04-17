import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.util.List;

public final class JsonUtils {
    private JsonUtils() {
    }

    /**
     * Serializes the {@link ViewPoint} list to JSON.
     *
     * @param viewPoints ViewPoint list to serialize
     *
     * @return The serialized JSON String or an empty String if the ViewPoints list was null
     */
    public static String serialize(List<ViewPoint> viewPoints) {
        if (viewPoints == null) {
            return "";
        }

        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true))) {
            return jsonb.toJson(viewPoints);
        }
        catch (Exception e) {
            System.err.println(
                "An exception occurred during the execution of the program.\n "
                    + "Error message:\n"
                    + e.getMessage());
        }

        return null;
    }
}
