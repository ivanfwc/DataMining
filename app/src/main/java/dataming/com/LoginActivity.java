package dataming.com;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private LoginButton mFacebookSignInButton;
    private CallbackManager mFacebookCallbackManager;
    //a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 9001;
    //Tag for the logs optional
    private static final String TAG = "DataMining";
    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;
    //And also a Firebase Auth object
    private FirebaseAuth mAuth;
    SignInButton signInButton;
    private ProfileTracker mProfileTracker;
    private AccessToken mAccessToken;
    private static final String EMAIL = "email";
    Button email_sign_in_button;

    Dialog errorDialog;

    String Name, Email, UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAccessToken = AccessToken.getCurrentAccessToken();

        //facebook
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookSignInButton = findViewById(R.id.login_button);
        mFacebookSignInButton.setReadPermissions(Collections.singletonList(EMAIL));
        mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        //TODO: Use the Profile class to get information about the current user.

                        handleFacebookAccessToken(loginResult.getAccessToken());

                        /*String userLoginId = loginResult.getAccessToken().getUserId();
                        Intent facebookIntent = new Intent(LoginActivity.this, MainActivity.class);
                        Profile mProfile = Profile.getCurrentProfile();
                        String firstName = mProfile.getFirstName();
                        String lastName = mProfile.getLastName();
                        String userId = mProfile.getId().toString();
                        String profileImageUrl = mProfile.getProfilePictureUri(96, 96).toString();

                        facebookIntent.putExtra(PROFILE_USER_ID, userId);
                        facebookIntent.putExtra(PROFILE_FIRST_NAME, firstName);
                        facebookIntent.putExtra(PROFILE_LAST_NAME, lastName);
                        facebookIntent.putExtra(PROFILE_IMAGE_URL, profileImageUrl);
                        startActivity(facebookIntent);
                        finish();*/

                        /*if(Profile.getCurrentProfile() == null) {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                    Log.v("facebook - profile", currentProfile.getFirstName());
                                    mProfileTracker.stopTracking();
                                }
                            };
                            // no need to call startTracking() on mProfileTracker
                            // because it is called by its constructor, internally.
                        }
                        else {
                            Profile profile = Profile.getCurrentProfile();
                            Log.v("facebook - profile", profile.getFirstName());
                        }

                        handleSignInResult(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                LoginManager.getInstance().logOut();
                                return null;
                            }
                        });*/
                    }

                    @Override
                    public void onCancel() {
                        //handleSignInResult(null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(MainActivity.class.getCanonicalName(), error.getMessage());
                        //handleSignInResult(null);
                    }
                }
        );

        checkPlayServices();

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Now we will attach a click listener to the sign_in_button
        //and inside onClick() method we are calling the signIn() method that will open
        //google sign in intent
        signInButton = findViewById(R.id.sign_in_button);

        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        //findViewById(R.id.email_sign_in_button).setOnClickListener(this);

    }

    /*private void checkInitialized() {
    }

    private void handleSignInResult(Object o) {
        //Handle sign result here
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }*/

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();

                            ResponseCreator();

                            /*// Facebook Email address
                            GraphRequest request = GraphRequest.newMeRequest(
                                    mAccessToken,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            Log.v("LoginActivity Response ", response.toString());

                                            try {
                                                String Name = object.getString("name");
                                                String FEmail = object.getString("email");
                                                Log.v("Email = ", " " + FEmail);
                                                Toast.makeText(getApplicationContext(), "Name " + Name, Toast.LENGTH_LONG).show();


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "name,email");
                            request.setParameters(parameters);
                            request.executeAsync();*/

                            GraphRequest request = GraphRequest.newMeRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            // Application code

                                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();

                                                /*String Email = object.getString("email");
                                                String Name = object.getString("name");*/

                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "email,name");
                            request.setParameters(parameters);
                            request.executeAsync();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), R.string.login_activity_failed, Toast.LENGTH_SHORT).show();
                //Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                //updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        showProgressDialog2();

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ResponseCreator();
                            //Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            //Toast.makeText(LoginActivity.this, user.getDisplayName() + " " + getString(R.string.signed_in), Toast.LENGTH_SHORT).show();

                            //sendRequest();

                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.login_activity_authentication_failed, Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        hideProgressDialog();
                        // ...
                    }
                });
    }

    private void ResponseCreator() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        SetFirstTimeRequest confirmMatric = new SetFirstTimeRequest(Name, Email, UserId, responseListener);
        //Toast.makeText(LoginActivity.this, user.getEmail().toString().trim(), Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(confirmMatric);
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {

                if (errorDialog == null) {
                    errorDialog = googleApiAvailability.getErrorDialog(this, resultCode, 2404);
                    errorDialog.setCancelable(false);
                }

                if (!errorDialog.isShowing())
                    errorDialog.show();

            }
        }

        return resultCode == ConnectionResult.SUCCESS;
    }

    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onPause() {
        super.onPause();

        if(mProgressDialog2 != null) {
            mProgressDialog2.dismiss();
            mProgressDialog2 = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mProgressDialog2 != null) {
            mProgressDialog2.dismiss();
            mProgressDialog2 = null;
        }
    }

    @Override
    public void onBackPressed() {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.login_activity_closing_application))
                .setContentText(getString(R.string.login_activity_confirmation))
                .setCancelText(getString(R.string.button_no))
                .setConfirmText(getString(R.string.button_yes))
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // reuse previous dialog instance, keep widget user state, reset them if you need
                        sDialog.cancel();

                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        sDialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.sign_in_button) {

            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = null;
            if (cm != null) {
                activeNetwork = cm.getActiveNetworkInfo();
            }
            //if (activeNetwork != null && activeNetwork.isConnectedOrConnecting() && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) { // connected to the internet
            if (activeNetwork != null && activeNetwork.isConnected()) { // connected to the internet

                signIn();

            } else {

                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.something_went_wrong))
                        .setContentText(getString(R.string.no_internet_connection))
                        .setCancelText(getString(R.string.exit_app))
                        .setConfirmText(getString(R.string.open_settings))
                        .setNeutralText(getString(R.string.try_again_dialog))
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                LoginActivity.this.finish();
                                sDialog.dismiss();

                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                sDialog.dismiss();
                            }
                        })
                        .setNeutralClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                ConnectivityManager cm = (ConnectivityManager) LoginActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                                NetworkInfo activeNetwork = null;
                                if (cm != null) {
                                    activeNetwork = cm.getActiveNetworkInfo();
                                }
                                //if (activeNetwork != null && activeNetwork.isConnectedOrConnecting() && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) { // connected to the internet
                                if (activeNetwork != null && activeNetwork.isConnected()) { // connected to the internet
                                    mAuth = FirebaseAuth.getInstance();
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    getApplicationContext();
                                    sDialog.dismiss();

                                } else {
                                    mAuth = FirebaseAuth.getInstance();
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    getApplicationContext();
                                    sDialog.dismiss();
                                }
                            }
                        })
                        .show();
            }
        } else if (i == R.id.email_sign_in_button) {
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        }
    }
}
