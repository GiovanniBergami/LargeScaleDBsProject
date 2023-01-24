#!/bin/bash

# Insert key here
KEY=

pushd $( dirname -- "$0"; )/

wget https://api.tfl.gov.uk/Road/all/Disruption?app_key=$KEY -O - | java -cp libCommon-0.0.3-jar-with-dependencies.jar londonSafeTravel.driver.tims.RoadDisruptionUpdate

popd
