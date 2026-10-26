-- Módulo progressão: recompensas, conquistas, missões, objetivos e eventos de progressão.

CREATE TABLE recompensa (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nome            VARCHAR(100) NOT NULL,
    descricao       VARCHAR(255) NULL,
    experiencia     INT          NOT NULL DEFAULT 0,
    tipo_atributo   VARCHAR(20)  NULL,
    pontos_atributo INT          NOT NULL DEFAULT 0,
    criado_em       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_recompensa PRIMARY KEY (id),
    CONSTRAINT ck_recompensa_experiencia CHECK (experiencia >= 0),
    CONSTRAINT ck_recompensa_tipo_atributo CHECK (tipo_atributo IS NULL OR tipo_atributo IN ('FORCA', 'RESISTENCIA', 'VIGOR', 'DISCIPLINA')),
    CONSTRAINT ck_recompensa_pontos_atributo CHECK (pontos_atributo >= 0),
    -- Pontos de atributo exigem um atributo de destino.
    CONSTRAINT ck_recompensa_atributo_coerente CHECK (pontos_atributo = 0 OR tipo_atributo IS NOT NULL)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE conquista (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    codigo        VARCHAR(50)  NOT NULL,
    nome          VARCHAR(100) NOT NULL,
    descricao     VARCHAR(255) NOT NULL,
    regra         VARCHAR(255) NOT NULL,
    recompensa_id BIGINT       NULL,
    criado_em     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_conquista PRIMARY KEY (id),
    CONSTRAINT uk_conquista_codigo UNIQUE (codigo),
    CONSTRAINT fk_conquista_recompensa FOREIGN KEY (recompensa_id) REFERENCES recompensa (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE conquista_personagem (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    personagem_id BIGINT      NOT NULL,
    conquista_id  BIGINT      NOT NULL,
    obtida_em     DATETIME(6) NOT NULL,
    criado_em     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_conquista_personagem PRIMARY KEY (id),
    -- Cada conquista é concedida uma única vez por personagem.
    CONSTRAINT uk_conquista_personagem UNIQUE (personagem_id, conquista_id),
    CONSTRAINT fk_conquista_personagem_personagem FOREIGN KEY (personagem_id) REFERENCES personagem (id) ON DELETE CASCADE,
    CONSTRAINT fk_conquista_personagem_conquista FOREIGN KEY (conquista_id) REFERENCES conquista (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE missao (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    personagem_id       BIGINT       NOT NULL,
    titulo              VARCHAR(100) NOT NULL,
    descricao           VARCHAR(500) NULL,
    tipo                VARCHAR(20)  NOT NULL,
    status              VARCHAR(20)  NOT NULL,
    inicia_em           DATETIME(6)  NOT NULL,
    prazo_em            DATETIME(6)  NOT NULL,
    concluida_em        DATETIME(6)  NULL,
    recompensa_id       BIGINT       NULL,
    nome_chefe          VARCHAR(100) NULL,
    vida_total_chefe    INT          NULL,
    vida_restante_chefe INT          NULL,
    criado_em           DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_missao PRIMARY KEY (id),
    CONSTRAINT fk_missao_personagem FOREIGN KEY (personagem_id) REFERENCES personagem (id) ON DELETE CASCADE,
    CONSTRAINT fk_missao_recompensa FOREIGN KEY (recompensa_id) REFERENCES recompensa (id) ON DELETE SET NULL,
    CONSTRAINT ck_missao_tipo CHECK (tipo IN ('SEMANAL', 'DESAFIO', 'CHEFE')),
    CONSTRAINT ck_missao_status CHECK (status IN ('ATIVA', 'CONCLUIDA', 'FALHADA', 'EXPIRADA')),
    CONSTRAINT ck_missao_prazo CHECK (prazo_em > inicia_em),
    -- Batalhas contra chefe exigem nome e pontos de vida válidos.
    CONSTRAINT ck_missao_chefe CHECK (
        tipo <> 'CHEFE'
        OR (nome_chefe IS NOT NULL AND vida_total_chefe > 0 AND vida_restante_chefe BETWEEN 0 AND vida_total_chefe)
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX ix_missao_personagem_status ON missao (personagem_id, status);

CREATE TABLE objetivo (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    missao_id     BIGINT        NOT NULL,
    descricao     VARCHAR(255)  NOT NULL,
    metrica       VARCHAR(30)   NOT NULL,
    exercicio_id  BIGINT        NULL,
    valor_alvo    DECIMAL(12,2) NOT NULL,
    valor_atual   DECIMAL(12,2) NOT NULL DEFAULT 0,
    concluido     BOOLEAN       NOT NULL DEFAULT FALSE,
    criado_em     DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_objetivo PRIMARY KEY (id),
    CONSTRAINT fk_objetivo_missao FOREIGN KEY (missao_id) REFERENCES missao (id) ON DELETE CASCADE,
    CONSTRAINT fk_objetivo_exercicio FOREIGN KEY (exercicio_id) REFERENCES exercicio (id),
    CONSTRAINT ck_objetivo_metrica CHECK (metrica IN ('VOLUME_TOTAL', 'SESSOES_CONCLUIDAS', 'SERIES_CONCLUIDAS', 'CARGA_MAXIMA', 'UM_RM_ESTIMADO')),
    CONSTRAINT ck_objetivo_valores CHECK (valor_alvo > 0 AND valor_atual >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE evento_progressao (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    personagem_id      BIGINT       NOT NULL,
    tipo               VARCHAR(30)  NOT NULL,
    regra              VARCHAR(50)  NOT NULL,
    descricao          VARCHAR(255) NOT NULL,
    experiencia        INT          NOT NULL DEFAULT 0,
    -- Chave determinística (ex.: SESSAO:42:XP_BASE). O índice único impede premiação duplicada em reenvios.
    chave_idempotencia VARCHAR(150) NOT NULL,
    sessao_treino_id   BIGINT       NULL,
    missao_id          BIGINT       NULL,
    conquista_id       BIGINT       NULL,
    ocorrido_em        DATETIME(6)  NOT NULL,
    criado_em          DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_evento_progressao PRIMARY KEY (id),
    CONSTRAINT uk_evento_progressao_chave UNIQUE (chave_idempotencia),
    CONSTRAINT fk_evento_progressao_personagem FOREIGN KEY (personagem_id) REFERENCES personagem (id) ON DELETE CASCADE,
    CONSTRAINT fk_evento_progressao_sessao FOREIGN KEY (sessao_treino_id) REFERENCES sessao_treino (id) ON DELETE SET NULL,
    CONSTRAINT fk_evento_progressao_missao FOREIGN KEY (missao_id) REFERENCES missao (id) ON DELETE SET NULL,
    CONSTRAINT fk_evento_progressao_conquista FOREIGN KEY (conquista_id) REFERENCES conquista (id) ON DELETE SET NULL,
    CONSTRAINT ck_evento_progressao_tipo CHECK (tipo IN ('EXPERIENCIA_GANHA', 'NIVEL_ALCANCADO', 'ATRIBUTO_EVOLUIDO', 'CONQUISTA_OBTIDA', 'MISSAO_CONCLUIDA', 'RECORDE_PESSOAL')),
    CONSTRAINT ck_evento_progressao_experiencia CHECK (experiencia >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX ix_evento_progressao_personagem_ocorrido ON evento_progressao (personagem_id, ocorrido_em);
