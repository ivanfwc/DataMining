package dataming.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dataming.com.Activities.MapsActivity;
import dataming.com.Activities.ProfileActivity;
import dataming.com.Fragments.ToBeAddedFragment;

import static dataming.com.CheckConnectivity.getConnectivityStatusString;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    FirebaseAuth mAuth;

    private LinearLayout linearLayout;
    private boolean internetConnected = true;
    private static long back_pressed;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.linearLayout);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.navigationDrawer);

        // since, NoActionBar was defined in theme, we set toolbar as our action bar.
        setSupportActionBar(toolbar);

        //this basically defines on click on each menu item.
        setUpNavigationView(navigationView);

        //This is for the Hamburger icon.
        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);

        //Inflate the first fragment,this is like home fragment before user selects anything.
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameContent,new MainTabLayout()).commit();
        setTitle("Home");

        View header = navigationView.getHeaderView(0);

        ImageView navprofile_imageView = header.findViewById(R.id.navprofile_imageView);
        TextView nav_name = header.findViewById(R.id.nav_name);
        TextView nav_email = header.findViewById(R.id.nav_email);

        if (user != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform()
                            .placeholder(R.mipmap.ic_launcher_round))
                    .into(navprofile_imageView);
        }

        navprofile_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                drawerLayout.closeDrawers();
            }
        });

        if (user != null) {
            nav_name.setText(user.getDisplayName());
        }
        if (user != null) {
            nav_email.setText(user.getEmail());
        }

    }

    /**
     * Inflate the fragment according to item clicked in navigation drawer.
     */
    private void setUpNavigationView(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        //replace the current fragment with the new fragment.
                        /*Fragment selectedFragment = selectDrawerItem(menuItem);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frameContent, selectedFragment).commit();
                        // the current menu item is highlighted in navigation tray.
                        navigationView.setCheckedItem(menuItem.getItemId());
                        setTitle(menuItem.getTitle());
                        //close the drawer when user selects a nav item.
                        drawerLayout.closeDrawers();*/

                        int id = menuItem.getItemId();

                        if (id == R.id.nav_item_home) {
                            MainTabLayout fragment = new MainTabLayout();
                            android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction1.addToBackStack(null);
                            fragmentTransaction1.replace(R.id.frameContent, fragment).commit();
                            snackbar = Snackbar.make(linearLayout, menuItem.getTitle() + " selected", Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(Color.WHITE);
                            View sbView = snackbar.getView();
                            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            snackbar.show();

                        } else if (id == R.id.nav_item_additional) {
                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                        navigationView.setCheckedItem(menuItem.getItemId());
                        setTitle(menuItem.getTitle());
                        //close the drawer when user selects a nav item.
                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }

    /*public Fragment selectDrawerItem(MenuItem menuItem){
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_item_home:
                fragment = new MainTabLayout();
                break;
            case R.id.nav_item_additional:
                Intent intent = new Intent(this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return fragment;
    }*/

    /**
     * This is to setup our Toggle icon. The strings R.string.drawer_open and R.string.drawer close, are for accessibility (generally audio for visually impaired)
     * use only. It is now showed on the screen. While the remaining parameters are required initialize the toggle.
     */
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,R.string.drawer_close);
    }

    /**
     * This makes sure that the action bar home button that is the toggle button, opens or closes the drawer when tapped.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * This synchronizes the drawer icon that rotates when the drawer is swiped left or right.
     * Called inside onPostCreate so that it can synchronize the animation again when the Activity is restored.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    /**
     * This is to handle generally orientation changes of your device. It is mandatory to include
     * android:configChanges="keyboardHidden|orientation|screenSize" in your activity tag of the manifest for this to work.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(getBaseContext(), getString(R.string.double_pressed_back), Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
            //finish();
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
