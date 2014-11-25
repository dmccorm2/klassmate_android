package com.example.daniel.myapplication;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Daniel on 11/11/2014.
 */
public class REST {
    public HttpURLConnection buildConnection(String link, String op) throws IOException {
        // might need to build cookie here
        HttpURLConnection conn = null;
        try {
            URL url = new URL(link);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod(op);
            conn.connect();
            return conn;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public int POST_JSON(JSONObject jsonParam, HttpURLConnection conn) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(jsonParam.toString());
        out.flush();
        out.close();
        Integer HttpResult = 1;
        StringBuilder sb = new StringBuilder();
        try {
        HttpResult = conn.getResponseCode();
        Log.w("HTTP Response Code", HttpResult.toString());

        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();

        }
        return HttpResult;
    }
}
