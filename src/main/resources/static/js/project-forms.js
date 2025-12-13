function updateGenresCount() {
    const checkboxes = document.querySelectorAll('input[name="genres"]:checked');
    const counter = document.getElementById('genresCounter');
    const count = checkboxes.length;

    if (counter) {
        counter.textContent = `Выбрано: ${count}/3`;

        if (count > 3) {
            counter.className = 'counter error';
        } else if (count === 3) {
            counter.className = 'counter warning';
        } else {
            counter.className = 'counter';
        }

        if (count >= 3) {
            document.querySelectorAll('input[name="genres"]:not(:checked)').forEach(cb => {
                cb.disabled = true;
            });
        } else {
            document.querySelectorAll('input[name="genres"]').forEach(cb => {
                cb.disabled = false;
            });
        }
    }
}

let tags = [];
document.addEventListener('DOMContentLoaded', function() {
    const savedTags = document.body.getAttribute('data-saved-tags');
    console.log("Сохраненные теги из атрибута:", savedTags);

    if (savedTags && savedTags !== 'null' && savedTags !== '[]') {
        try {
            if (savedTags.startsWith('[')) {
                const tagsArray = JSON.parse(savedTags.replace(/&#39;/g, '"'));
                console.log("Парсинг JSON:", tagsArray);
                tagsArray.forEach(tag => addTag(tag));
            }
            else if (savedTags.includes(',')) {
                const tagsArray = savedTags.split(',');
                console.log("Парсинг строки:", tagsArray);
                tagsArray.forEach(tag => {
                    const trimmedTag = tag.trim();
                    if (trimmedTag) addTag(trimmedTag);
                });
            }
            else if (savedTags.trim() !== '') {
                console.log("Один тег:", savedTags);
                addTag(savedTags.trim());
            }
        } catch (e) {
            console.error('Error parsing tags:', e, savedTags);
        }
    }
    updateTagsCounter();
});

function addTag(tagText) {
    tagText = tagText.trim();
    if (!tagText) return;

    if (tags.includes(tagText)) {
        alert('Такой тег уже добавлен');
        return;
    }

    if (tags.length >= 10) {
        alert('Максимум 10 тегов');
        return;
    }

    tags.push(tagText);
    const tagElement = document.createElement('span');
    tagElement.className = 'tag';
    tagElement.innerHTML = `${tagText}<button type="button" class="tag-remove" onclick="removeTag('${tagText}')">×</button>`;

    const container = document.querySelector('.tags-input-container');
    const input = document.getElementById('tagInput');
    container.insertBefore(tagElement, input);

    updateHiddenTagsField();
    updateTagsCounter();
    input.value = '';
}

function removeTag(tagText) {
    tags = tags.filter(t => t !== tagText);
    const container = document.querySelector('.tags-input-container');
    const tagElements = container.querySelectorAll('.tag');
    tagElements.forEach(element => {
        if (element.textContent.includes(tagText)) {
            element.remove();
        }
    });

    updateHiddenTagsField();
    updateTagsCounter();
}

function handleTagInput(event) {
    if (event.key === 'Enter' || event.key === ',') {
        event.preventDefault();
        addTag(event.target.value);
    }
}

function updateHiddenTagsField() {
    const hiddenField = document.getElementById('tagsHidden');
    if (hiddenField) {
        hiddenField.value = tags.join(",");
    }
}

function updateTagsCounter() {
    const counter = document.getElementById('tagsCounter');
    if (counter) {
        counter.textContent = `Добавлено: ${tags.length}/10`;

        if (tags.length > 10) {
            counter.className = 'counter error';
        } else if (tags.length === 10) {
            counter.className = 'counter warning';
        } else {
            counter.className = 'counter';
        }
    }
}

document.getElementById('projectForm')?.addEventListener('submit', function(e) {
    const selectedGenres = document.querySelectorAll('input[name="genres"]:checked');
    if (selectedGenres.length === 0) {
        e.preventDefault();
        alert('Выберите хотя бы один жанр');
        return;
    }

    if (selectedGenres.length > 3) {
        e.preventDefault();
        alert('Максимум 3 жанра');
        return;
    }

    if (tags.length > 10) {
        e.preventDefault();
        alert('Максимум 10 тегов');
        return;
    }

    updateHiddenTagsField();
});

function validateImage(input) {
    if (input.files[0] && input.files[0].size > 5 * 1024 * 1024) {
        alert('Файл слишком большой (макс 5MB)');
        input.value = '';
    }
}

function validateZip(input) {
    if (!input.files[0]) return;
    const file = input.files[0];

    if (!file.name.toLowerCase().endsWith('.zip')) {
        alert('Только ZIP архивы');
        input.value = '';
        return;
    }

    if (file.size > 100 * 1024 * 1024) {
        alert('Файл слишком большой (макс 100MB)');
        input.value = '';
    }
}