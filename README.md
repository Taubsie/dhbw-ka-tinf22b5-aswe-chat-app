## Chat App for the lecture `Advanced Software Engineering`

### Building the docker container: 
```shell
docker build -t aswe-chat-app .
```

### Running the docker container:
This is only supported on Linux out-of-the-box, due to the `network` option not being supported on Windows.
```shell
docker run -it --network="host" aswe-chat-app
```

### Running the container inside IntelliJ (not recommended)
Either:
- Execute the run configuration `execute-shadow-jar`
  - This only works if your main JRE is of version 23 or higher
- Execute the run configuration `execute-shadow-jar-fixed-jdk`
  - This only works on Windows and only if you downloaded `OpenJDK 23.0.1` through IntelliJ, as that run configuration executes:
      ```shell
        %userprofile%\.jdks\openjdk-23.0.1\bin\java -jar build/libs/aswe-chat-app-1.0.0-all.jar
      ```
- Execute the run configuration `build-shadow-jar`, then run the jar file ending in `-all.jar` which was newly generated inside `build/libs` using a JDK with version 23 or higher

NOTE: You need to have a JDK with version 23 or higher installed on your system to run this inside IntelliJ.
You can't use this inside the application run configuration, since the console bindings are incorrect there, causing some errors.