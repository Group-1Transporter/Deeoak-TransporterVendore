/*package com.transportervendor.Api;

import com.transportervendor.Bean.Transporter;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class VehicleService {
    public static VehicleService.VehicleApi vehicleApi=null;
    public static VehicleService.VehicleApi getVehicleApiInstance(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(ServerAddress.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if(vehicleApi==null){
            vehicleApi=retrofit.create(VehicleService.VehicleApi.class);
        }
        return vehicleApi;
    }
    public interface VehicleApi{
        @Multipart
        @POST("/vehicle/")
        public Call<Transporter> createVehicle(@Part("name") RequestBody name,
                                               @Part("count") RequestBody count,
                                               @Part("transporterId") RequestBody transporterId,
                                               @Part MultipartBody.Part file
        );
        @DELETE("/vehicle/{id}/{transporterId}")
        public Call<Transporter> deleteVehicle(@Path("id") String id,
                                               @Path("transporterId") String transporterId
        );
        @POST("/vehicle/update")
        public Call<Transporter> updateVehicle(@Body Transporter transporter);

        @Multipart
        @POST("/vehicle/update/image")
        public Call<Transporter> updateImage(@Part("id") RequestBody id,
                                             @Part("transporterId") RequestBody transporterId,
                                             @Part MultipartBody.Part file);
    }
}
*/
package com.transportervendor.Api;

import com.transportervendor.Bean.Transporter;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class VehicleService {
    public static VehicleService.VehicleApi vehicleApi=null;
    public static VehicleService.VehicleApi getVehicleApiInstance(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(ServerAddress.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if(vehicleApi==null){
            vehicleApi=retrofit.create(VehicleService.VehicleApi.class);
        }
        return vehicleApi;
    }
    public interface VehicleApi{
        @Multipart
        @POST("/vehicle/")
        public Call<Transporter> createVehicle(@Part("name") RequestBody name,
                                           @Part("count") RequestBody count,
                                           @Part("transporterId") RequestBody transporterId,
                                           @Part MultipartBody.Part file
        );
        @DELETE("/vehicle/{id}/{transporterId}")
        public Call<Transporter> deleteVehicle(@Path("id") String id,
                                               @Path("transporterId") String transporterId
        );
        @POST("/vehicle/update")
        public Call<Transporter> updateVehicle(@Body Transporter transporter);

        @Multipart
        @POST("/vehicle/update/image")
        public Call<Transporter> updateImage(@Part("id") RequestBody id,
                                             @Part("transporterId") RequestBody transporterId,
                                             @Part MultipartBody.Part file);

    }
}