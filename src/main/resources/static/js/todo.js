// ===== FILTER BY DATE =====
function filterByDate(filter) {
  if (filter === 'today') {
    window.location.href = '/todos/today';
  } else if (filter === 'week') {
    window.location.href = '/todos/week';
  } else if (filter === 'all') {
    window.location.href = '/todos/all';
  }
}

// ===== TOGGLE TODO =====
async function toggleTodo(todoId, checkboxElem) {
  const li = checkboxElem.closest('li');
  const isChecked = checkboxElem.checked;

  if (isChecked) {
    li.classList.add('completed');
  } else {
    li.classList.remove('completed');
  }

  try {
    const resp = await fetch(`/api/todos/${todoId}/toggle`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' }
    });

    if (!resp.ok) {
      checkboxElem.checked = !isChecked;
      if (!isChecked) li.classList.add('completed');
      else li.classList.remove('completed');
      console.error('Error al actualizar el todo:', resp.status);
      alert('No se pudo actualizar la tarea. Intenta de nuevo.');
    }
  } catch (err) {
    checkboxElem.checked = !isChecked;
    if (!isChecked) li.classList.add('completed');
    else li.classList.remove('completed');
    console.error(err);
    alert('Error de red. Revisa tu conexión.');
  }
}

// ===== SINGLE DOMContentLoaded =====
document.addEventListener('DOMContentLoaded', function() {
  console.log('🟢 DOM Cargado - inicializando todo.js');

  // ===== CREAR TAREA =====
  initCreateTodo();
  
  // ===== MENÚ DE TAREAS (EDIT/DELETE) =====
  initTodoMenu();
});

