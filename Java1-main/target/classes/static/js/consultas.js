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

// Fechar menu mobile ao clicar em um link
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

// =================== CALENDÁRIO ===================
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

function renderCalendar(month, year) {
    calendarDates.innerHTML = '';
    monthYear.textContent = `${months[month]} ${year}`;

    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    // Espaços antes do primeiro dia
    for (let i = 0; i < firstDay; i++) {
        const blank = document.createElement('div');
        calendarDates.appendChild(blank);
    }

    // Dias do mês
    const today = new Date();
    for (let i = 1; i <= daysInMonth; i++) {
        const day = document.createElement('div');
        day.textContent = i;

        // Marca o dia atual
        if (
            i === today.getDate() &&
            year === today.getFullYear() &&
            month === today.getMonth()
        ) {
            day.classList.add('current-date');
        }

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
