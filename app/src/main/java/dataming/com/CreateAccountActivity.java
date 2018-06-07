package dataming.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CreateAccountActivity extends BaseActivity implements View.OnClickListener {

    TextView field_fullname, field_email, field_password;
    Button create_account_button, cancel_create_account_button;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.toolbar_title)).setText("New Account");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        field_fullname = findViewById(R.id.field_fullname);
        field_email = findViewById(R.id.field_email);
        field_password = findViewById(R.id.field_password);

        findViewById(R.id.create_account_button).setOnClickListener(this);
        findViewById(R.id.cancel_create_account_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

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

    private void createAccount(String email, String password) {

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            userProfile();
                            Toast.makeText(CreateAccountActivity.this, "Account created successfully",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            //userProfile();
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void userProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(field_fullname.getText().toString().trim())
                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg")) // here you can set image link also.
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TESTING", "User profile updated.");
                            }
                        }
                    });
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = field_fullname.getText().toString();
        if (TextUtils.isEmpty(name)) {
            field_fullname.setError("Required.");
            valid = false;
        } else {
            field_fullname.setError(null);
        }

        String email = field_email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            field_email.setError("Required.");
            valid = false;
        } else {
            field_email.setError(null);
        }

        String password = field_password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            field_password.setError("Required.");
            valid = false;
        } else {
            field_password.setError(null);
        }

        return valid;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        if (i == R.id.create_account_button) {

            createAccount(field_email.getText().toString(), field_password.getText().toString());

        } else if (i == R.id.cancel_create_account_button) {
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
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            sDialog.dismiss();
                        }
                    })
                    .show();
        }

    }
}
