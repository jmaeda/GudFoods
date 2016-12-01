package edu.brandeis.cs.moseskim.gudfoods;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManager;
import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManagerTaskResult;
import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManagerType;
import edu.brandeis.cs.moseskim.gudfoods.aws.FoodItem_Dynamo;

/**
 * Created by Jon on 11/16/2016.
 */
public class TrendingFragment extends Fragment {

    public static final Integer LIMIT = new Integer(30);

    private View rootView;
    private ListView listView;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.trending_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView2);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

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
                    final List<FoodItem_Dynamo> foodList = DynamoDBManager.listTrendingFoodItems();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (foodList != null) {
                                    SwipedCustomAdapter swipedCustomAdapter = new SwipedCustomAdapter(getContext(), foodList);
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
                progressDialog.dismiss();
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
