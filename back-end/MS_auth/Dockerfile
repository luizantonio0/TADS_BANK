FROM gradle:9.4.1-jdk21
USER root
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN chown -R gradle:gradle /app
USER gradle
ENV GRADLE_USER_HOME=/home/gradle/.gradle
CMD ["gradle", "clean", "bootRun", "--no-daemon"]
