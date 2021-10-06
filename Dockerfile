FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /app
COPY . /app/
RUN mvn -B -Dmaven.artifact.threads=30 clean package

FROM openjdk:11.0.12-slim-buster
WORKDIR /app
COPY --from=build /app/target/ /app
COPY --from=build /app/res/ /app/res
CMD java -javaagent:"/app/res/dd-java-agent.jar" -Ddd.profiling.enabled=true -Ddd.logs.injection=true -Ddd.trace.sample.rate=1 -Ddd.trace.analytics.enabled=true -Ddd.service=bdo-bot -Ddd.env=prod -jar bdo-bot-0.1.jar
