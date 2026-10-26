-- Checkpoint 2: data do treino (LocalDate) para filtrar sessões por período e montar o histórico por dia.

ALTER TABLE sessao_treino ADD COLUMN data_treino DATE NULL AFTER iniciada_em;

-- Sessões já registradas: dia no fuso de Brasília (UTC-3, sem horário de verão desde 2019).
UPDATE sessao_treino SET data_treino = DATE(CONVERT_TZ(iniciada_em, '+00:00', '-03:00'));

ALTER TABLE sessao_treino MODIFY data_treino DATE NOT NULL;

-- Pesquisa de sessões do usuário por período.
CREATE INDEX ix_sessao_treino_usuario_data ON sessao_treino (usuario_id, data_treino);
