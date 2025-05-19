package anchovy.team.epialarm.utils;


import android.widget.NumberPicker;

public class NumberPickerHelper {

    public static void configureHourPicker(NumberPicker picker) {
        picker.setMinValue(0);
        picker.setMaxValue(23);
        picker.setWrapSelectorWheel(true);
        picker.setFormatter(i -> String.format("%02d", i));
    }

    public static void configureMinutePicker(NumberPicker picker) {
        picker.setMinValue(0);
        picker.setMaxValue(59);
        picker.setWrapSelectorWheel(true);
        picker.setFormatter(i -> String.format("%02d", i));
    }
}
