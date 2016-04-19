#!/bin/bash

fw_depends java maven

mvn clean package

java -jar target/testTechempowerBaratine-0.0.2-SNAPSHOT.jar "jdbc:mysql://${DBHOST}"
