<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <h1>1. Introdução</h1>
<p>O <strong>MedLink</strong> é uma aplicação web desenvolvida para revolucionar o gerenciamento de consultas médicas em hospitais e clínicas. O sistema tem como propósito centralizar e otimizar todo o fluxo de agendamento, permitindo que médicos cadastrem pacientes e marquem consultas de forma intuitiva através de um calendário integrado.</p>
<p>A motivação para o desenvolvimento desta aplicação surgiu da necessidade de simplificar processos burocráticos e descentralizados ainda comuns na área da saúde. No mundo real, essa falta de padronização gere retrabalho, confusão na agenda médica e uma experiência frustrante tanto para profissionais quanto para pacientes.</p>
<p>Dessa forma, o MedLink se apresenta como uma solução eficiente e confiável que não apenas auxilia na organização, mas resolve um problema específico: a gestão inadequada de agendas médicas, promovendo mais eficiência e qualidade no atendimento ao paciente.</p>
    <br>

<h1>2. Objetivos da Aplicação</h1>
    <ul>
        <li>Centralizar e gerenciar o prontuário eletrônico dos pacientes</li>
        <li>Fornecer relatórios analíticos com gráficos sobre a prática médica</li>
        <li>Otimizar o agendamento e cadastro de consultas através de uma interface intuitiva</li>
        <li>Garantir acesso rápido e seguro às informações médicas</li>
    </ul>
    <br>


<h3>3. Requisitos Funcionais (RF):</h3>
<ul>
    <li><strong>RF1:</strong> O sistema deverá permitir o cadastro de médicos com informações como nome, especialidade, e-mail e senha.</li>
    <li><strong>RF2:</strong> O sistema deverá permitir o cadastro completo de pacientes, incluindo dados pessoais, histórico médico e informações de contato.</li>
    <li><strong>RF3:</strong> O sistema deverá possibilitar o agendamento de consultas através de um calendário integrado, com seleção de data, horário e paciente.</li>
    <li><strong>RF4:</strong> O sistema deverá gerar relatórios analíticos com gráficos sobre a prática médica (número de consultas e etc.).</li>
    <li><strong>RF5:</strong> O sistema deverá permitir a visualização e edição do prontuário eletrônico dos pacientes.</li>
</ul>

<h3>Requisitos Não Funcionais (RNF):</h3>
<ul>
    <li><strong>RNF1:</strong> A aplicação deverá ser responsiva, funcionando de forma otimizada em dispositivos móveis e desktops.</li>
    <li><strong>RNF2:</strong> O sistema deve ter tempo de resposta inferior a 3 segundos para todas as operações realizadas pelo usuário.</li>
    <li><strong>RNF3:</strong> O sistema deve ser desenvolvido com interface intuitiva e de fácil usabilidade para profissionais da saúde.</li>
</ul>
    
   <h1>4. Especificação de Programas</h1>

<h3>Layout da Tela</h3>
<ul>
    <li>Tela de Login</li>
    <li>Tela de Registro de Usuário</li>
    <li>Tela Home (Dashboard Principal)</li>
    <li>Tela de Gerenciamento de Pacientes</li>
    <li>Tela de Agendamento de Consultas</li>
    <li>Tela de Configurações do Perfil</li>
    <li>Tela de Relatórios e Gráficos</li>
</ul>

<h3>Regras de Negócio:</h3>
<ul>
    <li>O CPF do paciente deve conter exatamente 11 dígitos numéricos</li>
    <li>Não é permitido agendar duas consultas no mesmo horário para o mesmo médico</li>
    <li>Uma consulta não pode ser marcada em datas retroativas</li>
    <li>O campo "idade" não pode ser negativo</li>
    <li>Campos obrigatórios devem ser preenchidos antes do cadastro (nome, CPF, data de nascimento)</li>
</ul>

<h3>Entidades Envolvidas (Classes):</h3>

<p><strong>Médico:</strong> Representa o profissional de saúde, com atributos como nome, especialidade, e-mail e senha. Responsável por gerenciar pacientes e agendar consultas.</p>

<p><strong>Paciente:</strong> Representa a pessoa atendida, com atributos como nome, CPF, idade, histórico de alergias, cirurgias anteriores e observações médicas.</p>

<p><strong>Consulta:</strong> Representa um agendamento médico, com atributos como data, horário e nome do paciente.</p>

<h3>Tabelas:</h3>

<h4>Tabela: registro</h4>

<p>Propósito: Guarda os dados dos usuários do sistema (médicos).

Colunas:

id (Primary Key, bigint, AI)

nome (varchar(255))

email (varchar(255), Único)

senha (varchar(255))

reset_token (varchar(255)) (Nota: Coluna antiga, não é funcional)

reset_token_expiry_date (datetime(6)) (Nota: Coluna antiga, não é funcional)

foto_perfil (LONGBLOB) </p>

<h4>Tabela: especialidades</h4>

<p>Propósito: Lista fixa de especialidades médicas.

Colunas:

id (Primary Key, bigint, AI)

nome (varchar(255))

Tabela: registro_especialidades
Propósito: Tabela de ligação ManyToMany (relação muitos-para-muitos) entre as tabelas registro e especialidades.

Colunas:

registro_id (Primary Key, Foreign Key para registro.id, bigint)

especialidade_id (Primary Key, Foreign Key para especialidades.id, bigint) </p>

<h4>Tabela: paciente</h4>

<p>Propósito: Armazena informações dos pacientes.

Colunas:

id (Primary Key, bigint, AI)

nome (varchar(255))

idade (int)

cpf (varchar(255))

alergias (varchar(255))

historico_cirurgias (varchar(255))

medico_id (Foreign Key para registro.id, bigint)

data_criacao (datetime(6))

observacoes (varchar(255)) </p>

<h4>Tabela: consulta</h4>

<p>Propósito: Armazena o registro de consultas entre um médico e um paciente.

Colunas:

id (Primary Key, bigint, AI)

medico_id (Foreign Key para registro.id, bigint)

paciente_id (Foreign Key para paciente.id, bigint)

data_hora (datetime(6))

observacoes (longtext) </p>

<br>

<h1>5. Diagrama de Classe</h1>
<p>Enviado através de um arquivo pdf separado.</p>


</body>
</html>
