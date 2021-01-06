package com.transportervendor;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.google.firebase.auth.FirebaseAuth;
import com.transportervendor.Api.TransporterService;
import com.transportervendor.Bean.Transporter;
import com.transportervendor.databinding.AcitivtyCreateProfileBinding;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProfileScreen extends AppCompatActivity {
    AcitivtyCreateProfileBinding binding;
    String transporterCategory = "";
    Uri imageUri;
    String currentUserId;
    SharedPreferences sp = null;
    boolean gst =false;
    boolean aadhar = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isInternetConnected = NetworkUtility.checkInternetConnection(CreateProfileScreen.this);
        sp = getSharedPreferences("transporter", MODE_PRIVATE);

        if (isInternetConnected) {
            binding = AcitivtyCreateProfileBinding.inflate(LayoutInflater.from(this));
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            currentUserId =currentUserId;
            setContentView(binding.getRoot());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
            }

            binding.civProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent();
                    in.setAction(Intent.ACTION_GET_CONTENT);
                    in.setType("image/*");
                    startActivityForResult(in, 1);
                }
            });

            binding.transporterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    transporterCategory = binding.transporterCategory.getSelectedItem().toString();
                    Toast.makeText(CreateProfileScreen.this, transporterCategory, Toast.LENGTH_SHORT).show();

                    if (transporterCategory.equalsIgnoreCase("Transporter company")) {
                        gst = true;
                        binding.etGstNo.setVisibility(View.VISIBLE);
                        aadhar = false;
                        binding.etAadharNo.setVisibility(View.INVISIBLE);
                    } else if (transporterCategory.equalsIgnoreCase("Truck owner")) {
                        gst = false;
                        aadhar = true;
                        binding.etGstNo.setVisibility(View.INVISIBLE);
                        binding.etAadharNo.setVisibility(View.VISIBLE);
                    } else if (transporterCategory.equalsIgnoreCase("Packer and movers")) {
                        gst = true;
                        aadhar = false;
                        binding.etGstNo.setVisibility(View.VISIBLE);
                        binding.etAadharNo.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            binding.btnCreatProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (imageUri != null) {
                    String gstNumber = binding.etGstNo.getText().toString();
                    if (gst && gstNumber.length() < 15) {
                        binding.etGstNo.setError("Enter valid GstNo");
                        return;
                    }
                    String aadharCardNumber = binding.etAadharNo.getText().toString();
                    if (aadhar && aadharCardNumber.length() < 12) {
                        binding.etAadharNo.setError("Enter valid aadhar number");
                        return;
                    }

                    String name = binding.etUserName.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        binding.etUserName.setError("User name is required");
                        return;
                    }
                    String address = binding.etAddress.getText().toString();
                    if (TextUtils.isEmpty(address)) {
                        binding.etAddress.setError("Address is required ");
                        return;
                    }

                    String contactNumber = binding.etContactNumber.getText().toString();
                    if (contactNumber.length() < 10) {
                        binding.etContactNumber.setError("Enter valid phone number");
                        return;
                    }
                    String rating = "";
                    String token = "43434343";


                        File file = FileUtils.getFile(CreateProfileScreen.this, imageUri);
                        RequestBody requestFile = RequestBody.create(
                                MediaType.parse(getContentResolver().getType(imageUri)), file);
                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        RequestBody transproterType = RequestBody.create(MultipartBody.FORM, transporterCategory);
                        RequestBody transporterName = RequestBody.create(MultipartBody.FORM, name);
                        RequestBody transproterContactNo = RequestBody.create(MultipartBody.FORM, contactNumber);
                        RequestBody transporterAddress = RequestBody.create(MultipartBody.FORM, address);
                        RequestBody transporterGstNo = RequestBody.create(MultipartBody.FORM, gstNumber);
                        RequestBody transporterAadharCardNo = RequestBody.create(MultipartBody.FORM, aadharCardNumber);
                        RequestBody transporterRating = RequestBody.create(MultipartBody.FORM, rating);
                        RequestBody transporterToken = RequestBody.create(MultipartBody.FORM, token);

                          TransporterService.TransportApi transportApi = TransporterService.getTransporterApiIntance();
                        Call<Transporter> call = transportApi.saveTransporter(body,
                                transproterType,
                                transporterName,
                                transproterContactNo,
                                transporterAddress,
                                transporterAadharCardNo,
                                transporterGstNo,
                                transporterToken,
                                transporterRating);
                        call.enqueue(new Callback<Transporter>() {
                            @Override
                            public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                if (response.code() == 200) {
                                    Transporter t = response.body();
                                    Toast.makeText(CreateProfileScreen.this, "Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CreateProfileScreen.this, ManageVehicleActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }

                            }

                            @Override
                            public void onFailure(Call<Transporter> call, Throwable t) {
                                Toast.makeText(CreateProfileScreen.this, "Failed : " + t, Toast.LENGTH_SHORT).show();
                            }

                        });

                    } else
                        Toast.makeText(CreateProfileScreen.this, "Please select profile pic", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(CreateProfileScreen.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Delete");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            binding.civProfile.setImageURI(imageUri);
        }

    }

    private void saveTransporterLocally(Transporter t) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", t.getName());
        editor.putString("contactNumber", t.getContactNumber());
        editor.putString("address", t.getAddress());
        editor.putString("type", t.getType());
        String gstNo = "";
        if (t.getGstNumber() != null)
            gstNo =     t.getGstNumber();
        editor.putString("gstNumber", gstNo);
        editor.putString("aadharNo",t.getAadharCardNumber());
        editor.putString("imageUrl", t.getImageUrl());
        editor.putString("transporterId", t.getTransporterId());
        editor.commit();
    }
}

