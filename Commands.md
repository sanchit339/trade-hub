# Commands used for the project

## Initial Docker run
- `docker compose up -d`  # booting up the docker / bring up the container
- `./mvnw spring-boot:run`  # for running application
- `docker compose exec postgres psql -U tradehub -d tradehub -c '\d trade'`  # shows the table

## Dependencies / notes
- Going with `<artifactId>camel-jackson3-starter</artifactId>`

## TODO
- Check - disable `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES` in `JsonConfig.java` — why is it required?

## Questions
- What's the use case of `Var` in `TradeValidator.java`?
