package edu.brandeis.cs.moseskim.gudfoods;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by moseskim on 10/16/16.
 */
public class CustomAdapter extends ArrayAdapter<FoodItem> {


    ArrayList<FoodItem> entries;

    public CustomAdapter(Context context, ArrayList<FoodItem> array){
        super(context,R.layout.food_item,array);
        this.entries = array;
    }



    @Override
    public View getView(int position, View convertview, ViewGroup parent){
        NetworkImageView image;

        FoodItem item = getItem(position);


        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.food_item, null, true);
        }

        image = (NetworkImageView) convertview.findViewById(R.id.thumbnail);
        image.setImageUrl(item.getImageURL(), AppController.getInstance().getImageLoader());

        TextView nameAndPrice = (TextView) convertview.findViewById(R.id.nameAndPrice);
        nameAndPrice.setText("" + item.getName() + " " + item.getPrice());


        return convertview;


    }



}
