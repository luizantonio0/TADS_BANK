#!/usr/bin/env bash

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

if ! [ -x "$(command -v docker)" ]; then
  echo -e "${RED}[ERRO] Docker não está instalado. Por favor, instale o Docker para continuar.${NC}" >&2
  exit 1
fi

DOCKER_COMPOSE_CMD=""
if docker compose version >/dev/null 2>&1; then
  DOCKER_COMPOSE_CMD="docker compose"
elif docker-compose version >/dev/null 2>&1; then
  DOCKER_COMPOSE_CMD="docker-compose"
else
  echo -e "${RED}[ERRO] Docker Compose não foi encontrado.${NC}" >&2
  exit 1
fi

if [ ! -f .env ]; then
  echo -e "${YELLOW}[AVISO] Arquivo .env não encontrado. Criando um modelo básico...${NC}"
  echo "GMAIL_USERNAME=seu_email@gmail.com" > .env
  echo "GMAIL_PASSWORD=sua_senha_de_app" >> .env
  echo -e "${RED}[IMPORTANTE] Edite o arquivo .env com suas credenciais do Gmail antes de rodar os microsserviços.${NC}\n"
fi

echo -e "${YELLOW}[1/3] Limpando ambiente...${NC}"
$DOCKER_COMPOSE_CMD down --remove-orphans -v

echo -e "\n${YELLOW}[2/3] Buildando imagens e subindo containers...${NC}"
$DOCKER_COMPOSE_CMD up --build -d

if [ $? -eq 0 ]; then
  echo -e "\n${GREEN}[SUCESSO] Todos os serviços foram inicializados com sucesso!${NC}"
else
  echo -e "\n${RED}[ERRO] Falha ao subir os containers. Verifique as configurações.${NC}"
  exit 1
fi