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

    String id;
    String name;
    String imageURL;
    String price;
    Double rating;
    Boolean open;

    public FoodItem(String name, String imageURL, String price, String id, Double rating, String open){
        this.name = name;
        this.imageURL = imageURL;
        this.price = price;
        this.id = id;
        this.rating = rating;

        if(open.equals("True")){
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

    public String getId(){return this.id;}
    public String getImageURL(){
        return this.imageURL;
    }
    public String getName(){return this.name;}
    public String getPrice() {return this.price;}
    public Double getRating() {return this.rating;}
    public Boolean getOpen() {return this.open;}

}
