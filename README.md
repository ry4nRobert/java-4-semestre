<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Documentação - MedLink</title>

    <style>
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f8f9fa;
        }

        h1 {
            color: #2c5aa0;
            border-left: 5px solid #2c5aa0;
            padding-left: 15px;
            margin-top: 40px;
        }

        h2 {
            color: #3a6bc0;
            margin-top: 30px;
        }

        h3 {
            color: #4a7bc8;
            margin-top: 25px;
            padding-bottom: 5px;
            border-bottom: 1px solid #e0e0e0;
        }

        p {
            margin-bottom: 15px;
            text-align: justify;
        }

        ul {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        li {
            margin-bottom: 8px;
            padding-left: 5px;
        }

        strong {
            color: #2c5aa0;
        }

        .container {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
    </style>
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

    <h1>3. Funções/Lista de Eventos - RF / RNF</h1>
    <p>
        Oi
    </p>
    <br>
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
<p></p>

<br>

<h1>5. Diagrama de Classe</h1>
<p>Enviado através de um arquivo pdf separado.</p>

<br>

<h1>6. Aplicação Web/Mobile</h1>
<h3>Tela de login</h3>
<p>Tela onde o Usuário depois de cadastrado, pode fazer o seu login usando e-mail e senha.</p>


</body>
</html>
