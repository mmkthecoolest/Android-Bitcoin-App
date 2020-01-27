package com.example.codingtestpreview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BidAndAsk extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem bid, ask;
    public PageAdapter pageAdapter;

    private class DownloadHTMLOB extends DownloadHTML{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                JSONObject jsonObject = new JSONObject(s);
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
                Long timestamp = jsonObject.getLong("timestamp") * 1000;

                setTitle("Order Book (" + sdf.format(timestamp) + ")");

            } catch (JSONException e){
                e.printStackTrace();
                toaster("Error occurred", true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_and_ask);

        setTitle("Order Book");

        tabLayout = findViewById(R.id.tabs);
        bid = findViewById(R.id.bid);
        ask = findViewById(R.id.ask);
        viewPager = findViewById(R.id.view_pager);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        DownloadHTMLOB downloader = new DownloadHTMLOB();
        downloader.execute("https://www.bitstamp.net/api/v2/order_book/btcusd/");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                DownloadHTMLOB downloader_new = new DownloadHTMLOB();
                downloader_new.execute("https://www.bitstamp.net/api/v2/order_book/btcusd/");

                if(tab.getPosition() >= 0 && tab.getPosition() <= 1){
                    pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void toaster(String string, boolean longToast){
        if (longToast){
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        }
        //getApplicationContext() gets context of app
    }
}
