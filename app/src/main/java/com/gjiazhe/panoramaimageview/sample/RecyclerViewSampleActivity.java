package com.gjiazhe.panoramaimageview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.gjiazhe.panoramaimageview.GyroscopeObserver;
import com.gjiazhe.panoramaimageview.PanoramaImageView;

public class RecyclerViewSampleActivity extends AppCompatActivity {

    private GyroscopeObserver gyroscopeObserver;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sample);

        gyroscopeObserver = new GyroscopeObserver();

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new MyAdapter());
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

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(RecyclerViewSampleActivity.this).inflate(R.layout.item_list_sample, null);
                holder = new ViewHolder();
                holder.panoramaImageView = (PanoramaImageView) convertView.findViewById(R.id.panorama_image_view);
                holder.panoramaImageView.setGyroscopeObserver(gyroscopeObserver);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


//            holder.panoramaImageView.setImageDrawable();
            return convertView;
        }
    }

    private class ViewHolder {
        public PanoramaImageView panoramaImageView;
    }
}
