# Assignment 2
## Part 1. Batch ingestion
### 1.1 File constraints 

The constraints of the customer data that has to be batch ingested are as follows:

- File size should not be bigger than 100 MB. If the file is bigger than the specified size, it will not be picked up by the **fetchdata** component. This means that the customer has to split this file into smaller ones.
- Maximum number of files per tenant (client) is 4. If there are submitted more than 4 files, then only 4 of them will be copied (in alphabetical order) in the system and processed at the beginning, and as some of those 4 files finished being processed, then a new file will be picked up from the remaining uncopied tenant's files.
- Another constraint, but a softer one, is that each file name must begin with tenant's name. Although it is not a mandatory one, but the system will preffer files that are named according to this pattern while picking them up.

The constraints are defined in an `.env` file and they can be seen at `./code/docker/fetch-data/.env`. In this manner, they will be transformed into system environmental variables and will be picked up by the `fetch-data` component without requiring recompilation.

### 1.2 **fetchdata** implementation

Because it was not specified in detail how should be the **client-input-directory** implemented, for the sake of proof of work, it was implemented a common directory for all the tenants. This approach was chosen **only to demonstrate the proof of work of the given solution**.

In production implementation, it should never be chosen a common directory for all the tenants because of the security reasons. A tenant should not have any information about other tenants (eg: their names of their ids) or about other tenants' data. Even when we store files in the same directory, the system must provide the possibility for the tenant to configure the access control for his files.

A production implementation of the **client-input-directory** would be separate folders for each customer and give the folders random names, so that other tenants could not guess the tenant name/id, with the permissions for viewing, writing, and listing the directory only to tenant. Additionally, the **fetchdata** component must have the right to view and to list the directory content and must have permission to copy and delete the files located in the **client-input-directory**.

The **fetchdata** component can be implemented in several ways:

1. As an application that must be installed on client's computer. In this case, the security concerns relies just in the securing the file delivery from client's machine to the **bdp-temp-file-storage** (the temporary location inside **bdp** of tenant files before being batch processed). 

2. As a web interface using browser for uploading files using HTTPS connections. In this case, the constraints will be applied before the files being uploaded, and an error message will be diplayed to user with the constraint violations and future actions to be executed. Once the files are uploaded in the system, they will be automatically stored in **bdp-temp-file-storage**.

3. As a web interface, without using browser. Similar to the second option.

For the proof of concept of the solution, the first option was implemented.

### 1.3 **clientbatchingestapp** and **batchingestmanager** definition

#### A. **clientbatchingestapp**

We give the tenant the flexibility and the responsibility to provide the implementation for batch ingestion. Although, we have to define several constraints in form of an api - **batch-ingest-api**. 

Several api's (in different programming languages: `java`, `python`, `go`, etc.) can be defined. For this proof of concept it was chosen `Java` programming language, along with `Spring` framework. The api is provided as a `.jar` file that can be linked to the project, or as a `maven` dependency. 

In order to implement the **client-batch-ingest-app**, the tenant is given the option to use a default implementation of the ingestion, packaged within the api, or to define his own ingestion strategy. The tenant cannot see any of the internal addresses of the **bmp** components - they are stored as env variables inside the platform.

Client implementations will later be shipped as docker containers to the platform.

For example for Java, the tenant must define the model of the parsed entity Univocity Parsers and of the entity stored inside Cassandra using the Spring-Data Cassandra API, implement an abstractconfiguration class: `UserConfig.java` where she should define: `modelClass` - the class of the defined model, `keyspace` - the name of the keyspace inside Cassandra, `entityBasePackages` - the base packages of the model classes. These are the minimal user configurations that have to be satisfied.

For the given proof of concept, the API and the client application was implemented inside the same project, but within separated classes and packages so that it could be easily split into the API and client implementation.

#### B. **batchingestmanager**

Batch ingest manager will have several components:

- a storage location, **client-batch-ingest-app-storage**, where the tenant provides her implementations in form of a **client-batch-ingest-app**s that are available for the manager to be invoked.
- a API (eg: REST API) for client app registration and invokation. In order to execute a tenant application, the tenant must register (upload) the application to the manager, and then to trigger app invokation (execution). 

For the given proof of concept, the **manager** API was implemented as a REST API using `Python`. It was chosen `Python` for the easiness of running custom processes programatically. There are provided several endpoints to be accessed by the tenant:

