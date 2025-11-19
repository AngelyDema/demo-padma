function filterByDate(filter) {
    if (filter === 'today') {
        window.location.href = '/todos/today';
    } else if (filter === 'week') {
        window.location.href = '/todos/week';
    } else if (filter === 'all') {
        window.location.href = '/todos/all';
    }

}

    //marcar hecho 

async function toggleTodo(todoId, checkboxElem) {
  // Optimista: aplicamos cambio visual inmediatamente
  const li = checkboxElem.closest('li');
  const isChecked = checkboxElem.checked;

  // aplicar clase visual mientras esperamos respuesta
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
      // revertir UI si hubo error
      checkboxElem.checked = !isChecked;
      if (!isChecked) li.classList.add('completed');
      else li.classList.remove('completed');

      console.error('Error al actualizar el todo:', resp.status);
      alert('No se pudo actualizar la tarea. Intenta de nuevo.');
    } else {
      // ok — si quieres, puedes leer body o mostrar toast
    }
  } catch (err) {
    // error de red — revertir cambios visuales
    checkboxElem.checked = !isChecked;
    if (!isChecked) li.classList.add('completed');
    else li.classList.remove('completed');

    console.error(err);
    alert('Error de red. Revisa tu conexión.');
  }
}

// ===== ESPERAR A QUE EL DOM CARGUE =====
document.addEventListener('DOMContentLoaded', function() {
  console.log('🟢 DOM Cargado - inicializando todo.js');

  // ===== OBTENER ELEMENTOS =====
  const taskNameInput = document.getElementById('taskName');
  const taskDateInput = document.getElementById('taskDate');
  const taskDescInput = document.getElementById('taskDesc');
  const taskListSelect = document.getElementById('taskList');
  const taskPrioritySelect = document.getElementById('taskPriority');
  const toggleMoreBtn = document.getElementById('toggleMoreBtn');
  const moreDetails = document.getElementById('moreDetails');
  const createBtn = document.getElementById('createBtn');
  const cancelBtn = document.getElementById('cancelBtn');

  console.log('📋 Elementos encontrados:', {
    taskName: taskNameInput ? '✅' : '❌',
    taskDate: taskDateInput ? '✅' : '❌',
    createBtn: createBtn ? '✅' : '❌'
  });

  // Validar que todos los elementos existan
  if (!taskNameInput || !taskDateInput || !createBtn) {
    console.error('❌ Faltan elementos en el HTML');
    return;
  }

  // ===== EVENT LISTENERS =====

  // Toggle detalles
  toggleMoreBtn.addEventListener('click', function() {
    console.log('▶️ Toggle More clicked');
    moreDetails.classList.toggle('hidden');
    toggleMoreBtn.textContent = moreDetails.classList.contains('hidden') ? '⋮' : '✕';
  });

  // Cancelar
  cancelBtn.addEventListener('click', function() {
    console.log('🔄 Cancelar clicked');
    resetForm();
  });

  // Crear tarea
  createBtn.addEventListener('click', function() {
    console.log('🚀 Crear clicked');
    createTodo();
  });

  // ===== FUNCIONES =====

  function resetForm() {
    console.log('🔄 Reseteando formulario...');
    taskNameInput.value = '';
    taskDateInput.value = new Date().toISOString().split('T')[0];
    taskDescInput.value = '';
    taskListSelect.value = '';
    taskPrioritySelect.value = 'Not Urgent but Important';
    moreDetails.classList.add('hidden');
    toggleMoreBtn.textContent = '⋮';
    taskNameInput.focus();
  }

  function createTodo() {
    console.log('========== CREAR TAREA ==========');

    // Validar nombre
    if (!taskNameInput.value.trim()) {
      console.warn('⚠️ Nombre vacío');
      alert('Por favor ingresa un nombre para la tarea');
      return;
    }

    // Obtener userId
    const userId = document.body.getAttribute('data-user-id');
    console.log('🔍 userId del body:', userId);

    if (!userId) {
      console.error('❌ No hay userId en el body');
      console.log('📋 Body attributes:', {
        'data-user-id': document.body.getAttribute('data-user-id')
      });
      alert('Error: Usuario no autenticado. Recarga la página.');
      return;
    }

    // Preparar payload
    const payload = {
      name: taskNameInput.value.trim(),
      description: taskDescInput.value.trim(),
      dueDate: taskDateInput.value,
      priority: taskPrioritySelect.value,
      users: { userId: parseInt(userId) },
      listTodos: taskListSelect.value ? { listTodoId: parseInt(taskListSelect.value) } : null
    };

    console.log('📦 Payload:', payload);

    // POST request
    console.log('📤 Enviando POST a /api/todos/create...');
    
    fetch('/api/todos/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    .then(res => {
      console.log('📨 Response status:', res.status);
      return res.json();
    })
    .then(data => {
      console.log('✅ Response data:', data);
      
      if (data.todoId) {
        console.log('✅✅ Tarea creada con ID:', data.todoId);
        alert('✅ Tarea creada exitosamente');
        resetForm();
        setTimeout(() => {
          console.log('🔄 Recargando página...');
          location.reload();
        }, 500);
      } else {
        console.error('❌ Error en respuesta:', data);
        alert('❌ Error: ' + (data.error || 'Error desconocido'));
      }
    })
    .catch(err => {
      console.error('❌ Fetch error:', err);
      alert('❌ Error creando tarea: ' + err.message);
    });
  }

});



