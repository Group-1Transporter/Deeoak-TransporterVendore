package com.transportervendor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.transportervendor.Adapter.TabAccessorAdapter;
import com.transportervendor.Api.TransporterService;
import com.transportervendor.Bean.Transporter;
import com.transportervendor.Bean.Vehicle;
import com.transportervendor.databinding.ActivityHomeBinding;
import com.transportervendor.databinding.HeaderDrawerBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    TabAccessorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityHomeBinding homeBinding=ActivityHomeBinding.inflate(LayoutInflater.from(HomeActivity.this));
        setContentView(homeBinding.getRoot());
       // setSupportActionBar(homeBinding.toolbar);
        adapter=new TabAccessorAdapter(getSupportFragmentManager(),1);

        homeBinding.drawerlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeBinding.drawerlayout.openDrawer(Gravity.RIGHT);
            }
        });
        String token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences shared = getSharedPreferences("Transporter", Context.MODE_PRIVATE);
        String json=shared.getString("Transporter","");
        Transporter transporter;
        if(json.equals("")){

             transporter = new Transporter(" "," "," "," "," "," "," "," ",new ArrayList<Vehicle>());
        }else {
            Gson gson = new Gson();
            transporter = gson.fromJson(json, Transporter.class);
        }

        if(transporter.getTransporterId()!=null){
            String user=FirebaseAuth.getInstance().getCurrentUser().getUid();
            TransporterService.TransportApi transporterApi= TransporterService.getTransporterApiIntance();
            Call<Transporter>call=transporterApi.getTransporterById(user);
            final CustomProgressDialog pd=new CustomProgressDialog(HomeActivity.this,"Please wait...");
            pd.show();
               String transporterid;
               transporter.getTransporterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }else if(transporter.getTransporterId()!=null){
            if(!(token.equals(transporter.getToken()))){
                transporter.setToken(token);
                TransporterService.TransportApi transporterApi=TransporterService.getTransporterApiIntance();
                Call<Transporter>call=transporterApi.updateTransporter(transporter);
                final CustomProgressDialog pd=new CustomProgressDialog(HomeActivity.this,"Please wait...");
                pd.show();
                call.enqueue(new Callback<Transporter>() {
                    @Override

                    public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                        pd.dismiss();
                        if(response.isSuccessful()){
                            Transporter t=response.body();
                            SharedPreferences mPrefs=getSharedPreferences("Transporter",MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(t);
                            prefsEditor.putString("Transporter", json);
                            prefsEditor.commit();
                            Toast.makeText(HomeActivity.this, "token updated", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Transporter> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(HomeActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        View view=homeBinding.navigationView.inflateHeaderView(R.layout.header_drawer);
        ImageView iv=view.findViewById(R.id.ivBackErrow);
        HeaderDrawerBinding header= HeaderDrawerBinding.inflate(LayoutInflater.from(HomeActivity.this));


//        homeBinding.drawerlayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(HomeActivity.this, "clicked", Toast.LENGTH_SHORT).show();
//                homeBinding.drawerlayout.closeDrawer(Gravity.RIGHT);
//            }
//        });

        homeBinding.viewPager.setAdapter(adapter);
        homeBinding.tabLayout.setupWithViewPager(homeBinding.viewPager);
        homeBinding.navigationView.setItemIconTintList(null);
        homeBinding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.civProfile){
                    Intent in =new Intent(HomeActivity.this,CreateProfileScreen.class);
                    in.putExtra("code",2);
                    startActivity(in);
                }
                else if(id==R.id.rvManageVehicleList){
                    Intent in =new Intent(HomeActivity.this,ManageVehicleActivity.class);
                    startActivity(in);
                }
               else if(id==R.id.privacy){
                    Intent in=new Intent(HomeActivity.this,PrivacyPolicy.class);
                    startActivity(in);
                }
               else if(id==R.id.contact) {
                    Intent in = new Intent(HomeActivity.this, ContactUs.class);
                    startActivity(in);
                }
               else if(id==R.id.about){
                        Intent in=new Intent(HomeActivity.this,AboutUs.class);
                        startActivity(in);
                }
               else if(id==R.id.terms){
                        Intent in=new Intent(HomeActivity.this,TermsnConditions.class);
                        startActivity(in);
                }else if(id==R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    SharedPreferences mPrefs=getSharedPreferences("Transporter",MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString("Transporter","");
                    prefsEditor.commit();
                    finish();
                }
                return true;
            }
        });
    }
}


/*package com.transportervendor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.transportervendor.Adapter.TabAccessorAdapter;
import com.transportervendor.Api.TransporterService;
import com.transportervendor.Bean.Transporter;
import com.transportervendor.Bean.Vehicle;
import com.transportervendor.databinding.ActivityHomeBinding;
import com.transportervendor.databinding.HeaderDrawerBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    TabAccessorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityHomeBinding homeBinding=ActivityHomeBinding.inflate(LayoutInflater.from(HomeActivity.this));
        setContentView(homeBinding.getRoot());
        adapter=new TabAccessorAdapter(getSupportFragmentManager(),1);

        homeBinding.drawerlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeBinding.drawerlayout.openDrawer(Gravity.RIGHT);
            }
        });
        String token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences shared = getSharedPreferences("Transporter", Context.MODE_PRIVATE);
        String json=shared.getString("Transporter","");
        Transporter transporter;
        if(json.equals("")){
            transporter=new Transporter(" "," "," "," "," "," "," "," "," "," ",new ArrayList<Vehicle>());
        }else {
            Gson gson = new Gson();
            transporter = gson.fromJson(json, Transporter.class);
        }

        if(transporter.getTransporterId().equals()){
            String user=FirebaseAuth.getInstance().getCurrentUser().getUid();
            TransporterService.TransportApi transporterApi=TransporterService.getTransporterApiIntance();
            Call<Transporter>call=transporterApi.getTransporterById(user);
            final CustomProgressDialog pd=new CustomProgressDialog(HomeActivity.this,"Please wait...");
            pd.show();
            call.enqueue(new Callback<Transporter>() {
                @Override
                public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                    pd.dismiss();
                    if(!response.isSuccessful()){
                        Intent in=new Intent(HomeActivity.this,CreateProfileScreen.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        in.putExtra("code",1);
                        startActivity(in);
                        finish();
                    }else if(response.code()==200){
                        Transporter t=response.body();
                        SharedPreferences mPrefs=getSharedPreferences("Transporter",MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(t);
                        prefsEditor.putString("Transporter", json);
                        prefsEditor.commit();
                    }
                }

                @Override
                public void onFailure(Call<Transporter> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(HomeActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else if(transporter.getTransporterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            if(!(token.equals(transporter.getToken()))){
                transporter.setToken(token);
                TransporterService.TransportApi transporterApi=TransporterService.getTransporterApiIntance();
                Call<Transporter>call=transporterApi.updateTransporter(transporter);
                final CustomProgressDialog pd=new CustomProgressDialog(HomeActivity.this,"Please wait...");
                pd.show();
                call.enqueue(new Callback<Transporter>() {
                    @Override
                    public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                        pd.dismiss();
                        if(response.isSuccessful()){
                            Transporter t=response.body();
                            SharedPreferences mPrefs=getSharedPreferences("Transporter",MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(t);
                            prefsEditor.putString("Transporter", json);
                            prefsEditor.commit();
                            Toast.makeText(HomeActivity.this, "token updated", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Transporter> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(HomeActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        View view=homeBinding.navigationView.inflateHeaderView(R.layout.header_drawer);
        ImageView iv=view.findViewById(R.id.btnback);
        HeaderDrawerBinding header= HeaderDrawerBinding.inflate(LayoutInflater.from(HomeActivity.this));

//        iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(HomeActivity.this, "clicked", Toast.LENGTH_SHORT).show();
//                homeBinding.drawerlayout.closeDrawer(Gravity.RIGHT);
//
//            }
//        });

        homeBinding.viewPager.setAdapter(adapter);
        homeBinding.tabLayout.setupWithViewPager(homeBinding.viewPager);
        homeBinding.navigationView.setItemIconTintList(null);
        homeBinding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.profile){
                    Intent in =new Intent(HomeActivity.this,CreateProfileScreen.class);
                    in.putExtra("code",2);
                    startActivity(in);
                }
                else if(id==R.id.vehicle){
                    Intent in =new Intent(HomeActivity.this,ManageVehicleActivity.class);
                    startActivity(in);
                }
                else if(id==R.id.terms){
                    Intent in=new Intent(HomeActivity.this,TermsnConditions.class);
                    startActivity(in);
                }else if(id==R.id.privacy){
                    Intent in=new Intent(HomeActivity.this,PrivacyPolicy.class);
                    startActivity(in);
                }else if(id==R.id.about){
                    Intent in=new Intent(HomeActivity.this,AboutUs.class);
                    startActivity(in);
                }
//                else if(id==R.id.contact){
//                    Intent in=new Intent(HomeActivity.this,ContactUs.class);
//                    startActivity(in);
//                }
                else if(id==R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    SharedPreferences mPrefs=getSharedPreferences("Transporter",MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString("Transporter","");
                    prefsEditor.commit();
                    finish();
                }
                return true;
            }
        });
    }
}
*/

