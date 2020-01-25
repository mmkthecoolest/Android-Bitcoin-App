package com.example.codingtestpreview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    LineChart lineChart;

    public class DownloadHTML extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            //string... means multiple string params
            // Log.i("URL", strings[0]);
//            String result = "";//html content
            StringBuilder sb = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;//think of it like a browser window

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();//stream to hold the input of data
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader b = new BufferedReader(inputStreamReader);

//                int data = inputStreamReader.read();//keeps track of location of reader in HTML

//                while (data != -1){//data keeps increasing as it goes on and after finishing has value of -1
//                    char current = (char) data;
//                    result += current;
//
//                    data = inputStreamReader.read();
//                }

                for (String line; (line = b.readLine()) != null; ) {
                    sb.append(line).append("\n");
                }

                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                toaster("Cannot interpret JSON data", true);
                return "Failed";
            }


        }

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

        DownloadHTML downloader = new DownloadHTML();
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

        DownloadHTML downloader = new DownloadHTML();
        downloader.execute("https://www.bitstamp.net/api/v2/transactions/btcusd/");

        toaster("Refresh complete", false);
    }
}
