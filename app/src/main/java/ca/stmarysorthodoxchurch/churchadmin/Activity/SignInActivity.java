package ca.stmarysorthodoxchurch.churchadmin.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import ca.stmarysorthodoxchurch.churchadmin.R;
import ca.stmarysorthodoxchurch.churchadmin.databinding.ActivitySigninBinding;
import ca.stmarysorthodoxchurch.churchadmin.helper.ScheduleLab;

/**
 * Created by roneythomas on 2016-10-03.
 */

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SignInActivity";
    private static int RC_SIGN_IN = 2420;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences sharedPref;

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
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
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            binding.signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
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
                sharedPref.edit().putBoolean(getString(R.string.preference_auth_status), true).commit();
                Log.d(TAG, dataSnapshot.toString());
                startActivity(new Intent(getApplicationContext(), ScheduleActivity.class));
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                sharedPref.edit().putBoolean(getString(R.string.preference_auth_status), false).commit();
                Toast.makeText(SignInActivity.this, "Not Authorized",
                        Toast.LENGTH_SHORT).show();
                Map<String, String> user = new HashMap<>();
                user.put("email", mAuth.getCurrentUser().getEmail());
                user.put("name", mAuth.getCurrentUser().getDisplayName());
                user.put("profile_picture", String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
                Log.d(TAG, "onCancelled: " + user.toString() + "\n" + mAuth.getCurrentUser().getUid());
                ScheduleLab.getDatabase("/queue").child(mAuth.getCurrentUser().getUid()).setValue(user);
                signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "Google Sign Out Status: " + status);
                    }
                });
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed" + connectionResult.toString());
    }
}
