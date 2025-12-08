// Полноэкранный режим для игры
function toggleFullscreen() {
    const gameIframe = document.getElementById('gameIframe');
    if (!document.fullscreenElement) {
        if (gameIframe.requestFullscreen) {
            gameIframe.requestFullscreen();
        } else if (gameIframe.webkitRequestFullscreen) {
            gameIframe.webkitRequestFullscreen();
        } else if (gameIframe.msRequestFullscreen) {
            gameIframe.msRequestFullscreen();
        }
    } else {
        if (document.exitFullscreen) {
            document.exitFullscreen();
        }
    }
}

// Инициализация рейтинга пользователя
document.addEventListener('DOMContentLoaded', function() {
    const userRate = document.body.getAttribute('data-user-rate');
    if (userRate && userRate !== 'null') {
        const stars = document.querySelectorAll('#userRating .star');
        stars.forEach((star, index) => {
            if (index < parseInt(userRate)) {
                star.classList.add('active');
            }
        });
        document.getElementById('userRatingText').textContent = `Ваша оценка: ${userRate}/5`;
    }

    const stars = document.querySelectorAll('#userRating .star');
    stars.forEach(star => {
        star.addEventListener('click', function() {
            const value = parseInt(this.getAttribute('data-value'));
            rateProject(value);
        });
    });
});

// Оценка проекта
function rateProject(rating) {
    if (!confirm(`Вы уверены, что хотите поставить оценку ${rating}?`)) {
        return;
    }

    const projectId = document.body.getAttribute('data-project-id');
    fetch(`/api/rates/project/${projectId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `rate=${rating}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            const stars = document.querySelectorAll('#userRating .star');
            stars.forEach((star, index) => {
                if (index < rating) {
                    star.classList.add('active');
                } else {
                    star.classList.remove('active');
                }
            });

            document.getElementById('userRatingText').textContent = `Ваша оценка: ${rating}/5`;
            updateAverageRating();
            alert('Оценка поставлена!');
        } else {
            alert('Ошибка: ' + data.error);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Ошибка при отправке оценки');
    });
}

// Обновление средней оценки (AJAX)
function updateAverageRating() {
    const projectId = document.body.getAttribute('data-project-id');
    fetch(`/api/rates/project/${projectId}/average`)
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            const avgElement = document.querySelector('.average-rating');
            if (avgElement) {
                avgElement.textContent = data.averageRate.toFixed(1);
            }

            const countElement = document.querySelector('.rating-count');
            if (countElement) {
                countElement.textContent = `(${data.totalRates} оценок)`;
            }
        }
    })
    .catch(error => console.error('Error updating rating:', error));
}

// Отправка комментария
function submitComment(event) {
    event.preventDefault();
    const commentText = document.getElementById('commentText').value.trim();
    if (!commentText) {
        alert('Введите текст комментария');
        return false;
    }

    const projectId = document.body.getAttribute('data-project-id');
    const formData = new FormData();
    formData.append('text', commentText);

    fetch(`/api/comments/project/${projectId}`, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            addCommentToDOM(data.comment);
            document.getElementById('commentText').value = '';
            alert('Комментарий добавлен!');
        } else {
            alert('Ошибка: ' + data.error);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Ошибка при отправке комментария');
    });

    return false;
}

// Добавление комментария в DOM
function addCommentToDOM(comment) {
    const commentsList = document.getElementById('commentsList');
    const commentHtml = `
        <div class="comment-item" id="comment-${comment.id}">
            <div class="comment-header">
                <div class="comment-author">
                    <a href="/profile/${comment.user.id}" style="text-decoration: none;">
                        <div class="comment-avatar-placeholder">
                            <span>${comment.user.username.charAt(0).toUpperCase()}</span>
                        </div>
                    </a>
                    <div class="comment-info">
                        <a href="/profile/${comment.user.id}" class="comment-username">
                            ${comment.user.username}
                        </a>
                        <div class="comment-date">
                            ${new Date(comment.createdAt).toLocaleDateString('ru-RU')} ${new Date(comment.createdAt).toLocaleTimeString('ru-RU', {hour: '2-digit', minute:'2-digit'})}
                        </div>
                    </div>
                </div>
                <div class="comment-actions">
                    <button class="comment-action-btn edit" onclick="editComment(${comment.id})">
                        Редактировать
                    </button>
                    <button class="comment-action-btn delete" onclick="deleteComment(${comment.id})">
                        Удалить
                    </button>
                </div>
            </div>
            <div class="comment-content" id="comment-content-${comment.id}">
                <span>${comment.text}</span>
            </div>
            <div class="comment-edit-form" id="comment-edit-form-${comment.id}" style="display: none;">
                <textarea class="comment-edit-textarea" id="comment-edit-text-${comment.id}">${comment.text}</textarea>
                <div style="display: flex; gap: 0.5rem;">
                    <button class="btn btn-primary" onclick="saveCommentEdit(${comment.id})">
                        Сохранить
                    </button>
                    <button class="btn btn-secondary" onclick="cancelCommentEdit(${comment.id})">
                        Отмена
                    </button>
                </div>
            </div>
        </div>
    `;

    if (commentsList.querySelector('.no-comments')) {
        commentsList.innerHTML = commentHtml;
    } else {
        commentsList.insertAdjacentHTML('afterbegin', commentHtml);
    }
}

// Редактирование комментария
function editComment(commentId) {
    document.getElementById(`comment-content-${commentId}`).style.display = 'none';
    document.getElementById(`comment-edit-form-${commentId}`).style.display = 'block';
}

// Сохранение отредактированного комментария
function saveCommentEdit(commentId) {
    const newText = document.getElementById(`comment-edit-text-${commentId}`).value.trim();
    if (!newText) {
        alert('Текст комментария не может быть пустым');
        return;
    }

    const formData = new FormData();
    formData.append('text', newText);

    fetch(`/api/comments/${commentId}`, {
        method: 'PUT',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            document.getElementById(`comment-content-${commentId}`).innerHTML = `<span>${newText}</span>`;
            document.getElementById(`comment-content-${commentId}`).style.display = 'block';
            document.getElementById(`comment-edit-form-${commentId}`).style.display = 'none';
            alert('Комментарий обновлен!');
        } else {
            alert('Ошибка: ' + data.error);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Ошибка при обновлении комментария');
    });
}

// Отмена редактирования
function cancelCommentEdit(commentId) {
    document.getElementById(`comment-content-${commentId}`).style.display = 'block';
    document.getElementById(`comment-edit-form-${commentId}`).style.display = 'none';
}

// Удаление комментария
function deleteComment(commentId) {
    if (!confirm('Вы уверены, что хотите удалить этот комментарий?')) {
        return;
    }

    fetch(`/api/comments/${commentId}`, {
        method: 'DELETE'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            document.getElementById(`comment-${commentId}`).remove();
            alert('Комментарий удален!');
        } else {
            alert('Ошибка: ' + data.error);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Ошибка при удалении комментария');
    });
}

// Автоматически фокусируем игру при клике на фрейм
document.getElementById('gameFrame')?.addEventListener('click', function() {
    document.getElementById('gameIframe')?.focus();
});

// Обработка выхода из полноэкранного режима
document.addEventListener('fullscreenchange', function() {
    const fullscreenBtn = document.querySelector('.fullscreen-btn');
    if (fullscreenBtn) {
        if (document.fullscreenElement) {
            fullscreenBtn.innerHTML = '<span style="font-size: 1.2rem;"></span> Выйти из полноэкранного режима';
        } else {
            fullscreenBtn.innerHTML = '<span style="font-size: 1.2rem;"></span> Во весь экран';
        }
    }
});