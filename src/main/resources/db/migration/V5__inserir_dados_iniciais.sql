-- Dados de referência: papéis, grupos musculares, exercícios, recompensas e conquistas.

INSERT INTO papel (nome, descricao) VALUES
    ('JOGADOR', 'Registra treinos e evolui o próprio personagem'),
    ('TREINADOR', 'Acompanha jogadores e cria planos de treino'),
    ('ADMINISTRADOR', 'Gerencia catálogos, conquistas e usuários');

INSERT INTO grupo_muscular (nome, descricao) VALUES
    ('Peito', 'Peitoral maior e menor'),
    ('Costas', 'Dorsais, trapézio e romboides'),
    ('Ombros', 'Deltoides anterior, lateral e posterior'),
    ('Bíceps', 'Flexores do cotovelo'),
    ('Tríceps', 'Extensores do cotovelo'),
    ('Quadríceps', 'Parte anterior da coxa'),
    ('Posterior de coxa', 'Isquiotibiais'),
    ('Glúteos', 'Glúteo máximo, médio e mínimo'),
    ('Panturrilhas', 'Gastrocnêmio e sóleo'),
    ('Abdômen', 'Reto abdominal, oblíquos e transverso');

-- Linhas em VALUES preservam a ordem dos ids (1 = supino reto, 11 = agachamento, 13 = terra...).
INSERT INTO exercicio (nome, descricao, grupo_muscular_id, tipo, equipamento) VALUES
    ('Supino reto com barra', 'Empurrar a barra a partir do peito, deitado em banco reto', (SELECT id FROM grupo_muscular WHERE nome = 'Peito'), 'COMPOSTO', 'BARRA'),
    ('Supino inclinado com halteres', 'Supino em banco inclinado entre 30 e 45 graus', (SELECT id FROM grupo_muscular WHERE nome = 'Peito'), 'COMPOSTO', 'HALTER'),
    ('Crucifixo no cabo', 'Adução horizontal dos braços na polia', (SELECT id FROM grupo_muscular WHERE nome = 'Peito'), 'ISOLADO', 'CABO'),
    ('Barra fixa', 'Puxada do próprio corpo até o queixo passar a barra', (SELECT id FROM grupo_muscular WHERE nome = 'Costas'), 'COMPOSTO', 'PESO_CORPORAL'),
    ('Remada curvada com barra', 'Remada com tronco inclinado à frente', (SELECT id FROM grupo_muscular WHERE nome = 'Costas'), 'COMPOSTO', 'BARRA'),
    ('Puxada frontal', 'Puxada na polia alta até a altura do peito', (SELECT id FROM grupo_muscular WHERE nome = 'Costas'), 'COMPOSTO', 'MAQUINA'),
    ('Desenvolvimento militar', 'Elevação da barra acima da cabeça, em pé', (SELECT id FROM grupo_muscular WHERE nome = 'Ombros'), 'COMPOSTO', 'BARRA'),
    ('Elevação lateral', 'Abdução dos braços com halteres', (SELECT id FROM grupo_muscular WHERE nome = 'Ombros'), 'ISOLADO', 'HALTER'),
    ('Rosca direta com barra', 'Flexão de cotovelos com barra', (SELECT id FROM grupo_muscular WHERE nome = 'Bíceps'), 'ISOLADO', 'BARRA'),
    ('Tríceps na polia', 'Extensão de cotovelos na polia alta', (SELECT id FROM grupo_muscular WHERE nome = 'Tríceps'), 'ISOLADO', 'CABO'),
    ('Agachamento livre', 'Agachamento com barra apoiada no trapézio', (SELECT id FROM grupo_muscular WHERE nome = 'Quadríceps'), 'COMPOSTO', 'BARRA'),
    ('Leg press 45', 'Empurrar a plataforma no aparelho inclinado', (SELECT id FROM grupo_muscular WHERE nome = 'Quadríceps'), 'COMPOSTO', 'MAQUINA'),
    ('Levantamento terra', 'Levantar a barra do chão até a extensão completa do quadril', (SELECT id FROM grupo_muscular WHERE nome = 'Posterior de coxa'), 'COMPOSTO', 'BARRA'),
    ('Mesa flexora', 'Flexão de joelhos deitado no aparelho', (SELECT id FROM grupo_muscular WHERE nome = 'Posterior de coxa'), 'ISOLADO', 'MAQUINA'),
    ('Elevação pélvica', 'Extensão de quadril com barra apoiada no quadril', (SELECT id FROM grupo_muscular WHERE nome = 'Glúteos'), 'COMPOSTO', 'BARRA'),
    ('Panturrilha em pé', 'Flexão plantar em pé no aparelho', (SELECT id FROM grupo_muscular WHERE nome = 'Panturrilhas'), 'ISOLADO', 'MAQUINA'),
    ('Prancha abdominal', 'Isometria em apoio de antebraços', (SELECT id FROM grupo_muscular WHERE nome = 'Abdômen'), 'ISOLADO', 'PESO_CORPORAL');

INSERT INTO recompensa (nome, descricao, experiencia, tipo_atributo, pontos_atributo) VALUES
    ('Bolsa de experiência pequena', '50 pontos de experiência', 50, NULL, 0),
    ('Bolsa de experiência média', '150 pontos de experiência', 150, NULL, 0),
    ('Tomo da Força', '100 de experiência e 1 ponto de Força', 100, 'FORCA', 1),
    ('Elixir da Disciplina', '100 de experiência e 1 ponto de Disciplina', 100, 'DISCIPLINA', 1),
    ('Espólio do Chefe', '300 de experiência e 2 pontos de Vigor', 300, 'VIGOR', 2);

INSERT INTO conquista (codigo, nome, descricao, regra, recompensa_id) VALUES
    ('PRIMEIRO_TREINO', 'Primeiro passo', 'Finalizou a primeira sessão de treino', 'Finalizar 1 sessão de treino', (SELECT id FROM recompensa WHERE nome = 'Bolsa de experiência pequena')),
    ('DEZ_SESSOES', 'Veterano da academia', 'Finalizou 10 sessões de treino', 'Finalizar 10 sessões de treino', (SELECT id FROM recompensa WHERE nome = 'Bolsa de experiência média')),
    ('PRIMEIRO_RECORDE', 'Quebrando limites', 'Bateu o primeiro recorde pessoal', 'Superar um recorde pessoal registrado anteriormente', (SELECT id FROM recompensa WHERE nome = 'Tomo da Força')),
    ('SEQUENCIA_7', 'Disciplina de ferro', 'Manteve a frequência semanal planejada por 7 treinos seguidos', 'Atingir sequência de 7 treinos sem quebrar a meta semanal', (SELECT id FROM recompensa WHERE nome = 'Elixir da Disciplina')),
    ('VOLUME_10T', 'Movedor de montanhas', 'Acumulou 10 toneladas de volume em uma única sessão', 'Volume total da sessão maior ou igual a 10.000 kg', (SELECT id FROM recompensa WHERE nome = 'Bolsa de experiência média'));
