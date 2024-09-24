document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');

    // 폼 제출 시 모든 필드를 다시 유효성 검사
    form.addEventListener('submit', async function (event) {
        event.preventDefault(); // 기본 폼 제출 방지 (제출 막음)

        const isValid = await validateForm();
        if (!isValid) {
            console.log("Form submission blocked due to validation failure.");
            return; // 유효하지 않으면 폼을 중단하고 더 이상 진행하지 않음
        }

        // 유효한 경우에만 폼 제출을 계속 진행
        console.log("Form is valid. Submitting...");
        form.submit(); // 유효할 경우 수동으로 폼 제출
    });

    // 이메일 도메인 선택 기능
    const emailDomainSelect = document.getElementById('emailDomain');
    const customEmailDomainInput = document.getElementById('customEmailDomain');

    emailDomainSelect.addEventListener('change', function () {
        if (emailDomainSelect.value === 'custom') {
            emailDomainSelect.style.display = 'none';
            customEmailDomainInput.style.display = 'block';
            customEmailDomainInput.required = true;
        } else {
            customEmailDomainInput.style.display = 'none';
            customEmailDomainInput.required = false;
            customEmailDomainInput.value = '';
        }
    });

    // 이용약관 및 개인정보 모달 설정
    setupModal('termsModal', '/non-member/terms', 'terms-content');
    setupModal('collectionsModal', '/non-member/collections', 'collections-content');
});

// 모달 콘텐츠 로드 로직
function setupModal(modalId, url, contentId) {
    const modal = document.getElementById(modalId);
    modal.addEventListener('shown.bs.modal', function () {
        fetch(url)
            .then(response => response.text())
            .then(data => {
                document.getElementById(contentId).innerHTML = data;
            })
            .catch(error => console.error(`Error fetching ${url}:`, error));
    });
}

// 폼 유효성 검사
async function validateForm() {
    let isValid = true;

    if (!(await validateName())) {
        isValid = false;
    }
    if (!(await validateEmailAccount())) {
        isValid = false;
    }
    if (!(await validateId())) {
        isValid = false;
    }
    if (!(await validatePassword())) {
        isValid = false;
    }
    if (!validateConfirmPassword()) {  // 비동기 아님
        isValid = false;
    }

    return isValid;
}

// 개별 유효성 검사 함수들
async function validateName() {
    const nameInput = document.getElementById('yourName').value;
    const nameValidationMsg = document.getElementById('nameValidationMsg');

    try {
        const response = await fetch(`/non-member/name?name=${encodeURIComponent(nameInput)}`);
        const data = await response.json();
        if (data.status === 'valid') {
            nameValidationMsg.textContent = data.message;
            nameValidationMsg.classList.remove('text-danger');
            nameValidationMsg.classList.add('text-success');
            return true;
        } else {
            nameValidationMsg.textContent = data.message;
            nameValidationMsg.classList.remove('text-success');
            nameValidationMsg.classList.add('text-danger');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        return false;
    }
}

async function validateEmailAccount() {
    const emailAccountInput = document.getElementById('emailAccount').value;
    const emailValidationMsg = document.getElementById('emailValidationMsg');

    try {
        const response = await fetch(`/non-member/emailAccount?emailAccount=${encodeURIComponent(emailAccountInput)}`);
        const data = await response.json();
        if (data.status === 'valid') {
            emailValidationMsg.textContent = data.message;
            emailValidationMsg.classList.remove('text-danger');
            emailValidationMsg.classList.add('text-success');
            return true;
        } else {
            emailValidationMsg.textContent = data.message;
            emailValidationMsg.classList.remove('text-success');
            emailValidationMsg.classList.add('text-danger');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        return false;
    }
}

async function validateId() {
    const idInput = document.getElementById('yourUsername').value;
    const idValidationMsg = document.getElementById('idValidationMsg');

    try {
        const response = await fetch(`/non-member/id?id=${encodeURIComponent(idInput)}`);
        const data = await response.json();
        if (data.status === 'valid') {
            idValidationMsg.textContent = data.message;
            idValidationMsg.classList.remove('text-danger');
            idValidationMsg.classList.add('text-success');
            return true;
        } else {
            idValidationMsg.textContent = data.message;
            idValidationMsg.classList.remove('text-success');
            idValidationMsg.classList.add('text-danger');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        return false;
    }
}

async function validatePassword() {
    const passwordInput = document.getElementById('yourPassword').value;
    const passwordValidationMsg = document.getElementById('passwordValidationMsg');

    try {
        const response = await fetch(`/non-member/password?password=${encodeURIComponent(passwordInput)}`);
        const data = await response.json();
        if (data.status === 'valid') {
            passwordValidationMsg.textContent = data.message;
            passwordValidationMsg.classList.remove('text-danger');
            passwordValidationMsg.classList.add('text-success');
            return true;
        } else {
            passwordValidationMsg.textContent = data.message;
            passwordValidationMsg.classList.remove('text-success');
            passwordValidationMsg.classList.add('text-danger');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        return false;
    }
}

function validateConfirmPassword() {
    const passwordInput = document.getElementById('yourPassword').value;
    const confirmPasswordInput = document.getElementById('confirmPassword').value;
    const confirmPasswordValidationMsg = document.getElementById('confirmPasswordValidationMsg');

    if (passwordInput === confirmPasswordInput) {
        confirmPasswordValidationMsg.textContent = "비밀번호가 일치합니다.";
        confirmPasswordValidationMsg.classList.remove('text-danger');
        confirmPasswordValidationMsg.classList.add('text-success');
        return true;
    } else {
        confirmPasswordValidationMsg.textContent = "비밀번호가 일치하지 않습니다.";
        confirmPasswordValidationMsg.classList.remove('text-success');
        confirmPasswordValidationMsg.classList.add('text-danger');
        return false;
    }
}
