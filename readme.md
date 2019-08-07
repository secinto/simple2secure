# Mongo database in docker

We can install a mongo database directly in docker, so that we can communicate
with other containers like simple2secure.portal.

### Installing

To install MongoDB container, run following command:
```
docker run --name mongo-s2s -d mongo:latest
```
