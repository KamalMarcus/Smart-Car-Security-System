package com.mohamed.graduationproj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends Activity {
    EditText usernameField;
    EditText passwordField;
    TextView changeMood;
    Boolean signUpActive;
    Button logButton;
    boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setTitle("Smart Car Security System");
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.drawable.logo2);
        signUpActive = false;
        usernameField = (EditText) findViewById(R.id.userName);
        passwordField = (EditText) findViewById(R.id.password);
        changeMood = (TextView) findViewById(R.id.changeMood);
        logButton = (Button) findViewById(R.id.signUpButton);

        if (getIntent().hasExtra("check")) {

        } else {
            try {
                Parse.initialize(new Parse.Configuration.Builder(this)
                                .applicationId("CZPhSTmagillekG1dX4WxFxMkknMiQCV4BYauEdO")
                                .clientKey("4L7IarTRiogC4uhUaHByS1W1buCsmBbCFxRcgC4h")
                                .server("https://parseapi.back4app.com")
                                .build()
                );
            } catch (android.net.ParseException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        try {
            // Update Installation (for notifications)
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("GCMSenderId", "245911530436");
            installation.saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }


        CheckUserLogIn();

    }

    private void CheckUserLogIn() {
        if (ParseUser.getCurrentUser() != null) {
            Intent mIntent = new Intent(MainActivity.this, MasterKeys.class);
            mIntent.putExtra("Name", ParseUser.getCurrentUser().getUsername());
            startActivity(mIntent);
            finish();
        }
    }

    public void changeMood(View view) {
        if (view.getId() == R.id.changeMood) {
            if (signUpActive == true) {
                signUpActive = false;
                changeMood.setText("Sign Up");
                logButton.setText("Log In");
            } else {
                signUpActive = true;
                changeMood.setText("Log In");
                logButton.setText("Sign Up");
            }
        }
    }

    public void signUpLogIn(View view) {

        String userName = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        if (signUpActive == true) {

            ParseUser user = new ParseUser();
            user.setUsername(userName);
            user.setPassword(password);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Successfully SignUp", Toast.LENGTH_SHORT).show();
//                        ParseObject object = new ParseObject("Location");
//                        object.put("username",ParseUser.getCurrentUser().getUsername());
//                        object.saveInBackground();
                    } else {
                        Toast.makeText(getApplicationContext(), e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            ParseUser.logInInBackground(usernameField.getText().toString(), passwordField.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        ParseQuery<ParseObject> mParseQuery = ParseQuery.getQuery("SensorState");
                        mParseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                        mParseQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> parseObjects, ParseException e) {
                                if (e == null) {
                                    if (parseObjects.size() > 0) {

                                    } else {
                                        ParseObject mParseObject = new ParseObject("SensorState");
                                        mParseObject.put("username", ParseUser.getCurrentUser().getUsername());
                                        mParseObject.put("state", true);
                                        mParseObject.saveInBackground();
                                    }
                                }
                            }
                        });
                        ParseObject object = new ParseObject("Location");
                        object.put("username", ParseUser.getCurrentUser().getUsername());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Intent intent = new Intent(getApplicationContext(), MasterKeys.class);
                                intent.putExtra("Name", ParseUser.getCurrentUser().getUsername());
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Login Successfully Welcome", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


}
