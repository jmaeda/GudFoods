package edu.brandeis.cs.moseskim.gudfoods;

/**
 * Created by moseskim on 10/11/16.
 */
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

public class YelpService {

    public static void findRestaurants(String location, String token, Callback callback) {
        Log.d("Entering", "YelpService.findRestaurants");
        Log.d("Token", "Bearer " + token);

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.YELP_BASE_URL_V3).newBuilder();
        urlBuilder.addQueryParameter(Constants.YELP_LOCATION_QUERY_PARAMETER, location);
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }


    public ArrayList<FoodItem> getItems(Response response){
        ArrayList<FoodItem> fooditems = new ArrayList<>();
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject yelpJSON = new JSONObject(jsonData);
                JSONArray businessesJSON = yelpJSON.getJSONArray("businesses");
                for (int i = 0; i < businessesJSON.length(); i++) {
                    JSONObject restaurantJSON = businessesJSON.getJSONObject(i);
                    String name = restaurantJSON.getString("name");
                    //String phone = restaurantJSON.optString("display_phone", "Phone not available");
                    //String website = restaurantJSON.getString("url");
                    //double rating = restaurantJSON.getDouble("rating");
                    String imageUrl = restaurantJSON.getString("image_url");
                    String price = restaurantJSON.getString("price");
                    //double latitude = restaurantJSON.getJSONObject("location")
                            //.getJSONObject("coordinate").getDouble("latitude");
                    //double longitude = restaurantJSON.getJSONObject("location")
                            //.getJSONObject("coordinate").getDouble("longitude");
                    //ArrayList<String> address = new ArrayList<>();
                    //JSONArray addressJSON = restaurantJSON.getJSONObject("location")
                            //.getJSONArray("display_address");
                    //for (int y = 0; y < addressJSON.length(); y++) {
                        //address.add(addressJSON.get(y).toString());
                    //}

                    FoodItem item = new FoodItem(name, imageUrl, price);
                    fooditems.add(item);
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
            Log.d("Exception","moses");

        } catch (JSONException e) {
            Log.d("JSON", "error");
            e.printStackTrace();
        }



        return fooditems;

    }




}
