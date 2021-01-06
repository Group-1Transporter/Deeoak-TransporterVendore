package com.transportervendor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.transportervendor.Bean.Transporter;

public class MainActivity extends AppCompatActivity {
   // ActivityMainBinding mainBinding;
    FirebaseUser currentUser;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//      mainBinding = ActivityMainBinding.inflate(LayoutInflater.from( this));
//        setContentView(mainBinding.getRoot());
          sp = getSharedPreferences("transporter",MODE_PRIVATE);
//        setSupportActionBar(mainBinding.mainToolBar);
//        getSupportActionBar().setTitle("Transport Service");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }
   @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null)
            sendUserToLoginActivity();
        else if(!isProfileCreated())
            sendUserToCreateProfileScreen();

    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout");
        return super.onCreateOptionsMenu(menu);
    }
    private void sendUserToLoginActivity(){
        Intent in = new Intent(this,LoginActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
        finish();
    }

    private boolean isProfileCreated(){
        String status = sp.getString("currentUserProfile","");
        return !status.equals("");
    }

    private void sendUserToCreateProfileScreen() {
        Intent createProfileIntent = new Intent(MainActivity.this,CreateProfileScreen.class);
        startActivity(createProfileIntent);
    }
    private void saveTransporterLocally(Transporter t) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", t.getName());
        editor.putString("contactNumber", t.getContactNumber());
        editor.putString("address", t.getAddress());
        editor.putString("type", t.getType());
        String gstNo = "";
        if (t.getGstNumber() != null)
            gstNo = t.getGstNumber();
        editor.putString("gstNumber", gstNo);
        editor.putString("aadharNo",t.getAadharCardNumber());
        editor.putString("imageUrl", t.getImageUrl());
        editor.putString("transporterId", t.getTransporterId());
        editor.commit();
    }
}