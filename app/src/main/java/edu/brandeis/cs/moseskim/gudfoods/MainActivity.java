package edu.brandeis.cs.moseskim.gudfoods;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import edu.brandeis.cs.moseskim.gudfoods.aws.AWSService;
import edu.brandeis.cs.moseskim.gudfoods.aws.AmazonClientManager;
import edu.brandeis.cs.moseskim.gudfoods.aws.FoodItem_Dynamo;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    String token;
    String username;
    public static AmazonClientManager clientManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clientManager = new AmazonClientManager(this);


        username = getIntent().getExtras().getString("name");

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //add tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.swiped_list_tab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.browse_tab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.trending_tab));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), username);
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
//    public void changeImageView(View view) {
//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                view.setBackgroundColor(R.drawable.filename);
//            }
//        });

//
//    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    public void addFoodItem(FoodItem_Dynamo foodItemDynamo) {
        SwipedListFragment.addToList(this, foodItemDynamo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                String message = "This app was created by Moses Kim, Jonathan Maeda, Alex Suk" +
                        " and Chunghyuk Takahashi.";

                builder.setTitle("About")
                        .setMessage(message)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.show();

                return true;
            case R.id.sign_out:
                AWSService.getPool().getUser(username).signOut();
                AWSService.setUser("");
                this.setResult(Activity.RESULT_OK);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }



    }
}



