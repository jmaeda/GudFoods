package edu.brandeis.cs.moseskim.gudfoods;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import retrofit2.Call;
//import retrofit2.Response;

import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.Business;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;
import java.util.Set;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();


    Button button;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        token = null;

        button = (Button) findViewById(R.id.browse);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetYelpToken().execute();
                Intent intent = new Intent("edu.brandeis.cs.moseskim.gudfoods.Browse");
                if(token == null){
                    intent.putExtra("token", "null");
                } else {
                    intent.putExtra("token", token);
                }
                startActivity(intent);
            }
        });
    }

    private class GetYelpToken extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String yelpJsonStr = null;

            try {
                // Construct the URL
                URL url = new URL("https://api.yelp.com/oauth2/token");
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("grant_type", "client_credentials");
                parameters.put("client_id", Constants.V3_CLIENT_ID);
                parameters.put("client_secret", Constants.V3_CLIENT_SECRET);
                Set set = parameters.entrySet();
                Iterator i = set.iterator();
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> param : parameters.entrySet()) {
                    if (postData.length() != 0) {
                        postData.append('&');
                    }
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");


                // Create the request and open the connection
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Log.d("Response Code", "" + conn.getResponseCode());
                // Read the input stream into a String
                InputStream inputStream = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                if (sb.length() == 0) {
                    return null;
                }
                yelpJsonStr = sb.toString();
                return yelpJsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            } finally{
                if (conn != null) {
                    conn.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s == null) {
                Log.d("response", "error");
            } else {
                try {
                    JSONObject obj = new JSONObject(s);
                    String accessToken = obj.getString("access_token");
                    token = accessToken;
                } catch (JSONException e){
                    Log.d("JSONException", e.toString());
                }
                Log.d("response", token);
            }
        }
    }


//    private void getRestaurants(String location) {
//        final YelpService yelpService = new YelpService();
//        yelpService.findRestaurants(location, new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    String jsonData = response.body().string();
//                    Log.v(TAG, jsonData);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }
//
//
//
//
//    public String yelpRequest(){
//
//        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer("JIJrn2v9l-2K2UQf054WKg",
//                "At2aOs-aMO9C6JP30JoafbtKyvY");
//        consumer.setTokenWithSecret("jZKPporU87Vs3AeHixkQiTixlipuFO9_",
//                "MbXkex1meQOfYm1z7-22YrCkdVw");
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(new SigningInterceptor(consumer))
//                .build();
//
//
//        YelpAPIFactory apiFactory = new YelpAPIFactory("JIJrn2v9l-2K2UQf054WKg",
//                "At2aOs-aMO9C6JP30JoafbtKyvY", "jZKPporU87Vs3AeHixkQiTixlipuFO9_",
//                "MbXkex1meQOfYm1z7-22YrCkdVw");
//        YelpAPI yelpAPI = apiFactory.createAPI();
//
//
//
//
//        Map<String,String> params = new HashMap<>();
//
//        params.put("term", "korean");
//        params.put("limit", "3");
//        params.put("lang", "en");
//
//        Call<SearchResponse> call = yelpAPI.search("Boston",params);
//
//
//        try {
//            SearchResponse response = call.execute().body();
//            int totalNumberOfResult = response.total();
//
//            ArrayList<Business> businesses = response.businesses();
//            String businessName = businesses.get(0).name();
//            double rating = businesses.get(0).rating();
//
//            return businessName;
//        }
//        catch (Exception e){
//
//        }
//
//
//
//
//        return null;
//
//    }




}



