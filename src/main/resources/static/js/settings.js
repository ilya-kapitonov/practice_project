
const categoriesTableBody = document.getElementById('categoriesTableBody');
const conditionsTableBody = document.getElementById('conditionsTableBody');

const addCategoryBtn = document.getElementById('addCategoryBtn');
const addConditionBtn = document.getElementById('addConditionBtn');

const categoryModal = new bootstrap.Modal(document.getElementById('categoryModal'));
const conditionModal = new bootstrap.Modal(document.getElementById('conditionModal'));

const categoryForm = document.getElementById('categoryForm');
const conditionForm = document.getElementById('conditionForm');

async function loadCategories() {
  try {
    const res = await fetch('/api/categories?sortBy=categoryName&sortDir=ASC');
    if (!res.ok) throw new Error('Ошибка загрузки категорий');
    const categories = await res.json();

    categoriesTableBody.innerHTML = '';
    categories.forEach(cat => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${cat.categoryName}</td>
        <td>${cat.description || ''}</td>
        <td>
          <button class="btn" onclick="editCategory(${cat.categoryId})" title="Редактировать"><i class="bi bi-pencil-square"></i></button>
          <button class="btn" onclick="deleteCategory(${cat.categoryId})" title="Удалить"><i class="bi bi-x-square-fill"></i></button>
        </td>
      `;
      categoriesTableBody.appendChild(tr);
    });
  } catch (error) {
    alert(error.message);
  }
}

async function loadConditions() {
  try {
    const res = await fetch('/api/conditions?sortBy=conditionName&sortDir=ASC');
    if (!res.ok) throw new Error('Ошибка загрузки состояний');
    const conditions = await res.json();

    conditionsTableBody.innerHTML = '';
    conditions.forEach(cond => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${cond.conditionName}</td>
        <td>
          <button class="btn" onclick="editCondition(${cond.conditionId})" title="Редактировать"><i class="bi bi-pencil-square"></i></button>
          <button class="btn" onclick="deleteCondition(${cond.conditionId})" title="Удалить"><i class="bi bi-x-square-fill"></i></button>
        </td>
      `;
      conditionsTableBody.appendChild(tr);
    });
  } catch (error) {
    alert(error.message);
  }
}

function clearCategoryForm() {
  categoryForm.reset();
  document.getElementById('categoryId').value = '';
  document.getElementById('categoryModalLabel').textContent = 'Добавить категорию';
}

function clearConditionForm() {
  conditionForm.reset();
  document.getElementById('conditionId').value = '';
  document.getElementById('conditionModalLabel').textContent = 'Добавить состояние';
}

addCategoryBtn.addEventListener('click', () => {
  clearCategoryForm();
  categoryModal.show();
});

addConditionBtn.addEventListener('click', () => {
  clearConditionForm();
  conditionModal.show();
});

categoryForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('categoryId').value;
  const categoryName = document.getElementById('categoryNameInput').value.trim();
  const description = document.getElementById('categoryDescriptionInput').value.trim();

  if (!categoryName) {
    alert('Название категории обязательно');
    return;
  }

  const payload = { categoryName, description };

  try {
    let res;
    if (id) {
      res = await fetch(`/api/categories/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
    } else {
      res = await fetch('/api/categories', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
    }
    if (!res.ok) {
      const err = await res.text();
      throw new Error(err || 'Ошибка при сохранении категории');
    }
    categoryModal.hide();
    loadCategories();
  } catch (error) {
    alert(error.message);
  }
});

conditionForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('conditionId').value;
  const conditionName = document.getElementById('conditionNameInput').value.trim();

  if (!conditionName) {
    alert('Название состояния обязательно');
    return;
  }

  const payload = { conditionName };

  try {
    let res;
    if (id) {
      res = await fetch(`/api/conditions/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
    } else {
      res = await fetch('/api/conditions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
    }
    if (!res.ok) {
      const err = await res.text();
      throw new Error(err || 'Ошибка при сохранении состояния');
    }
    conditionModal.hide();
    loadConditions();
  } catch (error) {
    alert(error.message);
  }
});

async function editCategory(id) {
  try {
    const res = await fetch(`/api/categories/${id}`);
    if (!res.ok) throw new Error('Ошибка загрузки категории');
    const category = await res.json();
    document.getElementById('categoryId').value = category.categoryId;
    document.getElementById('categoryNameInput').value = category.categoryName;
    document.getElementById('categoryDescriptionInput').value = category.description || '';
    document.getElementById('categoryModalLabel').textContent = 'Редактировать категорию';
    categoryModal.show();
  } catch (error) {
    alert(error.message);
  }
}

async function editCondition(id) {
  try {
    const res = await fetch(`/api/conditions/${id}`);
    if (!res.ok) throw new Error('Ошибка загрузки состояния');
    const condition = await res.json();
    document.getElementById('conditionId').value = condition.conditionId;
    document.getElementById('conditionNameInput').value = condition.conditionName;
    document.getElementById('conditionModalLabel').textContent = 'Редактировать состояние';
    conditionModal.show();
  } catch (error) {
    alert(error.message);
  }
}

async function deleteCategory(id) {
  if (!confirm('Удалить категорию?')) return;
  try {
    const res = await fetch(`/api/categories/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Ошибка удаления категории');
    loadCategories();
  } catch (error) {
    alert(error.message);
  }
}

async function deleteCondition(id) {
  if (!confirm('Удалить состояние?')) return;
  try {
    const res = await fetch(`/api/conditions/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Ошибка удаления состояния');
    loadConditions();
  } catch (error) {
    alert(error.message);
  }
}

window.onload = () => {
  loadCategories();
  loadConditions();
};

