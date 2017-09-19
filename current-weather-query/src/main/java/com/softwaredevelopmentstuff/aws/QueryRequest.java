package com.softwaredevelopmentstuff.aws;

public class QueryRequest {
    private Long cityId;

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "QueryRequest{" +
                "cityId=" + cityId +
                '}';
    }
}
