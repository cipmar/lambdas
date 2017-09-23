package com.softwaredevelopmentstuff.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.softwaredevelopmentstuff.aws.info.WeatherInfo;

import static java.util.Optional.ofNullable;

final class StorageService {
    void saveWeatherInfo(WeatherInfo weatherInfo) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("weather2");

        final Item item = new Item()
                .withPrimaryKey("cityId", weatherInfo.id)
                .withNumber("timestamp", weatherInfo.dt)
                .withString("cityName", weatherInfo.name)
                .withNumber("temp", weatherInfo.main.temp);

        ofNullable(weatherInfo.main.humidity).ifPresent(v -> item.withNumber("humidity", v));
        ofNullable(weatherInfo.main.pressure).ifPresent(v -> item.withNumber("pressure", v));
        ofNullable(weatherInfo.visibility).ifPresent(v -> item.withNumber("visibility", v));
        ofNullable(weatherInfo.wind.deg).ifPresent(v -> item.withNumber("windDeg", v));
        ofNullable(weatherInfo.wind.speed).ifPresent(v -> item.withNumber("windSpeed", v));

        table.putItem(item);
    }
}
