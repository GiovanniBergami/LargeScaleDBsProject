#!/bin/bash

pushd $( dirname -- "$0"; )/

java -cp libCommon-0.0.3-jar-with-dependencies.jar londonSafeTravel.server.Server

popd
