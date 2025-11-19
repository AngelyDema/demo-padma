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


// ===== EDITAR LISTA =====
document.addEventListener('DOMContentLoaded', function() {
  console.log('🟢 Inicializando menú de listas');
  
  const listMenuBtns = document.querySelectorAll('.list-menu-btn');
  const editListModal = document.getElementById('editListModal');
  const deleteListModal = document.getElementById('deleteListModal');
  
  let currentEditListId = null;
  let currentDeleteListId = null;
  
  // ===== BOTONES DEL MODAL DE EDICIÓN =====
  const cancelEditListBtn = document.getElementById('cancelEditListBtn');
  const saveEditListBtn = document.getElementById('saveEditListBtn');
  const editListName = document.getElementById('editListName');
  const editListDesc = document.getElementById('editListDesc');
  
  // ===== BOTONES DEL MODAL DE ELIMINACIÓN =====
  const cancelDeleteListBtn = document.getElementById('cancelDeleteListBtn');
  const confirmDeleteListBtn = document.getElementById('confirmDeleteListBtn');
  const deleteListName = document.getElementById('deleteListName');
  
  // ===== CLICK EN BOTÓN ⋮ =====
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

  // ===== BOTÓN EDITAR LISTA =====
  document.querySelectorAll('.edit-list-btn').forEach(btn => {
    btn.addEventListener('click', function() {
      const listId = this.getAttribute('data-list-id');
      console.log('✏️ Editar lista:', listId);
      
      currentEditListId = listId;
      
      // Obtener datos del DOM
      const listItem = document.querySelector(`a[href*="/todos/list/${listId}"]`);
      if (listItem) {
        const listNameText = listItem.textContent;
        editListName.value = listNameText;
        editListDesc.value = ''; // La descripción no está en el HTML, se cargará del servidor si es necesario
      }
      
      editListModal.classList.remove('hidden');
      editListName.focus();
      
      // Cerrar menú
      document.getElementById(`listmenu-${listId}`).classList.add('hidden');
    });
  });

  // ===== GUARDAR CAMBIOS DE LISTA =====
  saveEditListBtn.addEventListener('click', function() {
    const name = editListName.value.trim();
    const desc = editListDesc.value.trim();
    
    if (!name) {
      alert('El nombre es requerido');
      editListName.focus();
      return;
    }

    const payload = {
      name: name,
      description: desc
    };

    console.log('📤 Guardando lista:', payload);

    fetch(`/api/lists/id/${currentEditListId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    .then(res => res.json())
    .then(data => {
      if (data.error) {
        console.error('❌ Error:', data.error);
        alert('❌ Error: ' + data.error);
        return;
      }
      console.log('✅ Lista actualizada:', data);
      alert('✅ Lista actualizada exitosamente');
      editListModal.classList.add('hidden');
      setTimeout(() => location.reload(), 500);
    })
    .catch(err => {
      console.error('❌ Error:', err);
      alert('Error al actualizar la lista');
    });
  });

  // ===== CANCELAR EDICIÓN =====
  cancelEditListBtn.addEventListener('click', function() {
    editListModal.classList.add('hidden');
    currentEditListId = null;
  });

  // ===== BOTÓN ELIMINAR LISTA =====
  document.querySelectorAll('.delete-list-btn').forEach(btn => {
    btn.addEventListener('click', function() {
      const listId = this.getAttribute('data-list-id');
      console.log('🗑️ Eliminar lista:', listId);
      
      currentDeleteListId = listId;
      
      // Obtener nombre de la lista
      const listItem = document.querySelector(`a[href*="/todos/list/${listId}"]`);
      if (listItem) {
        deleteListName.textContent = listItem.textContent;
      }
      
      deleteListModal.classList.remove('hidden');
      
      // Cerrar menú
      document.getElementById(`listmenu-${listId}`).classList.add('hidden');
    });
  });

  // ===== CONFIRMAR ELIMINACIÓN =====
  confirmDeleteListBtn.addEventListener('click', function() {
    console.log('🗑️ Confirmando eliminación de lista:', currentDeleteListId);

    fetch(`/api/lists/id/${currentDeleteListId}`, {
      method: 'DELETE'
    })
    .then(res => {
      if (!res.ok) throw new Error('Error al eliminar');
      console.log('✅ Lista eliminada');
      alert('✅ Lista eliminada');
      deleteListModal.classList.add('hidden');
      setTimeout(() => location.reload(), 500);
    })
    .catch(err => {
      console.error('❌ Error:', err);
      alert('Error al eliminar la lista');
    });
  });

  // ===== CANCELAR ELIMINACIÓN =====
  cancelDeleteListBtn.addEventListener('click', function() {
    deleteListModal.classList.add('hidden');
    currentDeleteListId = null;
  });

  // ===== CERRAR MENÚS AL HACER CLICK FUERA =====
  document.addEventListener('click', function(e) {
    if (!e.target.classList.contains('list-menu-btn')) {
      document.querySelectorAll('.list-item-menu').forEach(m => {
        m.classList.add('hidden');
      });
    }
  });

  // ===== CERRAR MODALES AL HACER CLICK FUERA =====
  editListModal.addEventListener('click', function(e) {
    if (e.target === editListModal) {
      this.classList.add('hidden');
    }
  });

  deleteListModal.addEventListener('click', function(e) {
    if (e.target === deleteListModal) {
      this.classList.add('hidden');
    }
  });
});