- `/invoke/batch` - POST method, content-type: `application/json`, payload example: `'{"customer_name":"client_1"}'`, where the customer name is the name of the tenant (it is a keyword, no substitution).

When the given endpoint is accessed, the manager will search for the applications registered (uploaded) earlier by the tenant and will invoke it. It will return a string of the pattern: `[${client_app_name}_${log_id}]` - list of invoked applications, where `${client_app_name}` is evaluated to the name of the invoked client application, and `${log_id}` will be evaluated to a random string that is used to link the output logs of the invoked application.

- `/kill_all/batch` - POST method, content-type: `application/json`, payload example: `'{"customer_name":"client_1"}'`, where the customer name is the name of the tenant (it is a keyword, no substitution);

When the given endpoint is accessed, the manager will kill all the running instances of the **client-batch-ingest-app** for the given customer.

I addition to this, the API can be extended to more endpoints, that were not implemented in the given POC:

-  `/invoke/batch/{app_name}` - POST method, content-type: `application/json`, payload example: `'{"customer_name":"client_1"}'`, where the customer name is the name of the tenant (it is a keyword, no substitution).

The same as `/invoke/batch`, but instead of invoking all the registered applications, it will be invoked only the one identified by the name.

- `/kill_all/batch/{app_name}` - POST method, content-type: `application/json`, payload example: `'{"customer_name":"client_1"}'`, where the customer name is the name of the tenant (it is a keyword, no substitution);

The same as `/kill_all/batch`, but instead of killing all instances of running application for the given customer, it will kill only the one identified by the given name.


### 1.4 **clientbatchingestapp** testing

There were implemented two client apps for two tenants: `client_1` and `client_2`. Each of them have a keyspace correlated to their names.

Output the test results: TODO

### 1.5 Logging

Each of the client applications is providing logs in separate files with a unique identifier each time they are run, so that the logs are not overwritten. They can be found at the path `./code/log/${customer_name}_${app_jar_name}-${unique_hash}.log`, where it is logges the processing time of the file, and it's size, along with other implementation details.

The log files can be further processed and analyzed for future performance improvement.

## Part 2. Near-realtime ingestion

### 2.1 Message structure

The imposed message body structure for the `air_bnb` dataset is the following JSON example:

```json
{
    "id": 0,
    "name": "name",
    "hostId": 1,
    "hostName": "hostName",
    "neighbourhoodGroup": "neighbourhoodGroup",
    "neighbourhood": "neighbourhood",
    "latitude": "lat",
    "longitude": "long",
    "roomType": "roomType",
    "price": 12,
    "minimumNights": 3,
    "numberOfReviews": 1,
    "lastReview": "lastReview",
    "reviewPerMonth": "revPerMonth",
    "calculatedHostListingsCount": "calc",
    "availability365": 12
}
```

Other message properties must comply the given format example:

```
priority:	0
delivery_mode:	2
headers: __TypeId__:	fi.aalto.assignmenttwo.streamingest.model.Apartment
content_encoding:	UTF-8
content_type:	application/json
```

The tenant must ingest messages of this type into the queue correlated to the tenant id (customer_name). Further, these messages will be consumed and processed by the client application.

The given JSON message structure was chosen because of the datasize of the JSON marshalling comparing to other formats, for example XML, and for it's readability comparing to byte serialization formats. The given model fully reflects the structure of the rows of air_bnb dataset and similar models should be created for every dataset the customer is going to work with. The restriction is that it should be a JSON marshalled message.

Small message size will ensure high throughtput and low bandwidth consumption during message enqueueing and dequeing.

### 2.2 **streamingestmanager** implementation

#### A. Message broker

For the given design it was chosen **RabbitMQ** because it is lightweight and easy to deploy on premises and in the cloud. Moreover, RabbitMQ can be deployed in distributed and federated configurations to meet high-scale, high-availability requirements. Moreover, it has great management and monitoring plugins that provide broker metrics that facilitate development and scaling decisions.

For example the `avg_egress_rate` is a very usefull parameter for our **stream-ingest-manager**.

Each of the tenants will have a separate exchange, that can be identified by the tenant name. The queue registration must be done in advance, through the broker API, and the queues have to be persistent, and must comply the model name (lowercase).

For the given proof of concept, each tenant has just one queue with the name of the tenant.

#### B. Stream ingest manager

Stream ingest manager will be defined as an API set that will be responsible for invoking **client-stream-ingest-app** and will also be receiving metrics from each of the apps. 

