#!/bin/bash
set -e

# O comando psql usará as variáveis que definimos no docker-compose
psql -v ON_ERROR_STOP=1 --username "admin" --dbname "postgres" <<-EOSQL
    CREATE DATABASE ms_cliente;
    CREATE DATABASE ms_gerente;
    CREATE DATABASE ms_contas;
    
    GRANT ALL PRIVILEGES ON DATABASE ms_cliente TO admin;
    GRANT ALL PRIVILEGES ON DATABASE ms_gerente TO admin;
    GRANT ALL PRIVILEGES ON DATABASE ms_contas TO admin;
EOSQL