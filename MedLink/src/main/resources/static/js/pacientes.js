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

    inicializarMascarasCPF();
});

function togglePatientForm() {
    const form = document.getElementById('patient-form');
    form.style.display = form.style.display === 'none' ? 'block' : 'none';

    if (form.style.display === 'block') {
        window.scrollTo({ top: form.offsetTop - 20, behavior: 'smooth' });
    }
}

function viewPatientDetails(patientId) {
    const modal = document.getElementById('patient-modal');
    const modalBody = document.getElementById('modal-body');

    const patientCard = document.querySelector(`[onclick*="viewPatientDetails(${patientId})"]`).closest('.patient-card');
    const patientName = patientCard.querySelector('.patient-name').textContent;
    const patientDetails = patientCard.querySelector('.patient-details').textContent;
    const alergias = patientCard.querySelector('.summary-item:nth-child(1) .value')?.textContent || 'Não informado';
    const cirurgias = patientCard.querySelector('.summary-item:nth-child(2) .value')?.textContent || 'Não informado';
    const observacoes = patientCard.querySelector('.summary-item:nth-child(3) .value')?.textContent || 'Não informado';

    modalBody.innerHTML = `
        <div class="patient-detail">
            <h4>${patientName}</h4>
            <p><strong>${patientDetails}</strong></p>
            <div class="detail-section">
                <h5>Informações Médicas</h5>
                <p><strong>Alergias:</strong> ${alergias}</p>
                <p><strong>Histórico de Cirurgias:</strong> ${cirurgias}</p>
                <p><strong>Observações:</strong> ${observacoes}</p>
            </div>
        </div>
    `;

    modal.style.display = 'flex';
}

function closeModal() {
    document.getElementById('patient-modal').style.display = 'none';
}

function editPatient(patientId) {
    const patientCard = document.querySelector(`[onclick*="editPatient(${patientId})"]`).closest('.patient-card');

    document.getElementById('edit-patient-id').value = patientId;
    document.getElementById('edit-patient-nome').value = patientCard.querySelector('.patient-name').textContent;

    const ageText = patientCard.querySelector('.patient-details').textContent;
    const ageMatch = ageText.match(/(\d+) anos/);
    if (ageMatch) {
        document.getElementById('edit-patient-idade').value = ageMatch[1];
    }

    const cpfMatch = ageText.match(/CPF: (.+)/);
    if (cpfMatch) {
        document.getElementById('edit-patient-cpf').value = cpfMatch[1];
    }

    const alergias = patientCard.querySelector('.summary-item:nth-child(1) .value')?.textContent || '';
    const cirurgias = patientCard.querySelector('.summary-item:nth-child(2) .value')?.textContent || '';
    const observacoes = patientCard.querySelector('.summary-item:nth-child(3) .value')?.textContent || '';

    document.getElementById('edit-patient-alergias').value = alergias;
    document.getElementById('edit-patient-cirurgias').value = cirurgias;
    document.getElementById('edit-patient-observacoes').value = observacoes;

    document.getElementById('edit-patient-modal').style.display = 'flex';
}

function closeEditModal() {
    document.getElementById('edit-patient-modal').style.display = 'none';
}

function deletePatient(patientId) {
    if (confirm('Tem certeza que deseja excluir este paciente? Esta ação não pode ser desfeita.')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/pacientes/${patientId}/excluir`;

        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');

        if (csrfToken) {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = '_csrf';
            input.value = csrfToken;
            form.appendChild(input);
        }

        document.body.appendChild(form);
        form.submit();
    }
}

function aplicarMascaraCPF(input) {
    input.addEventListener('input', function (e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 11) value = value.substring(0, 11);

        if (value.length > 9) {
            value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
        } else if (value.length > 6) {
            value = value.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
        } else if (value.length > 3) {
            value = value.replace(/(\d{3})(\d{1,3})/, '$1.$2');
        }
        e.target.value = value;
    });
}

function inicializarMascarasCPF() {
    const cpfInput = document.getElementById('patient-cpf');
    if (cpfInput) {
        aplicarMascaraCPF(cpfInput);
    }

    const editCpfInput = document.getElementById('edit-patient-cpf');
    if (editCpfInput) {
        aplicarMascaraCPF(editCpfInput);
    }
}
