package edu.brandeis.cs.moseskim.gudfoods;

/**
 * Created by Jon on 11/15/2016.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class BrowseFragment extends Fragment {

    Button button;
    Button settings;
    ArrayList<FoodItem> entries = new ArrayList<FoodItem>();
    ListView listView;
    String location;
    String token;
    String username;
    private View rootView;
    YelpService yelpService;
    AsyncTask getYelpToken;
    ProgressDialog pDialog;


    int windowwidth;
    int screenCenter;
    int x_cord, y_cord, x, y;
    int Likes = 0;
    RelativeLayout parentView;
    float alphaValue = 0;
    private Context m_context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.browse_fragment, container, false);
        button = (Button) rootView.findViewById(R.id.browse);
        m_context = getActivity();


        //final YelpService yelpService = new YelpService();
        settings = (Button) rootView.findViewById(R.id.signout);
        yelpService = new YelpService();
        listView = (ListView) rootView.findViewById(R.id.listView);
        location = "02453"; //we will have to retrieve this info from preferences
        getYelpToken = new GetYelpToken().execute();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        parentView = (RelativeLayout) rootView.findViewById(R.id.layoutview);
        windowwidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        screenCenter = windowwidth / 2;

        //set method to get photos onclick
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yelpService.findRestaurants(location, token, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("Entering","YelpService.findRestaurants/onResponse");
                        entries = yelpService.getItems(response);


                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
                            public void run() {
                                //changes made by alex
//                                CustomAdapter adapter = new CustomAdapter(getActivity(), entries);
//                                listView.setAdapter(adapter);
//                                int[] myImageList = new int[] { R.drawable.cats, R.drawable.baby1, R.drawable.sachin,
//                                        R.drawable.cats, R.drawable.puppy };


                                for (int i = 0; i < entries.size(); i++) {
                                    LayoutInflater inflate = (LayoutInflater) m_context
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                                    FoodItem item = entries.get(i);
                                    final View m_view = inflate.inflate(R.layout.custom_layout, null);
                                    final ImageView m_image = (ImageView) m_view.findViewById(R.id.sp_image);
                                    String url = item.getImageURL();

                                    ImageRequest request = new ImageRequest(url,
                                            new com.android.volley.Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap bitmap) {
                                            m_image.setImageBitmap(bitmap);
                                        }
                                    }, 0, 0, null,
                                        new com.android.volley.Response.ErrorListener(){
                                            public void onErrorResponse(VolleyError error) {
                                            }
                                        });

//                                  from the custom adapter for testing -- Alex  m_image.setImageUrl(item.getImageURL(), AppController.getInstance().getImageLoader());

                                    LinearLayout m_topLayout = (LinearLayout) m_view.findViewById(R.id.sp_color);
//                                    LinearLayout m_bottomLayout = (LinearLayout) m_view.findViewById(R.id.sp_linh);

                                    // final RelativeLayout myRelView = new RelativeLayout(this);
                                    m_view.setLayoutParams(new LinearLayout.LayoutParams((windowwidth - 80), 450));
                                    m_view.setX(40);
                                    m_view.setY(40);
                                    m_view.setTag(i);

                                    //changes made by alex

//                                    m_image.setBackgroundResource();

                                    if (i == 0) {
                                        m_view.setRotation(-1);
                                    } else if (i == 1) {
                                        m_view.setRotation(-5);

                                    } else if (i == 2) {
                                        m_view.setRotation(3);

                                    } else if (i == 3) {
                                        m_view.setRotation(7);

                                    } else if (i == 4) {
                                        m_view.setRotation(-2);

                                    } else if (i == 5) {
                                        m_view.setRotation(5);
                                    }

//            // ADD dynamically like button on image.
//            final Button imageLike = new Button(m_context);
//            imageLike.setLayoutParams(new LayoutParams(100, 50));
//            imageLike.setBackgroundDrawable(getResources().getDrawable(R.drawable.like));
//            imageLike.setX(20);
//            imageLike.setY(-250);
//            imageLike.setAlpha(alphaValue);
//            m_topLayout.addView(imageLike);
//
//            // ADD dynamically dislike button on image.
//            final Button imagePass = new Button(m_context);
//            imagePass.setLayoutParams(new LayoutParams(100, 50));
//            imagePass.setBackgroundDrawable(getResources().getDrawable(R.drawable.dislike));
//
//            imagePass.setX(260);
//            imagePass.setY(-300);
//            imagePass.setAlpha(alphaValue);
//            m_topLayout.addView(imagePass);

                                    // Click listener on the bottom layout to open the details of the
                                    // image.
//            m_bottomLayout.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(m_context, DetailsActivity.class));
//
//                }
//            });

                                    // Touch listener on the image layout to swipe image right or left.
                                    m_topLayout.setOnTouchListener(new View.OnTouchListener() {

                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            x_cord = (int) event.getRawX();
                                            y_cord = (int) event.getRawY();

                                            m_view.setX(x_cord - screenCenter + 40);
                                            m_view.setY(y_cord - 150);
                                            switch (event.getAction()) {
                                                case MotionEvent.ACTION_DOWN:
                                                    x = (int) event.getX();
                                                    y = (int) event.getY();
                                                    Log.v("On touch", x + " " + y);
                                                    break;
                                                case MotionEvent.ACTION_MOVE:
                                                    x_cord = (int) event.getRawX(); // Updated for more
                                                    // smoother animation.
                                                    y_cord = (int) event.getRawY();
                                                    m_view.setX(x_cord - x);
                                                    m_view.setY(y_cord - y);
                                                    // m_view.setY(y_cord-y);﻿
                                                    // y_cord = (int) event.getRawY();
                                                    // m_view.setX(x_cord - screenCenter + 40);
                                                    // m_view.setY(y_cord - 150);
                                                    if (x_cord >= screenCenter) {
                                                        m_view.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 32)));
                                                        if (x_cord > (screenCenter + (screenCenter / 2))) {
//                                    imageLike.setAlpha(1);
                                                            if (x_cord > (windowwidth - (screenCenter / 4))) {
                                                                Likes = 2;
                                                            } else {
                                                                Likes = 0;
                                                            }
                                                        } else {
                                                            Likes = 0;
//                                    imageLike.setAlpha(0);
                                                        }
//                                imagePass.setAlpha(0);
                                                    } else {
                                                        // rotate
                                                        m_view.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 32)));
                                                        if (x_cord < (screenCenter / 2)) {
//                                    imagePass.setAlpha(1);
                                                            if (x_cord < screenCenter / 4) {
                                                                Likes = 1;
                                                            } else {
                                                                Likes = 0;
                                                            }
                                                        } else {
                                                            Likes = 0;
//                                    imagePass.setAlpha(0);
                                                        }
//                                imageLike.setAlpha(0);
                                                    }

                                                    break;
                                                case MotionEvent.ACTION_UP:
                                                    x_cord = (int) event.getRawX();
                                                    y_cord = (int) event.getRawY();

                                                    Log.e("X Point", "" + x_cord + " , Y " + y_cord);
//                            imagePass.setAlpha(0);
//                            imageLike.setAlpha(0);

                                                    if (Likes == 0) {
                                                        // Log.e("Event Status", "Nothing");
                                                        m_view.setX(40);
                                                        m_view.setY(40);
                                                        m_view.setRotation(0);
                                                    } else if (Likes == 1) {
                                                        // Log.e("Event Status", "Passed");
                                                        parentView.removeView(m_view);
                                                    } else if (Likes == 2) {

                                                        // Log.e("Event Status", "Liked");
                                                        parentView.removeView(m_view);
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                            return true;
                                        }
                                    });

                                    parentView.addView(m_view);

                                }

                            }
                        });
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
                        entries = yelpService.getItems(response);


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                CustomAdapter adapter = new CustomAdapter(getActivity(), entries);
                                listView.setAdapter(adapter);
                                pDialog.dismiss();
                            }
                        });
                    }
                });
            }
        }
    }
}
