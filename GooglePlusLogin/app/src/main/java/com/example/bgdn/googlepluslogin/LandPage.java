package com.example.bgdn.googlepluslogin;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.InputStream;

import utils.GlobalState;

public class LandPage extends ActionBarActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_page);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        GlobalState globalState = ((GlobalState) getApplicationContext());
        TextView welcome_msg = (TextView)findViewById(R.id.welcome_msg);
        ImageView profile_img = (ImageView) findViewById(R.id.profile_img);
        welcome_msg.setText("Welcome " + globalState.getUsername() + "!");
        Toast.makeText(this, mGoogleApiClient.isConnected()+" status_at_beginning", Toast.LENGTH_LONG);
        new LoadProfileImage(profile_img).execute(globalState.getProfileImgUrl());
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_land_page, menu);
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
        }else if(id == R.id.sign_out_button){

            Toast.makeText(this, mGoogleApiClient.isConnected()+" status", Toast.LENGTH_SHORT);
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                Intent intentSignOut = new Intent(this, LoginWithGooglePlus.class);
                startActivity(intentSignOut);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void forwardToHomeScreen(View view){
        Intent goToHome = new Intent(this, MainPage.class);
        startActivity(goToHome);
    }

    private class LoadProfileImage extends AsyncTask {

        ImageView downloadedImage;

        public LoadProfileImage(ImageView image) {

        this.downloadedImage = image;

    }

        @Override
        protected Object doInBackground(Object[] objects) {
            String url = (String)objects[0];
            Bitmap icon = null;

            try {
                InputStream in = new java.net.URL(url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return icon;
        }

        @Override
        protected void onPostExecute(Object o) {
            downloadedImage.setImageBitmap((Bitmap) o);
        }

    }
}