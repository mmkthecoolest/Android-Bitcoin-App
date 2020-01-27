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
public class Ask extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private class DownloadHTMLAsk extends DownloadHTML{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                JSONObject jsonObject = new JSONObject(s);
                JSONArray asks = jsonObject.getJSONArray("asks");

                ArrayList<ListEntry> list = new ArrayList<>();

                for(int i = 0; i < asks.length(); i++){
                    String price = "Price: $" + asks.getJSONArray(i).getString(0);
                    String amount = "Amount: $" + asks.getJSONArray(i).getString(1);

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


    public Ask() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ask, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = getView().findViewById(R.id.ask_recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);

        DownloadHTMLAsk downloader = new DownloadHTMLAsk();
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
