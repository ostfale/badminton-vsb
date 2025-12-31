# Badminton Statistics Web Application

dd

# Internal

## Configuration

### Links
    + https://vaadin.com/docs/latest/flow/integrations/spring/configuration#special-configuration-parameters

### Run Application

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw                                                               # runs `mvn spring-boot:run` for you
./mvnw package                                                       # builds a jar file
docker build -t my-application:latest .                              # builds a docker image
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .           # builds a docker image with a commercial component
```
