package edu.brandeis.cs.moseskim.gudfoods;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import retrofit2.Call;
//import retrofit2.Response;

import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.Business;

import java.util.HashMap;
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
import android.util.Log;





public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();


    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.browse);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("edu.brandeis.cs.moseskim.gudfoods.Browse"));



            }
        });



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


