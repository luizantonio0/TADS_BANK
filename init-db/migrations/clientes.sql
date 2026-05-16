CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE public.tb_cliente (
    id uuid NOT NULL,
    aprovado bool NULL,
    estado varchar(2) NULL,
    salario numeric(38, 2) NULL,
    cep varchar(8) NULL,
    criacao timestamp(6) NULL,
    cpf varchar(11) NOT NULL,
    telefone varchar(11) NULL,
    cidade varchar(30) NULL,
    nome varchar(30) NULL,
    cpf_gerente varchar(255) NULL,
    email varchar(255) NOT NULL,
    endereco varchar(255) NULL,
    CONSTRAINT tb_cliente_cpf_key UNIQUE (cpf),
    CONSTRAINT tb_cliente_email_key UNIQUE (email),
    CONSTRAINT tb_cliente_estado_check CHECK (((estado)::text = ANY ((ARRAY['AC'::character varying, 'AL'::character varying, 'AP'::character varying, 'AM'::character varying, 'BA'::character varying, 'CE'::character varying, 'DF'::character varying, 'ES'::character varying, 'GO'::character varying, 'MA'::character varying, 'MT'::character varying, 'MS'::character varying, 'MG'::character varying, 'PA'::character varying, 'PB'::character varying, 'PR'::character varying, 'PE'::character varying, 'PI'::character varying, 'RJ'::character varying, 'RN'::character varying, 'RS'::character varying, 'RO'::character varying, 'RR'::character varying, 'SC'::character varying, 'SP'::character varying, 'SE'::character varying, 'TO'::character varying])::text[]))),
    CONSTRAINT tb_cliente_pkey PRIMARY KEY (id)
); 

INSERT INTO public.tb_cliente 
(id, cpf, nome, email, salario, telefone, aprovado, cpf_gerente, estado, cidade, cep, endereco, criacao)
VALUES 
(uuid_generate_v4(), '12912861012', 'Catharyna', 'cli1@bantads.com.br', 10000.00, '19948208842', true, '98574307084' , 'SE', 'Aracaju', '49048320', 'Rua Radialista Wolney Silva, 100, Luzia', NOW()),
(uuid_generate_v4(), '09506382000', 'Cleuddônio', 'cli2@bantads.com.br', 20000.00, '41995292929', true, '64065268052' , 'SC', 'Brusque', '88354670', 'Rua Maximiliano Furbringer, 500, Jardim Maluche', NOW()),
(uuid_generate_v4(), '85733854057', 'Catianna', 'cli3@bantads.com.br', 3000.00, '22924402941', true, '23862179060' , 'SE', 'Aracaju', '49030790', 'Rua Edson de Oliveira, 33, Farolândia', NOW()),
(uuid_generate_v4(), '58872160006', 'Cutardo', 'cli4@bantads.com.br', 500.00, '87992429912', true, '98574307084' , 'AC', 'Rio Branco', '69902136', 'Rua Juruá, 200, Loteamento Jardim São Francisco', NOW()),
(uuid_generate_v4(), '76179646090', 'Coândrya', 'cli5@bantads.com.br', 1500.00, '18989882942', true, '64065268052' , 'BA', 'Salvador', '40393700', 'Travessa Candiubá, 39, Capelinha', NOW());