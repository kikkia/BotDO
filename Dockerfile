FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . /app/
RUN mvn -B clean package

FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/ /app
COPY --from=build /app/res/ /app/res
CMD java -javaagent:"/app/res/dd-java-agent.jar" -Ddd.profiling.enabled=true -Ddd.logs.injection=true -Ddd.trace.sample.rate=1 -Ddd.trace.analytics.enabled=true -Ddd.service=bdo-bot -Ddd.env=prod -jar bdo-bot-0.1.jar
