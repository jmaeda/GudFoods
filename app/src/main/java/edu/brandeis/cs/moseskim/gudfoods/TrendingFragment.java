package edu.brandeis.cs.moseskim.gudfoods;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.brandeis.cs.moseskim.gudfoods.aws.dynamodb.DynamoDBManager;
import edu.brandeis.cs.moseskim.gudfoods.aws.dynamodb.DynamoDBManagerTaskResult;
import edu.brandeis.cs.moseskim.gudfoods.aws.dynamodb.DynamoDBManagerType;
import edu.brandeis.cs.moseskim.gudfoods.aws.dynamodb.model.FoodItem_Dynamo;
import edu.brandeis.cs.moseskim.gudfoods.adapters.TrendingCustomAdapter;
import edu.brandeis.cs.moseskim.gudfoods.aws.dynamodb.model.UserSwipe_Dynamo;

/**
 * Created by Jon on 11/16/2016.
 */
public class TrendingFragment extends Fragment {

    private static final String TAG = "TrendingFragment";

    public static final Integer LIMIT = new Integer(30);

    private View rootView;
    private ListView listView;
    private TextView loading;
    private String username;
    private ProgressDialog progressDialog;
    private ProgressBar progressCircle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Create View");
        rootView = inflater.inflate(R.layout.trending_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView2);
        progressCircle = (ProgressBar) rootView.findViewById(R.id.progressBar);
        loading = (TextView) rootView.findViewById(R.id.loading);
        username = getArguments().getString("username");


//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Loading...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
        progressCircle.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);


        new DynamoDBTrendingListTask().execute(DynamoDBManagerType.LIST_TRENDING);

        return rootView;
    }

    public class DynamoDBTrendingListTask extends
            AsyncTask<DynamoDBManagerType, Void, DynamoDBManagerTaskResult> {

        protected DynamoDBManagerTaskResult doInBackground(
                DynamoDBManagerType... types) {

            String tableStatus = DynamoDBManager.getTestTableStatus();

            DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(types[0]);

            if (types[0] == DynamoDBManagerType.LIST_TRENDING) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    final List<FoodItem_Dynamo> trendingFoodList = DynamoDBManager.listTrendingFoodItems();
                    List<UserSwipe_Dynamo> userSwipes = DynamoDBManager.listUserSwipeRights(username);
                    Map<UserSwipe_Dynamo, FoodItem_Dynamo> foodMap = DynamoDBManager.listFoodItems(userSwipes);
                    final Set<FoodItem_Dynamo> visibleList = new HashSet<>();
                    for (UserSwipe_Dynamo u : foodMap.keySet()) {
                        if (u.isSwipeRight()) {
                            visibleList.add(foodMap.get(u));
                        }
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (trendingFoodList != null) {
                                    TrendingCustomAdapter swipedCustomAdapter = new TrendingCustomAdapter(getContext(), trendingFoodList, visibleList, username);
                                    listView.setAdapter(swipedCustomAdapter);
                                }
                            }
                        });
                    }
                }
            }

            return result;
        }

        protected void onPostExecute(DynamoDBManagerTaskResult result) {
            if (result.getTaskType() == DynamoDBManagerType.LIST_TRENDING
                    && result.getTableStatus().equalsIgnoreCase("ACTIVE")) {
//                progressDialog.dismiss();
                progressCircle.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else if (!result.getTableStatus().equalsIgnoreCase("ACTIVE")) {
                Toast.makeText(
                        TrendingFragment.this.getActivity(),
                        "The test table is not ready yet.\nTable Status: "
                                + result.getTableStatus(), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

}
