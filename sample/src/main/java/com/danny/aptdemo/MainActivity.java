package com.danny.aptdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.danny.wormhole.Wormhole;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testWormhole(View view) {
        Wormhole.getDefault().create(InterfaceDeclaration.class).interfaceTest();
    }
}