// ===== CREAR TAREA =====
function initCreateTodo() {
  const taskNameInput = document.getElementById('taskName');
  const taskDateInput = document.getElementById('taskDate');
  const taskDescInput = document.getElementById('taskDesc');
  const taskListSelect = document.getElementById('taskList');
  const taskPrioritySelect = document.getElementById('taskPriority');
  const toggleMoreBtn = document.getElementById('toggleMoreBtn');
  const moreDetails = document.getElementById('moreDetails');
  const createBtn = document.getElementById('createBtn');
  const cancelBtn = document.getElementById('cancelBtn');

  // ✅ Check if elements exist before adding listeners
  if (!taskNameInput || !createBtn) {
    console.warn('⚠️ Elementos de crear tarea no encontrados - saltando...');
    return;
  }

  console.log('✅ Elementos de crear tarea encontrados');

  // Toggle detalles
  if (toggleMoreBtn && moreDetails) {
    toggleMoreBtn.addEventListener('click', function() {
      moreDetails.classList.toggle('hidden');
      toggleMoreBtn.textContent = moreDetails.classList.contains('hidden') ? '⋮' : '✕';
    });
  }

  // Cancelar
  if (cancelBtn) {
    cancelBtn.addEventListener('click', function() {
      resetForm();
    });
  }

  // Crear tarea
  createBtn.addEventListener('click', function() {
    createTodo();
  });

  function resetForm() {
    taskNameInput.value = '';
    if (taskDateInput) taskDateInput.value = new Date().toISOString().split('T')[0];
    if (taskDescInput) taskDescInput.value = '';
    if (taskListSelect) taskListSelect.value = '';
    if (taskPrioritySelect) taskPrioritySelect.value = 'Not Urgent but Important';
    if (moreDetails) moreDetails.classList.add('hidden');
    if (toggleMoreBtn) toggleMoreBtn.textContent = '⋮';
    taskNameInput.focus();
  }

  function createTodo() {
    if (!taskNameInput.value.trim()) {
      alert('Por favor ingresa un nombre para la tarea');
      return;
    }

    const userId = document.body.getAttribute('data-user-id');
    if (!userId) {
      alert('Error: Usuario no autenticado. Recarga la página.');
      return;
    }

    const payload = {
      name: taskNameInput.value.trim(),
      description: taskDescInput ? taskDescInput.value.trim() : '',
      dueDate: taskDateInput ? taskDateInput.value : new Date().toISOString().split('T')[0],
      priority: taskPrioritySelect ? taskPrioritySelect.value : 'Not Urgent but Important',
      users: { userId: parseInt(userId) },
      listTodos: (taskListSelect && taskListSelect.value) ? { listTodoId: parseInt(taskListSelect.value) } : null
    };

    console.log('📦 Payload:', payload);

    fetch('/api/todos/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    .then(res => res.json())
    .then(data => {
      if (data.todoId) {
        alert('✅ Tarea creada exitosamente');
        resetForm();
        setTimeout(() => location.reload(), 500);
      } else {
        alert('❌ Error: ' + (data.error || 'Error desconocido'));
      }
    })
    .catch(err => {
      console.error('❌ Fetch error:', err);
      alert('❌ Error creando tarea: ' + err.message);
    });
  }
}

// ===== MENÚ DE TAREAS =====
function initTodoMenu() {
  const editModal = document.getElementById('editTodo');
  const cancelEditBtn = document.getElementById('cancelEditBtn');
  const saveEditBtn = document.getElementById('saveEditBtn');
  let currentEditTodoId = null;

  // ===== ATTACH EVENTS TO TODO BUTTONS =====
  attachTodoMenuEvents();

  // ===== MODAL EVENTS (only if modal exists) =====
  if (editModal && cancelEditBtn && saveEditBtn) {
    console.log('✅ Modal de edición encontrado');

    // Save changes
    saveEditBtn.addEventListener('click', function() {
      console.log('💾 Guardando cambios para todoId:', currentEditTodoId);

      const editTaskName = document.getElementById('editTaskName');
      const editTaskDesc = document.getElementById('editTaskDesc');
      const editTaskDate = document.getElementById('editTaskDate');
      const editTaskPriority = document.getElementById('editTaskPriority');
      const editTaskList = document.getElementById('editTaskList');

      const name = editTaskName ? editTaskName.value.trim() : '';
      const desc = editTaskDesc ? editTaskDesc.value.trim() : '';
      const date = editTaskDate ? editTaskDate.value : '';
      const priority = editTaskPriority ? editTaskPriority.value : '';
      const listValue = editTaskList ? editTaskList.value : '';

      if (!name) {
        alert('El nombre es requerido');
        return;
      }

      const payload = {
        name: name,
        description: desc,
        dueDate: date,
        priority: priority,
        listTodos: listValue ? { listTodoId: parseInt(listValue) } : null
      };

      fetch(`/api/todos/id/${currentEditTodoId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      .then(res => res.json())
      .then(data => {
        console.log('✅ Tarea actualizada:', data);
        alert('✅ Tarea actualizada exitosamente');
        editModal.classList.add('hidden');
        setTimeout(() => location.reload(), 500);
      })
      .catch(err => {
        console.error('❌ Error:', err);
        alert('Error al actualizar la tarea');
      });
    });

    // Cancel edit
    cancelEditBtn.addEventListener('click', function() {
      editModal.classList.add('hidden');
      currentEditTodoId = null;
    });

    // Close modal on background click
    editModal.addEventListener('click', function(e) {
      if (e.target === editModal) {
        editModal.classList.add('hidden');
      }
    });
  } else {
    console.warn('⚠️ Modal de edición no encontrado - funcionalidad de edición deshabilitada');
  }

  // ===== ATTACH TODO MENU EVENTS =====
  function attachTodoMenuEvents() {
    console.log('🔧 Agregando eventos a los botones de tareas...');

    // Menu buttons (⋮)
    document.querySelectorAll('.todo-menu-btn').forEach(btn => {
      // ✅ Remove existing listeners to prevent duplicates
      btn.replaceWith(btn.cloneNode(true));
    });

    // Re-select after clone
    document.querySelectorAll('.todo-menu-btn').forEach(btn => {
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();

        const todoId = this.getAttribute('data-todo-id');
        const menu = document.getElementById(`menu-${todoId}`);

        console.log('🔽 Click en ⋮ - TodoId:', todoId);

        if (!menu) {
          console.error('❌ No se encontró menu-' + todoId);
          return;
        }

        // Close other menus
        document.querySelectorAll('.todo-menu').forEach(m => {
          if (m.id !== `menu-${todoId}`) {
            m.classList.add('hidden');
          }
        });

        menu.classList.toggle('hidden');
      });
    });

    // Edit buttons
    document.querySelectorAll('.edit-btn').forEach(btn => {
      btn.replaceWith(btn.cloneNode(true));
    });

    document.querySelectorAll('.edit-btn').forEach(btn => {
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();

        const todoId = this.getAttribute('data-todo-id');
        console.log('✏️ Editar clickeado - TodoId:', todoId);

        openEditModal(todoId);

        // Close menu
        const menu = document.getElementById(`menu-${todoId}`);
        if (menu) menu.classList.add('hidden');
      });
    });

    // Delete buttons
    document.querySelectorAll('.delete-btn').forEach(btn => {
      btn.replaceWith(btn.cloneNode(true));
    });

    document.querySelectorAll('.delete-btn').forEach(btn => {
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();

        const todoId = this.getAttribute('data-todo-id');
        console.log('🗑️ Eliminar clickeado - TodoId:', todoId);

        if (confirm('¿Estás seguro de que quieres eliminar esta tarea?')) {
          deleteTodo(todoId);
        }

        // Close menu
        const menu = document.getElementById(`menu-${todoId}`);
        if (menu) menu.classList.add('hidden');
      });
    });

    console.log('✅ Eventos de menú agregados');
  }

  // ===== OPEN EDIT MODAL =====
  function openEditModal(todoId) {
    if (!editModal) {
      console.error('❌ Modal de edición no disponible');
      alert('Error: Modal de edición no cargado. Verifica que el fragmento esté incluido.');
      return;
    }

    currentEditTodoId = todoId;

    // Find todo item
    const todoItem = document.querySelector(`.todo-item-wrapper:has([data-todo-id="${todoId}"])`) ||
                     document.querySelector(`[data-todo-id="${todoId}"]`)?.closest('.todo-item-wrapper');

    if (!todoItem) {
      console.error('❌ No se encontró el todo item');
      return;
    }

    const nameEl = todoItem.querySelector('.todo-name');
    const dateEl = todoItem.querySelector('.todo-date');

    const name = nameEl ? nameEl.textContent : '';
    const dateText = dateEl ? dateEl.textContent : '';

    console.log('📋 Datos encontrados - Name:', name, 'Date:', dateText);

    // Fill form
    const editTaskName = document.getElementById('editTaskName');
    const editTaskDesc = document.getElementById('editTaskDesc');
    const editTaskDate = document.getElementById('editTaskDate');

    if (editTaskName) editTaskName.value = name;
    if (editTaskDesc) editTaskDesc.value = '';

    // Convert date format "Nov 16" to "2025-11-16"
    if (editTaskDate && dateText) {
      try {
        const months = { 'Jan': '01', 'Feb': '02', 'Mar': '03', 'Apr': '04', 'May': '05', 'Jun': '06',
                        'Jul': '07', 'Aug': '08', 'Sep': '09', 'Oct': '10', 'Nov': '11', 'Dec': '12' };
        const parts = dateText.split(' ');
        const month = months[parts[0]] || '01';
        const day = parts[1] ? parts[1].padStart(2, '0') : '01';
        const year = new Date().getFullYear();
        editTaskDate.value = `${year}-${month}-${day}`;
      } catch (e) {
        editTaskDate.value = new Date().toISOString().split('T')[0];
      }
    }

    editModal.classList.remove('hidden');
    console.log('✅ Modal abierto');
  }

  // ===== DELETE TODO =====
  function deleteTodo(todoId) {
    console.log('🗑️ Eliminando todoId:', todoId);

    fetch(`/api/todos/id/${todoId}`, {
      method: 'DELETE'
    })
    .then(res => {
      if (!res.ok) throw new Error('Error al eliminar');
      console.log('✅ Tarea eliminada');
      alert('✅ Tarea eliminada');
      setTimeout(() => location.reload(), 500);
    })
    .catch(err => {
      console.error('❌ Error:', err);
      alert('Error al eliminar la tarea');
    });
  }

  // ===== CLOSE MENUS ON OUTSIDE CLICK =====
  document.addEventListener('click', function(e) {
    if (!e.target.classList.contains('todo-menu-btn') && 
        !e.target.closest('.todo-menu')) {
      document.querySelectorAll('.todo-menu').forEach(m => m.classList.add('hidden'));
    }
  });
}