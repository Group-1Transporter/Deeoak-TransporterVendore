package com.transportervendor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.transportervendor.Adapter.ManageVehicleAdapter;
import com.transportervendor.Api.TransporterService;
import com.transportervendor.Bean.Transporter;
import com.transportervendor.Bean.Vehicle;
import com.transportervendor.databinding.ManageVehicleActivityBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageVehicleActivity extends AppCompatActivity {
    ManageVehicleActivityBinding manageVehicleBinding;
    String currentUser;
    ArrayList<Vehicle> vList;
    ManageVehicleAdapter adapter;
    ProgressDialog pd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manageVehicleBinding = ManageVehicleActivityBinding.inflate(getLayoutInflater());
        //manageVehicleBinding.toolbar.setTitle("Manage Vehicle");
        currentUser = FirebaseAuth.getInstance().getUid();

        Toast.makeText(ManageVehicleActivity.this, currentUser, Toast.LENGTH_SHORT).show();
        Log.e("current user"," "+currentUser);
        setContentView(manageVehicleBinding.getRoot());
        boolean isInternetConnected = NetworkUtility.checkInternetConnection(ManageVehicleActivity.this);
        if (isInternetConnected) {
            TransporterService.TransportApi transporterApi = TransporterService.getTransporterApiIntance();
            Call<Transporter> call = transporterApi.getTransporterById(currentUser);

            pd = new ProgressDialog(this);
            pd.setTitle("Data Fetch");
            pd.setMessage("Please Wait...");
            pd.show();

            call.enqueue(new Callback<Transporter>() {
                @Override
                public void onResponse(Call<Transporter> call, Response<Transporter> response) {

                    Toast.makeText(ManageVehicleActivity.this, "data faching", Toast.LENGTH_SHORT).show();
                    if (response.code() == 200) {
                        Toast.makeText(ManageVehicleActivity.this, "is data Available ", Toast.LENGTH_SHORT).show();
                        Transporter t = response.body();
                        vList = t.getVehicleList();
                        if (vList != null) {

                            Toast.makeText(ManageVehicleActivity.this, "Vehicle list available", Toast.LENGTH_SHORT).show();
                            adapter = new ManageVehicleAdapter(vList);
                            manageVehicleBinding.rvManageVehicleList.setAdapter(adapter);
                            manageVehicleBinding.rvManageVehicleList.setLayoutManager(new LinearLayoutManager(ManageVehicleActivity.this));
                            pd.dismiss();
                        }
                    }

                }
                @Override
                public void onFailure(Call<Transporter> call, Throwable transporter) {
                    Toast.makeText(ManageVehicleActivity.this, "Something went rong", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else{
            Toast.makeText(ManageVehicleActivity.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        }
        manageVehicleBinding.ivFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageVehicleActivity.this, AddVehicleActivity.class);
                startActivity(intent);
                finish();

            }
        });
        manageVehicleBinding.btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(ManageVehicleActivity.this,HomeActivity.class);
                startActivity(in);
                finish();
            }
        });
    }

    }

