

CREATE SEQUENCE IF NOT EXISTS seq_pauta;

CREATE TABLE IF NOT EXISTS pauta (
	cod_pauta BIGINT NOT NULL DEFAULT NEXTVAL('seq_pauta'),
	titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,


	CONSTRAINT pk_pauta PRIMARY KEY (cod_pauta)
)