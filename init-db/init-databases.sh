#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "admin" --dbname "postgres" <<-EOSQL
    CREATE DATABASE ms_cliente;
    CREATE DATABASE ms_gerente;
    CREATE DATABASE ms_contas;
    
    GRANT ALL PRIVILEGES ON DATABASE ms_cliente TO admin;
    GRANT ALL PRIVILEGES ON DATABASE ms_gerente TO admin;
    GRANT ALL PRIVILEGES ON DATABASE ms_contas TO admin;
EOSQL

echo "Iniciando migrations"
psql -v ON_ERROR_STOP=1 --username "admin" --dbname "ms_cliente" -f /docker-entrypoint-initdb.d/migrations/clientes.sql
psql -v ON_ERROR_STOP=1 --username "admin" --dbname "ms_gerente" -f /docker-entrypoint-initdb.d/migrations/gerentes.sql
psql -v ON_ERROR_STOP=1 --username "admin" --dbname "ms_contas" -f /docker-entrypoint-initdb.d/migrations/contas.sql