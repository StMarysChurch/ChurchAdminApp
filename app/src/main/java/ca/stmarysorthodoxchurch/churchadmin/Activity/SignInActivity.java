package ca.stmarysorthodoxchurch.churchadmin.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.FragmentActivity;
import ca.stmarysorthodoxchurch.churchadmin.R;
import ca.stmarysorthodoxchurch.churchadmin.databinding.ActivitySigninBinding;
import ca.stmarysorthodoxchurch.churchadmin.helper.ScheduleLab;

/**
 * Created by roneythomas on 2016-10-03.
 */

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private static int RC_SIGN_IN = 2420;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;
    private static GoogleSignInClient mGoogleSignInClient;

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut();
        mGoogleSignInClient.revokeAccess();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getApplication().getSharedPreferences(getString(R.string.preference_auth_status), Context.MODE_PRIVATE);
        boolean authStatus = sharedPref.getBoolean(getString(R.string.preference_auth_status), false);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null && authStatus) {
            // already signed in
            startActivity(new Intent(this, ScheduleActivity.class));
        } else {
            // not signed in
            ActivitySigninBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_signin);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            binding.signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult" + " Inside onActivityResult");
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                Log.d(TAG, "onActivityResult: Yes this is workiing");
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            authorization();
                        }
                    }
                });
    }

    public void authorization() {
        ScheduleLab.getDatabase("/scheduleUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sharedPref.edit().putBoolean(getString(R.string.preference_auth_status), true).apply();
                Log.d(TAG, dataSnapshot.toString());
                startActivity(new Intent(getApplicationContext(), ScheduleActivity.class));
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                sharedPref.edit().putBoolean(getString(R.string.preference_auth_status), false).apply();
                Toast.makeText(SignInActivity.this, "Not Authorized",
                        Toast.LENGTH_SHORT).show();
                Map<String, String> user = new HashMap<>();
                user.put("email", mAuth.getCurrentUser().getEmail());
                user.put("name", mAuth.getCurrentUser().getDisplayName());
                user.put("profile_picture", String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
                Log.d(TAG, "onCancelled: " + user.toString() + "\n" + mAuth.getCurrentUser().getUid());
                ScheduleLab.getDatabase("/queue").child(mAuth.getCurrentUser().getUid()).setValue(user);
                signOut();
            }
        });
    }
}
