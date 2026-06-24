-- =============================================================
-- data.sql — Dados fictícios para o CRM Vigenere
-- Ordem: usuarios → pacientes → consultas → agendamentos → itens_agendamento
-- =============================================================

-- 1. USUÁRIOS
-- Senha '123456' criptografada em BCrypt
INSERT IGNORE INTO usuarios (username, senha, perfil) VALUES
('dr.vigenere',  '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzPKnXqO.kXv4Jk9Bxg.Dk7a/zG4yO', 'ADMIN'),
('recepcao01',   '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzPKnXqO.kXv4Jk9Bxg.Dk7a/zG4yO', 'FUNCIONARIO'),
('enfermagem01', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzPKnXqO.kXv4Jk9Bxg.Dk7a/zG4yO', 'FUNCIONARIO');

-- 2. PACIENTES
INSERT IGNORE INTO pacientes (nome, cpf, email, telefone, endereco) VALUES
('Carlos Eduardo Almeida', '11122233344', 'carlos.almeida@email.com',  '(11) 98888-7777', 'Avenida Paulista, 1000 - São Paulo'),
('Mariana Silva Costa',   '55566677788', 'mariana.costa@email.com',   '(11) 97777-6666', 'Rua das Flores, 123 - Campinas'),
('Roberto Gomes',         '99988877766', 'roberto.gomes@email.com',   '(21) 96666-5555', 'Av. Copacabana, 500 - Rio de Janeiro'),
('Ana Beatriz Ferreira',  '12312312399', 'ana.ferreira@email.com',    '(31) 95555-4444', 'Rua dos Ipês, 78 - Belo Horizonte'),
('Lucas Martins Souza',   '98798798711', 'lucas.souza@email.com',     '(41) 94444-3333', 'Rua XV de Novembro, 200 - Curitiba');

-- 3. CONSULTAS (serviços/procedimentos disponíveis)
INSERT IGNORE INTO consultas (nome, descricao, preco, quantidade_disponivel) VALUES
('Consulta Clínica Geral',    'Avaliação médica de rotina e check-up',              150.00, 50),
('Exame Eletrocardiograma',   'Exame cardiológico com laudo médico',                280.00, 20),
('Retorno Médico',            'Consulta de retorno em até 15 dias após a consulta',   0.00, 100),
('Sessão de Fisioterapia',    'Sessão de 45 minutos de reabilitação motora',         90.00, 30),
('Exame de Sangue Completo',  'Hemograma completo com análise laboratorial',        120.00, 40),
('Consulta de Nutrição',      'Avaliação nutricional e plano alimentar',            200.00, 25);

-- 4. AGENDAMENTOS
-- Referencia paciente_id e usuario_id gerados pelo IDENTITY acima.
-- Usamos subselects para não depender de IDs fixos.
INSERT IGNORE INTO agendamentos (data, paciente_id, usuario_id, valor_total) VALUES
('2025-07-10 09:00:00', (SELECT id FROM pacientes WHERE cpf = '11122233344'), (SELECT id FROM usuarios WHERE username = 'dr.vigenere'),  430.00),
('2025-07-11 10:30:00', (SELECT id FROM pacientes WHERE cpf = '55566677788'), (SELECT id FROM usuarios WHERE username = 'recepcao01'),   150.00),
('2025-07-12 14:00:00', (SELECT id FROM pacientes WHERE cpf = '99988877766'), (SELECT id FROM usuarios WHERE username = 'dr.vigenere'),  200.00),
('2025-07-14 08:30:00', (SELECT id FROM pacientes WHERE cpf = '12312312399'), (SELECT id FROM usuarios WHERE username = 'recepcao01'),   370.00),
('2025-07-15 16:00:00', (SELECT id FROM pacientes WHERE cpf = '98798798711'), (SELECT id FROM usuarios WHERE username = 'dr.vigenere'),   90.00);

-- 5. ITENS DE AGENDAMENTO
-- agendamento_id referenciado também por subselect (data + paciente)
INSERT IGNORE INTO itens_agendamento (agendamento_id, consulta_id, quantidade, preco_unitario) VALUES
-- Agendamento 1: Carlos — Consulta Geral + Eletrocardiograma
(
  (SELECT a.id FROM agendamentos a JOIN pacientes p ON a.paciente_id = p.id WHERE p.cpf = '11122233344' AND a.data = '2025-07-10 09:00:00'),
  (SELECT id FROM consultas WHERE nome = 'Consulta Clínica Geral'),
  1, 150.00
),
(
  (SELECT a.id FROM agendamentos a JOIN pacientes p ON a.paciente_id = p.id WHERE p.cpf = '11122233344' AND a.data = '2025-07-10 09:00:00'),
  (SELECT id FROM consultas WHERE nome = 'Exame Eletrocardiograma'),
  1, 280.00
),
-- Agendamento 2: Mariana — Consulta Geral
(
  (SELECT a.id FROM agendamentos a JOIN pacientes p ON a.paciente_id = p.id WHERE p.cpf = '55566677788' AND a.data = '2025-07-11 10:30:00'),
  (SELECT id FROM consultas WHERE nome = 'Consulta Clínica Geral'),
  1, 150.00
),
-- Agendamento 3: Roberto — Consulta de Nutrição
(
  (SELECT a.id FROM agendamentos a JOIN pacientes p ON a.paciente_id = p.id WHERE p.cpf = '99988877766' AND a.data = '2025-07-12 14:00:00'),
  (SELECT id FROM consultas WHERE nome = 'Consulta de Nutrição'),
  1, 200.00
),
-- Agendamento 4: Ana — Exame de Sangue + Consulta Geral
(
  (SELECT a.id FROM agendamentos a JOIN pacientes p ON a.paciente_id = p.id WHERE p.cpf = '12312312399' AND a.data = '2025-07-14 08:30:00'),
  (SELECT id FROM consultas WHERE nome = 'Exame de Sangue Completo'),
  1, 120.00
),
(
  (SELECT a.id FROM agendamentos a JOIN pacientes p ON a.paciente_id = p.id WHERE p.cpf = '12312312399' AND a.data = '2025-07-14 08:30:00'),
  (SELECT id FROM consultas WHERE nome = 'Consulta Clínica Geral'),
  1, 150.00
),
-- Agendamento 5: Lucas — Fisioterapia (2 sessões)
(
  (SELECT a.id FROM agendamentos a JOIN pacientes p ON a.paciente_id = p.id WHERE p.cpf = '98798798711' AND a.data = '2025-07-15 16:00:00'),
  (SELECT id FROM consultas WHERE nome = 'Sessão de Fisioterapia'),
  1, 90.00
);