FROM openjdk:17

WORKDIR /app

COPY target/testTaskToEffectiveMobile-0.0.1-SNAPSHOT.jar testTaskToEffectiveMobile-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "testTaskToEffectiveMobile-0.0.1-SNAPSHOT.jar"]


