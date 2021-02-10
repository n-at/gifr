#!/bin/bash

npm install
npm run build

./mvnw clean package
cp ./target/gifr.jar .

rm -r ./src/main/resources/public/build
./mvnw clean
