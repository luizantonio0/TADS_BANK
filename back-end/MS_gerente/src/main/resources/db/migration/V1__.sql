CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE revinfo
(
    rev      INTEGER NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

CREATE TABLE tb_gerente
(
    id             UUID         NOT NULL,
    cpf            VARCHAR(11)  NOT NULL,
    nome           VARCHAR(20)  NOT NULL,
    email          VARCHAR(128) NOT NULL,
    senha          VARCHAR(255) NOT NULL,
    tipo           VARCHAR(255) NOT NULL,
    total_clientes INTEGER      NOT NULL,
    CONSTRAINT pk_tb_gerente PRIMARY KEY (id)
);

CREATE TABLE tb_gerente_aud
(
    rev            INTEGER NOT NULL,
    revtype        SMALLINT,
    id             UUID    NOT NULL,
    cpf            VARCHAR(11),
    nome           VARCHAR(20),
    email          VARCHAR(128),
    senha          VARCHAR(255),
    tipo           VARCHAR(255),
    total_clientes INTEGER,
    CONSTRAINT pk_tb_gerente_aud PRIMARY KEY (rev, id)
);

ALTER TABLE tb_gerente_aud
    ADD CONSTRAINT FK_TB_GERENTE_AUD_ON_REV FOREIGN KEY (rev) REFERENCES revinfo (rev);