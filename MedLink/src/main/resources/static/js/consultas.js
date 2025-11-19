function menuShow() {
    let menuMobile = document.querySelector('.mobile-menu');
    let iconMenu = document.getElementById('icon-menu');
    let iconClose = document.getElementById('icon-close');
    if (menuMobile.classList.contains('open')) {
        menuMobile.classList.remove('open');
        iconMenu.style.display = 'block';
        iconClose.style.display = 'none';
    } else {
        menuMobile.classList.add('open');
        iconMenu.style.display = 'none';
        iconClose.style.display = 'block';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const mobileLinks = document.querySelectorAll('.mobile-menu .nav-link');
    mobileLinks.forEach(link => {
        link.addEventListener('click', function() {
            let menuMobile = document.querySelector('.mobile-menu');
            let iconMenu = document.getElementById('icon-menu');
            let iconClose = document.getElementById('icon-close');
            menuMobile.classList.remove('open');
            iconMenu.style.display = 'block';
            iconClose.style.display = 'none';
        });
    });
});

const calendarDates = document.querySelector('.calendar-dates');
const monthYear = document.getElementById('month-year');
const prevMonthBtn = document.getElementById('prev-month');
const nextMonthBtn = document.getElementById('next-month');

let currentDate = new Date();
let currentMonth = currentDate.getMonth();
let currentYear = currentDate.getFullYear();

const months = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

async function fetchConsultas(mes, ano) {
    const mesParaApi = mes + 1; 
    
    try {
        const response = await fetch(`/api/consultas?ano=${ano}&mes=${mesParaApi}`);
        if (!response.ok) {
            console.error("Erro ao buscar consultas do backend");
            return []; 
        }
        const consultas = await response.json();
        return consultas;
        
    } catch (e) {
        console.error("Erro de rede ao buscar consultas:", e);
        return [];
    }
}

async function renderCalendar(month, year) {
    calendarDates.innerHTML = '';
    monthYear.textContent = `${months[month]} ${year}`;

    const consultasDoMes = await fetchConsultas(month, year);
    
    const consultasPorDia = {};
    consultasDoMes.forEach(consulta => {
        let dia = new Date(consulta.dataHora).getDate();
        if (!consultasPorDia[dia]) {
            consultasPorDia[dia] = [];
        }
        consultasPorDia[dia].push(consulta);
    });

    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const today = new Date();

    for (let i = 0; i < firstDay; i++) {
        calendarDates.appendChild(document.createElement('div'));
    }

    for (let i = 1; i <= daysInMonth; i++) {
        const day = document.createElement('div');
        day.textContent = i;
        day.style.cursor = 'pointer'; 

        if (i === today.getDate() && year === today.getFullYear() && month === today.getMonth()) {
            day.classList.add('current-date');
        }
        
        if (consultasPorDia[i]) {
            day.classList.add('dia-com-consulta');
            const dot = document.createElement('span');
            dot.className = 'consulta-dot';
            day.appendChild(dot);
        }

        // --- INÍCIO DA CORREÇÃO ---
        day.addEventListener('click', () => {
            
            // 1. Verifica se o dia já tem consultas
            if (consultasPorDia[i]) {
                
                // 2. Monta a lista de detalhes (igual a antes)
                let detalhes = consultasPorDia[i].map(c => {
                    let hora = c.dataHora.split('T')[1].substring(0, 5);
                    let pacienteNome = c.paciente.nome; 
                    return `- ${hora} com ${pacienteNome}`;
                }).join('\n');
                
                // 3. Mostra o 'alert' (igual a antes)
                alert(`Consultas para ${i}/${month+1}:\n${detalhes}`);

                // 4. DEPOIS do alert, pergunta se quer agendar MAIS UMA
                if (confirm("Deseja agendar OUTRA consulta para este dia?")) {
                    agendarConsulta(i, month, year);
                }

            } else {
                // Se o dia está livre, apenas agenda (igual a antes)
                agendarConsulta(i, month, year); 
            }
        });
        // --- FIM DA CORREÇÃO ---

        calendarDates.appendChild(day);
    }
}

renderCalendar(currentMonth, currentYear);

prevMonthBtn.addEventListener('click', () => {
    currentMonth--;
    if (currentMonth < 0) {
        currentMonth = 11;
        currentYear--;
    }
    renderCalendar(currentMonth, currentYear); 
});

nextMonthBtn.addEventListener('click', () => {
    currentMonth++;
    if (currentMonth > 11) {
        currentMonth = 0;
        currentYear++;
    }
    renderCalendar(currentMonth, currentYear); 
});

function agendarConsulta(dia, mes, ano) {
    let hora = prompt(`Agendar para ${dia}/${mes + 1}/${ano}.\nDigite a hora (HH:mm):`, "09:00");
    if (!hora) return; 

    let pacienteId = prompt("Digite o ID do Paciente:"); 
    if (!pacienteId) return;

    let mesFormatado = String(mes + 1).padStart(2, '0');
    let diaFormatado = String(dia).padStart(2, '0');
    let dataHoraISO = `${ano}-${mesFormatado}-${diaFormatado}T${hora}`;

    fetch('/api/consultas/nova', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            pacienteId: pacienteId, 
            dataHora: dataHoraISO
        })
    })
    .then(response => {
        return response.text().then(text => {
            alert(text); 
            if (response.ok) {
                // Recarrega o calendário para mostrar a nova "bolinha"
                renderCalendar(currentMonth, currentYear); 
            }
        });
    })
    .catch(error => {
        console.error('Erro de rede:', error)
        alert("Erro de conexão. Não foi possível salvar.");
    });
}