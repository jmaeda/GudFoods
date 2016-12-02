package edu.brandeis.cs.moseskim.gudfoods.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;
import java.util.Set;

import edu.brandeis.cs.moseskim.gudfoods.AppController;
import edu.brandeis.cs.moseskim.gudfoods.R;
import edu.brandeis.cs.moseskim.gudfoods.aws.dynamodb.DynamoDBManager;
import edu.brandeis.cs.moseskim.gudfoods.aws.dynamodb.DynamoDBManagerTaskResult;
import edu.brandeis.cs.moseskim.gudfoods.aws.dynamodb.DynamoDBManagerType;
import edu.brandeis.cs.moseskim.gudfoods.aws.dynamodb.model.FoodItem_Dynamo;

/**
 * Created by alexsuk on 11/30/16.
 */

public class TrendingCustomAdapter extends ArrayAdapter<FoodItem_Dynamo> {

    private static final String TAG = "TrendingCustomAdapter";

    private List<FoodItem_Dynamo> entries;
    private Set<FoodItem_Dynamo> alreadySwiped;
    private Context context;
    private String username;
    private boolean isSwipeRight;
    private FoodItem_Dynamo fi;

    public TrendingCustomAdapter(Context context, List<FoodItem_Dynamo> array, Set<FoodItem_Dynamo> alreadySwiped, String username){
        super(context,R.layout.trending_food_item,array);
        this.entries = array;
        this.alreadySwiped = alreadySwiped;
        this.context = context;
        this.username = username;
    }

    @Override
    public View getView(final int position, View convertview, ViewGroup parent){
        NetworkImageView image;
        final FoodItem_Dynamo item = getItem(position);

        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.trending_food_item, null, true);
        }

        TextView businessName = (TextView) convertview.findViewById(R.id.textView3);
        TextView rating = (TextView) convertview.findViewById(R.id.textView4);
        TextView price = (TextView) convertview.findViewById(R.id.textView5);
        final TextView swipeRightCount = (TextView) convertview.findViewById(R.id.swipeRightCount);

        businessName.setText(item.getBusinessName());
        rating.setText(Double.toString(item.getRating()));
        price.setText(item.getPrice());
        swipeRightCount.setText(item.getSwipeRightCount() + "");

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

        final ImageButton add = (ImageButton) convertview.findViewById(R.id.plus);
        if (alreadySwiped.contains(item)) {
            add.setOnClickListener(null);
            add.setImageResource(R.drawable.checkmark);
        } else {
            add.setImageResource(R.drawable.plus);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isSwipeRight = true;
                    new DynamoDBAddSwipeTask().execute(DynamoDBManagerType.INSERT_USER_SWIPE);
                    fi = item;

                    Toast.makeText(getContext(), context.getString(R.string.view_swiped_right, fi.getBusinessName()),
                            Toast.LENGTH_SHORT).show();
                    add.setImageResource(R.drawable.checkmark);
                    add.setOnClickListener(null);
                    swipeRightCount.setText(item.getSwipeRightCount() + 1 + "");

                }
            });
        }

        return convertview;
    }

    public class DynamoDBAddSwipeTask extends
            AsyncTask<DynamoDBManagerType, Void, DynamoDBManagerTaskResult> {

        protected DynamoDBManagerTaskResult doInBackground(
                DynamoDBManagerType... types) {

            String tableStatus = DynamoDBManager.getTestTableStatus();

            DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(types[0]);
            if (types[0] == DynamoDBManagerType.INSERT_USER_SWIPE) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    DynamoDBManager.incrementFoodItem(fi);
                    DynamoDBManager.insertUserSwipe(username, fi.getImageURL(), isSwipeRight);
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
