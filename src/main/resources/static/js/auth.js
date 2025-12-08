// Переключение между вкладками
function showTab(tabName) {
    document.querySelectorAll('.auth-tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.auth-tab').forEach(tab => {
        tab.classList.remove('active');
    });

    document.getElementById(tabName + '-tab').classList.add('active');
    document.querySelector(`.auth-tab[onclick="showTab('${tabName}')"]`).classList.add('active');
    clearMessages();
}

// Превью аватара
function previewAvatar(event) {
    const input = event.target;
    const preview = document.getElementById('avatar-preview');

    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
        }
        reader.readAsDataURL(input.files[0]);
    }
}

// Очистка сообщений
function clearMessages() {
    document.querySelectorAll('.auth-error-message, .auth-success-message').forEach(msg => {
        msg.style.display = 'none';
        msg.textContent = '';
    });
}

// Показать сообщение
function showMessage(elementId, message, isError = true) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.style.display = 'block';
    element.className = isError ? 'auth-error-message' : 'auth-success-message';
}

// Валидация пароля
function validatePassword(password, confirmPassword) {
    if (password.length < 4) {
        return 'Пароль должен содержать не менее 4 символов';
    }
    if (password !== confirmPassword) {
        return 'Пароли не совпадают';
    }
    return null;
}

// Валидация email
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

// Обработка входа
document.getElementById('login-form')?.addEventListener('submit', async function(e) {
    e.preventDefault();

    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;

    clearMessages();

    if (!email || !password) {
        showMessage('login-error', 'Заполните все поля');
        return;
    }

    if (!validateEmail(email)) {
        showMessage('login-error', 'Введите корректный email');
        return;
    }

    document.getElementById('login-loading').style.display = 'block';

    try {
        const formData = new FormData();
        formData.append('email', email);
        formData.append('password', password);

        const response = await fetch('/api/auth/login', {
            method: 'POST',
            body: formData
        });

        const result = await response.json();
        document.getElementById('login-loading').style.display = 'none';

        if (result.success) {
            showMessage('login-success', 'Успешный вход! Перенаправление...', false);
            setTimeout(() => {
                window.location.href = '/projects';
            }, 1000);
        } else {
            showMessage('login-error', result.error || 'Ошибка входа');
        }
    } catch (error) {
        document.getElementById('login-loading').style.display = 'none';
        showMessage('login-error', 'Ошибка сети или сервера');
    }
});

// Обработка регистрации
document.getElementById('register-form')?.addEventListener('submit', async function(e) {
    e.preventDefault();

    const email = document.getElementById('register-email').value.trim();
    const username = document.getElementById('register-username').value.trim();
    const role = document.getElementById('register-role').value;
    const password = document.getElementById('register-password').value;
    const confirmPassword = document.getElementById('register-confirm-password').value;
    const avatarInput = document.getElementById('avatar-input');

    clearMessages();

    if (!email || !username || !password || !confirmPassword || !role) {
        showMessage('register-error', 'Заполните все обязательные поля');
        return;
    }

    if (!validateEmail(email)) {
        showMessage('register-error', 'Введите корректный email');
        return;
    }

    const passwordError = validatePassword(password, confirmPassword);
    if (passwordError) {
        showMessage('register-error', passwordError);
        return;
    }

    document.getElementById('register-loading').style.display = 'block';

    try {
        const formData = new FormData();
        formData.append('email', email);
        formData.append('password', password);
        formData.append('username', username);
        formData.append('role', role);

        if (avatarInput.files[0]) {
            formData.append('avatar', avatarInput.files[0]);
        }

        const response = await fetch('/api/auth/register', {
            method: 'POST',
            body: formData
        });

        const result = await response.json();
        document.getElementById('register-loading').style.display = 'none';

        if (result.success) {
            showMessage('register-success', 'Регистрация успешна! Вы будете перенаправлены в профиль...', false);
            setTimeout(() => {
                window.location.href = `/profile/${result.id}`;
            }, 1000);
        } else {
            showMessage('register-error', result.error || 'Ошибка регистрации');
        }
    } catch (error) {
        document.getElementById('register-loading').style.display = 'none';
        showMessage('register-error', 'Ошибка сети или сервера');
    }
});

// Автозаполнение логина при переходе из регистрации
document.getElementById('register-email')?.addEventListener('blur', function() {
    const loginEmail = document.getElementById('login-email');
    if (!loginEmail.value) {
        loginEmail.value = this.value;
    }
});