#!/bin/bash

spruce merge --prune params bridge.yml parameters.yml > data.yml
fly -t alias set-pipeline -p mosquitto-rabbitmq-bridge -c data.yml
