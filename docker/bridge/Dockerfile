FROM java:8

COPY ./target/mosquitto-rabbitmq-bridge.jar .
COPY ./docker/bridge/build/application.conf .

CMD ["java","-Dconfig.file=application.conf","-jar","mosquitto-rabbitmq-bridge.jar"]