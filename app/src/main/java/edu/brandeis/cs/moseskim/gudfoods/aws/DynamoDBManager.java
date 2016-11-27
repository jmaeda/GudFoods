/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.brandeis.cs.moseskim.gudfoods.aws;

import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import java.util.ArrayList;
import java.util.Date;

import edu.brandeis.cs.moseskim.gudfoods.BrowseFragment;
import edu.brandeis.cs.moseskim.gudfoods.Constants;
import edu.brandeis.cs.moseskim.gudfoods.FoodItem;

public class DynamoDBManager {

    private static final String TAG = "DynamoDBManager";

    /*
     * Retrieves the table description and returns the table status as a string.
     */
    public static String getTestTableStatus() {

        try {
            AmazonDynamoDBClient ddb = BrowseFragment.clientManager
                    .ddb();

            DescribeTableRequest request = new DescribeTableRequest()
                    .withTableName(Constants.TEST_TABLE_NAME);
            DescribeTableResult result = ddb.describeTable(request);

            String status = result.getTable().getTableStatus();
            return status == null ? "" : status;

        } catch (ResourceNotFoundException e) {
        } catch (AmazonServiceException ex) {
            BrowseFragment.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return "";
    }

    /**
     * Increments the swipeLeft or swipeRight count for a single food item
     * @param fi
     * @param isSwipeRight
     */
    public static void incrementFoodItem(FoodItem fi, boolean isSwipeRight) {
        AmazonDynamoDBClient ddb = BrowseFragment.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        Log.d(TAG, fi.toString());
        FoodItem_Dynamo foodItem = mapper.load(FoodItem_Dynamo.class, fi.getImageURL());
        if(foodItem == null) {
            foodItem = new FoodItem_Dynamo();
            foodItem.setImageURL(fi.getImageURL());
            foodItem.setBusinessName(fi.getBusinessName());
            foodItem.setBusinessId(fi.getBusinessId());
            foodItem.setPrice(fi.getPrice());
            foodItem.setRating(fi.getRating());
            if (isSwipeRight) {
                foodItem.setSwipeRightCount(1);
                foodItem.setSwipeLeftCount(0);
            } else {
                foodItem.setSwipeRightCount(0);
                foodItem.setSwipeLeftCount(1);
            }
            foodItem.setLatitude(fi.getLatitude());
            foodItem.setLongitude(fi.getLongitude());
        } else {
            if (isSwipeRight) {
                foodItem.setSwipeRightCount(foodItem.getSwipeRightCount() + 1);
            } else {
                foodItem.setSwipeLeftCount(foodItem.getSwipeLeftCount() + 1);
            }
        }
        mapper.save(foodItem);
    }

    public static void insertUserSwipe(String username, String foodURL, boolean isSwipeRight) {
        AmazonDynamoDBClient ddb = BrowseFragment.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        Log.d(TAG, "INSERT USER SWIPE " + username);

        UserSwipe_Dynamo userSwipe = new UserSwipe_Dynamo();
        userSwipe.setUserId(username);
        userSwipe.setFoodImageURL(foodURL);
        userSwipe.setSwipeRight(isSwipeRight);
        userSwipe.setDeleted(false);
        userSwipe.setTimeAdded(new Date());

        mapper.save(userSwipe);
    }

    public static void listUserSwipeRights(String username) {
        AmazonDynamoDBClient ddb = BrowseFragment.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        Log.d("LIST_USER_SWIPE_RIGHTS", username + "");

        DynamoDBQueryExpression<UserSwipe_Dynamo> query = new DynamoDBQueryExpression<>();
        UserSwipe_Dynamo test = new UserSwipe_Dynamo();
        test.setUserId(username);
        query.setHashKeyValues(test);


        PaginatedQueryList<UserSwipe_Dynamo> resultsList = mapper.query(UserSwipe_Dynamo.class, query);
        String testing = "";
        for (UserSwipe_Dynamo u : resultsList) {
            testing += u.getFoodImageURL() + "\n";
        }
        Log.d("TAG USERS SWIPES", testing);

    }


}
