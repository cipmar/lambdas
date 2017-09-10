package com.softwaredevelopmentstuff.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class CurrentWeatherStreamer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final List<String> HEADER_LINES = Arrays.asList(
            "SEP=,\n",
            "cityId,timestamp,cityName,humidity,pressure,temp,visibility,windSpeed,windDeg\n"
    );
    private static final String WEATHER_DATA_BUCKET = System.getenv("weatherDataBucket");

    public void handler(InputStream inputStream) throws IOException {
        TypeReference ref = new TypeReference<Map<String, Object>>() {
        };

        Map<String, Object> records = OBJECT_MAPPER.readValue(new InputStreamReader(inputStream), ref);

        ((List<?>) records.get("Records")).forEach(record -> {
            Map<String, Object> recordMap = (Map<String, Object>) record;

            String cityId = getObjectByPath(recordMap, "dynamodb.Keys.cityId.N", String.class);
            String cityName = getObjectByPath(recordMap, "dynamodb.NewImage.cityName.S", String.class);

            StringJoiner sj = new StringJoiner(",")
                    .add(cityId)
                    .add(getObjectByPath(recordMap, "dynamodb.Keys.timestamp.N", String.class))
                    .add(cityName)
                    .add(getObjectByPath(recordMap, "dynamodb.NewImage.humidity.N", String.class))
                    .add(getObjectByPath(recordMap, "dynamodb.NewImage.pressure.N", String.class))
                    .add(getObjectByPath(recordMap, "dynamodb.NewImage.temp.N", String.class))
                    .add(getObjectByPath(recordMap, "dynamodb.NewImage.visibility.N", String.class))
                    .add(getObjectByPath(recordMap, "dynamodb.NewImage.windSpeed.N", String.class))
                    .add(getObjectByPath(recordMap, "dynamodb.NewImage.windDeg.N", String.class));
            try {
                saveToS3(sj.toString(), "weather-" + cityName + "-" + cityId + ".csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void saveToS3(String csvLine, String fileName) throws IOException {
        AmazonS3 amazonS3 = new AmazonS3Client();

        if (amazonS3.doesObjectExist(WEATHER_DATA_BUCKET, fileName)) {
            S3Object s3Object = amazonS3.getObject(WEATHER_DATA_BUCKET, fileName);

            File tmpFile = writeLineToTmpFile(s3Object, csvLine);

            AccessControlList acl = new AccessControlList();
            acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

            PutObjectRequest putObjectRequest = new PutObjectRequest(WEATHER_DATA_BUCKET, fileName, tmpFile);
            putObjectRequest.setAccessControlList(acl);

            amazonS3.putObject(putObjectRequest);

            System.out.println(fileName + " updated with a new line " + csvLine);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            for (String headerLine : HEADER_LINES) {
                bos.write(headerLine.getBytes());
            }

            bos.write(csvLine.getBytes());
            amazonS3.putObject(WEATHER_DATA_BUCKET, fileName, new String(bos.toByteArray()));

            System.out.println(fileName + " created with first line " + csvLine);
        }
    }

    private File writeLineToTmpFile(S3Object s3Object, String csvLine) throws IOException {
        File tmpFile = File.createTempFile(s3Object.getKey(), "tmp");

        try (
                InputStream input = new BufferedInputStream(s3Object.getObjectContent());
                OutputStream output = new BufferedOutputStream(new FileOutputStream(tmpFile))
        ) {
            byte[] buffer = new byte[1024];
            int len;

            while ((len = input.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }

            output.write("\n".getBytes());
            output.write(csvLine.getBytes());
        }

        return tmpFile;
    }

    private <T> T getObjectByPath(Map<String, Object> map, String path, Class<T> clazz) {
        if (path.contains(".")) {
            String[] pathSplit = path.split("\\.", 2);
            String key = pathSplit[0];
            String reminderPath = pathSplit[1];

            Object value = map.get(key);

            if (value instanceof Map) {
                Map<String, Object> subMap = (Map<String, Object>) value;
                return getObjectByPath(subMap, reminderPath, clazz);
            } else {
                return null;
            }
        } else {
            return clazz.cast(map.get(path));
        }
    }

    public static void main(String[] args) throws IOException {
        CurrentWeatherStreamer streamer = new CurrentWeatherStreamer();
        InputStream is = streamer.getClass().getResourceAsStream("/sample.json");
        streamer.handler(is);
    }
}
