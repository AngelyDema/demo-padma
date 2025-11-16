function filterByDate(filter) {
    if (filter === 'today') {
        window.location.href = '/todos/today';
    } else if (filter === 'week') {
        window.location.href = '/todos/week';
    } else if (filter === 'all') {
        window.location.href = '/todos/all';
    }
}

function toggleTodo(id) {
  fetch(`/api/todos/${id}/toggle`, {
    method: "PUT"
  }).then(() => window.location.reload());
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
  console.log('🟢 DOM Cargado - inicializando crear lista');
  
  // ===== CREAR LISTA =====
  const new_list_form = document.getElementById('new_list_form');
  const listNameInput = document.getElementById('listName');
  const createListBtn = document.getElementById('createListBtn');

  // Obtener userId DENTRO del DOMContentLoaded
  const userId = document.body.getAttribute('data-user-id');
  console.log('🔍 userId:', userId);

  if (!createListBtn) {
    console.error('❌ createListBtn no encontrado');
    return;
  }

  createListBtn.addEventListener('click', createList);

  function createList() {
    console.log('📝 Crear lista...');
    
    if (!listNameInput.value.trim()) {
      alert('Por favor ingresa un nombre para la lista');
      return;
    }

    if (!userId) {
      alert('Error: Usuario no autenticado');
      return;
    }

    const payload = {
      name: listNameInput.value.trim(),
      description: '',
      users: { userId: parseInt(userId) }
    };

    console.log('📤 Enviando:', payload);

    fetch('/api/lists/createList', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    .then(res => res.json())
    .then(data => {
      if (data.listTodoId) {
        console.log('✅ Lista creada:', data);
        alert('✅ Lista creada exitosamente');
        listNameInput.value = '';
        setTimeout(() => location.reload(), 500);
      } else {
        alert('❌ Error: ' + (data.error || 'Error desconocido'));
      }
    })
    .catch(err => {
      console.error('❌ Error:', err);
      alert('❌ Error creando lista: ' + err.message);
    });
  }
});


