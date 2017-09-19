package com.softwaredevelopmentstuff.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class CurrentWeatherQuery {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int DAYS_BACK = 3;

    public void handler(InputStream inputStream, OutputStream outputStream) throws IOException {
        QueryRequest queryRequest = OBJECT_MAPPER.readValue(inputStream, QueryRequest.class);

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDBMapper mapper = new DynamoDBMapper(client);

        Condition rangeKeyCondition = new Condition();
        rangeKeyCondition.withComparisonOperator(ComparisonOperator.GE)
                .withAttributeValueList(new AttributeValue().withN(String.valueOf(getTimestamp(DAYS_BACK))));

        DynamoDBQueryExpression<WeatherData> query = new DynamoDBQueryExpression<WeatherData>()
                .withHashKeyValues(new WeatherData(queryRequest.getCityId()))
                .withRangeKeyCondition("timestamp", rangeKeyCondition);

        List<WeatherData> weatherDataList = mapper.query(WeatherData.class, query);

        OBJECT_MAPPER.writeValue(outputStream, weatherDataList);
    }

    private Long getTimestamp(int daysBack) {
        return (System.currentTimeMillis() - daysBack * 24 * 60 * 60 * 1000) / 1000;
    }
}
