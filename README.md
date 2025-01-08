## Chat App for the lecture `Advanced Software Engineering`

### Building the docker container: 
```shell
docker build -t aswe-chat-app .
```

### Running the docker container:
```shell
docker run -it aswe-chat-app
```

### Running the container inside IntelliJ (not recommended)
Either:
- Execute the run configuration `execute-shadow-jar`
- Execute the run configuration `build-shadow-jar`, then run the newly generated jar file inside `build/libs` using a JDK with version 23 or higher

NOTE: You need to have a JDK with version 23 or higher installed on your system to run this inside IntelliJ.
You can't use this inside the application run configuration, since the console bindings are incorrect there, causing some errors.