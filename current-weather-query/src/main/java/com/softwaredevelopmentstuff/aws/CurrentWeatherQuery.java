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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CurrentWeatherQuery {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void handler(InputStream inputStream, OutputStream outputStream) throws IOException {
        printInput(inputStream);

        final Long CITY_ID = 2800867L;
        final String TIMESTAMP = "1504407300";

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDBMapper mapper = new DynamoDBMapper(client);

        Condition rangeKeyCondition = new Condition();
        rangeKeyCondition.withComparisonOperator(ComparisonOperator.GE)
                .withAttributeValueList(new AttributeValue().withN(TIMESTAMP));

        DynamoDBQueryExpression<WeatherData> query = new DynamoDBQueryExpression<WeatherData>()
                .withHashKeyValues(new WeatherData(CITY_ID))
                .withRangeKeyCondition("timestamp", rangeKeyCondition);

        List<WeatherData> weatherDataList = mapper.query(WeatherData.class, query);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("statusCode", "200");
        response.put("body", OBJECT_MAPPER.writeValueAsString(weatherDataList));
        OBJECT_MAPPER.writeValue(outputStream, response);
    }

    private static void printInput(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024 * 1024];
        int len = inputStream.read(buffer);
        System.out.println(new String(buffer, 0, len));
    }
}
