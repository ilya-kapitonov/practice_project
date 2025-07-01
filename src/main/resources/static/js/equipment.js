const equipmentTableBody = document.getElementById('equipmentTableBody');
const equipmentForm = document.getElementById('equipmentForm');
const categorySelect = document.getElementById('categorySelect');
const conditionSelect = document.getElementById('conditionSelect');
const saveBtn = document.getElementById('saveEquipmentBtn');
const cancelBtn = document.getElementById('cancelEquipmentBtn');
const equipmentIdInput = document.getElementById('equipmentId');

function clearForm() {
    equipmentForm.reset();
    equipmentIdInput.value = '';
}

function fillForm(equipment) {
    equipmentIdInput.value = equipment.equipmentId;
    document.getElementById('serialNumber').value = equipment.serialNumber || '';
    document.getElementById('equipmentName').value = equipment.equipmentName || '';
    document.getElementById('quantity').value = equipment.quantity ?? '';
    document.getElementById('lastCheckDate').value = equipment.lastCheckDate ? equipment.lastCheckDate.slice(0, 10) : '';
    document.getElementById('endOfServiceDate').value = equipment.endOfServiceDate ? equipment.endOfServiceDate.slice(0, 10) : '';
    document.getElementById('description').value = equipment.description || '';
    categorySelect.value = equipment.category?.categoryId || '';
    conditionSelect.value = equipment.condition?.conditionId || '';
}
function isExpiredOrToday(dateStr) {
    if (!dateStr) return false;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const endDate = new Date(dateStr);
    endDate.setHours(0, 0, 0, 0);
    return endDate <= today;
}

async function loadCategories() {
    const res = await fetch('/api/categories?sortBy=categoryName&sortDir=ASC');
    if (!res.ok) throw new Error('Ошибка загрузки категорий');
    const categories = await res.json();
    categorySelect.innerHTML = '<option value="">-- Выберите категорию --</option>';
    categories.forEach(cat => {
        const option = document.createElement('option');
        option.value = cat.categoryId;
        option.textContent = cat.categoryName;
        categorySelect.appendChild(option);
    });
}

async function loadConditions() {
    const res = await fetch('/api/conditions?sortBy=conditionName&sortDir=ASC');
    if (!res.ok) throw new Error('Ошибка загрузки состояний');
    const conditions = await res.json();
    conditionSelect.innerHTML = '<option value="">-- Выберите состояние --</option>';
    conditions.forEach(cond => {
        const option = document.createElement('option');
        option.value = cond.conditionId;
        option.textContent = cond.conditionName;
        conditionSelect.appendChild(option);
    });
}

async function loadEquipment() {
    const res = await fetch('/api/equipment?sortBy=serialNumber&sortDir=ASC');
    if (!res.ok) throw new Error('Ошибка загрузки оборудования');
    const equipmentList = await res.json();

    equipmentTableBody.innerHTML = '';
    equipmentList.forEach(eq => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${eq.serialNumber}</td>
            <td>${eq.equipmentName}</td>
            <td>${eq.category?.categoryName || ''}</td>
            <td>${eq.condition?.conditionName || ''}</td>
            <td>${eq.quantity}</td>
            <td>${eq.lastCheckDate ? new Date(eq.lastCheckDate).toLocaleDateString() : ''}</td>
            <td>${eq.endOfServiceDate ? new Date(eq.endOfServiceDate).toLocaleDateString() : ''}</td>
            <td>${eq.description || ''}</td>
            <td>
                <button class="btn btn-sm" data-id="${eq.equipmentId}" data-action="edit" title="Редактировать">
                    <i class="bi bi-pencil-square"></i>
                </button>
                <button class="btn btn-sm" data-id="${eq.equipmentId}" data-action="delete" title="Удалить">
                    <i class="bi bi-x-square-fill"></i>
                </button>
            </td>
        `;
        equipmentTableBody.appendChild(tr);
    });
}

equipmentTableBody.addEventListener('click', async (e) => {
    const button = e.target.closest('button');
    if (!button) return;

    const id = button.dataset.id;
    const action = button.dataset.action;

    if (!id || !action) return;

    if (action === 'edit') {
        try {
            const res = await fetch(`/api/equipment/${id}`);
            if (!res.ok) throw new Error('Ошибка загрузки оборудования');
            const equipment = await res.json();
            fillForm(equipment);

            const collapseEl = document.getElementById('equipmentFormCollapse');
            let bsCollapse = bootstrap.Collapse.getInstance(collapseEl);
            if (!bsCollapse) {
                bsCollapse = new bootstrap.Collapse(collapseEl, { toggle: false });
            }
            bsCollapse.show();
        } catch (error) {
            alert(error.message);
        }
    } else if (action === 'delete') {
        if (!confirm('Удалить оборудование?')) return;
        try {
            const res = await fetch(`/api/equipment/${id}`, { method: 'DELETE' });
            if (!res.ok) throw new Error('Ошибка удаления оборудования');
            await loadEquipment();
        } catch (error) {
            alert(error.message);
        }
    }
});

equipmentForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = equipmentIdInput.value;
    const serialNumber = document.getElementById('serialNumber').value.trim();
    const equipmentName = document.getElementById('equipmentName').value.trim();
    const categoryId = categorySelect.value;
    const conditionId = conditionSelect.value;
    const quantity = parseInt(document.getElementById('quantity').value, 10);
    const lastCheckDate = document.getElementById('lastCheckDate').value;
    const endOfServiceDate = document.getElementById('endOfServiceDate').value;
    const description = document.getElementById('description').value.trim();

    if (!serialNumber || !equipmentName || !categoryId || !conditionId || isNaN(quantity) || quantity < 0 || !endOfServiceDate) {
        alert('Пожалуйста, заполните все обязательные поля корректно');
        return;
    }

    const payload = {
        serialNumber,
        equipmentName,
        quantity,
        lastCheckDate: lastCheckDate || null,
        endOfServiceDate,
        description,
        category: { categoryId: parseInt(categoryId) },
        condition: { conditionId: parseInt(conditionId) }
    };

    try {
        let res;
        if (id) {
            res = await fetch(`/api/equipment/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        } else {
            res = await fetch('/api/equipment', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        }

        if (!res.ok) {
            const err = await res.text();
            throw new Error(err || 'Ошибка при сохранении оборудования');
        }

        clearForm();
        await loadEquipment();

        const collapseEl = document.getElementById('equipmentFormCollapse');
        const bsCollapse = bootstrap.Collapse.getInstance(collapseEl);
        if (bsCollapse) bsCollapse.hide();

    } catch (error) {
        alert(error.message);
    }
});

cancelBtn.addEventListener('click', () => {
    clearForm();
    const collapseEl = document.getElementById('equipmentFormCollapse');
    const bsCollapse = bootstrap.Collapse.getInstance(collapseEl);
    if (bsCollapse) bsCollapse.hide();
});

window.onload = async () => {
    try {
        await loadCategories();
        await loadConditions();
        await loadEquipment();
    } catch (error) {
        alert(error.message);
    }
};

