#!/usr/bin/env bash

mvn clean package shade:shade

aws --profile personal lambda update-function-code  \
    --function-name current-weather-fetcher \
    --zip-file fileb://c:/Projects/lambdas/current-weather-fetcher/target/current-weather-fetcher-1.0-SNAPSHOT.jar
