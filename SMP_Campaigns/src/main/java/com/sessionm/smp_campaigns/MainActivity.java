package com.sessionm.smp_campaigns;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.campaign.api.data.FeedMessage;
import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.core.api.provider.AuthenticationProvider;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.provider.SessionMOauthEmailProvider;
import com.sessionm.identity.api.provider.SessionMOauthProvider;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements CampaignsFragment.OnDeepLinkTappedListener {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";
    private TextView userBalanceTextView;
    private SessionMOauthEmailProvider _sessionMOauthEmailProvider;
    private UserManager _userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        _sessionMOauthEmailProvider = new SessionMOauthEmailProvider();
        SessionM.setAuthenticationProvider(_sessionMOauthEmailProvider, new AuthenticationProvider.OnAuthenticationProviderSetFromAuthenticationProvider() {
            @Override
            public void onUpdated(SessionMError sessionMError) {

            }
        });
        _userManager = UserManager.getInstance();

        userBalanceTextView = findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserManager.getInstance().getCurrentUser() == null)
                    _sessionMOauthEmailProvider.authenticateUser("test@sessionm.com", "aaaaaaaa1", new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (sessionMError != null) {
                                Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                _userManager.fetchUser(new UserManager.OnUserFetchedListener() {
                                    @Override
                                    public void onFetched(SMPUser smpUser, Set<String> set, SessionMError sessionMError) {
                                        if (sessionMError != null) {
                                            Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (smpUser != null) {
                                                userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
                                            } else
                                                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                                        }
                                    }
                                });
                            }
                        }
                    });
                else
                    _sessionMOauthEmailProvider.logoutUser(new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (authenticatedState.equals(SessionMOauthProvider.AuthenticatedState.NotAuthenticated))
                                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                        }
                    });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDeepLinkTapped(FeedMessage.MessageActionType actionType, String actionURL) {
        if (actionURL != null) {
            Uri uri = Uri.parse(actionURL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra("url", actionURL);
            startActivity(intent);
        }
    }
}
