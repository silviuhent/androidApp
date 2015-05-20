package com.example.bgdn.googlepluslogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.model.people.Person;

import utils.GlobalState;

public class LoginWithGooglePlus extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";// A key for saveInstanceState
    boolean mResolvingError = false;
    private GlobalState globalState = null;
    ProgressDialog progressDialog = null;

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "isGooglePlayServicesAvailable:"
                +     GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_google_plus);

        globalState = ((GlobalState) getApplicationContext());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }
    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_with_google_plus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnected() && mConnectionResult!=null){
            try{
                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                progressDialog = ProgressDialog.show(this, "Please wait!", "Connecting to Google Play services ...");
                mResolvingError = true;
            }catch(IntentSender.SendIntentException e){
                progressDialog.dismiss();
                mResolvingError = false;
                mConnectionResult = null;
                mGoogleApiClient.connect();
            }
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent){
        mResolvingError = false;

        progressDialog.dismiss();
        if(requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK){
            mConnectionResult = null;
            if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        getProfileInformation();
        Intent toLandingPageIntent = new Intent(this, LandPage.class);
        startActivity(toLandingPageIntent);
        finish();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    //Failed to connect automatically to the G+ account, or even after the connect button. It will retry to connect. The user can also choose meanwhile to press the connect button, still no problem.
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        mConnectionResult = connectionResult;

        if(mResolvingError){
            return;
        }else if (!mResolvingError && connectionResult.hasResolution()) {
                try {
                    progressDialog = ProgressDialog.show(this, "Please wait!", "Connecting to Google Play services ...");//comment added for test.
                    mResolvingError = true;
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    progressDialog.dismiss();
                    mResolvingError = false;
                    mGoogleApiClient.connect();
                }
        }else{
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, REQUEST_CODE_RESOLVE_ERR); //This shows an error dialog in case the mConnectionResult is not able to start a resolution.
        }
    }

    private void getProfileInformation() {

        try {

            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

                String personName = currentPerson.getDisplayName();
                this.globalState.setUsername(personName);

                String personPhotoUrl = currentPerson.getImage().getUrl();
                this.globalState.setProfileImgUrl(personPhotoUrl);

                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                this.globalState.setEmail(email);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}