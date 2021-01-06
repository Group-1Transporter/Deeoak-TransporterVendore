package com.transportervendor.Adapter;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.transportervendor.Api.VehicleService;
import com.transportervendor.Bean.Transporter;
import com.transportervendor.Bean.Vehicle;
import com.transportervendor.CustomProgressDialog;
import com.transportervendor.EditVehicle;
import com.transportervendor.NetworkUtility;
import com.transportervendor.databinding.VehicleListBinding;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageVehicleAdapter extends RecyclerView.Adapter<ManageVehicleAdapter.VehicleListViewHolder> {
    ArrayList<Vehicle> vlist;
    Context context;
    public ManageVehicleAdapter(Context context){
        this.context= context;
    }

    public ManageVehicleAdapter(ArrayList<Vehicle> vList) {
        this.vlist = vList;
    }


    @NonNull
    @Override
    public VehicleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VehicleListBinding binding= VehicleListBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VehicleListViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull final VehicleListViewHolder holder, int position) {
        final Vehicle vehicle = vlist.get(position);
        Picasso.get().load(vehicle.getImgUrl()).into(holder.vehicleListBinding.ivVehicle);
        holder.vehicleListBinding.tvVehicleName.setText(vehicle.getName());
        holder.vehicleListBinding.tvVehicleCount.setText(""+vehicle.getCount());

        holder.vehicleListBinding.ivMoreVert.setOnClickListener(new View.OnClickListener() {
  /*          @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(),holder.vehicleListBinding.ivMoreVert);
                Menu menu = popupMenu.getMenu();
                menu.add("Delete");
                menu.add("Update");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.equals("Delete")){

                        }else if (menuItem.equals("Update")){

                        }
                        return true;
                    }
                });
            }
        });
    }
*/
     @Override

            public void onClick(final View v) {

                PopupMenu popup = new PopupMenu( context, holder.vehicleListBinding.ivMoreVert);
                Menu menu=popup.getMenu();
                menu.add("Edit");
                menu.add("Delete");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title=item.getTitle().toString();
                        if(title.equalsIgnoreCase("Edit")){
                            Intent in=new Intent(context, EditVehicle.class);
                            in.putExtra("vehicle", (Serializable) vehicle);
                            context.startActivity(in);
                        }else if (title.equalsIgnoreCase("Delete")){
                            if(NetworkUtility.checkInternetConnection(context)){
                                VehicleService.VehicleApi vehicleApi=VehicleService.getVehicleApiInstance();
                                Call<Transporter> call=vehicleApi.deleteVehicle(vehicle.getVehicelId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                final CustomProgressDialog pd=new CustomProgressDialog(context,"Please wait...");
                                pd.show();
                                call.enqueue(new Callback<Transporter>() {
                                    @Override
                                    public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                        pd.dismiss();
                                        if(response.isSuccessful()){
                                            SharedPreferences mPrefs =  context.getSharedPreferences("Transporter",Context.MODE_PRIVATE);
                                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(response.body());
                                            prefsEditor.putString("Transporter", json);
                                            prefsEditor.commit();
                                            Toast.makeText(context, "vehicle deleted.", Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();
                                        }else{
                                            Gson gson = new Gson();
                                            String json = gson.toJson(response.body());
                                            Toast.makeText(context, response.code()+"", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                      @Override
                                    public void onFailure(Call<Transporter> call, Throwable t) {
                                        pd.dismiss();
                                        Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return vlist.size();
    }

    public class VehicleListViewHolder extends RecyclerView.ViewHolder {
        VehicleListBinding vehicleListBinding;
        public VehicleListViewHolder(VehicleListBinding vehicleListBinding) {
            super(vehicleListBinding.getRoot());
            this.vehicleListBinding = vehicleListBinding;
        }
    }
}
/*

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.transportervendor.Api.VehicleService;
import com.transportervendor.Bean.Transporter;
import com.transportervendor.Bean.Vehicle;
import com.transportervendor.CustomProgressDialog;
import com.transportervendor.EditVehicle;
import com.transportervendor.NetworkUtility;
import com.transportervendor.databinding.VehicleListBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageVehicleAdapter extends RecyclerView.Adapter<ManageVehicleAdapter.ManageVehicleViewHolder> {
    Context context;
    ArrayList<Vehicle>al;

    public ManageVehicleAdapter(Context context, ArrayList<Vehicle> al) {
        this.context = context;
        this.al = al;
    }

    @NonNull
    @Override
    public ManageVehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VehicleListBinding binding=VehicleListBinding.inflate(LayoutInflater.from(context));
        return new ManageVehicleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ManageVehicleViewHolder holder, int position) {
        final Vehicle vehicle=al.get(position);
        holder.binding.count.setText("   "+vehicle.getCount());
        holder.binding.name.setText(vehicle.getName());
        Picasso.get().load(vehicle.getImgUrl()).into(holder.binding.pic);
        holder.binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.binding.more);
                Menu menu=popup.getMenu();
                menu.add("Edit");
                menu.add("Delete");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title=item.getTitle().toString();
                        if(title.equalsIgnoreCase("Edit")){
                            Intent in=new Intent(context, EditVehicle.class);
                            in.putExtra("vehicle", (Parcelable) vehicle);
                            context.startActivity(in);
                        }else if (title.equalsIgnoreCase("Delete")){
                            if(NetworkUtility.checkInternetConnection(context)){
                                VehicleService.VehicleApi vehicleApi=VehicleService.getVehicleApiInstance();
                                Call<Transporter>call=vehicleApi.deleteVehicle(vehicle.getVehicelId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                final CustomProgressDialog pd=new CustomProgressDialog(context,"Please wait...");
                                pd.show();
                                call.enqueue(new Callback<Transporter>() {
                                    @Override
                                    public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                        pd.dismiss();
                                        if(response.isSuccessful()){
                                            SharedPreferences mPrefs =  context.getSharedPreferences("Transporter",Context.MODE_PRIVATE);
                                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(response.body());
                                            prefsEditor.putString("Transporter", json);
                                            prefsEditor.commit();
                                            Toast.makeText(context, "vehicle deleted.", Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();
                                        }else{
                                            Gson gson = new Gson();
                                            String json = gson.toJson(response.body());
                                            Toast.makeText(context, response.code()+"", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Transporter> call, Throwable t) {
                                        pd.dismiss();
                                        Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class ManageVehicleViewHolder extends RecyclerView.ViewHolder{
        VehicleListBinding binding;
        public ManageVehicleViewHolder(@NonNull VehicleListBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}*/