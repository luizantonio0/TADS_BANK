CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE public.tb_gerente (
    id uuid NOT NULL,
    total_clientes int4 NOT NULL,
    cpf varchar(11) NOT NULL,
    nome varchar(20) NOT NULL,
    email varchar(128) NOT NULL,
    tipo varchar(255) NOT NULL,
    telefone varchar(11) DEFAULT '',
    CONSTRAINT tb_gerente_pkey PRIMARY KEY (id)
); 

INSERT INTO public.tb_gerente 
(id, cpf, nome, email, telefone, tipo, total_clientes)
VALUES 
(uuid_generate_v4(), '98574307084', 'Geniéve', 'ger1@bantads.com.br', '49998925524', 'GERENTE', 2),
(uuid_generate_v4(), '64065268052', 'Godophredo', 'ger2@bantads.com.br', '41903324922', 'GERENTE', 2),
(uuid_generate_v4(), '23862179060', 'Gyândula', 'ger3@bantads.com.br', '81994829002', 'GERENTE', 1),
(uuid_generate_v4(), '40501740066', 'Adamântio', 'adm1@bantads.com.br','54994229202', 'ADMINISTRADOR', 0);