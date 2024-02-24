FROM openjdk:11-slim as build
LABEL maintainer="Dmitry Druzyako <druzyakodima@gmail.com>"
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)

FROM openjdk:11-slim
#Добавить том, ссылающийся на каталог /tmp
VOLUME /tmp
#Скопировать распакованное приложение в новый контейнер. BOOT-INF/lib содержит все внутренние и внешние зависимости
ARG DEPENDENCY=/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
#Запустить приложение
ENTRYPOINT ["java","-cp","app:app/lib/*","com.optimagrowth.gateway.ApiGatewayServerApplication"]

