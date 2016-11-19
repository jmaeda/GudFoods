package edu.brandeis.cs.moseskim.gudfoods;

/**
 * Created by Jon on 11/15/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

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
import java.util.concurrent.ExecutionException;

import edu.brandeis.cs.moseskim.gudfoods.aws.AWSService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BrowseFragment extends Fragment {

    Button button;
    Button settings;
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<FoodItem> entries = new ArrayList<FoodItem>();
    ListView listView;
    String location;
    String token;
    String username;
    private View rootView;
    YelpService yelpService;
    AsyncTask getYelpToken;
    ProgressDialog pDialog;

    Callback entriesCallback;
    Callback finalCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.browse_fragment, container, false);
        button = (Button) rootView.findViewById(R.id.browse);
        settings = (Button) rootView.findViewById(R.id.signout);
        yelpService = new YelpService();
        listView = (ListView) rootView.findViewById(R.id.listView);
        location = "02453"; //we will have to retrieve this info from preferences

        //initialize entriesCallback, to be called after each restaurant api call
        entriesCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                entries.addAll(yelpService.getItems(response));
                if(response.isSuccessful()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("uiEntries", "#" + entries.size());
                            CustomAdapter adapter = new CustomAdapter(getActivity(), entries);
                            listView.setAdapter(adapter);
                        }
                    });
                    pDialog.dismiss();
                }
            }
        };

        getYelpToken = new GetYelpToken().execute();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        yelpService.findRestaurants(location, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("findRestaurants","OnResponse");

                //get a list of the restaurant ids using api call response
                idList = yelpService.getIDList(response);

                //clear entries list
                if(entries != null) {
                    entries.clear();
                }

                //for each id, make an api call and get some pictures, append to entries
                for (int i = 0; i <idList.size(); i++) {
                    if (i == idList.size() - 1) {
                        yelpService.pickImages(idList.get(i), token, finalCallback);
                    } else {
                        yelpService.pickImages(idList.get(i), token, entriesCallback);
                    }
                }
            }
        });

        //set method to get photos onclick
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Loading...");
                pDialog.show();
                yelpService.findRestaurants(location, token, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("findRestaurants","OnResponse");

                        //get a list of the restaurant ids using api call response
                        idList = yelpService.getIDList(response);

                        //clear entries list
                        if(entries != null) {
                            entries.clear();
                        }

                        //for each id, make an api call and get some pictures, append to entries
                        for (int i = 0; i <idList.size(); i++) {
                            if (i == idList.size() - 1) {
                                yelpService.pickImages(idList.get(i), token, finalCallback);
                            } else {
                                yelpService.pickImages(idList.get(i), token, entriesCallback);
                            }
                        }
                    }
                });
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AWSService.getPool().getUser(username).signOut();
                AWSService.setUser("");
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });

        return rootView;
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
                yelpService.findRestaurants(location, token, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("findRestaurants","OnResponse");

                        //get a list of the restaurant ids using api call response
                        idList = yelpService.getIDList(response);

                        //clear entries list
                        if(entries != null) {
                            entries.clear();
                        }

                        //for each id, make an api call and get some pictures, append to entries
                        for (int i = 0; i <idList.size(); i++) {
                            if (i == idList.size() - 1) {
                                yelpService.pickImages(idList.get(i), token, finalCallback);
                            } else {
                                yelpService.pickImages(idList.get(i), token, entriesCallback);
                            }
                        }
                    }
                });
            }
        }
    }
}
