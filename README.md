# LemonCash Challenge
## _Fuck Off As A Service API_

A simple coding challenge implemented following this [guidelines](https://thorn-paperback-665.notion.site/L2-Coding-Challenge-f55f26875e1c4871b528f07e109c0e52).

### Features

- Consumes FOAAS API
- Implements Rate Limiting (Redis)
- Configurable API

This are the main features supported by our Challenge API. We also looked to provide scalability along the solution implementation.

### Tech

Our code uses the following technologies:

- [Java]
- [Spring Boot Framework]
- [Maven]
- [Redis]
- [Docker]

### Requirements

In order to install Docker go and check their [download page](https://docs.docker.com/get-docker/) for the suported version for your OS.

In order to run this project you must also have a Java 11 JDK install. A simple way to install it is by using [sdkman](https://sdkman.io/install).

Once you have installed this dependencies you are good to run this project as a local server.

### How to Run

In order to run this project you must first initialize our Redis Docker container (which is used as our RateLimiter backend). To do so, run the following command from project source:

```sh
docker compose up --build -d
```
Afterwards you might start the Spring Boot application which will be listening at **localhost:8080**.

### References

[foaas](https://www.foaas.com/)
[redis rate limiter](https://developer.redis.com/howtos/ratelimiting/#using-java)
[lettuce redis client](https://redis.com/blog/jedis-vs-lettuce-an-exploration/)
