// Обработка загрузки аватара
document.getElementById('avatarInput')?.addEventListener('change', function(e) {
    if (this.files && this.files[0]) {
        if (this.files[0].size > 5 * 1024 * 1024) {
            alert('Файл слишком большой. Максимальный размер: 5MB');
            this.value = '';
            return;
        }

        const button = this.closest('.avatar-container').querySelector('button');
        const originalText = button.textContent;
        button.textContent = 'Загружаем...';
        button.disabled = true;
    }
});

// Добавляем обработчик для формы аватара
document.getElementById('avatarForm')?.addEventListener('submit', function(e) {
    console.log('Загружаем аватар...');
});