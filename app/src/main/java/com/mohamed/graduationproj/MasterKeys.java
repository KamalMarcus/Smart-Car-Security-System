package com.mohamed.graduationproj;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class MasterKeys extends Activity implements View.OnClickListener {

    private Button mButtonLock;
    private Button mButtonUnLock;
    private Button mButtonCallHelp;
    private Button mButtonSetting;
    private Button mButtonLogout;
    private boolean mCheckLock=true;
    private String name;
    private SharedPreferences mSharedPreferences;
    private String mNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_keys);
        name = getIntent().getStringExtra("Name");
        ActionBarMange();
        Variables();
    }

    private void ActionBarMange() {
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setTitle(name);
        getActionBar().setIcon(R.drawable.logo2);
    }

    private void Variables() {
        mButtonLock= (Button) findViewById(R.id.bLock);
        mButtonUnLock= (Button) findViewById(R.id.bUnlock);
        mButtonCallHelp= (Button) findViewById(R.id.bCallHelp);
        mButtonSetting= (Button) findViewById(R.id.bSetting);
        mButtonLogout= (Button) findViewById(R.id.bLogout);
        mButtonCallHelp.setOnClickListener(this);
        mButtonLogout.setOnClickListener(this);
        mButtonLock.setOnClickListener(this);
        mButtonUnLock.setOnClickListener(this);
        mButtonSetting.setOnClickListener(this);

    }

    public void startTracking (View view){
        Intent intent = new Intent(this,CarLocation.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bCallHelp:
                mSharedPreferences= PreferenceManager.getDefaultSharedPreferences(MasterKeys.this);
                mNumber=mSharedPreferences.getString("HelpNum","911");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+mNumber));
                startActivity(callIntent);
                break;
            case R.id.bLogout:
                ParseQuery<ParseObject> mParseObjectParseQuery = new ParseQuery<>("Location");
                mParseObjectParseQuery.whereEqualTo("username",name);
                mParseObjectParseQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if (e == null) {
                            if (parseObjects.size() > 0) {
                                for (ParseObject object : parseObjects) {
                                    object.deleteInBackground();
                                }
                            }
                        }
                    }
                });
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent mIntent=new Intent(MasterKeys.this,MainActivity.class);
                        mIntent.putExtra("check",false);
                        startActivity(mIntent);
                        Toast.makeText(MasterKeys.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                break;
            case R.id.bLock:
                mButtonLock.setBackgroundResource(R.drawable.backgroundactive);
                if (mCheckLock==true) {
                    ParseQuery<ParseObject> mParseQuery = ParseQuery.getQuery("SensorState");
                    mParseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    mParseQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if (e == null) {
                                if (parseObjects.size() > 0) {
                                    parseObjects.get(0).put("state", false);
                                    parseObjects.get(0).saveInBackground();
                                }
                            }
                        }
                    });
                    mCheckLock=false;
                }else if (mCheckLock==false){
                    Toast.makeText(MasterKeys.this,"The System Already Locked",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bUnlock:
                mButtonLock.setBackgroundResource(R.drawable.background);
               if (mCheckLock==false){
                   ParseQuery<ParseObject> mParseQuery = ParseQuery.getQuery("SensorState");
                   mParseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                   mParseQuery.findInBackground(new FindCallback<ParseObject>() {
                       @Override
                       public void done(List<ParseObject> parseObjects, ParseException e) {
                           if (e == null) {
                               if (parseObjects.size() > 0) {
                                   parseObjects.get(0).put("state", true);
                                   parseObjects.get(0).saveInBackground();
                               }
                           }
                       }
                   });
                   mCheckLock=true;
               }else if (mCheckLock==true){
                   Toast.makeText(MasterKeys.this,"The System Already Unlocked",Toast.LENGTH_SHORT).show();
               }
                break;
            case R.id.bSetting:
                  Intent mIntentPrefs=new Intent(MasterKeys.this,Prefs.class);
                  startActivity(mIntentPrefs);
                break;
        }
    }

}
