CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE public.tb_conta (
    id uuid NOT NULL,
    limite numeric(38, 2) NOT NULL,
    saldo numeric(38, 2) NOT NULL,
    criacao timestamp(6) NOT NULL,
    conta varchar(10) NOT NULL,
    cpf varchar(11) NOT NULL,
    cpf_gerente varchar(11) NOT NULL,
    CONSTRAINT tb_conta_conta_key UNIQUE (conta),
    CONSTRAINT tb_conta_cpf_key UNIQUE (cpf),
    CONSTRAINT tb_conta_pkey PRIMARY KEY (id)
); 

CREATE TABLE public.tb_movimentacao (
    id uuid NOT NULL,
    valor numeric(38, 2) NOT NULL,
    data_hora timestamp(6) NOT NULL,
    conta_destino varchar(10) NULL,
    conta_origem varchar(10) NOT NULL,
    tipo varchar(255) NOT NULL,
    CONSTRAINT tb_movimentacao_pkey PRIMARY KEY (id),
    CONSTRAINT tb_movimentacao_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['DEPOSITO'::character varying, 'SAQUE'::character varying, 'TRANSFERENCIA'::character varying])::text[])))
); 