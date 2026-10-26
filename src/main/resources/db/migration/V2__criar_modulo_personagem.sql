-- Módulo personagem: avatar RPG do usuário, objetivos pessoais e atributos.

CREATE TABLE personagem (
    id                      BIGINT      NOT NULL AUTO_INCREMENT,
    usuario_id              BIGINT      NOT NULL,
    nome                    VARCHAR(50) NOT NULL,
    objetivo_principal      VARCHAR(20) NOT NULL,
    frequencia_semanal_alvo INT         NOT NULL,
    nivel                   INT         NOT NULL DEFAULT 1,
    experiencia_total       BIGINT      NOT NULL DEFAULT 0,
    sequencia_atual         INT         NOT NULL DEFAULT 0,
    maior_sequencia         INT         NOT NULL DEFAULT 0,
    ultimo_treino_em        DATE        NULL,
    criado_em               DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em           DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_personagem PRIMARY KEY (id),
    CONSTRAINT uk_personagem_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_personagem_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT ck_personagem_objetivo CHECK (objetivo_principal IN ('FORCA', 'HIPERTROFIA', 'RESISTENCIA', 'CONDICIONAMENTO')),
    CONSTRAINT ck_personagem_frequencia CHECK (frequencia_semanal_alvo BETWEEN 1 AND 7),
    CONSTRAINT ck_personagem_nivel CHECK (nivel >= 1),
    CONSTRAINT ck_personagem_experiencia CHECK (experiencia_total >= 0),
    CONSTRAINT ck_personagem_sequencia CHECK (sequencia_atual >= 0 AND maior_sequencia >= sequencia_atual)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE atributo (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    personagem_id BIGINT      NOT NULL,
    tipo          VARCHAR(20) NOT NULL,
    nivel         INT         NOT NULL DEFAULT 1,
    experiencia   INT         NOT NULL DEFAULT 0,
    criado_em     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_atributo PRIMARY KEY (id),
    CONSTRAINT uk_atributo_personagem_tipo UNIQUE (personagem_id, tipo),
    CONSTRAINT fk_atributo_personagem FOREIGN KEY (personagem_id) REFERENCES personagem (id) ON DELETE CASCADE,
    CONSTRAINT ck_atributo_tipo CHECK (tipo IN ('FORCA', 'RESISTENCIA', 'VIGOR', 'DISCIPLINA')),
    CONSTRAINT ck_atributo_nivel CHECK (nivel >= 1),
    CONSTRAINT ck_atributo_experiencia CHECK (experiencia >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
