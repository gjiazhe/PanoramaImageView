package com.gjiazhe.panoramaimageview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gjiazhe.panoramaimageview.GyroscopeObserver;
import com.gjiazhe.panoramaimageview.PanoramaImageView;

public class RecyclerViewSampleActivity extends AppCompatActivity {

    private GyroscopeObserver gyroscopeObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_sample);

        gyroscopeObserver = new GyroscopeObserver();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        gyroscopeObserver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gyroscopeObserver.unregister();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_sample, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            switch (position % 3) {
                case 0 : holder.panoramaImageView.setImageResource(R.drawable.horizontal1); break;
                case 1 : holder.panoramaImageView.setImageResource(R.drawable.horizontal2); break;
                case 2 : holder.panoramaImageView.setImageResource(R.drawable.horizontal3); break;
            }
        }

        @Override
        public int getItemCount() {
            return 6;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            PanoramaImageView panoramaImageView;
            MyViewHolder(View itemView) {
                super(itemView);
                panoramaImageView = (PanoramaImageView) itemView.findViewById(R.id.panorama_image_view);
                panoramaImageView.setGyroscopeObserver(gyroscopeObserver);
            }
        }
    }

}
