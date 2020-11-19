package com.beratucgul.facebookandgooglelogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView info;
    TextView username;
    ImageView profile;
    private LoginButton login;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        info = findViewById(R.id.info);
        profile = findViewById(R.id.profile);
        login = findViewById(R.id.login);
        username = findViewById(R.id.userName);

        callbackManager = CallbackManager.Factory.create();
        

        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;
            @Override
            public void onSuccess(LoginResult loginResult) {

                 setFacebookConnection();

            /*    if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            Log.v("facebook - profile", currentProfile.getFirstName());
                            username.setText(currentProfile.getName());
                            info.setText("User id : " + loginResult.getAccessToken().getUserId());

                            String imageURL = currentProfile.getProfilePictureUri(70,70).toString();
                            Picasso.get().load(imageURL).into(profile);


                            mProfileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                }
            }

             */

            }
            
            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void setFacebookConnection() {

        LoginManager.getInstance().logOut();


        List<String> permissionNeeds = Arrays.asList("public_profile, email");

        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, permissionNeeds);

        FacebookSdk.sdkInitialize(MainActivity.this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {


            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Log.d("res", object.toString());
                        Log.d("res_obj", response.toString());
                        try {

                            String id = object.getString("id");
                            try {
                                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150&redirect=0");
                                Log.i("profile_pic", profile_pic + "");

                                String f_name = object.getString("first_name");
                                String l_name = object.getString("last_name");
                                String name = f_name + " " + l_name;
                                String email = object.getString("email");
                                String image = profile_pic.toString();


                                Profile currentProfile = Profile.getCurrentProfile();

                                String imageURL = currentProfile.getProfilePictureUri(70, 70).toString();
                                Picasso.get().load(imageURL).into(profile);


                                Log.d("datadeneme", email + " " + name + " " + imageURL);
                                String type = "facebook";

                                info.setText(name);
                                username.setText(email);
                              //  Picasso.get().load(image).into(profile);

                                if (email == null) {

                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }


                        } catch (JSONException e) {

                            e.printStackTrace();

                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

                Log.d("fb_exception", "cancel by user");
            }

            @Override
            public void onError(FacebookException exception) {

                Log.d("fb_exception", exception.toString());

            }
        });


    }
    }


