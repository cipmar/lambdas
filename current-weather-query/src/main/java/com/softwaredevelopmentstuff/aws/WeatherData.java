package com.softwaredevelopmentstuff.aws;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "weather2")
public class WeatherData {
    private Long cityId;
    private String cityName;
    private Long timestamp;
    private Integer temp;
    private Integer pressure;
    private Integer humidity;
    private Integer visibility;
    private Double windSpeed;
    private Double windDeg;

    public WeatherData() {

    }

    public WeatherData(Long cityId) {
        this.cityId = cityId;
    }

    @DynamoDBHashKey(attributeName = "cityId")
    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    @DynamoDBAttribute(attributeName = "cityName")
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @DynamoDBRangeKey(attributeName = "timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBAttribute(attributeName = "temp")
    public Integer getTemp() {
        return temp;
    }

    public void setTemp(Integer temp) {
        this.temp = temp;
    }

    @DynamoDBAttribute(attributeName = "pressure")
    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    @DynamoDBAttribute(attributeName = "humidity")
    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    @DynamoDBAttribute(attributeName = "visibility")
    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    @DynamoDBAttribute(attributeName = "windSpeed")
    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    @DynamoDBAttribute(attributeName = "windDeg")
    public Double getWindDeg() {
        return windDeg;
    }

    public void setWindDeg(Double windDeg) {
        this.windDeg = windDeg;
    }
}
