package anchovy.team.epialarm;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;

public class DeleteEnvLoader {
    private static JSONObject env;

    public static void init(Context context) {
        try {
            InputStream is = context.getAssets().open("env");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            env = new JSONObject(jsonStr);
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }
    }

    public static String get(String key) {
        if (env == null) {
            return null;
        }
        return env.optString(key, null);
    }
}