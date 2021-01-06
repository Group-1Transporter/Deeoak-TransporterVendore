package com.transportervendor;

import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.transportervendor.databinding.TermsConditionBinding;

public class TermsnConditions extends AppCompatActivity {
    TermsConditionBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=TermsConditionBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }
}