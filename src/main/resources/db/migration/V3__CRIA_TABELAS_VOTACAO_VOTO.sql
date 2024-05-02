
-- TABLE VOTACAO

CREATE SEQUENCE IF NOT EXISTS seq_votacao;

CREATE TABLE IF NOT EXISTS votacao (
	cod_votacao BIGINT NOT NULL DEFAULT NEXTVAL('seq_votacao'),
    data_abertura timestamptz  NULL,
    data_encerramento timestamptz  NULL,

	CONSTRAINT pk_votacao PRIMARY KEY (cod_votacao)
);

-- TABLE VOTO

CREATE SEQUENCE IF NOT EXISTS seq_voto;

CREATE TABLE IF NOT EXISTS voto (
    cod_voto BIGINT NOT NULL DEFAULT NEXTVAL('seq_voto'),
    cod_votacao BIGINT NOT NULL,
    cod_associado BIGINT NOT NULL,
    opcao varchar(30) NOT NULL,

    CONSTRAINT ck_voto_opcao CHECK (opcao IN ('SIM', 'NAO')),

    CONSTRAINT pk_voto PRIMARY KEY (cod_voto),
    CONSTRAINT fk_votacao_cod_votacao FOREIGN KEY (cod_votacao) REFERENCES votacao(cod_votacao),
    CONSTRAINT fk_associado_cod_associado FOREIGN KEY (cod_associado) REFERENCES associado(cod_associado)
);

-- ALTER TABLE PAUTA

ALTER TABLE pauta
ADD COLUMN cod_votacao BIGINT NULL;

ALTER TABLE pauta
ADD CONSTRAINT fk_votacao_cod_votacao
FOREIGN KEY (cod_votacao) REFERENCES votacao(cod_votacao);
