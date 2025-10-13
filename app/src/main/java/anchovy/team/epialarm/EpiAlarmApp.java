package anchovy.team.epialarm;

import android.app.Application;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import java.lang.reflect.Field;

public class EpiAlarmApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Typeface geist = ResourcesCompat.getFont(this, R.font.geist);
        if (geist != null) {
            overrideDefaultTypeface("DEFAULT", geist);
            overrideDefaultTypeface("SANS_SERIF", geist);
            overrideDefaultTypeface("SERIF", geist);
            overrideDefaultTypeface("MONOSPACE", geist);
        }
    }

    private void overrideDefaultTypeface(String staticTypefaceFieldName, Typeface newTypeface) {
        try {
            Field defaultField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            defaultField.setAccessible(true);
            defaultField.set(null, newTypeface);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // Ignore font override failures
        }
    }
}


