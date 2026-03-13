#!/bin/bash
docker build -t ms_cliente_image -f db.Dockerfile . &&
docker run -d --name ms_cliente_container -p 5000:5432 ms_cliente_image