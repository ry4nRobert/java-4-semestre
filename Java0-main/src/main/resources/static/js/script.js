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