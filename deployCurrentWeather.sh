#!/usr/bin/env bash

mvn clean package shade:shade

aws --profile personal lambda update-function-code  \
    --function-name current-weather \
    --zip-file fileb://c:/Projects/lambdas/currentweather/target/current-weather-1.0-SNAPSHOT.jar
