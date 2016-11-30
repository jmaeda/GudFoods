package edu.brandeis.cs.moseskim.gudfoods;

/**
 * Created by moseskim on 10/11/16.
 */
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brandeis.cs.moseskim.gudfoods.aws.UserBusiness_Dynamo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YelpService {

    private double user_prefered_rating;
    private Map<String, Integer> imageIndexMap;

    public YelpService(){
        user_prefered_rating = 0;
        imageIndexMap = new HashMap<>();
    }

    public void setRating(String user_prefered_rating){
        this.user_prefered_rating = Double.parseDouble(user_prefered_rating);
    }

    public double getRating(){
        return user_prefered_rating;
    }

    public int getImageIndex(String businessId) {
        return imageIndexMap.get(businessId) != null ? imageIndexMap.get(businessId) : 0;
    }

    public void setImageIndex(String businessId, int imageIndex) {
        imageIndexMap.put(businessId, imageIndex);
    }

    public void setImageIndexMap(List<UserBusiness_Dynamo> listOfUserBusinessDynamo) {
        for (UserBusiness_Dynamo u : listOfUserBusinessDynamo) {
            imageIndexMap.put(u.getBusinessId(), u.getImageIndex());
        }
    }

    public static void findRestaurants(Double latitude, Double longitude, String token, Callback callback) {
        Log.d("Entering", "YelpService.findRestaurants");
        Log.d("Token", "Bearer " + token);

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.YELP_BASE_URL_V3).newBuilder();
        String lat = "" + latitude;
        String lon = "" + longitude;
        urlBuilder.addQueryParameter(Constants.YELP_LATITUDE_PARAMETER, lat);
        urlBuilder.addQueryParameter(Constants.YELP_LONGITUDE_PARAMETER, lon);
        urlBuilder.addQueryParameter(Constants.YELP_RADIUS_PARAMETER, "8046");
        urlBuilder.addQueryParameter(Constants.YELP_LIMIT_PARAMETER, "30");
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void advancedSearch(Double longitude, Double latitude, Boolean flag, String location, String price, String radius, String token, Callback callback) {
        Log.d("Token", "Bearer " + token);

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.YELP_BASE_URL_V3).newBuilder();

        if(flag == true){
            String lat = "" + latitude;
            String lon = "" + longitude;
            urlBuilder.addQueryParameter(Constants.YELP_LATITUDE_PARAMETER, lat);
            urlBuilder.addQueryParameter(Constants.YELP_LONGITUDE_PARAMETER, lon);
        } else {
            urlBuilder.addQueryParameter(Constants.YELP_LOCATION_PARAMETER,location);
        }

        urlBuilder.addQueryParameter(Constants.YELP_PRICE_PARAMETER, price);
        urlBuilder.addQueryParameter(Constants.YELP_RADIUS_PARAMETER, radius);
        urlBuilder.addQueryParameter(Constants.YELP_LIMIT_PARAMETER, "30");
        String url = urlBuilder.build().toString();

        Log.d("url", url);

        Request request= new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void pickImages(String id, String token, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.YELP_BASE_BUSINESS_URL_V3 + id).newBuilder();
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
                String id = yelpJSON.getString("id");
                String name = yelpJSON.getString("name");
                Double rating = yelpJSON.getDouble("rating");
                String price = yelpJSON.getString("price");
                JSONObject hours = (JSONObject) yelpJSON.getJSONArray("hours").get(0);
                String open = hours.getString("is_open_now");
                JSONArray pictures = yelpJSON.getJSONArray("photos");
                JSONObject coordinates = yelpJSON.getJSONObject("coordinates");
                Double longitude = coordinates.getDouble("longitude");
                Double latitude = coordinates.getDouble("latitude");
//                Log.d("YELP SERVICE", "" + getImageIndex(id) + " " + id);
                for(int i = getImageIndex(id); i < pictures.length(); i++){
                    if(user_prefered_rating > 0 && rating >= user_prefered_rating) {
                        FoodItem item = new FoodItem(name, pictures.get(i).toString(), price, id, rating, open, longitude, latitude);
                        fooditems.add(item);
                        if (i >= getImageIndex(id) + 2) {
                            break;
                        }
                    } else if (user_prefered_rating == 0) {
                        FoodItem item = new FoodItem(name, pictures.get(i).toString(), price, id, rating, open, longitude, latitude);
                        fooditems.add(item);
                        if (i >= getImageIndex(id) + 2) {
                            break;
                        }
                    }
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

    public ArrayList<String> getIDList(Response response){
        ArrayList<String> idList = new ArrayList<String>();
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject yelpJSON = new JSONObject(jsonData);
                JSONArray businessesJSON = yelpJSON.getJSONArray("businesses");
                for (int i = 0; i < businessesJSON.length(); i++) {
                    JSONObject restaurantJSON = businessesJSON.getJSONObject(i);
                    String id = restaurantJSON.getString("id");
                    idList.add(id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Exception","moses");
        } catch (JSONException e) {
            Log.d("JSON", "error");
            e.printStackTrace();
        }

        return idList;
    }



}
