package edu.brandeis.cs.moseskim.gudfoods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Jon on 11/24/2016.
 */
public class AdvancedSearchActivity extends AppCompatActivity{
    Intent returnIntent;
    Button submit;
    EditText location;
    Spinner price;
    Spinner minPrice;
    Spinner rating;
    Spinner radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_search);

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //setup other widgets and intent
        returnIntent = new Intent();
        returnIntent.putExtra("location","current");
        returnIntent.putExtra("price","1,2,3,4");
        returnIntent.putExtra("rating","1");
        returnIntent.putExtra("radius","16093");
        submit = (Button) findViewById(R.id.submit_search);
        location = (EditText) findViewById(R.id.location_text);
        price = (Spinner) findViewById(R.id.price_spinner);
        minPrice = (Spinner) findViewById(R.id.min_price_spinner);
        rating = (Spinner) findViewById(R.id.rating_spinner);
        radius = (Spinner) findViewById(R.id.radius_spinner);

        //setup location
        location.setText(TemporaryPreferences.location);
        returnIntent.putExtra("location",TemporaryPreferences.location);
        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String loc = location.getText().toString();

                if(loc.equals("")){
                    TemporaryPreferences.theLocation = "current";
                    returnIntent.putExtra("location","current");
                } else {
                    TemporaryPreferences.theLocation = loc;
                    returnIntent.putExtra("location", loc);
                }

                TemporaryPreferences.location = loc;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //setup price spinner
        ArrayAdapter<CharSequence> priceAdapter = ArrayAdapter.createFromResource(this,
                R.array.priceArray , android.R.layout.simple_spinner_item);
        price.setAdapter(priceAdapter);
        price.setSelection(TemporaryPreferences.priceInt);
        price.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String maxPrice = price.getSelectedItem().toString();
                String minPriceString = minPrice.getSelectedItem().toString();
                setPriceExtra(maxPrice,minPriceString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        //setup min price spinner
        ArrayAdapter<CharSequence> minPriceAdapter = ArrayAdapter.createFromResource(this,
                R.array.minPriceArray , android.R.layout.simple_spinner_item);
        minPrice.setAdapter(minPriceAdapter);
        minPrice.setSelection(TemporaryPreferences.minPriceInt);
        minPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String maxPrice = price.getSelectedItem().toString();
                String minPriceString = minPrice.getSelectedItem().toString();
                setPriceExtra(maxPrice,minPriceString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        //setup rating spinner
        ArrayAdapter<CharSequence> ratingAdapter = ArrayAdapter.createFromResource(this,
                R.array.ratingArray , android.R.layout.simple_spinner_item);
        rating.setAdapter(ratingAdapter);
        rating.setSelection(TemporaryPreferences.ratingInt);
        rating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String text = rating.getSelectedItem().toString();
                if(text.equals("1 Star (Default)")){
                    Log.d("rating","1 star default");
                    TemporaryPreferences.ratingInt = 0;
                    TemporaryPreferences.theRating = "1";
                    returnIntent.putExtra("rating","1");
                } else if(text.equals("2 Stars")){
                    TemporaryPreferences.ratingInt = 1;
                    TemporaryPreferences.theRating = "2";
                    returnIntent.putExtra("rating","2");
                } else if(text.equals("3 Stars")){
                    TemporaryPreferences.ratingInt = 2;
                    TemporaryPreferences.theRating = "3";
                    returnIntent.putExtra("rating","3");
                } else if(text.equals("4 Stars")){
                    TemporaryPreferences.ratingInt = 3;
                    TemporaryPreferences.theRating = "4";
                    returnIntent.putExtra("rating","4");
                } else if(text.equals("5 Stars")){
                    TemporaryPreferences.ratingInt = 4;
                    TemporaryPreferences.theRating = "5";
                    returnIntent.putExtra("rating","5");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        //setup radius spinner
        ArrayAdapter<CharSequence> radiusAdapter = ArrayAdapter.createFromResource(this,
                R.array.radiusArray , android.R.layout.simple_spinner_item);
        radius.setAdapter(radiusAdapter);
        radius.setSelection(TemporaryPreferences.radiusInt);
        radius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String text = radius.getSelectedItem().toString();
                //1609--1, 8046--5, 16093--10, 24140--15, 32187--20
                if(text.equals("10 Miles")){
                    TemporaryPreferences.radiusInt = 0;
                    TemporaryPreferences.theRadius = "16093";
                    returnIntent.putExtra("radius","16093");
                } else if(text.equals("5 Miles (Default)")){
                    TemporaryPreferences.radiusInt = 1;
                    TemporaryPreferences.theRadius = "8046";
                    returnIntent.putExtra("radius","8046");
                } else if(text.equals("2 Miles")){
                    TemporaryPreferences.radiusInt = 2;
                    TemporaryPreferences.theRadius = "3218";
                    returnIntent.putExtra("radius","3218");
                } else if(text.equals("1 Mile")){
                    TemporaryPreferences.radiusInt = 3;
                    TemporaryPreferences.theRadius = "1609";
                    returnIntent.putExtra("radius","1609");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    public void setPriceExtra(String max, String min){
        int start;
        int end;
        if(max.equals("$$$$ (Default)")){
            TemporaryPreferences.priceInt = 0;
            end = 4;
        } else if (max.equals("$$$")){
            TemporaryPreferences.priceInt = 1;
            end = 3;
        } else if (max.equals("$$")) {
            TemporaryPreferences.priceInt = 2;
            end = 2;
        } else {
            TemporaryPreferences.priceInt = 3;
            end = 1;
        }

        if(min.equals("$ (Default)")){
            TemporaryPreferences.minPriceInt = 0;
            start = 1;
        } else if (min.equals("$$")){
            TemporaryPreferences.minPriceInt = 1;
            start = 2;
        } else if (min.equals("$$$")){
            TemporaryPreferences.minPriceInt = 2;
            start = 3;
        } else {
            TemporaryPreferences.minPriceInt = 3;
            start = 4;
        }

        String priceString = "";
        for(int i = start; i <= end; i++){
            if(i == start){
                priceString = "" + i;
            } else {
                priceString = priceString + "," + i;
            }
        }
        TemporaryPreferences.thePrice = priceString;
        returnIntent.putExtra("price",priceString);
    }
}
