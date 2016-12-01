package edu.brandeis.cs.moseskim.gudfoods;

/**
 * Created by Jon on 11/15/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManager;
import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManagerTaskResult;
import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManagerType;
import edu.brandeis.cs.moseskim.gudfoods.aws.FoodItem_Dynamo;
import edu.brandeis.cs.moseskim.gudfoods.aws.UserSwipe_Dynamo;

public class SwipedListFragment extends Fragment {

    private static final String TAG = "SwipedListFragment";

    private View rootView;
    private static ListView listView;
    private String username;
    private ProgressDialog progressDialog;
    private Spinner sortBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "CreateView");
        rootView = inflater.inflate(R.layout.swiped_list_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView2);
        sortBy = (Spinner) rootView.findViewById(R.id.sort_spinner);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        username = getArguments().getString("username");
        new DynamoDBSwipedListTask().execute(DynamoDBManagerType.LIST_USERS_SWIPES);

        ArrayAdapter<CharSequence> sortByAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.sortByArray , android.R.layout.simple_spinner_item);
        sortBy.setAdapter(sortByAdapter);
        sortBy.setSelection(0);
        sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (listView.getAdapter() != null) {
                    String text = sortBy.getSelectedItem().toString();
                    SwipedCustomAdapter listAdapter = (SwipedCustomAdapter) listView.getAdapter();
                    if (text.equals(getString(R.string.sort_by_name))) {
                        listAdapter.sortBy(MyListSortOrder.NAME);
                    } else if (text.equals(getString(R.string.sort_by_price_lo_hi))) {
                        listAdapter.sortBy(MyListSortOrder.PRICE_LO_TO_HI);
                    } else if (text.equals(getString(R.string.sort_by_price_hi_lo))) {
                        listAdapter.sortBy(MyListSortOrder.PRICE_HI_TO_LO);
                    } else if (text.equals(getString(R.string.sort_by_rating))) {
                        listAdapter.sortBy(MyListSortOrder.RATING);
                    } else if (text.equals(getString(R.string.sort_by_distance))) {
                        listAdapter.sortBy(MyListSortOrder.DISTANCE);
                    }
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        return rootView;
    }

    public static void addToList(Activity activity, FoodItem_Dynamo fi) {
        final FoodItem_Dynamo foodItem_dynamo = fi;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((SwipedCustomAdapter) listView.getAdapter()).addFoodItem(foodItem_dynamo);
                ((SwipedCustomAdapter) listView.getAdapter()).notifyDataSetChanged();

            }
        });
    }

    public class DynamoDBSwipedListTask extends
            AsyncTask<DynamoDBManagerType, Void, DynamoDBManagerTaskResult> {

        protected DynamoDBManagerTaskResult doInBackground(
                DynamoDBManagerType... types) {

            String tableStatus = DynamoDBManager.getTestTableStatus();

            DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(types[0]);

            if (types[0] == DynamoDBManagerType.LIST_USERS_SWIPES) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    List<UserSwipe_Dynamo> userSwipes = DynamoDBManager.listUserSwipeRights(username);
                    Map<UserSwipe_Dynamo, FoodItem_Dynamo> foodMap = DynamoDBManager.listFoodItems(userSwipes);
                    final List<FoodItem_Dynamo> visibleList = new LinkedList<>();
                    for (UserSwipe_Dynamo u : foodMap.keySet()) {
                        if (u.isSwipeRight() && !u.isDeleted()) {
                            visibleList.add(foodMap.get(u));
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (visibleList != null) {
                                SwipedCustomAdapter swipedCustomAdapter = new SwipedCustomAdapter(getContext(), visibleList, username);
                                listView.setAdapter(swipedCustomAdapter);
                            }
                        }
                    });
                }
            }

            return result;
        }

        protected void onPostExecute(DynamoDBManagerTaskResult result) {
            if (result.getTaskType() == DynamoDBManagerType.LIST_USERS_SWIPES
                    && result.getTableStatus().equalsIgnoreCase("ACTIVE")) {
                progressDialog.dismiss();
            } else if (!result.getTableStatus().equalsIgnoreCase("ACTIVE")) {
                Toast.makeText(
                        SwipedListFragment.this.getActivity(),
                        "The test table is not ready yet.\nTable Status: "
                                + result.getTableStatus(), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

}
