package com.softwaredevelopmentstuff.aws;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwaredevelopmentstuff.aws.info.WeatherInfo;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.System.getenv;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.Status.OK;

public class CurrentWeatherFetcher {
    private static final Logger LOGGER = Logger.getLogger("WeatherFetcher");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final WebTarget WEATHER_WEB_TARGET = ClientBuilder
            .newClient()
            .target("http://api.openweathermap.org")
            .path("/data/2.5/weather")
            .queryParam("units", "metric")
            .queryParam("appid", getenv("openWeatherAppId"));

    public void handler(Object ignore) throws IOException {
        List<String> cities = asList(getenv("cities").split(","));

        for (String cityId : cities) {
            LOGGER.info("Fetching weather data for cityId " + cityId);

            Response response = WEATHER_WEB_TARGET
                    .queryParam("id", cityId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == OK.getStatusCode()) {
                handleOkResponse(response);
            } else {
                handleErrorResponse(response);
            }
        }
    }

    private void handleOkResponse(Response response) throws IOException {
        String strResponse = response.readEntity(String.class);
        WeatherInfo weatherInfo = OBJECT_MAPPER.readValue(strResponse, WeatherInfo.class);

        try {
            new StorageService().saveWeatherInfo(weatherInfo);
            LOGGER.info("Weather data fetched successfully");
        } catch (Exception e) {
            LOGGER.severe("Failed to save weather data: " + strResponse);
            throw e;
        }
    }

    private void handleErrorResponse(Response response) {
        String strResponse = response.readEntity(String.class);
        LOGGER.severe("ERROR: Failed to call weather open API: Status code: " + response.getStatus() + " and body: " + strResponse);
    }
}
