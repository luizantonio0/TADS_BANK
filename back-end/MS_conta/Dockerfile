FROM gradle:jdk21

WORKDIR /app

COPY build.gradle settings.gradle ./

RUN gradle dependencies --no-daemon

COPY src ./src

EXPOSE 8080

CMD ["gradle", "bootRun", "--no-daemon"]
