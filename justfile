app-version := '0.1.0'

run-dev:
    mvn -pl metadata -am spring-boot:run
    mvn -pl rest-server -am spring-boot:run

build-docker-image:
    mvn package -DskipTests
    docker build -t dianw/frappe-metadata:{{app-version}} ./metadata
    docker build -t dianw/frappe-rest-server:{{app-version}} ./rest-server
    docker rmi $(docker images -f dangling=true -q)

run-dep-apps:
    docker-compose -f docker-compose-dev.yml up -d

stop-dep-apps:
    docker-compose -f docker-compose-dev.yml down