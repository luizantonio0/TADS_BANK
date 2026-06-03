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
('c5ed645a-05ef-4a21-a847-4ecc0622cb58', '1291', '12912861012', '98574307084', 800.00, 5000.00, NOW()),
('1b732a9b-82e9-4baf-86d4-737c4d3a34af', '0950', '09506382000', '64065268052', -10000.00, 10000.00, NOW()),
('cb30db11-e65c-44e5-97da-b1fc3d95ed40', '8573', '85733854057', '23862179060', -1000.00, 1500.00, NOW()),
('e44251f8-dfa1-4638-8c6e-3c2edf815cf1', '5887', '58872160006', '98574307084', 150000.00, 0.00, NOW()),
('d67612cc-ea11-4667-82b2-b1b48cd6e017', '7617', '76179646090', '64065268052', 1500.00, 0.00, NOW());

INSERT INTO public.tb_movimentacao 
(id, data_hora, tipo, conta_origem, conta_destino, valor)
VALUES 
('d67612cc-ea11-4667-82b2-b1b48cd6e017', '2020-01-01 10:00:00', 'DEPOSITO', '1291', NULL, 1000.00),
('767eb3c8-15d0-42da-bc05-9ed9c4e9ab50', '2020-01-01 11:00:00', 'DEPOSITO', '1291', NULL, 900.00),
('647c85de-b4b4-485f-a8fa-5e977810e47d', '2020-01-01 12:00:00', 'SAQUE', '1291', NULL, 550.00),
('c579be8a-5a8d-4960-812a-d73b49a1111a', '2020-01-01 13:00:00', 'SAQUE', '1291', NULL, 350.00),
('1047a46d-d329-4f98-af06-0892263bccd6', '2020-01-10 15:00:00', 'DEPOSITO', '1291', NULL, 2000.00),
('9c88fbdf-0d76-4be6-b77e-9622c2be2cb6', '2020-01-15 08:00:00', 'SAQUE', '1291', NULL, 500.00),
('8c69b96e-7925-4cb5-8875-a47dccaabae0', '2020-01-20 12:00:00', 'TRANSFERENCIA', '1291', '0950', 1700.00),
('f6fa1365-f1e9-4fc0-a8da-1716c4f765ed', '2025-01-01 12:00:00', 'DEPOSITO', '0950', NULL, 1000.00),
('93611a0a-af09-4b7f-aa92-3e633a7026ee', '2025-01-02 10:00:00', 'DEPOSITO', '0950', NULL, 5000.00),
('8f2a7102-b207-4697-9f99-4b80931b272a', '2025-01-10 10:00:00', 'SAQUE', '0950', NULL, 200.00),
('bae41297-12b5-48d1-8edf-23f3a09aaf96', '2025-02-05 10:00:00', 'DEPOSITO', '0950', NULL, 7000.00),
('685f1e32-7756-41de-9d01-e32a1a8814b6', '2025-05-05 10:00:00', 'DEPOSITO', '8573', NULL, 1000.00),
('ae1a2cbb-8631-467c-aabb-3fac63acbb6b', '2025-05-06 10:00:00', 'SAQUE', '8573', NULL, 2000.00),
('170e5950-44a8-4f50-ad90-331c76e1b4f0', '2025-01-06 10:00:00', 'DEPOSITO', '5887', NULL, 150000.00),
('0f96f1b4-81b9-4d40-a312-4c5d082c78cc', '2025-01-07 10:00:00', 'DEPOSITO', '7617', NULL, 1500.00);