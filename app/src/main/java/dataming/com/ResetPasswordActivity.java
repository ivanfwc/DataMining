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
import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    TextView field_email_forgot_password;
    Button reset_password_button, cancel_reset_password;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.toolbar_title)).setText("Reset Password");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        field_email_forgot_password = findViewById(R.id.field_email_forgot_password);

        findViewById(R.id.reset_password_button).setOnClickListener(this);
        findViewById(R.id.cancel_reset_password).setOnClickListener(this);

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

    private void resetPassword() {

        showProgressDialog();

        String email = field_email_forgot_password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.reset_password_button) {

            resetPassword();

        } else if (i == R.id.cancel_reset_password) {

            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }
    }
}
