package com.numeris_ci.dailyfortune;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import app.AppController;

public class FortuneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fortune);
        MyPreferences pref = new MyPreferences(FortuneActivity.this);
        String msg;
        if (pref.isFirstTime()) {
            msg = "Hi " + pref.getUserName();
            pref.setOld(true);
        } else {
            msg = "Welcome back " + pref.getUserName();
        }
        Toast.makeText(FortuneActivity.this, msg, Toast.LENGTH_LONG).show();

        //Read quotes from internet or from cache
        ConnetionDirector cd = new ConnetionDirector(this);
        if (cd.isConnectingToInternet()) {
            getFortuneOnline();
        } else {
            readFortuneFromFile();
        }
    }

    private void getFortuneOnline() {
        //Set the fortune text to loading
        final TextView fortuneText = (TextView) findViewById(R.id.fortune);
        fortuneText.setText("Loading...");
        //Create an instance for the request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://api.icndb.com/jokes/random",
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                        String fortune;
                        //Parse the quote
                        try {
                            fortune = response.getString("value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            fortune = "Error";
                        }
                        //Set the fortune text to the parsed quote
                        fortuneText.setText(fortune);
                        writeToFile(fortune);
                    }
                }, new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("Response", "Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
        });
        //Add request to request queue
        AppController.getInstance().addToRequestQueue(request);
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("Fortune.json", MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Message:", "File write failed: " + e.toString());
        }
    }

    private void readFortuneFromFile() {
        String fortune = "";
        try {
            InputStream inputStream = openFileInput("Fortune.json");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferReader = new BufferedReader(inputStreamReader);
                String receivingString = "";
                StringBuilder stringBuilder = new StringBuilder();
                Log.v("Message:", "reading...");
                while ((receivingString= bufferReader.readLine()) != null) {
                    stringBuilder.append(receivingString);
                }
                inputStream.close();
                fortune = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("Message:", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Message:", "Cannot read the file: " + e.toString());
        }
        TextView fortuneTxt = (TextView) findViewById(R.id.fortune);
        fortuneTxt.setText(fortune);
    }
}
