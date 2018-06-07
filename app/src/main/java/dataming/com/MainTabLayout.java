package dataming.com;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dataming.com.Fragments.HomeFragment;
import dataming.com.Fragments.ToBeAddedFragment;

public class MainTabLayout extends Fragment {

    ProgressDialog dialog;
    MainTabLayout mContext;
    FirebaseAuth mAuth;

    Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static String POSITION = "POSITION";

    private int[] tabIcons = {
            R.drawable.ic_home_white,
            R.drawable.ic_account_circle_white,
    };

    private int[] tabIcons2 = {
            R.drawable.ic_home_black,
            R.drawable.ic_account_circle_black,
    };

    String[] tabsTitles;

    boolean isChecked;
    int oldTheme;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        isChecked = sharedPref.getBoolean("caps_pref", false);
        String lister = sharedPref.getString("list_preference", "1");
        oldTheme = Integer.parseInt(lister);

        View rootView = inflater.inflate(R.layout.activity_tablayout, container, false);

        tabsTitles = new String[]{getString(R.string.home_fragment_title), "To be Added"};

        dialog = new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.loading_app));

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user;

        mContext = this;

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                //String uid = profile.getUid();

                // Name, email address, and profile photo Url
                Uri photoUrl = profile.getPhotoUrl();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    currentUser.getPhotoUrl();
                }
                if (currentUser != null) {
                    currentUser.getEmail();
                }
                if (currentUser != null) {
                    currentUser.getDisplayName();
                }
            }
        }

        viewPager = rootView.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);

        setupViewPager(viewPager);

        tabLayout = rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener (new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition(), true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (tab.getIcon() != null) {
                        tab.getIcon().setColorFilter(Objects.requireNonNull(getContext()).getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    }
                } else {
                    Drawable drawableProgress = null;
                    if (tab.getIcon() != null) {
                        drawableProgress = DrawableCompat.wrap(tab.getIcon());
                    }
                    if (drawableProgress != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            DrawableCompat.setTint(drawableProgress, ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimary));
                        }
                    }
                    //tab.getIcon().setColorFilter();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(0)).getIcon()).clearColorFilter();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getIcon()).clearColorFilter();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupTabIcons(lister);

        viewPager.setCurrentItem(1);
        viewPager.setCurrentItem(0);

        return rootView;
    }

    private void setupTabIcons(String checkTheme) {

        int checkTabIcons[];
        if (Integer.parseInt(checkTheme) == 1) {
            checkTabIcons = tabIcons;
        } else {
            checkTabIcons = tabIcons2;
        }
        for (int i = 0; i < checkTabIcons.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(tabLayout.getTabAt(i)).setIcon(checkTabIcons[i]);
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        MainTabLayout.ViewPagerAdapter adapter = new MainTabLayout.ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        adapter.addFrag(new HomeFragment(), getString(R.string.home_fragment_title));
        adapter.addFrag(new ToBeAddedFragment(), "To be Added");
        adapter.notifyDataSetChanged();
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;  // This will get invoke as soon as you call notifyDataSetChanged on viewPagerAdapter.
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new HomeFragment();

                case 1:
                    return new ToBeAddedFragment();
            }

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
            //return null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

}
