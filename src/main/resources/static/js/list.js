// ===== SINGLE DOMContentLoaded =====
document.addEventListener('DOMContentLoaded', function() {
  console.log('🟢 DOM Cargado - inicializando list.js');
  
  // ===== CREAR LISTA =====
  initCreateList();
  
  // ===== MENÚ DE LISTAS (EDIT/DELETE) =====
  initListMenu();
});

// ===== CREAR LISTA =====
function initCreateList() {
  const listNameInput = document.getElementById('listName');
  const createListBtn = document.getElementById('createListBtn');
  const userId = document.body.getAttribute('data-user-id');

  if (!createListBtn || !listNameInput) {
    console.warn('⚠️ Elementos de crear lista no encontrados - saltando...');
    return;
  }

  console.log('✅ Elementos de crear lista encontrados');

  createListBtn.addEventListener('click', function() {
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
  });
}

// ===== MENÚ DE LISTAS =====
function initListMenu() {
  const editListModal = document.getElementById('editListModal');
  const deleteListModal = document.getElementById('deleteListModal');

  let currentEditListId = null;
  let currentDeleteListId = null;

  // ===== ATTACH MENU EVENTS =====
  attachListMenuEvents();

  // ===== EDIT MODAL EVENTS =====
  if (editListModal) {
    const cancelEditListBtn = document.getElementById('cancelEditListBtn');
    const saveEditListBtn = document.getElementById('saveEditListBtn');
    const editListName = document.getElementById('editListName');
    const editListDesc = document.getElementById('editListDesc');

    if (saveEditListBtn && editListName) {
      console.log('✅ Modal de edición de lista encontrado');

      saveEditListBtn.addEventListener('click', function() {
        const name = editListName.value.trim();
        const desc = editListDesc ? editListDesc.value.trim() : '';

        if (!name) {
          alert('El nombre es requerido');
          editListName.focus();
          return;
        }

        const payload = { name: name, description: desc };
        console.log('📤 Guardando lista:', payload);

        fetch(`/api/lists/id/${currentEditListId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        })
        .then(res => res.json())
        .then(data => {
          if (data.error) {
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

      if (cancelEditListBtn) {
        cancelEditListBtn.addEventListener('click', function() {
          editListModal.classList.add('hidden');
          currentEditListId = null;
        });
      }

      // Close on background click
      editListModal.addEventListener('click', function(e) {
        if (e.target === editListModal) {
          editListModal.classList.add('hidden');
        }
      });
    }
  } else {
    console.warn('⚠️ Modal de edición de lista no encontrado');
  }

  // ===== DELETE MODAL EVENTS =====
  if (deleteListModal) {
    const cancelDeleteListBtn = document.getElementById('cancelDeleteListBtn');
    const confirmDeleteListBtn = document.getElementById('confirmDeleteListBtn');

    if (confirmDeleteListBtn) {
      console.log('✅ Modal de eliminación de lista encontrado');

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

      if (cancelDeleteListBtn) {
        cancelDeleteListBtn.addEventListener('click', function() {
          deleteListModal.classList.add('hidden');
          currentDeleteListId = null;
        });
      }

      // Close on background click
      deleteListModal.addEventListener('click', function(e) {
        if (e.target === deleteListModal) {
          deleteListModal.classList.add('hidden');
        }
      });
    }
  } else {
    console.warn('⚠️ Modal de eliminación de lista no encontrado');
  }

  // ===== ATTACH LIST MENU EVENTS =====
  function attachListMenuEvents() {
    console.log('🔧 Agregando eventos a los botones de listas...');

    // ===== MENU BUTTONS (⋮) =====
    document.querySelectorAll('.list-menu-btn').forEach(btn => {
      // Clone to remove existing listeners
      const newBtn = btn.cloneNode(true);
      btn.parentNode.replaceChild(newBtn, btn);
    });

    document.querySelectorAll('.list-menu-btn').forEach(btn => {
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();

        const listId = this.getAttribute('data-list-id');
        const menu = document.getElementById(`listmenu-${listId}`);

        console.log('🔽 Click en ⋮ (lista) - ListId:', listId);

        if (!menu) {
          console.error('❌ No se encontró listmenu-' + listId);
          return;
        }

        // Close other menus
        document.querySelectorAll('.list-item-menu').forEach(m => {
          if (m.id !== `listmenu-${listId}`) {
            m.classList.add('hidden');
          }
        });

        menu.classList.toggle('hidden');
        console.log('✅ Menu toggled. Hidden:', menu.classList.contains('hidden'));
      });
    });

    // ===== EDIT BUTTONS =====
    document.querySelectorAll('.edit-list-btn').forEach(btn => {
      const newBtn = btn.cloneNode(true);
      btn.parentNode.replaceChild(newBtn, btn);
    });

    document.querySelectorAll('.edit-list-btn').forEach(btn => {
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();

        const listId = this.getAttribute('data-list-id');
        console.log('✏️ Editar lista:', listId);

        currentEditListId = listId;

        // Get list name from DOM
        const listItem = document.querySelector(`a[href*="/todos/list/${listId}"]`);
        const editListName = document.getElementById('editListName');
        const editListDesc = document.getElementById('editListDesc');

        if (listItem && editListName) {
          editListName.value = listItem.textContent.trim() || '';
        }
        if (editListDesc) {
          editListDesc.value = '';
        }

        // Close menu
        const menuEl = document.getElementById(`listmenu-${listId}`);
        if (menuEl) menuEl.classList.add('hidden');

        // Open modal
        if (editListModal) {
          editListModal.classList.remove('hidden');
          if (editListName) editListName.focus();
        } else {
          alert('Error: Modal de edición no disponible');
        }
      });
    });

    // ===== DELETE BUTTONS =====
    document.querySelectorAll('.delete-list-btn').forEach(btn => {
      const newBtn = btn.cloneNode(true);
      btn.parentNode.replaceChild(newBtn, btn);
    });

    document.querySelectorAll('.delete-list-btn').forEach(btn => {
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();

        const listId = this.getAttribute('data-list-id');
        console.log('🗑️ Eliminar lista:', listId);

        currentDeleteListId = listId;

        // Get list name
        const listItem = document.querySelector(`a[href*="/todos/list/${listId}"]`);
        const deleteListName = document.getElementById('deleteListName');

        if (listItem && deleteListName) {
          deleteListName.textContent = listItem.textContent.trim();
        }

        // Close menu
        const menuEl = document.getElementById(`listmenu-${listId}`);
        if (menuEl) menuEl.classList.add('hidden');

        // Open modal
        if (deleteListModal) {
          deleteListModal.classList.remove('hidden');
        } else {
          // Fallback to confirm
          if (confirm(`¿Eliminar la lista "${listItem?.textContent || ''}"?`)) {
            fetch(`/api/lists/id/${listId}`, { method: 'DELETE' })
              .then(res => {
                if (!res.ok) throw new Error('Error');
                alert('✅ Lista eliminada');
                setTimeout(() => location.reload(), 500);
              })
              .catch(() => alert('Error al eliminar'));
          }
        }
      });
    });

    console.log('✅ Eventos de menú de listas agregados');
  }

  // ===== CLOSE MENUS ON OUTSIDE CLICK =====
  document.addEventListener('click', function(e) {
    if (!e.target.classList.contains('list-menu-btn') && 
        !e.target.closest('.list-item-menu')) {
      document.querySelectorAll('.list-item-menu').forEach(m => {
        m.classList.add('hidden');
      });
    }
  });
}