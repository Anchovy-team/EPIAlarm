package anchovy.team.epialarm;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private static final String PREF_NAME = "epi_alarm_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TYPE = "chosen_type";
    private static final String KEY_GROUP_ID = "group_id";
    private static final String KEY_GROUP_NAME = "group_name";
    private static final String KEY_TEACHER_ID = "teacher_id";
    private static final String KEY_TEACHER_NAME = "teacher_name";
    private static final String KEY_ADV_ALARM = "advance_alarm";
    private static final String KEY_ADV_REMINDER = "advance_reminder";

    private static UserSession instance;
    private final SharedPreferences prefs;

    private String token;
    private String chosenType;
    private long groupId;
    private long teacherId;
    private String groupName;
    private String teacherName;
    private int advanceMinutesAlarm;
    private int advanceMinutesReminder;

    private UserSession(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadFromPrefs();
    }

    public static synchronized UserSession getInstance(Context context) {
        if (instance == null) {
            instance = new UserSession(context.getApplicationContext());
        }
        return instance;
    }

    private void loadFromPrefs() {
        token = prefs.getString(KEY_TOKEN, null);
        chosenType = prefs.getString(KEY_TYPE, null);
        groupId = prefs.getLong(KEY_GROUP_ID, -1);
        groupName = prefs.getString(KEY_GROUP_NAME, null);
        teacherId = prefs.getLong(KEY_TEACHER_ID, -1);
        teacherName = prefs.getString(KEY_TEACHER_NAME, null);
        advanceMinutesAlarm = prefs.getInt(KEY_ADV_ALARM, 60);
        advanceMinutesReminder = prefs.getInt(KEY_ADV_REMINDER, 10);
    }

    private void save(String key, Object value) {
        SharedPreferences.Editor e = prefs.edit();
        if (value instanceof String) {
            e.putString(key, (String) value);
        } else if (value instanceof Long) {
            e.putLong(key, (Long) value);
        } else if (value instanceof Integer) {
            e.putInt(key, (Integer) value);
        }
        e.apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
        token = null;
        chosenType = null;
        groupId = -1;
        groupName = null;
        teacherId = -1;
        teacherName = null;
        advanceMinutesAlarm = -1;
        advanceMinutesReminder = -1;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        save(KEY_TOKEN, token);
    }

    public String getChosenType() {
        return chosenType;
    }

    public void setChosenType(String chosenType) {
        this.chosenType = chosenType;
        save(KEY_TYPE, chosenType);
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
        save(KEY_GROUP_ID, groupId);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
        save(KEY_GROUP_NAME, groupName);
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
        save(KEY_TEACHER_ID, teacherId);
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
        save(KEY_TEACHER_NAME, teacherName);
    }

    public int getAdvanceMinutesAlarm() {
        return advanceMinutesAlarm;

    }

    public void setAdvanceMinutesAlarm(int advanceMinutesAlarm) {
        this.advanceMinutesAlarm = advanceMinutesAlarm;
        save(KEY_ADV_ALARM, advanceMinutesAlarm);
    }

    public int getAdvanceMinutesReminder() {
        return advanceMinutesReminder;
    }

    public void setAdvanceMinutesReminder(int advanceMinutesReminder) {
        this.advanceMinutesReminder = advanceMinutesReminder;
        save(KEY_ADV_REMINDER, advanceMinutesReminder);
    }
}