As with the **batch-ingest-manager**, for the given proof of concept it was chosen `Python` language from the same considerations: the easiness of running custom processes programatically. 

Besides the endpoints listed below, batch ingest manager will communicate with the data broker and will get queue metrics data that will be used for taking scaling decisions of the client stream ingest apps. For retrieving queue metrics, the following endpoint will be invoked using method GET: `http://rabbitmq:15672/api/queues/%2F/${queue_name}` (for our POC, the queue name is the tenant name: `client_1`). The given request must be authenticated with teh administrator credentials. The default credentials are `user = guest; password = guest`.

There are provided several endpoints to be accessed by the tenants:

- `/register/queue` - POST method, content-type: `application/json`, payload example: `'{"queue_name":"client_1"}'`, where the queue name is the name of the queue (it is a keyword, no substitution). As it can be seen in this example, the name of the queue is the tenant name.

It is needed to register the queue because it is needed to have a durable queue before inserting the data. Usually the data can stay several time before being processed by the consumer.

- `/invoke/streaming` - POST method, content-type: `application/json`, payload example: `'{"customer_name":"client_1"}'`, where the customer name is the name of the tenant (it is a keyword, no substitution).

When the given endpoint is accessed, the manager will search for the applications registered (uploaded) earlier by the tenant and will invoke it. It will return a string of the pattern: `[${client_app_name}_${log_id}]` - list of invoked applications, where `${client_app_name}` is evaluated to the name of the invoked client application, and `${log_id}` will be evaluated to a random string that is used to link the output logs of the invoked application.

- `/kill_all/streaming` - POST method, content-type: `application/json`, payload example: `'{"customer_name":"client_1"}'`, where the customer name is the name of the tenant (it is a keyword, no substitution);

When the given endpoint is accessed, the manager will kill all the running instances of the **client-stream-ingest-app** for the given customer.

I addition to this, the API can be extended to more endpoints, that were not implemented in the given POC:

-  `/invoke/streaming/{app_name}` - POST method, content-type: `application/json`, payload example: `'{"customer_name":"client_1"}'`, where the customer name is the name of the tenant (it is a keyword, no substitution).

The same as `/invoke/streaming`, but instead of invoking all the registered applications, it will be invoked only the one identified by the name.

- `/kill_all/streaming/{app_name}` - POST method, content-type: `application/json`, payload example: `'{"customer_name":"client_1"}'`, where the customer name is the name of the tenant (it is a keyword, no substitution);

The same as `/kill_all/streaming`, but instead of killing all instances of running application for the given customer, it will kill only the one identified by the given name.


### 2.3 **clientstreamingestapp** implementation

The implementation of the client stream ingest app can be found on path `./code/clientstreamingestapp/`. As with the batch ingest app, the platform provides an API that tenant has to follow. It can, again, be implemented in several programming languages: `Java`, `Python`, `Go`, etc.

In case of `Python`, the dependency can be expressed in a script shipped as an image, from which the client has to build his image on top of.

For our POC, we used `Java` programming language and, for the ease of demonstration the concept, the API is not shipped separately, but it is just divided into packages and tenant implementation.

In order to implement the API, the tenant has to define the keyspace of Cassandra DB, entity model class, and the message listener.

There are prepared two maven profiles for testing: `client_1` and `client_2`. Each of them will build a jar prefixied with profile name, which will be then registered to the **stream ingest manager** to be invoked later on.

The log outputs with all the streaming metrics data can be found at `./code/docker/log/`. 

**Note**: During testing on local machine, it was observed that the sum of the average ingestion rates of the instances of the **stream ingest app**s remains constant for 1, 2, 3, or 4 instances, and it is equal to the egress rate of the message broker. From the other point of view, the ingress rate of the message broker is much higher than the egress one. This means that even though we increase the number of instances,
we cannot improve the overall performance of the platform because the bottleneck is the DB.

The components of the client stream ingest app:

- Cassandra DB connection configuration
- RabbitMQ message listener configuration
- Quartz Job to send metrics data
- Delivered messege counter


### 2.4 Reporting
 
In addition to the existing API, **stream-ingest-manager** has to be enhanced with an endpoint for receiving report metrics from **client-stream-ingest-app**s. It can be easily done using REST API endpoints:

- `/report/metrics` - POST method, content-type: `application/json`, payload example:
`'{"customerId": "client_1", "numberOfMessages": 20340, "processingRate":203.4, "averageIngestionTime": 0.023, "totalIngestionDataSize": 2045.6}'`, where the `customerId` is the tenant name.

