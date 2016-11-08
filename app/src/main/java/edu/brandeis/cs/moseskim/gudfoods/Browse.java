package edu.brandeis.cs.moseskim.gudfoods;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by moseskim on 10/16/16.
 */
public class Browse extends AppCompatActivity {
    ArrayList<FoodItem> entries = new ArrayList<FoodItem>();
    ListView listView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);



        final YelpService yelpService = new YelpService();
        listView = (ListView) findViewById(R.id.listView);

        String location = "02453"; //"02134"; //we will have to retrieve this info from preferences
        String token = getIntent().getExtras().get("token").toString();

//        FoodItem item = new FoodItem("http://pngimg.com/upload/balloon_PNG4969.png");
//        CustomAdapter adapter = new CustomAdapter(this,entries);
//        adapter.add(item);

//        imageView = (ImageView) findViewById(R.id.imageView2);
//
//        imageView.setImageResource(R.drawable.test);


//        listView.setAdapter(adapter);



        yelpService.findRestaurants(location, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("Entering","YelpService.findRestaurants/onResponse");
                entries = yelpService.getItems(response);


                Browse.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        CustomAdapter adapter = new CustomAdapter(Browse.this, entries);
                        listView.setAdapter(adapter);
                    }
                });
            }
        });

    }




}
