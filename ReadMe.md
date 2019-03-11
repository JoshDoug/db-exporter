# Provide DB login details for exporter via env.list

MariaDB container command: 

```
docker run -d --rm --name test-export-db -p 3306:3306 --env-file config/env.list -v `pwd`/test-db-data:/docker-entrypoint-initdb.d mariadb:latest
```

Package application into jar: `mvn package -DskipTests=true`

Create docker image containing jar: `docker build -t db-exporter .`

When not using compose, need to create a user-defined bridge and connect both the db and this image to it, doesn't seem ideal.
This will probably not be necessary when using docker-compose.

Creating a user-defined bridge network: `docker network create ev-test-net`, can be removed with `docker network rm ev-test-net`

Connecting a container to a network:

* running a container, use the `docker create --name db-exporter-app \
                                  --network ev-test-net \
                                  --publish 8080:80 \
                                  nginx:latest`
* connect network to a running container: `docker network connect ev-test-net db-exporter-app`

Running named container with port mappings, env.list config, network bridge connection, volume share, autoremove for tagged image:

```bash
docker run --name db-exporter-app -p 8080:8080 --env-file config/env.list  --network ev-test-net -v `pwd`/external:/exports --rm -d db-exporter:0.0.5
```

Go to `hostname:8080/export/download` to download a zipped db export file, where hostname and the ports depend on the mappings used for the container instance.