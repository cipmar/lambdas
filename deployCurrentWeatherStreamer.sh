#!/usr/bin/env bash

mvn clean package shade:shade

aws --profile personal lambda update-function-code  \
    --function-name current-weather-streamer \
    --zip-file fileb://c:/Projects/lambdas/current-weather-streaming/target/current-weather-streaming-1.0-SNAPSHOT.jar
