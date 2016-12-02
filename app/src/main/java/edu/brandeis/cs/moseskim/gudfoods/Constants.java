package edu.brandeis.cs.moseskim.gudfoods;

/**
 * Created by moseskim on 10/11/16.
 */
public class Constants {

    public static final String YELP_CONSUMER_KEY = BuildConfig.YELP_CONSUMER_KEY;
    public static final String YELP_CONSUMER_SECRET = BuildConfig.YELP_CONSUMER_SECRET;
    public static final String YELP_TOKEN = BuildConfig.YELP_TOKEN;
    public static final String YELP_TOKEN_SECRET = BuildConfig.YELP_TOKEN_SECRET;
    public static final String YELP_BASE_URL = "https://api.yelp.com/v2/search?term=food";
    public static final String YELP_LATITUDE_PARAMETER = "latitude";
    public static final String YELP_LONGITUDE_PARAMETER = "longitude";
    public static final String YELP_TERM_PARAMETER = "term";
    public static final String YELP_LIMIT_PARAMETER = "limit";
    public static final String YELP_LOCATION_PARAMETER = "location";
    public static final String YELP_PRICE_PARAMETER = "price";
    public static final String YELP_RADIUS_PARAMETER = "radius";


//    public static final String V3_CLIENT_ID = BuildConfig.V3_CLIENT_ID;
    public static final String V3_CLIENT_ID = "jIHCe1e803Yo84Y5xbws2A";
//    public static final String V3_CLIENT_SECRET = BuildConfig.V3_CLIENT_SECRET;

    public static final String V3_CLIENT_SECRET = "mDCmk9nJOP6no13iaoKj9yuNZoXefmq9DEfuy39pozyOsPYcweK4kndoxyjMRsTu";
    public static final String YELP_BASE_URL_V3 = "https://api.yelp.com/v3/businesses/search?term=food";
    public static final String YELP_BASE_BUSINESS_URL_V3 = "https://api.yelp.com/v3/businesses/";

    public static final String AWS_POOL_ID = BuildConfig.AWS_POOL_ID;
    public static final String AWS_USER_ID = BuildConfig.AWS_USER_ID;

    public static final String IDENTITY_POOL_ID = "us-east-1:ee2fb8d9-eb6d-4713-8e8a-85785b15adc4";
    public static final String TEST_TABLE_NAME = "FoodItem";

}
