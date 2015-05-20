package utils;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Bgdn on 3/23/2015.
 */
public class GlobalState extends Application {

    String username = "";
    String email = "";
    String profileImgUrl = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

}