document.addEventListener('DOMContentLoaded', function() {
  console.log('🟢 DOM Cargado - inicializando menú de tareas');
  
  // ===== VARIABLES ===== 
  const editModal = document.getElementById('editModal');
  const cancelEditBtn = document.getElementById('cancelEditBtn');
  const saveEditBtn = document.getElementById('saveEditBtn');
  let currentEditTodoId = null;

  // ===== FUNCIÓN PARA AGREGAR EVENTOS A BOTONES =====
  function attachTodoEvents() {
    console.log('🔧 Agregando eventos a los botones de tareas...');
    
    // ===== MENÚ DE TAREAS =====
    document.querySelectorAll('.todo-menu-btn').forEach(btn => {
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        console.log('📍 Botón ⋮ clickeado');
        
        const todoId = this.getAttribute('data-todo-id');
        console.log('📝 TodoId:', todoId);
        const menu = document.getElementById(`menu-${todoId}`);
        
        if (!menu) {
          console.error('❌ No se encontró menu-' + todoId);
          return;
        }

        // Cerrar otros menús
        document.querySelectorAll('.todo-menu').forEach(m => {
          if (m.id !== `menu-${todoId}`) {
            m.classList.add('hidden');
          }
        });
        
        console.log('✅ Menu toggled. Hidden:', menu.classList.contains('hidden'));
        menu.classList.toggle('hidden');
      });
    });

    // ===== BOTÓN EDITAR =====
    document.querySelectorAll('.edit-btn').forEach(btn => {
      btn.addEventListener('click', function() {
        const todoId = this.getAttribute('data-todo-id');
        console.log('✏️ Editar clickeado - TodoId:', todoId);
        openEditModal(todoId);
        
        // Cerrar menú
        const menu = document.getElementById(`menu-${todoId}`);
        if (menu) menu.classList.add('hidden');
      });
    });

    // ===== BOTÓN ELIMINAR =====
    document.querySelectorAll('.delete-btn').forEach(btn => {
      btn.addEventListener('click', function() {
        const todoId = this.getAttribute('data-todo-id');
        console.log('🗑️ Eliminar clickeado - TodoId:', todoId);
        
        if (confirm('¿Estás seguro de que quieres eliminar esta tarea?')) {
          deleteTodo(todoId);
        }
        
        // Cerrar menú
        const menu = document.getElementById(`menu-${todoId}`);
        if (menu) menu.classList.add('hidden');
      });
    });

    console.log('✅ Eventos agregados');
  }

  // ===== ABRIR MODAL DE EDICIÓN =====
  function openEditModal(todoId) {
    console.log('📝 Abriendo modal para editar todoId:', todoId);
    
    currentEditTodoId = todoId;
    
    // Obtener datos de la tarea del DOM
    const todoItem = document.querySelector(`[data-todo-id="${todoId}"]`).closest('.todo-item-wrapper');
    if (!todoItem) {
      console.error('❌ No se encontró todoItem');
      return;
    }

    const name = todoItem.querySelector('.todo-name').textContent;
    const date = todoItem.querySelector('.todo-date').textContent;
    
    console.log('📋 Datos encontrados - Name:', name, 'Date:', date);

    // Llenar el formulario
    document.getElementById('editTaskName').value = name;
    document.getElementById('editTaskDesc').value = '';
    
    // Convertir formato de fecha "Nov 16" a "2025-11-16"
    const today = new Date();
    const dateObj = new Date(today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + date.split(' ')[1]);
    document.getElementById('editTaskDate').value = dateObj.toISOString().split('T')[0];
    
    editModal.classList.remove('hidden');
    console.log('✅ Modal abierto');
  }

  // ===== GUARDAR CAMBIOS =====
  saveEditBtn.addEventListener('click', function() {
    console.log('💾 Guardando cambios para todoId:', currentEditTodoId);
    
    const name = document.getElementById('editTaskName').value.trim();
    const desc = document.getElementById('editTaskDesc').value.trim();
    const date = document.getElementById('editTaskDate').value;
    const priority = document.getElementById('editTaskPriority').value;
    const listValue = document.getElementById('editTaskList').value;
    
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

    console.log('📤 Enviando payload:', payload);

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

  // ===== CANCELAR EDICIÓN =====
  cancelEditBtn.addEventListener('click', function() {
    editModal.classList.add('hidden');
    currentEditTodoId = null;
  });

  // ===== ELIMINAR TAREA =====
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

  // ===== CERRAR MENÚS AL HACER CLICK FUERA =====
  document.addEventListener('click', function(e) {
    if (!e.target.classList.contains('todo-menu-btn')) {
      document.querySelectorAll('.todo-menu').forEach(m => m.classList.add('hidden'));
    }
  });

  // ===== EJECUTAR INICIAL Y DESPUÉS DE CAMBIOS =====
  attachTodoEvents();
  
  // Opcional: si las tareas se cargan dinámicamente, recargar eventos cada 500ms
  // setInterval(attachTodoEvents, 500);
});

