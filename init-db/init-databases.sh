#!/bin/bash
set -e

echo "Criando os bancos de dados..."
psql -v ON_ERROR_STOP=1 --username "admin" --dbname "postgres" -c "CREATE DATABASE ms_cliente;"
psql -v ON_ERROR_STOP=1 --username "admin" --dbname "postgres" -c "CREATE DATABASE ms_gerente;"
psql -v ON_ERROR_STOP=1 --username "admin" --dbname "postgres" -c "CREATE DATABASE ms_contas;"

echo "Iniciando migrations"
psql -v ON_ERROR_STOP=1 --username "admin" --dbname "ms_cliente" -f /docker-entrypoint-initdb.d/migrations/clientes.sql
psql -v ON_ERROR_STOP=1 --username "admin" --dbname "ms_gerente" -f /docker-entrypoint-initdb.d/migrations/gerentes.sql
psql -v ON_ERROR_STOP=1 --username "admin" --dbname "ms_contas" -f /docker-entrypoint-initdb.d/migrations/contas.sql
