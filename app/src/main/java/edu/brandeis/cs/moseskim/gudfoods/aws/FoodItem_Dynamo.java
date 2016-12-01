package edu.brandeis.cs.moseskim.gudfoods.aws;

/**
 * Created by Chungyuk on 11/19/2016.
 */

import android.location.Location;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.util.Comparator;

import edu.brandeis.cs.moseskim.gudfoods.BrowseFragment;

@DynamoDBTable(tableName = "FoodItem")
public class FoodItem_Dynamo implements Comparable{
    private String imageURL;
    private String businessName;
    private String businessId;
    private String price;
    private double rating;
    private int swipeLeftCount;
    private int swipeRightCount;
    private double latitude;
    private double longitude;

    @DynamoDBHashKey(attributeName = "Image_URL")
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @DynamoDBAttribute(attributeName = "Business_Name")
    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    @DynamoDBAttribute(attributeName = "Business_Id")
    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @DynamoDBAttribute(attributeName = "Price")
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @DynamoDBAttribute(attributeName = "Rating")
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @DynamoDBAttribute(attributeName = "Swipe_Left_Count")
    public int getSwipeLeftCount() {
        return swipeLeftCount;
    }

    public void setSwipeLeftCount(int swipeLeftCount) {
        this.swipeLeftCount = swipeLeftCount;
    }

    @DynamoDBAttribute(attributeName = "Swipe_Right_Count")
    public int getSwipeRightCount() {
        return swipeRightCount;
    }

    public void setSwipeRightCount(int swipeRightCount) {
        this.swipeRightCount = swipeRightCount;
    }

    @DynamoDBAttribute(attributeName = "Latitude")
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @DynamoDBAttribute(attributeName = "Longitude")
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static final Comparator<FoodItem_Dynamo> NameComparator = new Comparator<FoodItem_Dynamo>() {
        @Override
        public int compare(FoodItem_Dynamo o1, FoodItem_Dynamo o2) {
            return o1.getBusinessName().compareTo(o2.getBusinessName());
        }
    };

    public static final Comparator<FoodItem_Dynamo> PriceLoToHiComparator = new Comparator<FoodItem_Dynamo>() {
        @Override
        public int compare(FoodItem_Dynamo o1, FoodItem_Dynamo o2) {
            return o1.getPrice().length() - o2.getPrice().length();
        }
    };

    public static final Comparator<FoodItem_Dynamo> PriceHiToLoComparator = new Comparator<FoodItem_Dynamo>() {
        @Override
        public int compare(FoodItem_Dynamo o1, FoodItem_Dynamo o2) {
            return o2.getPrice().length() - o1.getPrice().length();
        }
    };

    public static final Comparator<FoodItem_Dynamo> RatingComparator = new Comparator<FoodItem_Dynamo>() {
        @Override
        public int compare(FoodItem_Dynamo o1, FoodItem_Dynamo o2) {
            return (int) ((o2.getRating() - o1.getRating()) * 10);
        }
    };

    public static final Comparator<FoodItem_Dynamo> DistanceComparator = new Comparator<FoodItem_Dynamo>() {
        @Override
        public int compare(FoodItem_Dynamo o1, FoodItem_Dynamo o2) {
            float[] results1 = new float[1];
            Location.distanceBetween(BrowseFragment.latitude, BrowseFragment.longitude, o1.latitude, o1.longitude, results1);
            float[] results2 = new float[1];
            Location.distanceBetween(BrowseFragment.latitude, BrowseFragment.longitude, o2.latitude, o2.longitude, results2);
            return (int) (results1[0] - results2[0]);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o instanceof FoodItem_Dynamo) {
            return this.getImageURL().equals(((FoodItem_Dynamo) o).getImageURL());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.getImageURL() != null ? this.getImageURL().hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof FoodItem_Dynamo) {
            if (((FoodItem_Dynamo) o).getImageURL().equals(this.getImageURL())) {
                return 0;
            }
            return ((FoodItem_Dynamo) o).getSwipeRightCount() - this.getSwipeRightCount();
        } else {
            return -1;
        }
    }
}