document.addEventListener('DOMContentLoaded', function() {
  console.log('🟢 Inicializando menú de listas');
  
  const listMenuBtns = document.querySelectorAll('.list-menu-btn');
  
  listMenuBtns.forEach(btn => {
    btn.addEventListener('click', function(e) {
      e.preventDefault();
      e.stopPropagation();
      
      const listId = this.getAttribute('data-list-id');
      const menu = document.getElementById(`listmenu-${listId}`);
      
      console.log('📍 Click en botón ⋮ - ListId:', listId);
      
      // Cerrar otros menús
      document.querySelectorAll('.list-item-menu').forEach(m => {
        if (m.id !== `listmenu-${listId}`) {
          m.classList.add('hidden');
        }
      });
      
      menu.classList.toggle('hidden');
    });
  });

  // Editar lista
  document.querySelectorAll('.edit-list-btn').forEach(btn => {
    btn.addEventListener('click', function() {
      const listId = this.getAttribute('data-list-id');
      console.log('✏️ Editar lista:', listId);
      // Aquí ya está el código en tu JavaScript anterior
      openEditListModal(listId);
      document.getElementById(`listmenu-${listId}`).classList.add('hidden');
    });
  });

  // Eliminar lista
  document.querySelectorAll('.delete-list-btn').forEach(btn => {
    btn.addEventListener('click', function() {
      const listId = this.getAttribute('data-list-id');
      console.log('🗑️ Eliminar lista:', listId);
      if (confirm('¿Estás seguro?')) {
        deleteList(listId);
      }
      document.getElementById(`listmenu-${listId}`).classList.add('hidden');
    });
  });

  // Cerrar menús al hacer click fuera
  document.addEventListener('click', function(e) {
    if (!e.target.classList.contains('list-menu-btn')) {
      document.querySelectorAll('.list-item-menu').forEach(m => {
        m.classList.add('hidden');
      });
    }
  });
});


