# Autosuggestion API
## Version
 * Java 17 or higher (JDK Java SE 17 or later)
  * Maven 3.2+
  * Docker version 24.0.6

## Installation

    * clone the project from -- https://github.com/NaguaG/Demp.git
    * download the demo file (file.xlsx) from -- https://docs.google.com/spreadsheets/d/1LCE41nSqrtqhphw23APO-gVkiKtE5yKQjqYkhCDfQiY/edit?usp=sharing
    * copy the demo into the data folder 
    * rewrite the "file paths"(dump.rdb && UpdatedLocations.xlsx) in the application.properties file

## SET UP command for REDIS STACK

```bash
docker-compose up
```

## RUN the application 
or
```bash
./mvnw spring-boot:run
```

## Usage

* check Frontend url: http://localhost:8080/
* check Backend endpoint: http://localhost:8080/api/v1/location/search/{q}
* check data in Redis Insight -- http://localhost:8001/
    * /optional/ delete all cached data from redis cli - "flushall"

##
