FROM public.ecr.aws/docker/library/amazoncorretto:21-alpine
ARG APP_VERSION
EXPOSE 8080
WORKDIR /opt/zeletrik

COPY build/libs/*-${APP_VERSION}.jar service.jar
ENV SENTRY_RELEASE=${APP_VERSION}

ENTRYPOINT exec java $JAVA_OPTS -jar service.jar
