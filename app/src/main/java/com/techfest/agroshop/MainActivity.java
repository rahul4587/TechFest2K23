package com.techfest.agroshop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.techfest.agroshop.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.TextView.setText("Rith");



    }
}