package com.transportervendor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.transportervendor.Api.VehicleService;
import com.transportervendor.Bean.Transporter;
import com.transportervendor.Bean.Vehicle;
import com.transportervendor.databinding.AddVehicleActivityBinding;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditVehicle extends AppCompatActivity {
    AddVehicleActivityBinding binding;
    Uri imgUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=AddVehicleActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.addVehicleToolBar);
        getSupportActionBar().setTitle("Edit Vehicle");
        Intent in=getIntent();
        final Vehicle vehicle= (Vehicle) in.getSerializableExtra("vehicle");
        if(NetworkUtility.checkInternetConnection(this)) {
            Picasso.get().load(vehicle.getImgUrl()).placeholder(R.drawable.transporter_logo).into(binding.civProfile);
        }else
            Toast.makeText(this, "please enable internet connection", Toast.LENGTH_SHORT).show();
        binding.etVehicleType.setText(vehicle.getName());
        binding.etvehicleCount.setText(vehicle.getCount());
        binding.btnDone.setText("Update");
        binding.ivBackErrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.civProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111);
            }
        });
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgUri!=null){
                    File file = FileUtils.getFile(EditVehicle.this, imgUri);
                    RequestBody requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(imgUri)),
                                    file
                            );
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    VehicleService.VehicleApi vehicleApi=VehicleService.getVehicleApiInstance();
                    RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, vehicle.getVehicelId());
                    RequestBody transporterId = RequestBody.create(okhttp3.MultipartBody.FORM, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Call<Transporter>call=vehicleApi.updateImage(id,transporterId,body);
                    if(NetworkUtility.checkInternetConnection(EditVehicle.this)) {
                        final CustomProgressDialog pd=new CustomProgressDialog(EditVehicle.this,"Please wait...");
                        pd.show();
                        call.enqueue(new Callback<Transporter>() {
                            @Override
                            public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                pd.dismiss();
                                if (response.code() == 200) {
                                    Transporter t = response.body();
                                    SharedPreferences mPrefs = getSharedPreferences("Transporter", MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(t);
                                    prefsEditor.putString("Transporter", json);
                                    prefsEditor.commit();
                                    Toast.makeText(EditVehicle.this, "image updated", Toast.LENGTH_SHORT).show();
                                    imgUri=null;
                                }
                            }

                            @Override
                            public void onFailure(Call<Transporter> call, Throwable t) {
                                pd.dismiss();
                                Toast.makeText(EditVehicle.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(EditVehicle.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    int etcount= Integer.parseInt(binding.etvehicleCount.getText().toString());
                    String etcat=binding.etVehicleType.getText().toString();
                    SharedPreferences mPrefs = getSharedPreferences("Transporter", MODE_PRIVATE);
                    String json=mPrefs.getString("Transporter","");
                    Gson gson = new Gson();
                    Transporter transporter=gson.fromJson(json,Transporter.class);
                    ArrayList<Vehicle>al=transporter.getVehicleList();
                    for(int i=0;i<al.size();i++){
                        Vehicle ve=al.get(i);
                        if(vehicle.getVehicelId().equals(ve.getVehicelId())){
                            ve.setName(etcat);
                            ve.setCount(etcount);
                            al.set(i,ve);
                            break;
                        }
                    }
                    transporter.setVehicleList(al);
                    VehicleService.VehicleApi vehicleApi = VehicleService.getVehicleApiInstance();
                    Call<Transporter> cal = vehicleApi.updateVehicle(transporter);
                    if(NetworkUtility.checkInternetConnection(EditVehicle.this)) {
                        final CustomProgressDialog pd=new CustomProgressDialog(EditVehicle.this,"Please wait...");
                        pd.show();
                        cal.enqueue(new Callback<Transporter>() {
                            @Override
                            public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                pd.dismiss();
                                if (response.isSuccessful()) {
                                    Transporter t = response.body();
                                    SharedPreferences mPrefs = getSharedPreferences("Transporter", MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(t);
                                    prefsEditor.putString("Transporter", json);
                                    prefsEditor.commit();
                                    Toast.makeText(EditVehicle.this, "updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Transporter> call, Throwable t) {
                                pd.dismiss();
                                Toast.makeText(EditVehicle.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else
                        Toast.makeText(EditVehicle.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imgUri=data.getData();
            binding.civProfile.setImageURI(imgUri);
        }
    }
}