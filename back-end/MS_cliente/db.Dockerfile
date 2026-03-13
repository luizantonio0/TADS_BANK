FROM postgres:15.17-trixie

WORKDIR /

COPY ./init-scripts /docker-entrypoint-initdb.d/

ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=123456
ENV POSTGRES_DB=cliente_db

EXPOSE 5432
