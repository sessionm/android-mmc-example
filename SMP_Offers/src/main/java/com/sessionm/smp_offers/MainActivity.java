package com.sessionm.smp_offers;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.smp_offers.my_offers.MyOffersFragment;
import com.sessionm.smp_offers.store_offers.StoreOffersFragment;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements StoreOffersFragment.Callback {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";

    private final UserManager userManager = UserManager.getInstance();
    private final IdentityManager identityManager = IdentityManager.getInstance();

    private Button authenticate;
    private TextView userBalance;
    private TabLayout _tabs;
    private ViewPager _pager;
    private OffersPagerAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userBalance = findViewById(R.id.user_balance);
        authenticate = findViewById(R.id.authenticate);
        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userManager.getCurrentUser() == null) {
                    identityManager.authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
                } else {
                    identityManager.logOutUser();
                }
            }
        });

        _tabs = findViewById(R.id.tabs);
        _pager = findViewById(R.id.pager);
        _pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                fetchForPage(position);
            }

            @Override public void onPageScrollStateChanged(int state) {}
        });

        _adapter = new OffersPagerAdapter(getSupportFragmentManager(), _myOffersFragment, _storeOffersFragment);
        _pager.setAdapter(_adapter);
        _tabs.setupWithViewPager(_pager);

    }

    @Override
    public void updatePoints(int points) {
        userBalance.setText(points + " pts");
    }

    private void fetchForPage(int position) {
        switch (position) {
            case 0:
                _myOffersFragment.fetchOffers();
                break;
            case 1:
                _storeOffersFragment.fetchOffers();
                break;
            default:
                Log.d("TAG", "Too Many Tabs");
        }
    }

    private MyOffersFragment _myOffersFragment = new MyOffersFragment();
    private StoreOffersFragment _storeOffersFragment = new StoreOffersFragment();

    @Override
    protected void onResume() {
        super.onResume();

        userManager.setListener(_userListener);
        if (userManager.getCurrentUser() != null) {
            updatePoints(userManager.getCurrentUser().getAvailablePoints());
            fetchForPage(_pager.getCurrentItem());
        } else {
            userManager.fetchUser();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        userManager.setListener(null);
    }

    UserListener _userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            if (smpUser != null) {
                updatePoints(smpUser.getAvailablePoints());
                authenticate.setText("Logout");
                fetchForPage(_pager.getCurrentItem());
            } else {
                userBalance.setText("");
                authenticate.setText("Login");
            }
        }

        @Override
        public void onFailure(SessionMError sessionMError) {
            Toast.makeText(MainActivity.this, "Failure: '" + sessionMError.getCode() + "' " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
}
