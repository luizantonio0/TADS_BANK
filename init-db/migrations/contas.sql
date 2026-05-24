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

INSERT INTO public.tb_conta 
(id, conta, cpf, cpf_gerente, saldo, limite, criacao)
VALUES 
(uuid_generate_v4(), '1291', '12912861012', '98574307084', 800.00, 5000.00, NOW()),
(uuid_generate_v4(), '0950', '09506382000', '64065268052', -10000.00, 10000.00, NOW()),
(uuid_generate_v4(), '8573', '85733854057', '23862179060', -1000.00, 1500.00, NOW()),
(uuid_generate_v4(), '5887', '58872160006', '98574307084', 150000.00, 0.00, NOW()),
(uuid_generate_v4(), '7617', '76179646090', '64065268052', 1500.00, 0.00, NOW());

INSERT INTO public.tb_movimentacao 
(id, data_hora, tipo, conta_origem, conta_destino, valor)
VALUES 
(uuid_generate_v4(), '2020-01-01 10:00:00', 'DEPOSITO', '1291', NULL, 1000.00),
(uuid_generate_v4(), '2020-01-01 11:00:00', 'DEPOSITO', '1291', NULL, 900.00),
(uuid_generate_v4(), '2020-01-01 12:00:00', 'SAQUE', '1291', NULL, 550.00),
(uuid_generate_v4(), '2020-01-01 13:00:00', 'SAQUE', '1291', NULL, 350.00),
(uuid_generate_v4(), '2020-01-10 15:00:00', 'DEPOSITO', '1291', NULL, 2000.00),
(uuid_generate_v4(), '2020-01-15 08:00:00', 'SAQUE', '1291', NULL, 500.00),
(uuid_generate_v4(), '2020-01-20 12:00:00', 'TRANSFERENCIA', '1291', '0950', 1700.00),
(uuid_generate_v4(), '2025-01-01 12:00:00', 'DEPOSITO', '0950', NULL, 1000.00),
(uuid_generate_v4(), '2025-01-02 10:00:00', 'DEPOSITO', '0950', NULL, 5000.00),
(uuid_generate_v4(), '2025-01-10 10:00:00', 'SAQUE', '0950', NULL, 200.00),
(uuid_generate_v4(), '2025-02-05 10:00:00', 'DEPOSITO', '0950', NULL, 7000.00),
(uuid_generate_v4(), '2025-05-05 10:00:00', 'DEPOSITO', '8573', NULL, 1000.00),
(uuid_generate_v4(), '2025-05-06 10:00:00', 'SAQUE', '8573', NULL, 2000.00),
(uuid_generate_v4(), '2025-01-06 10:00:00', 'DEPOSITO', '5887', NULL, 150000.00),
(uuid_generate_v4(), '2025-01-07 10:00:00', 'DEPOSITO', '7617', NULL, 1500.00);