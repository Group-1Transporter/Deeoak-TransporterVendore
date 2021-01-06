package com.transportervendor;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.transportervendor.Api.VehicleService;
import com.transportervendor.Bean.Transporter;
import com.transportervendor.databinding.AddVehicleActivityBinding;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleActivity extends AppCompatActivity {
    AddVehicleActivityBinding binding;
    Uri imgUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=AddVehicleActivityBinding.inflate(LayoutInflater.from(AddVehicleActivity.this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.addVehicleToolBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imgUri=data.getData();
                    binding.civProfile.setImageURI(imgUri);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.civProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(AddVehicleActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddVehicleActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},111);
                }

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),111);
            }
        });
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtility.checkInternetConnection(AddVehicleActivity.this)) {
                    String etcategory = binding.etVehicleType.getText().toString();
                    if (etcategory.isEmpty()) {
                        binding.etVehicleType.setError("this field can't be empty.");
                        return;
                    }
                    String etcount = binding.etvehicleCount.getText().toString();
                    if (etcount.isEmpty()) {
                        binding.etvehicleCount.setError("this field can't be empty.");
                        return;
                    }
                    String token = "4324343434343432432434343434";
                    if (imgUri != null) {
                        File file = FileUtils.getFile(AddVehicleActivity.this, imgUri);
                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse(getContentResolver().getType(imgUri)),
                                        file
                                );
                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        RequestBody name=RequestBody.create(okhttp3.MultipartBody.FORM, etcategory);
                        RequestBody count=RequestBody.create(okhttp3.MultipartBody.FORM, etcount);
                        RequestBody transporterId=RequestBody.create(okhttp3.MultipartBody.FORM, FirebaseAuth.getInstance().getCurrentUser().getUid());

                        VehicleService.VehicleApi vehicleApi= VehicleService.getVehicleApiInstance();
                        Call<Transporter>call=vehicleApi.createVehicle(name,count,transporterId,body);
                        final CustomProgressDialog pd=new CustomProgressDialog(AddVehicleActivity.this,"Please wait...");
                        pd.show();
                        call.enqueue(new Callback<Transporter>() {
                            @Override
                            public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                pd.dismiss();
                                if (response.code()==200){
                                    Transporter t=response.body();
                                    SharedPreferences mPrefs = getSharedPreferences("Transporter",MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(t);
                                    Log.e("spanshoe",""+json);
                                    prefsEditor.putString("Transporter", json);
                                    prefsEditor.commit();
                                    Intent in=new Intent();
                                    setResult(222,in);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Transporter> call, Throwable t) {
                                pd.dismiss();
                                Toast.makeText(AddVehicleActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        Toast.makeText(AddVehicleActivity.this, "Please select vehicle picture.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddVehicleActivity.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.ivBackErrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

/*
import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.transportervendor.Api.VehicleService;
import com.transportervendor.Bean.Vehicle;
import com.transportervendor.databinding.AddVehicleActivityBinding;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleActivity extends AppCompatActivity {
    AddVehicleActivityBinding vehicleBinding;
    Uri imageUri;
    String currentUserId;
    VehicleService.VehicleApi vehicleApi;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vehicleBinding = AddVehicleActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(vehicleBinding.getRoot());
        try {
            currentUserId = FirebaseAuth.getInstance().getUid();
            setSupportActionBar(vehicleBinding.addVehicleToolBar);
            vehicleApi = VehicleService.getVehicleApiInstance();
            final Intent i = getIntent();
            final Vehicle v = (Vehicle) i.getSerializableExtra("vehicle");
            if (v != null) {
                Picasso.get().load(v.getImgUrl()).into(vehicleBinding.civProfile);
                vehicleBinding.etVehicleType.setText(v.getName());
                vehicleBinding.etvehicleCount.setText("" + v.getCount());
                vehicleBinding.btnDone.setText("update");
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
            }
            vehicleBinding.btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String name = vehicleBinding.etVehicleType.getText().toString();
                    if (name.isEmpty()) {
                        vehicleBinding.etVehicleType.setError("Enter Vehicle  type");
                        return;
                    }
                    int count = Integer.parseInt(vehicleBinding.etvehicleCount.getText().toString());
                    if (TextUtils.isEmpty(count + "")) {
                        vehicleBinding.etvehicleCount.setError("Enter Number of Vehicle");
                        return;
                    }
                    String button = vehicleBinding.btnDone.getText().toString();
                    if (button.equalsIgnoreCase("Done")) {
                        if (imageUri != null) {
                            try {
                                File file = FileUtils.getFile(AddVehicleActivity.this, imageUri);
                                RequestBody requestFile =
                                        RequestBody.create(
                                                MediaType.parse(getContentResolver().getType(imageUri)),
                                                file);

                                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                                RequestBody vehicleType = RequestBody.create(MultipartBody.FORM, name);
                                RequestBody transporterid = RequestBody.create(MultipartBody.FORM, currentUserId);
                                RequestBody numberOfVehicle = RequestBody.create(MultipartBody.FORM, String.valueOf(count));
                                Call<Vehicle> call = vehicleApi.createVehicle(body,
                                        vehicleType,
                                        numberOfVehicle,
                                        transporterid
                                );
                                call.enqueue(new Callback<Vehicle>() {
                                    @Override
                                    public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                                        int status = response.code();
                                        Toast.makeText(AddVehicleActivity.this, response.code(), Toast.LENGTH_SHORT).show();
                                        if (status == 200) {
                                            try {
                                                Vehicle v = response.body();
                                                Toast.makeText(AddVehicleActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                sendUserToManageVehicleActivity();
                                            } catch (Exception e) {
                                                Toast.makeText(AddVehicleActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        } else if (status == 500) {
                                            Toast.makeText(AddVehicleActivity.this, "Failed..", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Vehicle> call, Throwable t) {
                                        Toast.makeText(AddVehicleActivity.this, "Something went wrong : " + t, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }catch (Exception e){
                                Toast.makeText(AddVehicleActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(AddVehicleActivity.this, "plese select Image..", Toast.LENGTH_SHORT).show();
                    }
                  /*  else if (button.equalsIgnoreCase("update")) {
                        v.setCount(count);
                        v.setName(name);
                        Call<Vehicle> call = vehicleApi.createVehicle(name,count,v);
                        call.enqueue(new Callback<Vehicle>() {
                            @Override
                            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                                if (response.code() == 200) {
                                    Toast.makeText(AddVehicleActivity.this, "Vehicle Update SuccessFully", Toast.LENGTH_SHORT).show();
                                    sendUserToManageVehicleActivity();
                                }
                            }

                            @Override
                            public void onFailure(Call<Vehicle> call, Throwable t) {

                            }
                        });

                    }*/
/*
                }
            });
            vehicleBinding.civProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(AddVehicleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddVehicleActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
                    } else {
                        Intent in = new Intent();
                        in.setAction(Intent.ACTION_GET_CONTENT);
                        in.setType("image/*");
                        startActivityForResult(in, 111);
                    }
                }
            });
            setSupportActionBar(vehicleBinding.addVehicleToolBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendUserToManageVehicleActivity() {
        Intent in = new Intent(AddVehicleActivity.this, HomeActivity.class);
        startActivity(in);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            vehicleBinding.civProfile.setImageURI(imageUri);
        }

        vehicleBinding.ivBackErrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}*/


/*
import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.transportervendor.Api.VehicleService;
import com.transportervendor.Bean.Vehicle;
import com.transportervendor.databinding.AddVehicleActivityBinding;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleActivity extends AppCompatActivity {

    AddVehicleActivityBinding binding;
    String currentUserId;
    Uri imgUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=AddVehicleActivityBinding.inflate(LayoutInflater.from(AddVehicleActivity.this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.addVehicleToolBar);
        currentUserId = FirebaseAuth.getInstance().getUid();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==222 && resultCode==RESULT_OK){
            imgUri=data.getData();
        }
        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data!=null ) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
                binding.civProfile.setImageURI(imgUri);

            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.civProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(AddVehicleActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddVehicleActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},111);
                }
                else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 222);
                }

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddVehicleActivity.this);
            }
        });
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtility.checkInternetConnection(AddVehicleActivity.this)) {
                    String etVehiclecategory = binding.etVehicleType.getText().toString();
                    if (etVehiclecategory.isEmpty()) {
                        binding.etVehicleType.setError("this field can't be empty.");
                        return;
                    }
                    String etcount = binding.etvehicleCount.getText().toString();
                    if (etcount.isEmpty()) {
                        binding.etvehicleCount.setError("this field can't be empty.");
                        return;
                    }
                    String token = "43434343";
                    if (imgUri != null) {
                        Toast.makeText(AddVehicleActivity.this, "image not null", Toast.LENGTH_SHORT).show();
                        File file = FileUtils.getFile(AddVehicleActivity.this, imgUri);
                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse(getContentResolver().getType(imgUri)),
                                        file
                                );
                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        RequestBody name =RequestBody.create(okhttp3.MultipartBody.FORM,etVehiclecategory);
                        RequestBody count=RequestBody.create(okhttp3.MultipartBody.FORM, etcount);
                        RequestBody  transporterId=RequestBody.create(MultipartBody.FORM, FirebaseAuth.getInstance().getCurrentUser().getUid());

                        VehicleService.VehicleApi vehicleApi = VehicleService.getVehicleApiInstance();
                        Call<Vehicle> call = vehicleApi.createVehicle(body,name,count,transporterId);
                        call.enqueue(new Callback<Vehicle>() {
                            @Override

                            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                                Log.e("response","abc");
                                Log.e("show",""+response.body());
                                if (response.code() == 200)
                                    finish();
                            }

                            @Override
                            public void onFailure(Call<Vehicle> call, Throwable t) {
                                Toast.makeText(AddVehicleActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("show",""+t.getMessage());
                            }
                        });

                    } else
                        Toast.makeText(AddVehicleActivity.this, "Please select vehicle image.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddVehicleActivity.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        binding.edit.setOnClickListener(new View.OnClickListener() {
//               @Override
//                        public void onClick(View v) {
//                         if(ActivityCompat.checkSelfPermission(AddVehicleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PermissionChecker.PERMISSION_GRANTED){
//                             ActivityCompat.requestPermissions(AddVehicleActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},11);
//                         }

        binding.ivBackErrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
*/