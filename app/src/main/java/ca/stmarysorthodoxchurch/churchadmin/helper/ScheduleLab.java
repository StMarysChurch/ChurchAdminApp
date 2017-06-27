package ca.stmarysorthodoxchurch.churchadmin.helper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ca.stmarysorthodoxchurch.churchadmin.models.Schedule;

/**
 * Created by roneythomas on 2016-10-11.
 */

public class ScheduleLab {
    private static FirebaseDatabase mDatabase;
    private static ArrayList<Schedule> schedule = new ArrayList<>();
    private static ArrayList<String> keys = new ArrayList<>();

    public static DatabaseReference getDatabase(String ref) {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        DatabaseReference mReference = mDatabase.getReference(ref);
        mReference.keepSynced(true);
        return mReference;
    }

    public static ArrayList<Schedule> getSchedule() {
        return schedule;
    }

    public static void setSchedule(Schedule mSchedule) {
        schedule.add(mSchedule);
    }

    public static ArrayList<String> getKeys() {
        return keys;
    }

    public static void setKeys(String mKeys) {
        keys.add(mKeys);
    }
}
