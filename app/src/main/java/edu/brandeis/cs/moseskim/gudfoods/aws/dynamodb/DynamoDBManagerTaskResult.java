package edu.brandeis.cs.moseskim.gudfoods.aws.dynamodb;

/**
 * Created by Chungyuk on 11/27/2016.
 */


public class DynamoDBManagerTaskResult {
    private DynamoDBManagerType taskType;
    private String tableStatus;

    public DynamoDBManagerType getTaskType() {
        return taskType;
    }

    public void setTaskType(DynamoDBManagerType taskType) {
        this.taskType = taskType;
    }

    public String getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(String tableStatus) {
        this.tableStatus = tableStatus;
    }
}