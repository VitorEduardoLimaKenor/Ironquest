-- Módulo treino: catálogo de exercícios, planos, sessões, séries e recordes pessoais.

CREATE TABLE grupo_muscular (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(50)  NOT NULL,
    descricao     VARCHAR(255) NULL,
    criado_em     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_grupo_muscular PRIMARY KEY (id),
    CONSTRAINT uk_grupo_muscular_nome UNIQUE (nome)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE exercicio (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(100) NOT NULL,
    descricao         VARCHAR(500) NULL,
    grupo_muscular_id BIGINT       NOT NULL,
    tipo              VARCHAR(20)  NOT NULL,
    equipamento       VARCHAR(20)  NOT NULL,
    ativo             BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em         DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_exercicio PRIMARY KEY (id),
    CONSTRAINT uk_exercicio_nome UNIQUE (nome),
    CONSTRAINT fk_exercicio_grupo_muscular FOREIGN KEY (grupo_muscular_id) REFERENCES grupo_muscular (id),
    CONSTRAINT ck_exercicio_tipo CHECK (tipo IN ('COMPOSTO', 'ISOLADO')),
    CONSTRAINT ck_exercicio_equipamento CHECK (equipamento IN ('BARRA', 'HALTER', 'MAQUINA', 'CABO', 'PESO_CORPORAL', 'KETTLEBELL', 'OUTRO'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE plano_treino (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id    BIGINT       NOT NULL,
    autor_id      BIGINT       NULL,
    nome          VARCHAR(100) NOT NULL,
    descricao     VARCHAR(500) NULL,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_plano_treino PRIMARY KEY (id),
    CONSTRAINT fk_plano_treino_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_plano_treino_autor FOREIGN KEY (autor_id) REFERENCES usuario (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE item_plano_treino (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    plano_treino_id   BIGINT       NOT NULL,
    exercicio_id      BIGINT       NOT NULL,
    ordem             INT          NOT NULL,
    series_alvo       INT          NOT NULL,
    repeticoes_alvo   INT          NOT NULL,
    descanso_segundos INT          NULL,
    observacao        VARCHAR(255) NULL,
    criado_em         DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_item_plano_treino PRIMARY KEY (id),
    CONSTRAINT uk_item_plano_treino_ordem UNIQUE (plano_treino_id, ordem),
    CONSTRAINT fk_item_plano_treino_plano FOREIGN KEY (plano_treino_id) REFERENCES plano_treino (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_plano_treino_exercicio FOREIGN KEY (exercicio_id) REFERENCES exercicio (id),
    CONSTRAINT ck_item_plano_treino_ordem CHECK (ordem >= 1),
    CONSTRAINT ck_item_plano_treino_series CHECK (series_alvo BETWEEN 1 AND 20),
    CONSTRAINT ck_item_plano_treino_repeticoes CHECK (repeticoes_alvo BETWEEN 1 AND 100),
    CONSTRAINT ck_item_plano_treino_descanso CHECK (descanso_segundos IS NULL OR descanso_segundos BETWEEN 0 AND 900)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE sessao_treino (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    usuario_id      BIGINT        NOT NULL,
    plano_treino_id BIGINT        NULL,
    status          VARCHAR(20)   NOT NULL,
    iniciada_em     DATETIME(6)   NOT NULL,
    finalizada_em   DATETIME(6)   NULL,
    volume_total_kg DECIMAL(12,2) NOT NULL DEFAULT 0,
    observacoes     VARCHAR(500)  NULL,
    versao          BIGINT        NOT NULL DEFAULT 0,
    criado_em       DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em   DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_sessao_treino PRIMARY KEY (id),
    CONSTRAINT fk_sessao_treino_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_sessao_treino_plano FOREIGN KEY (plano_treino_id) REFERENCES plano_treino (id) ON DELETE SET NULL,
    CONSTRAINT ck_sessao_treino_status CHECK (status IN ('EM_ANDAMENTO', 'FINALIZADA', 'CANCELADA')),
    CONSTRAINT ck_sessao_treino_periodo CHECK (finalizada_em IS NULL OR finalizada_em >= iniciada_em),
    CONSTRAINT ck_sessao_treino_volume CHECK (volume_total_kg >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Histórico do usuário ordenado por data (painel de evolução).
CREATE INDEX ix_sessao_treino_usuario_iniciada ON sessao_treino (usuario_id, iniciada_em);

CREATE TABLE serie (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    sessao_treino_id  BIGINT        NOT NULL,
    exercicio_id      BIGINT        NOT NULL,
    ordem             INT           NOT NULL,
    tipo              VARCHAR(20)   NOT NULL,
    repeticoes        INT           NOT NULL,
    carga_kg          DECIMAL(6,2)  NOT NULL,
    rpe               DECIMAL(3,1)  NULL,
    rir               INT           NULL,
    volume_kg         DECIMAL(10,2) NOT NULL,
    um_rm_estimado_kg DECIMAL(7,2)  NOT NULL,
    criado_em         DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em     DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_serie PRIMARY KEY (id),
    CONSTRAINT uk_serie_sessao_ordem UNIQUE (sessao_treino_id, ordem),
    CONSTRAINT fk_serie_sessao_treino FOREIGN KEY (sessao_treino_id) REFERENCES sessao_treino (id) ON DELETE CASCADE,
    CONSTRAINT fk_serie_exercicio FOREIGN KEY (exercicio_id) REFERENCES exercicio (id),
    CONSTRAINT ck_serie_tipo CHECK (tipo IN ('AQUECIMENTO', 'TRABALHO')),
    CONSTRAINT ck_serie_ordem CHECK (ordem >= 1),
    CONSTRAINT ck_serie_repeticoes CHECK (repeticoes BETWEEN 1 AND 100),
    CONSTRAINT ck_serie_carga CHECK (carga_kg >= 0),
    CONSTRAINT ck_serie_rpe CHECK (rpe IS NULL OR rpe BETWEEN 1 AND 10),
    CONSTRAINT ck_serie_rir CHECK (rir IS NULL OR rir BETWEEN 0 AND 10)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE recorde_pessoal (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    usuario_id    BIGINT        NOT NULL,
    exercicio_id  BIGINT        NOT NULL,
    tipo          VARCHAR(30)   NOT NULL,
    valor         DECIMAL(10,2) NOT NULL,
    serie_id      BIGINT        NULL,
    alcancado_em  DATETIME(6)   NOT NULL,
    criado_em     DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_recorde_pessoal PRIMARY KEY (id),
    -- Apenas o melhor resultado atual por usuário, exercício e tipo de recorde.
    CONSTRAINT uk_recorde_pessoal_usuario_exercicio_tipo UNIQUE (usuario_id, exercicio_id, tipo),
    CONSTRAINT fk_recorde_pessoal_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_recorde_pessoal_exercicio FOREIGN KEY (exercicio_id) REFERENCES exercicio (id),
    CONSTRAINT fk_recorde_pessoal_serie FOREIGN KEY (serie_id) REFERENCES serie (id) ON DELETE SET NULL,
    CONSTRAINT ck_recorde_pessoal_tipo CHECK (tipo IN ('MAIOR_CARGA', 'MAIOR_1RM_ESTIMADO', 'MAIOR_VOLUME_SERIE')),
    CONSTRAINT ck_recorde_pessoal_valor CHECK (valor >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
