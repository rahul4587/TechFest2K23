package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.techfest.agroshop02.databinding.ActivityMainBinding;

import Models.PreferanceManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    PreferanceManager preferanceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferanceManager=new PreferanceManager(getApplicationContext());




        binding.TextView.setText("Hello");



    }
}