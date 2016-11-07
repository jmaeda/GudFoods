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

    String name;
    String imageURL;
    String price;

    public FoodItem(String name, String imageURL, String price){
        this.name = name;
        this.imageURL = imageURL;
        this.price = price;
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

    public String getImageURL(){
        return this.imageURL;
    }
    public String getName(){return this.name;}
    public String getPrice() {return this.price;}

}
