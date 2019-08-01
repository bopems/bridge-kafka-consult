FROM docker-registry.bopems.local:5000/tools/java/8-jdk-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="-Xmx2048m -Xms256m"

ADD target/modulo-*-*-*.jar /app.jar

CMD echo "The application will starting now..." && \
    sleep 0 && \
    java ${JAVA_OPTS} \
      -Dapp.project=${APP_PROJECT} \
      -Dapp.module=${APP_MODULE} \
      -Dapp.service=${APP_SERVICE} \
      -Dapp.name=${APP_NAME} \
      -Dserver.port=${SERVER_PORT} \
      -Dspring.cloud.inetutils.default-hostname=${DISCOVERY_HOST} \
      -Dspring.cloud.host=${DISCOVERY_HOST} \
      -Dspring.cloud.consul.host=${DISCOVERY_HOST} \
      -Dspring.cloud.consul.port=${DISCOVERY_PORT} \
      -Dkafka.encryption.key=${KAFKA_ENCRYPTION_KEY} \
      -Dkafka.encryption.iv.enabled=true \
      -Dspring.cloud.bus.enabled=true \
      -Dspring.cloud.bootstrap.enabled=true \
      -Dspring.cloud.discovery.enabled=true \
      -Dspring.cloud.consul.enabled=true \
      -Dspring.cloud.consul.config.enabled=true \
      -Dspring.cloud.config.discovery.enabled=true \
      -Dspring.cloud.consul.discovery.ip-address=${IP_ADDRESS} \
      -Djava.security.egd=file:/dev/./urandom -jar /app.jar
