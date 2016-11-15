package edu.brandeis.cs.moseskim.gudfoods;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.Business;

import android.view.View;
import android.widget.*;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.browse_tab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.swiped_list_tab));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


//    private void getRestaurants(String location) {
//        final YelpService yelpService = new YelpService();
//        yelpService.findRestaurants(location, new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    String jsonData = response.body().string();
//                    Log.v(TAG, jsonData);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }
//
//
//    public String yelpRequest(){
//
//        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer("JIJrn2v9l-2K2UQf054WKg",
//                "At2aOs-aMO9C6JP30JoafbtKyvY");
//        consumer.setTokenWithSecret("jZKPporU87Vs3AeHixkQiTixlipuFO9_",
//                "MbXkex1meQOfYm1z7-22YrCkdVw");
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(new SigningInterceptor(consumer))
//                .build();
//
//
//        YelpAPIFactory apiFactory = new YelpAPIFactory("JIJrn2v9l-2K2UQf054WKg",
//                "At2aOs-aMO9C6JP30JoafbtKyvY", "jZKPporU87Vs3AeHixkQiTixlipuFO9_",
//                "MbXkex1meQOfYm1z7-22YrCkdVw");
//        YelpAPI yelpAPI = apiFactory.createAPI();
//
//
//
//
//        Map<String,String> params = new HashMap<>();
//
//        params.put("term", "korean");
//        params.put("limit", "3");
//        params.put("lang", "en");
//
//        Call<SearchResponse> call = yelpAPI.search("Boston",params);
//
//
//        try {
//            SearchResponse response = call.execute().body();
//            int totalNumberOfResult = response.total();
//
//            ArrayList<Business> businesses = response.businesses();
//            String businessName = businesses.get(0).name();
//            double rating = businesses.get(0).rating();
//
//            return businessName;
//        }
//        catch (Exception e){
//
//        }
//
//
//
//
//        return null;
//
//    }

}



