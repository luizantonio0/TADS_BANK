CREATE TABLE IF NOT EXISTS tb_cliente (
    id UUID PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    nome VARCHAR(30),
    telefone VARCHAR(11),
    salario NUMERIC(19, 2),
    cep VARCHAR(8),
    cidade VARCHAR(30),
    estado VARCHAR(2)
    );

CREATE INDEX IF NOT EXISTS idx_cliente_cpf ON tb_cliente(cpf);