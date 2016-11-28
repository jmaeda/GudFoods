package edu.brandeis.cs.moseskim.gudfoods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import edu.brandeis.cs.moseskim.gudfoods.aws.FoodItem_Dynamo;

/**
 * Created by moseskim on 11/22/16.
 */
public class SwipedCustomAdapter extends ArrayAdapter<FoodItem_Dynamo> {


    private List<FoodItem_Dynamo> entries;

    public SwipedCustomAdapter(Context context, List<FoodItem_Dynamo> array){
        super(context,R.layout.swiped_food_item,array);
        this.entries = array;
    }



    @Override
    public View getView(int position, View convertview, ViewGroup parent){
        NetworkImageView image;
        FoodItem_Dynamo item = getItem(position);

        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.swiped_food_item, null, true);
        }

        image = (NetworkImageView) convertview.findViewById(R.id.thumbnail2);
        image.setImageUrl(item.getImageURL(), AppController.getInstance().getImageLoader());

        return convertview;
    }

    public void addFoodItem(FoodItem_Dynamo foodItemDynamo) {
        entries.add(foodItemDynamo);
        notifyDataSetChanged();
    }

}
