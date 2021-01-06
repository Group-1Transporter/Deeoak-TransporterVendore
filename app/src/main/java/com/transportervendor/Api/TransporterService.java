package com.transportervendor.Api;


import com.transportervendor.Bean.Transporter;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class TransporterService {
  public static TransportApi transportApi;

  public static TransportApi getTransporterApiIntance(){
      Retrofit retrofit = new Retrofit.Builder()
              .baseUrl(ServerAddress.BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
      if(transportApi == null)
         transportApi = retrofit.create(TransportApi.class);
      return transportApi;
  }
  public interface TransportApi{
     // api end points
     @GET("/transporter/")
     public Call<Transporter> getTransporterList();

     @GET("/transporter/{id}")
     public Call<Transporter> getTransporterById(@Path("id") String id);

      @POST("/transporter/update")
      public Call<Transporter> updateTransporter(@Body Transporter transporter);

      @Multipart
      @POST("/transporter/")
      public Call<Transporter> saveTransporter(
              @Part MultipartBody.Part file,
              @Part("type") RequestBody type,
              @Part("name") RequestBody name,
              @Part("contactNumber") RequestBody contactNumber,
              @Part("address") RequestBody address,
              @Part("aadharCardNumber") RequestBody aadharCardNumber,
              @Part("gstNumber") RequestBody gstNumber,
              @Part("token") RequestBody token,
              @Part("rating") RequestBody rating);
  }
}
