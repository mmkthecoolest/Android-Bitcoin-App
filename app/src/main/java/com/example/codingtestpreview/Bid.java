package com.example.codingtestpreview;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Bid extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private class DownloadHTMLBid extends DownloadHTML{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                JSONObject jsonObject = new JSONObject(s);
                JSONArray bids = jsonObject.getJSONArray("bids");

                ArrayList<ListEntry> list = new ArrayList<>();

                for(int i = 0; i < bids.length(); i++){
                    String price = "Price: $" + bids.getJSONArray(i).getString(0);
                    String amount = "Amount: $" + bids.getJSONArray(i).getString(1);

                    list.add(new ListEntry(price, amount));
                }

                mAdapter = new EntryAdapter(list);
                mRecyclerView.setAdapter(mAdapter);//set this once done with list
            } catch (JSONException e){
                e.printStackTrace();
                toaster("Error occurred", true);
            }
        }
    }


    public Bid() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = getView().findViewById(R.id.bid_recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);

        DownloadHTMLBid downloader = new DownloadHTMLBid();
        downloader.execute("https://www.bitstamp.net/api/v2/order_book/btcusd/");
    }

    private void toaster(String string, boolean longToast){
        if (longToast){
            Toast.makeText(getContext(), string, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
        }
        //getApplicationContext() gets context of app
    }
}