This endpoint is designed to receive the metrics data from each of the client stream applications being invoked. The metrics are filtered by tenant name and scaling decision can be made based on them.

In adition to this, when stream ingest manager receives a metric data for a specific queue, it also calls the management endpoint (`http://rabbitmq:15672/api/queues/%2F/${queue_name}`) of the message broker to gather queue metrics. This is needed because a better scaling strategy can be considered when we are having an aggregated metric data from both places.

Each client stream ingest app, has as part of the **client-stream-ingest-api**, a schdeduler job that is running at a configurable time interval, and that invokes report sending to the manager.

A more resielient and user friendly approach would be to use DataDog for collecting metrics and monitoring.

### 2.5 Scaling based on reporting

The feature to receive reporting was implemented inside **client-stream-ingest-manager** following the description from the above section.

The model for scaling up can be defined as follows:

```python
if avg_ingress_rate > avg_egress_rate 
        or number_of_messages_in_queue > MAX_MESSAGES * nr_of_instances:
    spawn_another_client_app()
    if avg_egress_rate > db_insert_rate:
        spawn_another_db_instance()
```

The model for scaling down can defined as follows:

```python
if number_of_messages_in_queue < MIN_MESSAGES * (nr_of_instances - 1):
    kill_one_client_app()
```

## Part 3. Integration and Extension

### 3.1 Integrated architecture

The integrated architecture can be examined in the below diagram.

Instead of sending and marchalling each row from the file, we read X rows (for example 10 rows) and enqueue them as string message to our message broker. This operation can be performed by **batch-to-stream-ingest-app** - an intermediary component that is similar to **batch-ingest-app**, but instead of inserting the data into the DB, it streams it to the message broker. 

To be noted that the message structure has changed. Thus, it is needed to adjust the **stream-ingest-app** to the new message structure.

The consumer of the message will be our **stream-ingest-app**, but with a slight modification of the received message. After receiving the message, stream ingest app will make 10 entities (or a less secure optimization would be to insert raw data) and will insert them into Cassandra DB.

### 3.2 File splitting

In case of a file bigger than a specified limit (determined empirically by the benchmarks of our application: for example it weights several GB), the **fetch-data** component must split it into several smaller files and store them inside the platform, because the chance that there will be errors during file transportation is very big.

An even better approach is to implement a separate component as part of fetch-data that will perform the splitting. Let's call it **file-chunking**. It will create the files following the file name pattern of the original file.

One way of implementing this is to streamingly read the file and loading in memory only X rows of the file - for example 1000 rows. When there were read 1000 rows, dump the loaded rows into a new file. 

Next, having smaller files we can invoke out **batch-ingest-app** from Part 1 or we could do even better - to reuse our **batch-to-stream-ingest-app** from previous section together with **stream-ingest-app** from Part 2. 

### 3.3 Code inspection

The platform must not see or inspect the code of the customer because it is a proprietary asset which may contain copyright code. 

If we insist in inspecting the code, the customers may choose a competitor which is offering a similar service without this regulation. Also, it is an industry standard to treat customer code as a proprietary resource.

A platform can assume it knows the code in the case that it provides the service to write this code. If the platform deliveres this service and is hired by the customer, then it can fully control the implementation of the customer code.

### 3.4 Data quality

It is expensive to have an additional processor of the data that checks if it respects the constraints or not, rather than simply ingesting it straight away.

There can be performed several benchmarks with data of different quality, derive the time spent for processing the errors of different levels, and the resource consumption when additional processors are filtering and sanitize the data.

Basing on the benchmarks, there can be derived several charging plans and present them to customer and argument the position of different charging plans. Even though we don't have access to the data, we know it's size and the platform processing power for each component, and we have the data average ingestion rate for each component that we can compare with the empirical rate of the customer. 

As a result, our charging plans may be oriented towards the processing time, if, for example, earlier it was based on data size.

### 3.5 Multiple models per tenant

In the case of batch ingestion, it would be suitable to have a different naming pattern for each of the data files. Then the **fetch-data** component can be adjusted to store each data type files for each customer in a separate location. Then a small adjustment can be made to run the **client-batch-ingest-app**s with data storage location parameter for the app to find the right location.

In the case of stream ingestion, it would be suitable to have separate queues per data model. This means a small adjustment of data broker configuration and adjustment of **client-stream-ingest-app**s to accept the queue name as parameter or to derive it based on a pattern (model name).




