document.addEventListener('DOMContentLoaded', function() {
    carregarGraficos();
    carregarEstatisticas();
});

async function carregarGraficos() {
    try {
        // Carregar dados dos pacientes por mês
        const responsePacientes = await fetch('/api/graficos/pacientes-por-mes');
        const dadosPacientes = await responsePacientes.json();
        
        // Carregar dados das consultas por mês
        const responseConsultas = await fetch('/api/graficos/consultas-por-mes');
        const dadosConsultas = await responseConsultas.json();
        
        // Carregar distribuição por idade
        const responseIdade = await fetch('/api/graficos/distribuicao-idade');
        const dadosIdade = await responseIdade.json();
        
        // Criar gráficos
        criarGraficoPacientes(dadosPacientes);
        criarGraficoConsultas(dadosConsultas);
        criarGraficoIdade(dadosIdade);
        
    } catch (error) {
        console.error('Erro ao carregar dados dos gráficos:', error);
    }
}

async function carregarEstatisticas() {
    try {
        const response = await fetch('/api/graficos/estatisticas-gerais');
        const estatisticas = await response.json();
        
        document.getElementById('totalPacientes').textContent = estatisticas.totalPacientes;
        document.getElementById('consultasMes').textContent = estatisticas.consultasMes;
        document.getElementById('mediaIdade').textContent = estatisticas.mediaIdade;
        
    } catch (error) {
        console.error('Erro ao carregar estatísticas:', error);
    }
}

function criarGraficoPacientes(dados) {
    const ctx = document.getElementById('pacientesChart').getContext('2d');
    const labels = Object.keys(dados);
    const valores = Object.values(dados);
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Pacientes Cadastrados',
                data: valores,
                borderColor: '#007bff',
                backgroundColor: 'rgba(0, 123, 255, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

function criarGraficoConsultas(dados) {
    const ctx = document.getElementById('consultasChart').getContext('2d');
    const labels = Object.keys(dados);
    const valores = Object.values(dados);
    
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Consultas Realizadas',
                data: valores,
                backgroundColor: '#28a745',
                borderColor: '#218838',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

function criarGraficoIdade(dados) {
    const ctx = document.getElementById('idadeChart').getContext('2d');
    const labels = Object.keys(dados);
    const valores = Object.values(dados);
    
    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: valores,
                backgroundColor: [
                    '#ff6384',
                    '#36a2eb',
                    '#ffce56',
                    '#4bc0c0',
                    '#9966ff'
                ]
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'right',
                }
            }
        }
    });
}