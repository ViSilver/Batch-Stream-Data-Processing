# Deployment

First of all, the applications must be build. All the paths are relative to the root folder.

### Fetch-data build

```bash
cd ./code/fetchdata/
mvn clean install
```

The input files must be inserted into directory `./code/docker/bdp/fetchdata/data/`. From there, the **bdp** will automatically copy and move them to a temporary directory inside the platform.

For this assignment it was chosen the air_bnb dataset. The `data.csv` is already copied to the above mentioned directory.

### Ingest manager build

```bash
cd ./code/ingest-manager/
./create-docker-image.sh
```

And only after that we can checkout to docker folder:

```bash
cd ./code/docker/bdp/
docker-compose up -d
```

Please be patient because container running may take some time. Therefore, we may ensure that all the services started up when the ingest manager is available to receive requests. It can be checked by running:

```bash
docker logs -f ingest-manager
```

### Batch ingest app

```bash
cd ./code/batchingestapi/
mvn clean install -Pclient_1 
```

The last command will build the image and will create a `jar` file prefixed with `client_1` - which is our testing profile. Next we have to copy (register) the jarin our bdp:

```bash
cp ./target/client_1_batch-ingest-api-1.0-SNAPSHOT.jar ../docker/bdp/batch-client-apps/client_1_batch-ingest-api-1.0-SNAPSHOT.jar
```

Next, we invoke our client batch ingest app:

```bash
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"customer_name":"client_1"}' \
  http://localhost:5000/invoke/batch
```

The log file directory is `./code/docker/dbp/log/batch-logs/` where it can be seen the processing time of the file and other details of the run.

The result can be checked by inspecting the DB table:

```
docker exec -it cassandra-node-1 "/usr/bin/cqlsh"
cqlsh> select count(*) from client_1_air_bnb.apartment;
```

### Stream ingest app

Similar to batch ingest app.

```bash
cd ./code/clientstreamingestapp/
mvn clean install -Pclient_1 
```

The last command will build the image and will create a `jar` file prefixed with `client_1` - which is our testing profile. Next we have to copy (register) the jarin our bdp:

```bash
cp ./target/client_1_client-stream-ingest-app-1.0-SNAPSHOT.jar ../docker/bdp/streaming-client-apps/client_1_client-stream-ingest-app-1.0-SNAPSHOT.jar
```

Before invoking our client app, we need to register and create our durable queue:

```bash
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"queue_name":"client_1"}' \
  http://localhost:5000/register/queue
```

The management page of the rabbitmq can be accessed by the url: `http://localhost:15672`

Next, we invoke our client stream ingest app:

```bash
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"customer_name":"client_1"}' \
  http://localhost:5000/invoke/streaming
```

The log file directory is `./code/docker/dbp/log/streaming-logs/`.

In order to test the application, we have to insert some data into the queue, for this I have created a test project with a small test that will insert configurable amount of models in our `client_1` queue:

```bash
cd ./code/testclientstreaingestapp/
mvn clean test
```

The number of elements is defined inside the test and can be adjusted. See the `RabbitMQ.feature` file.

In the log files we can see the metrics, and if we are not satisfied with the procressing rate, we can spawn another instance of **client-stream-ingest-app** by requesting the **ingest-manager** to spawn one more.

```bash
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"customer_name":"client_1"}' \
  http://localhost:5000/invoke/streaming
```

