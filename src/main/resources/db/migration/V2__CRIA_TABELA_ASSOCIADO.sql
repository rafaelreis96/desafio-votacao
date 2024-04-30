

CREATE SEQUENCE IF NOT EXISTS seq_associado;

CREATE TABLE IF NOT EXISTS associado (
    cod_associado BIGINT NOT NULL DEFAULT NEXTVAL('seq_associado'),
    nome varchar(255) NOT NULL,
    cpf varchar(11) UNIQUE NOT NULL,

    CONSTRAINT pk_associado PRIMARY KEY (cod_associado)
);
