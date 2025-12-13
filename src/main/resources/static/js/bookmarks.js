function toggleBookmark(projectId) {
    const btn = document.getElementById('bookmarkBtn');
    const icon = document.getElementById('bookmarkIcon');
    const text = document.getElementById('bookmarkText');

    const url = btn.classList.contains('active')
        ? `/bookmarks/api/project/${projectId}/delete`
        : `/bookmarks/api/project/${projectId}/add`;

    fetch(url, {method: 'POST'})
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            if (btn.classList.contains('active')) {
                btn.classList.remove('active');
                icon.textContent = '☆';
                text.textContent = 'В избранное';
                btn.style.backgroundColor = '';
                btn.style.color = '';
            } else {
                btn.classList.add('active');
                icon.textContent = '★';
                text.textContent = 'В избранном';
                btn.style.backgroundColor = '#ffc107';
                btn.style.color = '#000';
            }
            alert(data.message);
        } else {
            alert('Ошибка: ' + data.error);
        }
    })
    .catch(e => {
        console.error(e);
        alert('Ошибка');
    });
}

function removeBookmark(projectId, event) {
    event.stopPropagation();
    if (!confirm('Удалить из избранного?')) return;

    fetch(`/bookmarks/api/project/${projectId}/delete`, {method: 'POST'})
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            event.target.closest('.project-card').remove();
        } else {
            alert('Ошибка: ' + data.error);
        }
    })
    .catch(e => {
        console.error(e);
        alert('Ошибка');
    });
}