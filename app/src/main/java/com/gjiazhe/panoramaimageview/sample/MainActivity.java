package com.gjiazhe.panoramaimageview.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openRecyclerViewSample(View view) {
        startActivity(new Intent(this, RecyclerViewSampleActivity.class));
    }

    public void openVerticalSample(View view) {
        startActivity(new Intent(this, VerticalSampleActivity.class));
    }

    public void openHorizontalSample(View view) {
        startActivity(new Intent(this, HorizontalSampleActivity.class));
    }
}
