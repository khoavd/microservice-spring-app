Build app

`./gradlew clean build`

Build a sub-project

`./gradlew :cloud-services:authz:build`

Clean a sub-project

`./gradlew :cloud-services:authz:clean`

Run standalone project with profile `docker`

`java -jar app.jar --spring.profiles.active=docker`

Set docker compose file 

`export COMPOSE_FILE=docker-compose-partitions.yml`

Unset docker compose file

`unset COMPOSE_FILE`

Build docker compose image

`docker-compose build`

Run docker compose image

`docker-compose up -d`

Shutdown docker compose

`docker-compose down`

Kafka see all topics 

`docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list`

To see the partitions in a specific topic `products`

`docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic products`

To see all the messages in a specific partition of a topic `1`

`docker-compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic products --from-beginning --timeout-ms 1000 --partition 1`

Scale up a service `review`

`docker-compose up -d --scale review=3`

Check service started 

`docker-compose logs review | grep Started`

Check how many instances of a service

`docker-compose logs review | grep "Response size"`

`Create `keytool`

`keytool -genkeypair -alias localhost -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore edge.p12 -validity 3650`

`


