package edu.brandeis.cs.moseskim.gudfoods;

/**
 * Created by Jon on 11/15/2016.
 */

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.brandeis.cs.moseskim.gudfoods.aws.AWSService;
import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManager;
import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManagerTaskResult;
import edu.brandeis.cs.moseskim.gudfoods.aws.DynamoDBManagerType;
import edu.brandeis.cs.moseskim.gudfoods.aws.FoodItem_Dynamo;
import edu.brandeis.cs.moseskim.gudfoods.aws.TemporaryPreferences;
import link.fls.swipestack.SwipeStack;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class BrowseFragment extends Fragment{

    private Button button;
    private Button advanced;
    private Button settings;
    private boolean refreshed = false;
    private ArrayList<String> idList = new ArrayList<String>();
    private ArrayList<FoodItem> entries = new ArrayList<FoodItem>();
    private ArrayList<FoodItem> entriesforUI = new ArrayList<FoodItem>();
    private ListView listView;
    private String token;
    private String username;
    private String rating;

    double latitude;
    double longitude;
    private View rootView;
    private YelpService yelpService;
    private AsyncTask getYelpToken;
    private ProgressDialog pDialog;
    private TemporaryPreferences temp;

    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 123;
    private MyLocationListener loc;
    private LocationManager locManager;

    private Callback entriesCallback;
    private Callback finalCallback;
    private Callback findRestaurantsCallback;

    private FoodItem fi;
    private boolean isSwipeRight;


    private ImageButton moreInfo;
    private Button mButtonLeft, mButtonRight;
    private FloatingActionButton mFab;
    private SwipeStack mSwipeStack;
    private SwipeStackAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TEST 1","TESTING");

        rootView = inflater.inflate(R.layout.browse_fragment, container, false);
        button = (Button) rootView.findViewById(R.id.browse);
        advanced = (Button) rootView.findViewById(R.id.advanced_search);
        yelpService = new YelpService();
        new DynamoDBImageSwipeTask().execute(DynamoDBManagerType.LIST_USER_BUSINESS_INDEX);

        moreInfo = (ImageButton) rootView.findViewById(R.id.info_button);
        mButtonLeft = (Button) rootView.findViewById(R.id.buttonSwipeLeft);
        mButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeStack.swipeTopViewToLeft();
            }
        });
        mButtonRight = (Button) rootView.findViewById(R.id.buttonSwipeRight);
        mButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSwipeStack.swipeTopViewToRight();
            }
        });

        mSwipeStack = (SwipeStack) rootView.findViewById(R.id.swipeStack);
        mSwipeStack.setListener(new SwipeStack.SwipeStackListener() {
            @Override
            public void onViewSwipedToLeft(int position) {
                FoodItem swipedElement = mAdapter.getItem(position);

                isSwipeRight = false;
                fi = swipedElement;
                new DynamoDBImageSwipeTask().execute(DynamoDBManagerType.INSERT_USER_SWIPE);


                Toast.makeText(getContext(), getString(R.string.view_swiped_left, swipedElement.getBusinessName()),
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onViewSwipedToRight(int position) {
                FoodItem swipedElement = mAdapter.getItem(position);

                isSwipeRight = true;
                new DynamoDBImageSwipeTask().execute(DynamoDBManagerType.INSERT_USER_SWIPE);
                fi = swipedElement;
                FoodItem_Dynamo foodItemDynamo = foodItemToDynamo(swipedElement);
                ((MainActivity) BrowseFragment.this.getActivity()).addFoodItem(foodItemDynamo);

                Toast.makeText(getContext(), getString(R.string.view_swiped_right, swipedElement.getBusinessName()),
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStackEmpty() {
                Toast.makeText(getContext(), R.string.stack_empty, Toast.LENGTH_SHORT).show();
            }
        });

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mSwipeStack.getCurrentPosition();
                FoodItem currentItem = mAdapter.getItem(currentPosition);
                String businessID = currentItem.getBusinessId();
                String fullUrl = "https://www.yelp.com/biz/" + businessID;
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl));
                startActivity(i);
            }
        });


        username = getArguments().getString("username");


        //initialize entriesCallback, to be called after each restaurant api call
        entriesCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                pDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                entries.addAll(yelpService.getItems(response));
            }
        };

        //initialize finalCallback, to be called after final restaurant api call, also updates UI
        finalCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                pDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                entries.addAll(yelpService.getItems(response));
                if (response.isSuccessful()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("uiEntries", "#" + entries.size());
                            Log.d("RIGHT BEFORE ADAPT", "SOMETHING HAPPENING PLEASE");
                            entriesforUI = entries;

                            mAdapter = new SwipeStackAdapter(getActivity(), entriesforUI);
                            mSwipeStack.setAdapter(mAdapter);
                            if(entriesforUI.size()==0) {
                                Toast.makeText(getContext(), "There are no resturaunts nearby",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if(refreshed) {
                                mSwipeStack.resetStack();
                                refreshed = false;
                            }
                        }
                    });
                }
                yelpService.setRating("0");
                pDialog.dismiss();
                pDialog.dismiss();
            }
        };

        //general callback for yelpservice.findRestaurants()
        findRestaurantsCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("findRestaurants", "OnResponse");

                //get a list of the restaurant ids using api call response
                idList = yelpService.getIDList(response);

                //clear entries list
                if (entries != null) {
                    entries.clear();
                }

                //for each id, make an api call and get some pictures, append to entries
                for (int i = 0; i < idList.size(); i++) {
                    if (i == idList.size() - 1) {
                        yelpService.pickImages(idList.get(i), token, finalCallback);
                    } else {
                        yelpService.pickImages(idList.get(i), token, entriesCallback);
                    }
                }
            }
        };
        loc = new MyLocationListener();
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getYelpToken = new GetYelpToken().execute();
//        locManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
//        getYelpToken = new GetYelpToken().execute();

        //set method to get photos onclick
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeStack.invalidate();
                pDialog.show();
                findLocation();
                yelpService.findRestaurants(latitude, longitude, token, findRestaurantsCallback);
                refreshed = true;

            }
        });
        advanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdvancedSearchActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        return rootView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String location = (String) data.getExtras().get("location");
            Boolean flag = false;
            if(location.equals("") || location.equals("current")){
               findLocation();
                flag = true;
            }

            String rating = (String) data.getExtras().get("rating");
            String price = (String) data.getExtras().get("price");
            String radius = (String) data.getExtras().get("radius");

            pDialog.show();
            yelpService.setRating(rating);
            yelpService.advancedSearch(longitude, latitude, flag, location, price, radius, token, findRestaurantsCallback);
            refreshed = true;
        }
    }

    public void findLocation(){

        if ( ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getActivity(),"Permission has been granted",Toast.LENGTH_SHORT).show();
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Log.d("permission","granted");

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, loc);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, loc);
            //i added an if statement to get around the network provider error
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,0,loc);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,loc);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.d("Lat","" + latitude);
            Log.d("Long","" + longitude);
        }

        else{
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toast.makeText(getActivity(),"Permission is needed to access location",Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },this.MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }
    }

    private FoodItem_Dynamo foodItemToDynamo(FoodItem fi) {
        FoodItem_Dynamo foodItemDynamo = new FoodItem_Dynamo();
        foodItemDynamo.setImageURL(fi.getImageURL());
        foodItemDynamo.setBusinessName(fi.getBusinessName());
        foodItemDynamo.setBusinessId(fi.getBusinessId());
        foodItemDynamo.setPrice(fi.getPrice());
        foodItemDynamo.setRating(fi.getRating());
        foodItemDynamo.setLatitude(fi.getLatitude());
        foodItemDynamo.setLongitude(fi.getLongitude());
        return foodItemDynamo;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == MY_PERMISSION_ACCESS_COURSE_LOCATION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                findLocation();
            }
            else{
                Toast.makeText(getActivity(),"Permission has been denied",Toast.LENGTH_SHORT).show();
            }
        }

        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class GetYelpToken extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String yelpJsonStr = null;


            try {
                // Construct the URL
                URL url = new URL("https://api.yelp.com/oauth2/token");
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("grant_type", "client_credentials");
                parameters.put("client_id", Constants.V3_CLIENT_ID);
                parameters.put("client_secret", Constants.V3_CLIENT_SECRET);
                Set set = parameters.entrySet();
                Iterator i = set.iterator();
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> param : parameters.entrySet()) {
                    if (postData.length() != 0) {
                        postData.append('&');
                    }

                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");


                // Create the request and open the connection
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Log.d("Response Code", "" + conn.getResponseCode());
                // Read the input stream into a String
                InputStream inputStream = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                if (sb.length() == 0) {
                    return null;
                }
                yelpJsonStr = sb.toString();
                return yelpJsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            } finally{
                if (conn != null) {
                    conn.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s == null) {
                Log.d("response", "error");
            } else {
                try {
                    JSONObject obj = new JSONObject(s);
                    token = obj.getString("access_token");
                } catch (JSONException e){
                    Log.d("JSONException", e.toString());
                }
                Log.d("response", token);
                findLocation();
                yelpService.findRestaurants(latitude, longitude, token, findRestaurantsCallback);
            }
        }

    }

    public void onClick(View v) {
        if (v.equals(mButtonLeft)) {
            mSwipeStack.swipeTopViewToLeft();
        } else if (v.equals(mButtonRight)) {
            mSwipeStack.swipeTopViewToRight();
        } else if (v.equals(mFab)) {
//            mData.add(getString(R.string.dummy_fab));
            mAdapter.notifyDataSetChanged();
        }
    }



    public class SwipeStackAdapter extends ArrayAdapter<FoodItem> {

        private ArrayList<FoodItem> foodEntries;

        public SwipeStackAdapter(Context context, ArrayList<FoodItem> array){
            super(context,R.layout.cards,array);
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.cards, parent, false);
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

    private class DynamoDBImageSwipeTask extends
            AsyncTask<DynamoDBManagerType, Void, DynamoDBManagerTaskResult> {

        protected DynamoDBManagerTaskResult doInBackground(
                DynamoDBManagerType... types) {

            String tableStatus = DynamoDBManager.getTestTableStatus();

            DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(types[0]);
            if (types[0] == DynamoDBManagerType.INSERT_USER_SWIPE) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    DynamoDBManager.incrementFoodItem(fi, isSwipeRight);
                    DynamoDBManager.insertUserSwipe(username, fi.getImageURL(), isSwipeRight);
                    int x = DynamoDBManager.incrementImageIndex(username, fi.getBusinessId());
                    Log.d("`1234567890-09876534", "" + x);
                    yelpService.setImageIndex(fi.getBusinessId(), x);
                }
            } else if (types[0] == DynamoDBManagerType.LIST_USER_BUSINESS_INDEX) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    yelpService.setImageIndexMap(DynamoDBManager.getAllImageIndexes(username));
                }
            }

            return result;
        }

        protected void onPostExecute(DynamoDBManagerTaskResult result) {
            if (result.getTaskType() == DynamoDBManagerType.LIST_USERS_SWIPES
                    && result.getTableStatus().equalsIgnoreCase("ACTIVE")) {
                new DynamoDBImageSwipeTask().execute(DynamoDBManagerType.LIST_USERS_SWIPES);
            } else if (!result.getTableStatus().equalsIgnoreCase("ACTIVE")) {
                Toast.makeText(
                        BrowseFragment.this.getActivity(),
                        "The test table is not ready yet.\nTable Status: "
                                + result.getTableStatus(), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}