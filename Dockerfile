FROM gradle:8.10-jdk23 AS BUILD

ENV APP_HOME=/app/build/
WORKDIR $APP_HOME

COPY . .
RUN gradle build

FROM openjdk:23-jdk
RUN microdnf install findutils

ENV ARTIFACT_NAME=aswe-chat-app-1.0.0.tar
ENV APP_HOME=/app/

WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME/build/build/distributions/$ARTIFACT_NAME .

RUN tar -C "$APP_HOME" -xvf "$ARTIFACT_NAME"

EXPOSE 8080
ENTRYPOINT ["aswe-chat-app-1.0.0/bin/aswe-chat-app"]
