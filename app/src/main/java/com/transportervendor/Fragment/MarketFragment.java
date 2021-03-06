package com.transportervendor.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.transportervendor.Adapter.MarketLeadAdapter;
import com.transportervendor.Api.LeadsService;
import com.transportervendor.Api.StateService;
import com.transportervendor.Bean.Leads;
import com.transportervendor.Bean.State;
import com.transportervendor.CustomProgressDialog;
import com.transportervendor.Filter;
import com.transportervendor.FilterAdapter;
import com.transportervendor.NetworkUtility;
import com.transportervendor.databinding.DialogViewBinding;
import com.transportervendor.databinding.FragmentMarketBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketFragment extends Fragment {
    FragmentMarketBinding fragment;
    MarketLeadAdapter adapter;
    ArrayList<String> lid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment = FragmentMarketBinding.inflate(LayoutInflater.from(getContext()));
        View v = fragment.getRoot();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter = new MarketLeadAdapter(getContext(), new ArrayList<Leads>());
        final LeadsService.LeadsApi leadsApi = LeadsService.getLeadsApiInstance();
        Call<ArrayList<String>> call1 = leadsApi.getcurrentLeadsId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final Call<ArrayList<Leads>> call2 = leadsApi.getAllLeads();
        if (NetworkUtility.checkInternetConnection(getContext())) {
            final CustomProgressDialog pd=new CustomProgressDialog(getContext(),"Please wait...");
            pd.show();
            call2.enqueue(new Callback<ArrayList<Leads>>() {
                @Override
                public void onResponse(Call<ArrayList<Leads>> call, Response<ArrayList<Leads>> response) {
                    pd.dismiss();
                    if (response.code() == 200) {
                        ArrayList<Leads> al = response.body();
                        fragment.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new MarketLeadAdapter(getContext(), al);
                        fragment.rv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Leads>> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            pd.show();
            call1.enqueue(new Callback<ArrayList<String>>() {
                @Override
                public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                    pd.dismiss();
                    if (response.code() == 200) {
                        lid = response.body();
                        if (lid == null)
                            lid = new ArrayList<>();
                        adapter.setLid(lid);
                        adapter.notifyDataSetChanged();
                        fragment.rv.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            fragment.filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DialogViewBinding alb = DialogViewBinding.inflate(LayoutInflater.from(getContext()));
                    StateService.StateApi stateApi;
                    stateApi = StateService.getStateApiInstance();
                    Call<ArrayList<com.transportervendor.Bean.State>> call = stateApi.getState();
                    alb.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    if (NetworkUtility.checkInternetConnection(getContext())) {
                        pd.show();
//
//                        call.enqueue(new Callback<ArrayList<State>>() {
//                            @Override
//                            public void onResponse(Call<ArrayList<State>> call, Response<ArrayList<State>> response) {
//                                pd.dismiss();
//                            if (response.isSuccessful()){
//                                ArrayList<State>al=response.body();
//                                FilterAdapter adapter = new FilterAdapter(getContext(),al);
//                                alb.rv.setAdapter(adapter);
//                              }
//                            }
//                            @Override
//                            public void onFailure(Call<ArrayList<State>> call, Throwable t) {
//                                 pd.dismiss();
//                                Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });

                        call.enqueue(new Callback<ArrayList<State>>() {
                            @Override
                            public void onResponse(Call<ArrayList<State>> call, Response<ArrayList<State>> response) {
                                pd.dismiss();
                                if (response.isSuccessful()) {
                                    ArrayList<State>al=response.body();
                                    FilterAdapter adapter = new FilterAdapter(getContext(),al);
                                    alb.rv.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<State>> call, Throwable t) {
                                pd.dismiss();
                                Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Please enable internet connection.", Toast.LENGTH_SHORT).show();
                    }
                    final AlertDialog ab = new AlertDialog.Builder(getContext()).create();
                    ab.setView(alb.getRoot());
                    alb.btncancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ab.dismiss();
                        }
                    });
                    alb.btnupdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!(alb.all.isChecked())) {
                                LeadsService.LeadsApi leadsApi1 = LeadsService.getLeadsApiInstance();
                                Call<ArrayList<Leads>> call = leadsApi1.getfilteredLeads(Filter.getInstance());
                                if (NetworkUtility.checkInternetConnection(getContext())) {
                                    final CustomProgressDialog pd=new CustomProgressDialog(getContext(),"Please wait...");
                                    pd.show();
                                    call.enqueue(new Callback<ArrayList<Leads>>() {
                                        @Override
                                        public void onResponse(Call<ArrayList<Leads>> call, Response<ArrayList<Leads>> response) {
                                            pd.dismiss();
                                            if (response.isSuccessful()) {
                                                adapter = new MarketLeadAdapter(getContext(), response.body());
                                                adapter.setLid(lid);
                                                fragment.rv.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                                ab.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ArrayList<Leads>> call, Throwable t) {
                                            pd.dismiss();
                                            Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else
                                    Toast.makeText(getContext(), "Please enable internet connection.", Toast.LENGTH_SHORT).show();
                            }else{
                                LeadsService.LeadsApi leadsApi1=LeadsService.getLeadsApiInstance();
                                Call<ArrayList<Leads>>call5=leadsApi1.getAllLeads();
                                if(NetworkUtility.checkInternetConnection(getContext())) {
                                    final CustomProgressDialog pd=new CustomProgressDialog(getContext(),"Please wait...");
                                    pd.show();
                                    call5.enqueue(new Callback<ArrayList<Leads>>() {
                                        @Override
                                        public void onResponse(Call<ArrayList<Leads>> call, Response<ArrayList<Leads>> response) {
                                            pd.dismiss();
                                            if (response.code() == 200) {
                                                ArrayList<Leads> al = response.body();
                                                fragment.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                                                adapter = new MarketLeadAdapter(getContext(), al);
                                                fragment.rv.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                                ab.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ArrayList<Leads>> call, Throwable t) {
                                            pd.dismiss();
                                            Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), "please enable internet connection.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    ab.show();
                }
            });
        } else
            Toast.makeText(getContext(), "Please enable internet connection.", Toast.LENGTH_SHORT).show();
    }
}