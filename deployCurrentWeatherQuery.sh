#!/usr/bin/env bash

mvn clean package shade:shade

aws --profile personal lambda update-function-code  \
    --function-name current-weather-query \
    --zip-file fileb://c:/Users/Marius/IdeaProjects/lambdas/current-weather-query/target/current-weather-query-1.0-SNAPSHOT.jar
    
     
