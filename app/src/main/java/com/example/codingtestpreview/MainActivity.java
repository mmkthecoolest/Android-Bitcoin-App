package com.example.codingtestpreview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static private LineChart lineChart;
    private final static String TAG = "MainActivity";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.bidask){
            Intent intent = new Intent(MainActivity.this, BidAndAsk.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    private static class DownloadHTMLMain extends DownloadHTML {


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("Result string", s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                ArrayList<Long> descendingUNIXDates = new ArrayList<>();
                //ArrayList<String> stringDates = new ArrayList<>();

                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
                ArrayList<Entry> buyingPrices = new ArrayList<>();
                ArrayList<Entry> sellingPrices = new ArrayList<>();

                for(int i = 0; i < jsonArray.length() ; i++){
                    descendingUNIXDates.add(jsonArray.getJSONObject(i).getLong("date") * 1000L);

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if(jsonObject.getInt("type") == 0){
                        buyingPrices.add(new Entry(Float.parseFloat(jsonArray.getJSONObject(i).getString("date")) - Float.parseFloat(jsonArray.getJSONObject(jsonArray.length() - 1).getString("date")),
                                Float.parseFloat(jsonArray.getJSONObject(i).getString("price"))));
                    }

                    else if(jsonObject.getInt("type") == 1){
                        sellingPrices.add(new Entry(Float.parseFloat(jsonArray.getJSONObject(i).getString("date")) - Float.parseFloat(jsonArray.getJSONObject(jsonArray.length() - 1).getString("date")),
                                Float.parseFloat(jsonArray.getJSONObject(i).getString("price"))));
                    }

                    //stringDates.add(sdf.format(descendingUNIXDates.get(i)));
                    System.out.println(i + ": Date: " + sdf.format(descendingUNIXDates.get(i)));
                    System.out.println(i + ": Timestamp: " + descendingUNIXDates.get(i) / 1000);
                    System.out.println(i + ": Price: " + jsonArray.getJSONObject(i).getString("price"));
                    System.out.println(i + ": Type: " + jsonObject.getString("type"));


                }

                ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
                LineDataSet lineDataSet1 = new LineDataSet(buyingPrices, "Buying Prices");
                lineDataSet1.setColor(Color.GREEN);
                lineDataSet1.setDrawCircles(false);

                LineDataSet lineDataSet2 = new LineDataSet(sellingPrices, "Selling Prices");
                lineDataSet2.setColor(Color.RED);
                lineDataSet2.setDrawCircles(false);
                System.out.println("Line Data set complete");

                lineDataSets.add(lineDataSet1);
                lineDataSets.add(lineDataSet2);

                //Collections.reverse(stringDates);

                String[] xvals = new String[jsonArray.getJSONObject(0).getInt("date") - jsonArray.getJSONObject(jsonArray.length() - 1).getInt("date")];

                for(int i = 0; i < xvals.length; i++){
                    xvals[i] = sdf.format((jsonArray.getJSONObject(jsonArray.length() - 1).getLong("date") + i) * 1000);
                }

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xvals));
                xAxis.setLabelCount(3);

                lineChart.setData(new LineData(lineDataSets));
                lineChart.setPinchZoom(true);
                lineChart.invalidate();


            } catch (JSONException e) {
                e.printStackTrace();
            }


            //Log.i("Website result:", s);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = (LineChart) findViewById(R.id.line_chart);

        DownloadHTMLMain downloader = new DownloadHTMLMain();
        downloader.execute("https://www.bitstamp.net/api/v2/transactions/btcusd/");
    }

    private void toaster(String string, boolean longToast){
        if (longToast){
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        }
        //getApplicationContext() gets context of app
    }

    public void refresh(View view){
        lineChart.clear();

        DownloadHTMLMain downloader = new DownloadHTMLMain();
        downloader.execute("https://www.bitstamp.net/api/v2/transactions/btcusd/");

        toaster("Refresh complete", false);
    }
}
