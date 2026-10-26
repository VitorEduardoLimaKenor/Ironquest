-- Módulo usuário: contas, papéis e RBAC (jogador, treinador, administrador).

CREATE TABLE papel (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(30)  NOT NULL,
    descricao     VARCHAR(255) NOT NULL,
    criado_em     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_papel PRIMARY KEY (id),
    CONSTRAINT uk_papel_nome UNIQUE (nome),
    CONSTRAINT ck_papel_nome CHECK (nome IN ('JOGADOR', 'TREINADOR', 'ADMINISTRADOR'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE usuario (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    senha_hash    VARCHAR(100) NOT NULL,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email UNIQUE (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE usuario_papel (
    usuario_id BIGINT NOT NULL,
    papel_id   BIGINT NOT NULL,
    CONSTRAINT pk_usuario_papel PRIMARY KEY (usuario_id, papel_id),
    CONSTRAINT fk_usuario_papel_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_papel_papel FOREIGN KEY (papel_id) REFERENCES papel (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
