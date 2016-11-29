package edu.brandeis.cs.moseskim.gudfoods;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by moseskim on 10/16/16.
 */
public class FoodItem {

    private String businessId;
    private String businessName;
    private String imageURL;
    private String price;
    private Double latitude;
    private Double longitude;
    private Double rating;
    private Boolean open;

    public FoodItem(String name, String imageURL, String price, String id, Double rating, String open, Double longitude, Double latitude){
        this.businessName = name;
        this.imageURL = imageURL;
        this.price = price;
        this.businessId = id;
        this.rating = rating;
        this.longitude = longitude;
        this.latitude = latitude;

        if(open.equals("true")){
            this.open = true;
        } else {
            this.open = false;
        }
    }


    public Bitmap getBitmap(){
        try{
            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getBusinessId(){return this.businessId;}
    public String getImageURL(){
        return this.imageURL;
    }
    public String getBusinessName(){return this.businessName;}
    public String getPrice() {return this.price;}
    public Double getRating() {return this.rating;}
    public Double getLatitude() {return this.latitude;}
    public Double getLongitude() {return this.longitude;}
    public Boolean getOpen() {return this.open;}

}
