# Badminton Statistics Web Application

+ Java, Vaadin, Spring Boot , EclipseStore

# Internal

## Configuration

### Links
    + https://vaadin.com/docs/latest/flow/integrations/spring/configuration#special-configuration-parameters 
    + https://github.com/xdev-software/spring-data-eclipse-store

### Run Application

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw                                                               # runs `mvn spring-boot:run` for you
./mvnw package                                                       # builds a jar file
docker build -t my-application:latest .                              # builds a docker image
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .           # builds a docker image with a commercial component
```
Run local jar: 

```bash
mvn clean package -Pproduction

java --add-exports java.base/jdk.internal.misc=ALL-UNNAMED \
     --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     --add-opens java.base/java.time=ALL-UNNAMED \
     --add-opens java.base/java.nio=ALL-UNNAMED \
     -Dspring.profiles.active=prod \
     -jar target/bad-vsb.jar
```
