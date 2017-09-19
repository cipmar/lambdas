package com.softwaredevelopmentstuff.aws;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwaredevelopmentstuff.aws.info.WeatherInfo;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.lang.System.getenv;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

public class CurrentWeatherFetcher {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final WebTarget WEATHER_WEB_TARGET = ClientBuilder
            .newClient()
            .target("http://api.openweathermap.org")
            .path("/data/2.5/weather")
            .queryParam("units", "metric")
            .queryParam("appid", getenv("openWeatherAppId"));

    public void handler(InputStream inputStream) throws IOException {
        List<String> cities = asList(getenv("cities").split(","));

        for (String cityId : cities) {
            Response response = WEATHER_WEB_TARGET
                    .queryParam("id", cityId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            String strResponse = response.readEntity(String.class);
            WeatherInfo weatherInfo = OBJECT_MAPPER.readValue(strResponse, WeatherInfo.class);

            try {
                saveToDynamoDb(weatherInfo);
            } catch (Exception e) {
                System.out.println("Failed to save weather data: " + strResponse);
                throw e;
            }
        }
    }

    private void saveToDynamoDb(WeatherInfo weatherInfo) {
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
