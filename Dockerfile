FROM eclipse-temurin:17-jdk-alpine as build
LABEL org.opencontainers.image.source=https://github.com/REDWANE-AIT-OUKAZZAMANE/it-support-system
LABEL org.opencontainers.image.description="IT Support System Backend"
LABEL org.opencontainers.image.version="1.1.0"

WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY server/pom.xml server/
COPY server/src server/src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../../server/target/support-server-0.0.1-SNAPSHOT-exec.jar)

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.support.SupportApplication"] 