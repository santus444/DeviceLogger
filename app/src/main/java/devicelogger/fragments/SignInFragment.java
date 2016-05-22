package devicelogger.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.santoshmandadi.deviceloggerone.R;
import com.santoshmandadi.deviceloggerone.utils.Constants;

import java.io.IOException;

/**
 * Created by santosh on 5/19/16.
 */

public class SignInFragment extends BaseFragment implements GoogleApiClient.OnConnectionFailedListener {

    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 1;
    /* A Google account object that is populated if the user signs in with Google */
    public GoogleSignInAccount mGoogleAccount;
    /**
     * Variables related to Google Login
     */
/* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private Button signOutButton;
    private Firebase firebaseObj;
    private String LOG_TAG = SignInFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
        initialize(rootView);

        if (firebaseObj.getAuth() == null) {
            signOutButton.setVisibility(View.INVISIBLE);
        }

        //Google Sign in related
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.UNIQUE_GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinWithGoogle(view);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseObj.getAuth() != null) {
                    firebaseObj.unauth();
                    signOutButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    private void initialize(View rootView) {
        signInButton = (SignInButton) rootView.findViewById(R.id.signInButton);
        signOutButton = (Button) rootView.findViewById(R.id.signOutButton);
        firebaseObj = new Firebase(Constants.FIREBASE_URL);

    }

    //in case any Google sign in connection error occurred the  following method will be invoked.

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showErrorSnackBar(connectionResult.toString());
    }

    public void signinWithGoogle(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
    }

    /**
     * This callback is triggered when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    /* Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); */
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Results", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
        /* Signed in successfully, get the OAuth token */
            mGoogleAccount = result.getSignInAccount();
            // showErrorSnackBar(mGoogleAccount.getDisplayName() + "," + mGoogleAccount.getEmail() + "," + mGoogleAccount.getIdToken());
            getGoogleOAuthTokenAndLogin();
            // loginWithGoogle(mGoogleAccount.getIdToken());
            signOutButton.setVisibility(View.VISIBLE);


        } else {
            if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                showErrorSnackBar(getString(R.string.error_message_google_singin_cancelled));
            } else {
                showErrorSnackBar(getString(R.string.error_message_error_handling_signin) + result.getStatus().getStatusMessage());
            }

        }
    }


    /**
     * Gets the GoogleAuthToken and logs in.
     */
    private void getGoogleOAuthTokenAndLogin() {
        //* Get OAuth token in Background *//*
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String mErrorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {
                    String scope = String.format(getString(R.string.oauth2_format), new Scope(Scopes.PROFILE)) + " email";
                    token = GoogleAuthUtil.getToken(getActivity(), mGoogleAccount.getEmail(), scope);

                } catch (IOException transientEx) {
                    //* Network or server error *//*
                    Log.e("Result For Google", transientEx.getMessage());
                    mErrorMessage = transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                    Log.w("Result For Google", e.toString());

                    //* We probably need to ask for permissions, so start the intent if there is none pending *//*
                    if (!mGoogleIntentInProgress) {
                        mGoogleIntentInProgress = true;
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, RC_GOOGLE_LOGIN);
                    }
                } catch (GoogleAuthException authEx) {
                    //* The call is not ever expected to succeed assuming you have already verified that Google Play services is installed. *//
                    Log.e("Result For Google", " " + authEx.getMessage(), authEx);
                    mErrorMessage = authEx.getMessage();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {

                if (token != null) {
                    //* Successfully got OAuth token, now login with Google *//*
                    loginWithGoogle(token);
                } else if (mErrorMessage != null) {
                    showErrorSnackBar(mErrorMessage);
                }
            }
        };

        task.execute();
    }

    private void loginWithGoogle(String token) {
        firebaseObj.authWithOAuthToken("google", token, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Log.d(LOG_TAG, "Provider: " + authData.getProvider()+ ", Token: "+authData.getToken()+"Uid: "+authData.getUid());
                showErrorSnackBar(getString(R.string.google_signin_success_confirmation_message));
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                showErrorSnackBar(getString(R.string.error_message_firebase_google_authentication) + firebaseError.getMessage());
            }
        });
    }


}
