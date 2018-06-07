package dataming.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    TextView field_email, field_password;
    Button email_sign_in_button, create_account_sign_in_button, reset_password_sign_in_button;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinwithemail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.toolbar_title)).setText("Sign In");
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        field_email = findViewById(R.id.field_email);
        field_password = findViewById(R.id.field_password);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.create_account_sign_in_button).setOnClickListener(this);
        findViewById(R.id.reset_password_sign_in_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    /*@Override
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
    }*/

    private void signIn(String email, String password) {

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent i = new Intent(SignInActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();

                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

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
                        Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
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
        if (i == R.id.email_sign_in_button) {

            signIn(field_email.getText().toString(), field_password.getText().toString());

        } else if (i == R.id.create_account_sign_in_button) {

            Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else if (i == R.id.reset_password_sign_in_button) {

            Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }
    }
}
