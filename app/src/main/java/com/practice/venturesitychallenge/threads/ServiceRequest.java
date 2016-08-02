package com.practice.venturesitychallenge.threads;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.practice.venturesitychallenge.callbacks.INetworkCallback;
import com.practice.venturesitychallenge.constants.Constants;
import com.practice.venturesitychallenge.model.ModelTO;
import com.practice.venturesitychallenge.parser.JSONHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by akshayaggarwal08 on 7/31/16.
 */
public class ServiceRequest extends AsyncTask<String, Void, String> {

    private INetworkCallback callback;
    private String url;
    private String method;
    private String dataModel;
    private final String TAG = getClass().getSimpleName();

    public ServiceRequest(String url,String method, String dataModel,INetworkCallback callback){
        this.callback = callback;
        this.url = url;
        this.method= method;
        this.dataModel = dataModel;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            URL u = new URL(url);

            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod(method);

            if(Constants.POST_REQUEST.equalsIgnoreCase(method)){
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(dataModel);

                writer.flush();
                writer.close();
                os.close();
            }

            con.connect();

            InputStream inputStream = con.getInputStream();
            String data = convertToString(inputStream);

            return data;

        } catch (Exception e){
            Log.d(TAG,e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callback.response(s);
    }

    private String convertToString(InputStream inputStream){

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String data = "";
        try{
            while((line = bufferedReader.readLine())!= null){
                data+= line;
            }
            inputStream.close();
        }catch(IOException ie){
            ie.printStackTrace();
        }
        return data;

    }

}
