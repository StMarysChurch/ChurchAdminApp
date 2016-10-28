package ca.stmarysorthodoxchurch.churchadmin.helper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by roneythomas on 2016-10-11.
 */

public class ScheduleLab {
    private static FirebaseDatabase mDatabase;

    public static DatabaseReference getDatabase(String ref) {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        DatabaseReference mReference = mDatabase.getReference(ref);
        mReference.keepSynced(true);
        return mReference;
    }
}
