package edu.brandeis.cs.moseskim.gudfoods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

public class SwipeStackAdapter extends ArrayAdapter<FoodItem> {

    private ArrayList<FoodItem> foodEntries;

    public SwipeStackAdapter(Context context, ArrayList<FoodItem> array){
        super(context, R.layout.cards,array);
        this.foodEntries = array;
    }

    @Override
    public int getCount() {
        return foodEntries.size();
    }

    @Override
    public FoodItem getItem(int position) {
        return foodEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        NetworkImageView image;

        FoodItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cards, parent, false);
        }
        TextView starText = (TextView) convertView.findViewById(R.id.star_text);
        if(item.getRating()!=null) {
            starText.setText(item.getRating().toString());
        }
        image = (NetworkImageView) convertView.findViewById(R.id.textViewCard);
        image.setImageUrl(item.getImageURL(), AppController.getInstance().getImageLoader());

        TextView name = (TextView) convertView.findViewById(R.id.nameResturaunt);
        name.setText("" + item.getBusinessName());

        TextView priceRating = (TextView) convertView.findViewById(R.id.priceRating);
        priceRating.setText("" + item.getPrice());


        return convertView;
    }
}
