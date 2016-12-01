package edu.brandeis.cs.moseskim.gudfoods;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManager;
import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManagerTaskResult;
import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManagerType;
import edu.brandeis.cs.moseskim.gudfoods.aws.FoodItem_Dynamo;

/**
 * Created by moseskim on 11/22/16.
 */
public class SwipedCustomAdapter extends ArrayAdapter<FoodItem_Dynamo> {


    private List<FoodItem_Dynamo> entries;
    private Context context;
    private String username;
    private String foodImageURL;

    public SwipedCustomAdapter(Context context, List<FoodItem_Dynamo> array, String username){
        super(context,R.layout.swiped_food_item,array);
        this.entries = array;
        this.context = context;
        this.username = username;
    }

    @Override
    public View getView(final int position, View convertview, ViewGroup parent){
        NetworkImageView image;
        final FoodItem_Dynamo item = getItem(position);

        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.swiped_food_item, null, true);
        }
        ImageButton findItem = (ImageButton) convertview.findViewById(R.id.button);
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
        if(item != null) {
            image.setImageUrl(item.getImageURL(), AppController.getInstance().getImageLoader());
        }
        //implment delete button
        ImageButton delete = (ImageButton) convertview.findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodImageURL = item.getImageURL();
                entries.remove(position);
                notifyDataSetChanged();
                new DynamoDBRemoveSwipeTask().execute(DynamoDBManagerType.REMOVE_USER_SWIPE);
            }
        });

        TextView businessName = (TextView) convertview.findViewById(R.id.textView3);
        TextView rating = (TextView) convertview.findViewById(R.id.textView4);
        TextView price = (TextView) convertview.findViewById(R.id.textView5);

        businessName.setText(item.getBusinessName());
        rating.setText(Double.toString(item.getRating()));
        price.setText(item.getPrice());



        return convertview;
    }

    public void addFoodItem(FoodItem_Dynamo foodItemDynamo) {
        entries.add(foodItemDynamo);
    }


    public class DynamoDBRemoveSwipeTask extends
            AsyncTask<DynamoDBManagerType, Void, DynamoDBManagerTaskResult> {

        protected DynamoDBManagerTaskResult doInBackground(
                DynamoDBManagerType... types) {

            String tableStatus = DynamoDBManager.getTestTableStatus();

            DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(types[0]);

            if (types[0] == DynamoDBManagerType.REMOVE_USER_SWIPE) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    Log.d("SWIPED DELETED", "foodImage " + foodImageURL + " " + username);
                    DynamoDBManager.deleteUserSwipe(username, foodImageURL);
                }
            }

            return result;
        }

        protected void onPostExecute(DynamoDBManagerTaskResult result) {
            if (result.getTaskType() == DynamoDBManagerType.LIST_USERS_SWIPES
                    && result.getTableStatus().equalsIgnoreCase("ACTIVE")) {

            }else if (!result.getTableStatus().equalsIgnoreCase("ACTIVE")) {

            }
        }
    }


}
