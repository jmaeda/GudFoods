package edu.brandeis.cs.moseskim.gudfoods.aws;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import edu.brandeis.cs.moseskim.gudfoods.AppController;
import edu.brandeis.cs.moseskim.gudfoods.R;

/**
 * Created by alexsuk on 11/30/16.
 */

public class TrendingCustomAdapter extends ArrayAdapter<FoodItem_Dynamo> {


    private List<FoodItem_Dynamo> entries;
    private Context context;

    public TrendingCustomAdapter(Context context, List<FoodItem_Dynamo> array){
        super(context, R.layout.swiped_food_item,array);
        this.entries = array;
        this.context = context;
    }



    @Override
    public View getView(int position, View convertview, ViewGroup parent){
        NetworkImageView image;
        final FoodItem_Dynamo item = getItem(position);

        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.swiped_food_item, null, true);
        }
        Button findItem = (Button) convertview.findViewById(R.id.button);
        findItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String businessID = item.getBusinessId();
                String fullUrl = "https://www.yelp.com/biz/" + businessID;
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl));
                context.startActivity(i);
            }
        });



        image = (NetworkImageView) convertview.findViewById(R.id.thumbnail2);
        image.setImageUrl(item.getImageURL(), AppController.getInstance().getImageLoader());

        return convertview;
    }

    public void addFoodItem(FoodItem_Dynamo foodItemDynamo) {
        entries.add(foodItemDynamo);
    }

}
