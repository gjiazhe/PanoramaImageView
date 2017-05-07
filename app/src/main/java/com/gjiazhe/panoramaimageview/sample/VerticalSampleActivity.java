package com.gjiazhe.panoramaimageview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gjiazhe.panoramaimageview.PanoramaImageView;
import com.gjiazhe.panoramaimageview.SensorObserver;

public class VerticalSampleActivity extends AppCompatActivity {

    private SensorObserver sensorObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_sample);

        sensorObserver = new SensorObserver();

        PanoramaImageView panoramaImageView = (PanoramaImageView) findViewById(R.id.panorama_image_view);
        panoramaImageView.setSensorObserver(sensorObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorObserver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorObserver.unregister();
    }
}
