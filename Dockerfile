FROM postgres:15-alpine

RUN apk add --no-cache dos2unix

RUN dos2unix /docker-entrypoint-initdb.d/*.sh || true