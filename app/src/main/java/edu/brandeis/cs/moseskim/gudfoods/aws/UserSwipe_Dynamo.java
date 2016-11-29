package edu.brandeis.cs.moseskim.gudfoods.aws;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.Date;

/**
 * Created by Chungyuk on 11/19/2016.
 */

@DynamoDBTable(tableName = "UserSwipe")
public class UserSwipe_Dynamo {
    private String userId;
    private String foodImageURL;
    private boolean isSwipeRight;
    private boolean isDeleted;
    private Date timeAdded;

    @DynamoDBHashKey(attributeName = "User_Id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBRangeKey(attributeName = "Food_Image_URL")
    public String getFoodImageURL() {
        return foodImageURL;
    }

    public void setFoodImageURL(String foodImageURL) {
        this.foodImageURL = foodImageURL;
    }

    @DynamoDBAttribute(attributeName = "Is_Swipe_Right")
    public boolean isSwipeRight() {
        return isSwipeRight;
    }

    public void setSwipeRight(boolean swipeRight) {
        this.isSwipeRight = swipeRight;
    }

    @DynamoDBAttribute(attributeName = "Is_Deleted")
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @DynamoDBAttribute(attributeName = "Time_Added")
    public Date getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Date timeAdded) {
        this.timeAdded = timeAdded;
    }
}
