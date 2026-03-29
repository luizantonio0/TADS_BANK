FROM container-registry.oracle.com/graalvm/native-image:21
WORKDIR /app
COPY . .

#ENV JAVA_HOME=/usr/lib64/graalvm/graalvm-java21/bin/java
##ENV PATH=$JAVA_HOME/bin:$PATH

RUN java -version
RUN echo $JAVA_HOME

RUN chmod +x ./gradlew
