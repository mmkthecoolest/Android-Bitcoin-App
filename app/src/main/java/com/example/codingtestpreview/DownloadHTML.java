package com.example.codingtestpreview;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
            return "Failed";
        }


    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
