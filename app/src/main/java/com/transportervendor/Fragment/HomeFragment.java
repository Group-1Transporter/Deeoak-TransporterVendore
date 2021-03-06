package com.transportervendor.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.transportervendor.Adapter.CurrentLeadsAdapter;
import com.transportervendor.Api.LeadsService;
import com.transportervendor.Bean.BidWithLead;
import com.transportervendor.CustomProgressDialog;
import com.transportervendor.NetworkUtility;
import com.transportervendor.databinding.FragmentHomeBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    FragmentHomeBinding fragment;
    CustomProgressDialog pd;

     RecyclerView.Adapter<CurrentLeadsAdapter.CurrentLeadsViewHolder>adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final CustomProgressDialog pd=new CustomProgressDialog(getContext(),"Please wait...");
        pd.show();
        fragment=FragmentHomeBinding.inflate(LayoutInflater.from(getActivity()));
        View v=fragment.getRoot();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(NetworkUtility.checkInternetConnection(getContext())) {

        LeadsService.LeadsApi leadApi =LeadsService.getLeadsApiInstance();
        Call<ArrayList<BidWithLead>> call = leadApi.getCurrentLeads(FirebaseAuth.getInstance().getCurrentUser().getUid());

            call.enqueue(new Callback<ArrayList<BidWithLead>>() {
                @Override
                public void onResponse(Call<ArrayList<BidWithLead>> call, Response<ArrayList<BidWithLead>> response) {
                    pd.dismiss();
                    if(response.code()==200) {
                        ArrayList<BidWithLead> al = response.body();
                        if(al==null)
                            al=new ArrayList<>();
                        CurrentLeadsAdapter adapter = new CurrentLeadsAdapter(getContext(), al);
                        fragment.rv.setAdapter(adapter);
                        fragment.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                        SharedPreferences mPrefs= getActivity().getSharedPreferences("Transporter",MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        prefsEditor.putString("pending",al.size()+"");
                        prefsEditor.commit();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<BidWithLead>> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else
            Toast.makeText(getContext(), "Please enable internet connection.", Toast.LENGTH_SHORT).show();
    }
}