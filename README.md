# mosquitto-rabbitmq-bridge



Mosquitto RabbitMQ Bridge is responsible to move device events from mosquitto broker to rabbitmq. These events are then processed by https://github.com/KonkerLabs/konker-platform. 


## Pre-requisites
Mosquitto RabbitMQ Bridge runs and compiles on Java 8.

It has a compile-time dependency on Lombok (see below) and runtime dependencies on Eclipse Jetty, Rabbit, and Mosquitto.

### Dependencies
#### Lombok

###### Intellij
Just install the Lombok plugin.

###### Eclipse
1. ```java -jar $MAVEN_REPOSITORY/org/projectlombok/lombok/$VERSION/lombok-$VERSION.jar```
2. Click on "Specify Location"
3. Select the eclipse executable
4. Click on Install / Update
5. Restart the Eclipse

### Building
Mosquitto RabbitMQ Bridge is built by using Apache Maven

```maven install```

### Build Docker Image
In root folder (mosquitto-rabbitmq-bridge) run the following to build image docker

```docker build .  -f docker/bridge/Dockerfile -t DOCKER_REPOSITORY/DOCKER_IMAGE_NAME```

Push the docker image to the repository

```docker push DOCKER_REPOSITORY/DOCKER_IMAGE_NAME```

### Run in Developer Env
#### Run standalone container ####
To run a standalone container in developer mode just execute

``` 
docker run -it \
   -e MQTT_HOSTNAME="tcp://localhost:1883" \
   -e MQTT_PUB_PASSWORD="pass" \
   -e MQTT_PUB_USERNAME="user" \
   -e MQTT_SUB_PASSWORD="pass" \
   -e MQTT_SUB_USERNAME="user" \
   -e RABBIT_DEVICE_CONTEXT="" \
   -e RABBIT_HOSTNAME="localhost:5672" \
   -e RABBIT_PASSWORD="" \
   -e RABBIT_USERNAME="" \
   --network host DOCKER_REPOSITORY/DOCKER_IMAGE_NAME
```

#### Run container with docker-compose and other dependencies ####
This project depend on others components that are into the following docker-compose.yml. You can run this docker-compose recipe to start all base components that konker-platform needs.

```
version: '2'
services:
  mosquitto:
    image: "eclipse-mosquitto:1.4.8"
    ports:
      - "1883:1883"
  mongo:
    image: "mongo:3.5"
    ports:
      - "27017:27017"
    volumes:
        - /var/lib/mongodb:/data/db:rw
  redis:
    image: "redis:alpine"
    ports:
      - "6379:6379"
  rabbit:
    image: "rabbitmq:management-alpine"
    ports:
      - "5672:5672"
      - "8083:15672"
  mosquitto-bridge:
    image: "DOCKER_REPOSITORY/DOCKER_IMAGE_NAME"
    depends_on:
      - "rabbit"
      - "mosquitto"
    environment:
      MQTT_HOSTNAME: "tcp://mosquitto:1883"
      MQTT_PUB_PASSWORD: "pass"
      MQTT_PUB_USERNAME: "user"
      MQTT_SUB_PASSWORD: "pass"
      MQTT_SUB_USERNAME: "user"
      RABBIT_DEVICE_CONTEXT: ""
      RABBIT_HOSTNAME: "rabbit"
      RABBIT_PASSWORD: ""
      RABBIT_USERNAME: ""
    
```

### Run in Production Env
#### Run standalone container ####
To run a standalone container in production mode just execute

``` 
docker run -it \
   -e MQTT_HOSTNAME="tcp://HOST_BROKER_MQTT:1883" \
   -e MQTT_PUB_PASSWORD="PASSWORD_MQTT_USER" \
   -e MQTT_PUB_USERNAME="MQTT_USERNAME" \
   -e MQTT_SUB_PASSWORD="PASSWORD_MQTT_USER" \
   -e MQTT_SUB_USERNAME="MQTT_USERNAME" \
   -e RABBIT_DEVICE_CONTEXT="RABBIT_QUEUE_CONTEXT" \
   -e RABBIT_HOSTNAME="HOST_RABBIT:5672" \
   -e RABBIT_PASSWORD="RABBIT_PASSWORD" \
   -e RABBIT_USERNAME="RABBIT_USERNAME" \
   --network host DOCKER_REPOSITORY/DOCKER_IMAGE_NAME
```