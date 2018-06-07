package dataming.com.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dataming.com.LoginActivity;
import dataming.com.R;

import static dataming.com.CheckConnectivity.getConnectivityStatusString;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView fragprofile_imageView;
    TextView profile_tvName, profile_tvEmail;
    Button bAbout, bResetPin, bSignOut;
    FirebaseAuth mAuth;
    private LinearLayout linearLayout;
    private boolean internetConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        linearLayout = findViewById(R.id.linearLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.toolbar_title)).setText("Profile");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        fragprofile_imageView = findViewById(R.id.fragprofile_imageView);
        profile_tvName = findViewById(R.id.profile_tvName);
        profile_tvEmail = findViewById(R.id.profile_tvEmail);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform()
                            .placeholder(R.mipmap.ic_launcher_round))
                    .into(fragprofile_imageView);
        }

        //imageView.setImageBitmap(BitmapFactory.decodeStream(photoUrl));
        if (user != null) {
            profile_tvName.setText(user.getDisplayName());
        }
        if (user != null) {
            profile_tvEmail.setText(user.getEmail());
        }

        bResetPin = findViewById(R.id.bResetPin);
        bResetPin.setOnClickListener(this);

        bAbout = findViewById(R.id.bSettings);
        bAbout.setOnClickListener(this);

        bSignOut = findViewById(R.id.bSignOut);
        bSignOut.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.bSignOut) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                new SweetAlertDialog(Objects.requireNonNull(this), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.signout_app))
                        .setContentText(getString(R.string.main_activity_confirmation))
                        .setCancelText(getString(R.string.button_no))
                        .setConfirmText(getString(R.string.button_yes))
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();

                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                FirebaseUser user = mAuth.getCurrentUser();
                                AuthUI.getInstance()
                                        .signOut(getApplicationContext())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        //Log.i(TAG, "onResume()");
        registerInternetCheckReceiver();
    }

    /**
     *  Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     *  Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            setSnackbarMessage(status,false);
        }
    };

    private void setSnackbarMessage(String status, boolean showBar) {
        String internetStatus="";
        if(status.equalsIgnoreCase(getString(R.string.Wifi_enabled))||status.equalsIgnoreCase(getString(R.string.Mobile_data_enabled))){
            internetStatus=getString(R.string.Internet_Connected);
        }else {
            internetStatus=getString(R.string.Lost_Internet_Connection);
        }

        Snackbar snackbar;
        if(internetStatus.equalsIgnoreCase(getString(R.string.Lost_Internet_Connection))){
            if(internetConnected){
                snackbar = Snackbar
                        .make(linearLayout, internetStatus, Snackbar.LENGTH_INDEFINITE);
                        /*.setAction("X", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });*/
                snackbar.setActionTextColor(Color.WHITE);
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
                internetConnected=false;
            }
        }else{
            if(!internetConnected){
                snackbar = Snackbar
                        .make(linearLayout, internetStatus, Snackbar.LENGTH_LONG);
                        /*.setAction("X", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });*/
                snackbar.setActionTextColor(Color.WHITE);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.green_500));
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                internetConnected=true;
                snackbar.show();
            }
        }
    }
}