/*
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.transportervendor.Adapter.ManageVehicleAdapter;
import com.transportervendor.Api.TransporterService;
import com.transportervendor.Api.VehicleService;
import com.transportervendor.Bean.Transporter;
import com.transportervendor.Bean.Vehicle;
import com.transportervendor.databinding.ManageVehicleActivityBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageVehicleActivity extends AppCompatActivity {
    ManageVehicleActivityBinding manageVehicleBinding ;
    List<Vehicle> vehicleList;
    ManageVehicleAdapter adapter;
    ProgressDialog pd;
    String currentUserId;
    TransporterService.TransportApi transporterApi;
    VehicleService.VehicleApi vehicleApi;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            currentUserId = FirebaseAuth.getInstance().getUid();
            vehicleApi = VehicleService.getVehicleApiInstance();
            manageVehicleBinding = ManageVehicleActivityBinding.inflate(getLayoutInflater());
            setContentView(manageVehicleBinding.getRoot());
            if (NetworkUtility.checkInternetConnection(this)) {
                transporterApi = TransporterService.getTransporterApiIntance();
                Call<Transporter> call = transporterApi.getTransporterById(currentUserId);
                pd = new ProgressDialog(this);
                pd.setMessage("Check Data Please Wait...");
                pd.show();
                call.enqueue(new Callback<Transporter>() {
                    @Override
                    public void onResponse(final Call<Transporter> call, Response<Transporter> response) {
                        Toast.makeText(ManageVehicleActivity.this, ""+response.code(), Toast.LENGTH_SHORT).show();
                        if (response.code() == 200) {
                            try {
                                Transporter t = response.body();
                                vehicleList = t.getVehicleList();
                                pd.dismiss();
                                if (vehicleList != null) {
                                    adapter = new ManageVehicleAdapter((ArrayList<Vehicle>) vehicleList);
                                    manageVehicleBinding.rvManageVehicleList.setAdapter(adapter);
                                    manageVehicleBinding.rvManageVehicleList.setLayoutManager(new LinearLayoutManager(ManageVehicleActivity.this));

                                    adapter.setOnRecyclerListener(new ManageVehicleAdapter().OnRecyclerViewClick() {
                                        @Override
                                        public void onItemClick(final Vehicle vehicle, final int postion, final String status) {
                                            if (status.equalsIgnoreCase("Delete")) {
                                                AlertDialog.Builder ab = new AlertDialog.Builder(ManageVehicleActivity.this);
                                                ab.setTitle("Delete");
                                                ab.setMessage("Are You Sure ?");
                                                ab.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        final ProgressDialog pd1 = new ProgressDialog(ManageVehicleActivity.this);
                                                        pd1.setMessage("Delete Vehicles");
                                                        pd1.show();
                                                        Call<Vehicle> call1 = vehicleApi.deleteTransporterVehicle(vehicle.getVehicelId(), currentUserId);
                                                        call1.enqueue(new Callback<Vehicle>() {
                                                            @Override
                                                            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                                                                if (response.code() == 200) {
                                                                    vehicleList.remove(postion);
                                                                    adapter.notifyDataSetChanged();
                                                                    Snackbar.make(manageVehicleBinding.rvManageVehicleList, "Vehicle Delete Successfully", Snackbar.LENGTH_LONG);
                                                                }
                                                                pd1.dismiss();
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Vehicle> call, Throwable t) {
                                                                Toast.makeText(ManageVehicleActivity.this, "" + t, Toast.LENGTH_SHORT).show();
                                                                pd1.dismiss();
                                                            }
                                                        });
                                                    }
                                                });
                                                ab.setNegativeButton("Cancel", null);
                                                ab.show();

                                            } else if (status.equalsIgnoreCase("Edit")) {
                                                try{
                                                    Toast.makeText(ManageVehicleActivity.this, "hell", Toast.LENGTH_SHORT).show();
                                                    Intent in = new Intent(ManageVehicleActivity.this,AddVehicleActivity.class);
                                                    in.putExtra("vehicle", (Parcelable) vehicle);
                                                    startActivity(in);
                                                }catch (Exception e){
                                                    Toast.makeText(ManageVehicleActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(ManageVehicleActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            }catch (Exception e){
                                Toast.makeText(ManageVehicleActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Transporter> call, Throwable t) {
                        Toast.makeText(ManageVehicleActivity.this, "Something went rong", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });


                manageVehicleBinding.ivFloatingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(ManageVehicleActivity.this, AddVehicleActivity.class);
                        startActivity(in);
                    }
                });
            } else {
                final AlertDialog ab = new AlertDialog.Builder(ManageVehicleActivity.this)
                        .setCancelable(false)
                        .setTitle("Network Not Connected")
                        .setMessage("Please check your network connection")
                        .setPositiveButton("Retry", null)
                        .show();
                Button positive = ab.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NetworkUtility.checkInternetConnection(ManageVehicleActivity.this)) {
                            ab.dismiss();
                        }
                    }
                });
            }
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        setSupportActionBar(manageVehicleBinding.tbToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
*/