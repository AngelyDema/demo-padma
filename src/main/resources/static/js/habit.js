// ===== ESPERAR A QUE TODO EL DOM CARGUE =====
document.addEventListener('DOMContentLoaded', function() {
  console.log('🟢 Habits.js cargado y DOM listo');
  
  const userId = document.querySelector('body').getAttribute('data-user-id');

  // ✅ VALIDATE COMPLETION STATUS ON PAGE LOAD (Backend validation)
  validateCompletionStatus();

  // ===== CREAR HÁBITO =====
  const habitTitle = document.getElementById('habitTitle');
  const habitForm = document.getElementById('habitForm');
  const toggleHabitMoreBtn = document.getElementById('toggleHabitMoreBtn');
  const cancelHabitBtn = document.getElementById('cancelHabitBtn');
  
  const habitDesc = document.getElementById('habitDesc');
  const habitGoal = document.getElementById('habitGoal');
  const habitArea = document.getElementById('habitArea');

  if (!habitTitle || !habitForm) {
    console.error('❌ Elementos del formulario de hábito no encontrados');
    return;
  }

  console.log('✅ Formulario encontrado');

  // ===== RESETEAR CHECKBOXES A MEDIANOCHE =====
//
  function validateCompletionStatus() {
    if (!userId) {
      console.warn('⚠️ No userId found - skipping validation');
      return;
    }

    console.log('🔄 Validando estado de hábitos con backend...');

    fetch(`/api/habits/validate-completion-status/${userId}`)
      .then(res => res.json())
      .then(data => {
        if (data.completionStatus) {
          console.log('✅ Estado validado:', data.completionStatus);

          // Update checkboxes based on backend state
          Object.entries(data.completionStatus).forEach(([habitId, completed]) => {
            const checkbox = document.querySelector(`.habit-checkbox[data-habit-id="${habitId}"]`);
            if (checkbox) {
              checkbox.checked = completed;

              // Update visual state
              const wrapper = checkbox.closest('.habit-item-wrapper');
              if (wrapper) {
                if (completed) {
                  wrapper.classList.add('completed');
                } else {
                  wrapper.classList.remove('completed');
                }
              }
            }
          });

          console.log('✅ Checkboxes sincronizados con backend');
        }
      })
      .catch(err => {
        console.error('❌ Error validando estado:', err);
      });
  }

  // Toggle formulario
  toggleHabitMoreBtn.addEventListener('click', function() {
    console.log('📍 Toggle formulario');
    habitForm.classList.toggle('hidden');
    if (!habitForm.classList.contains('hidden')) {
      habitDesc.focus();
    }
  });

  // Cancelar
  cancelHabitBtn.addEventListener('click', function() {
    console.log('❌ Cancelar formulario');
    habitForm.classList.add('hidden');
    habitForm.reset();
    habitTitle.value = '';
    habitTitle.focus();
  });

  // Crear hábito
  habitForm.addEventListener('submit', function(e) {
    e.preventDefault();
    console.log('📝 Enviando formulario de hábito');

    const title = habitTitle.value.trim();
    const description = habitDesc.value.trim();
    const goal = parseInt(habitGoal.value);
    const areaId = habitArea.value ? parseInt(habitArea.value) : null;

    if (!title) {
      alert('El título es requerido');
      habitTitle.focus();
      return;
    }

    if (goal < 1) {
      alert('La meta debe ser mayor a 0');
      habitGoal.focus();
      return;
    }

    const userId = document.querySelector('body').getAttribute('data-user-id');

    if (!userId) {
      console.error('❌ userId no encontrado');
      alert('Error: usuario no identificado');
      return;
    }

    const payload = {
      userId: parseInt(userId),
      title: title,
      description: description,
      goal: goal,
      areaId: areaId
    };

    console.log('📤 Payload:', payload);

    fetch('/api/habits/createHabit', {
      method: 'POST',
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
      console.log('✅ Hábito creado:', data);
      alert('✅ ¡Hábito creado exitosamente!');
      
      habitForm.reset();
      habitForm.classList.add('hidden');
      habitTitle.value = '';
      habitTitle.focus();
      
      setTimeout(() => location.reload(), 500);
    })
    .catch(err => {
      console.error('❌ Error de red:', err);
      alert('Error al crear el hábito');
    });
  });

  // ===== COMPLETAR HABITO =====
  const habitCheckboxes = document.querySelectorAll('.habit-checkbox');
  const completeHabitModal = document.getElementById('completeHabitModal');
  const modalHabitTitle = document.getElementById('modalHabitTitle');
  const habitNote = document.getElementById('habitNote');
  const saveCompleteBtn = document.getElementById('saveCompleteBtn');
  const cancelCompleteBtn = document.getElementById('cancelCompleteBtn');

  let currentHabitId = null;
  let currentCheckbox = null;

  habitCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function() {
      currentHabitId = this.getAttribute('data-habit-id');
      currentCheckbox = this;
      const wrapper = this.closest('.habit-item-wrapper');
      const titleEl = wrapper.querySelector('.habit-title');

      if (this.checked) {
        if (!completeHabitModal) {
          alert('Modal no encontrado');
          this.checked = false;
          return;
        }
        modalHabitTitle.textContent = titleEl.textContent;
        habitNote.value = '';
        completeHabitModal.classList.remove('hidden');
        habitNote.focus();
      } else {
        if (confirm('Desmarcar habito?')) {
          fetch(`/api/habits/${currentHabitId}/uncomplete`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' }
          })
          .then(res => res.json())
          .then(data => {
            if (data.error) {
              alert('Error: ' + data.error);
              this.checked = true;
              return;
            }
            alert('Habito desmarcado. Racha: ' + data.streak);
            setTimeout(() => location.reload(), 500);
          })
          .catch(err => {
            this.checked = true;
            alert('Error al desmarcar habito');
          });
        } else {
          this.checked = true;
        }
      }
    });
  });

  if (saveCompleteBtn) {
    saveCompleteBtn.addEventListener('click', function() {
      const note = habitNote.value.trim();
      if (!note) {
        alert('Aniade una nota');
        return;
      }

      fetch(`/api/habits/${currentHabitId}/complete`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ note: note })
      })
      .then(res => res.json())
      .then(data => {
        if (data.error) {
          if (data.alreadyCompleted) {
            alert('Ya fue completado hoy');
            if (currentCheckbox) currentCheckbox.checked = true;
          } else {
            alert('Error: ' + data.error);
            if (currentCheckbox) currentCheckbox.checked = false;
          }
          completeHabitModal.classList.add('hidden');
          return;
        }
        alert('Habito completado! Racha: ' + data.streak);
        completeHabitModal.classList.add('hidden');
        setTimeout(() => location.reload(), 800);
      })
      .catch(err => {
        alert('Error al completar habito');
        if (currentCheckbox) currentCheckbox.checked = false;
      });
    });
  }

  if (cancelCompleteBtn) {
    cancelCompleteBtn.addEventListener('click', function() {
      completeHabitModal.classList.add('hidden');
      habitNote.value = '';
      if (currentCheckbox) currentCheckbox.checked = false;
      currentHabitId = null;
      currentCheckbox = null;
    });
  }

  if (completeHabitModal) {
    completeHabitModal.addEventListener('click', function(e) {
      if (e.target === completeHabitModal) {
        this.classList.add('hidden');
        if (currentCheckbox) currentCheckbox.checked = false;
      }
    });
  }

 // initAreaMenu();
  initHabitMenu();

});

// ============================
// MENU PARA AREAS
// ============================
function initAreaMenu() {
  const editModal = document.getElementById("editAreaModal");
  const deleteModal = document.getElementById("deleteAreaModal");

  if (!editModal || !deleteModal) return;

  let currentEditId = null;
  let currentDeleteId = null;

  const editName = document.getElementById("editAreaName");
  const editDesc = document.getElementById("editAreaDesc");
  const saveEditBtn = document.getElementById("saveEditAreaBtn");
  const cancelEditBtn = document.getElementById("cancelEditAreaBtn");

  if (saveEditBtn) {
    saveEditBtn.addEventListener("click", function() {
      const name = editName.value.trim();
      if (!name) {
        alert("Nombre requerido");
        return;
      }

      fetch(`/api/areas/id/${currentEditId}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({ name, description: editDesc.value.trim() })
      })
      .then(r => r.json())
      .then(data => {
        if (data.error) { alert("Error: " + data.error); return; }
        alert("Area actualizada");
        editModal.classList.add("hidden");
        setTimeout(() => location.reload(), 500);
      });
    });

    cancelEditBtn.addEventListener("click", () => editModal.classList.add("hidden"));
    editModal.addEventListener("click", e => {
      if (e.target === editModal) editModal.classList.add("hidden");
    });
  }

  const confirmDeleteBtn = document.getElementById("confirmDeleteAreaBtn");
  const cancelDeleteBtn = document.getElementById("cancelDeleteAreaBtn");

  if (confirmDeleteBtn) {
    confirmDeleteBtn.addEventListener("click", function() {
      fetch(`/api/areas/id/${currentDeleteId}`, { method: "DELETE" })
      .then(r => {
        if (!r.ok) throw new Error();
        alert("Area eliminada");
        deleteModal.classList.add("hidden");
        setTimeout(() => location.reload(), 500);
      })
      .catch(() => alert("Error al eliminar area"));
    });

    cancelDeleteBtn.addEventListener("click", () => deleteModal.classList.add("hidden"));
    deleteModal.addEventListener("click", e => {
      if (e.target === deleteModal) deleteModal.classList.add("hidden");
    });
  }

  document.querySelectorAll(".area-menu-btn").forEach(btn => {
    btn.addEventListener("click", function(e) {
      e.stopPropagation();
      const areaId = this.getAttribute("data-area-id");
      const menu = document.getElementById(`areamenu-${areaId}`);
      document.querySelectorAll(".area-item-menu").forEach(m => {
        if (m !== menu) m.classList.add("hidden");
      });
      menu.classList.toggle("hidden");
    });
  });

  document.querySelectorAll(".edit-area-btn").forEach(btn => {
    btn.addEventListener("click", function() {
      const areaId = this.getAttribute("data-area-id");
      currentEditId = areaId;
      const nameEl = document.querySelector(`a[data-area-link="${areaId}"]`);
      editName.value = nameEl.textContent.trim();
      editDesc.value = "";
      const menu = document.getElementById(`areamenu-${areaId}`);
      if (menu) menu.classList.add("hidden");
      document.getElementById("editAreaModal").classList.remove("hidden");
    });
  });

  document.querySelectorAll(".delete-area-btn").forEach(btn => {
    btn.addEventListener("click", function() {
      const areaId = this.getAttribute("data-area-id");
      currentDeleteId = areaId;
      const menu = document.getElementById(`areamenu-${areaId}`);
      if (menu) menu.classList.add("hidden");
      document.getElementById("deleteAreaModal").classList.remove("hidden");
    });
  });

  document.addEventListener("click", function(e) {
    if (!e.target.closest(".area-menu-btn") && !e.target.closest(".area-item-menu")) {
      document.querySelectorAll(".area-item-menu").forEach(m => m.classList.add("hidden"));
    }
  });
}

// ============================
// MENU PARA HABITOS
// ============================
function initHabitMenu() {
  const editModal = document.getElementById("editHabitModal");
  const deleteModal = document.getElementById("deleteHabitModal");

  if (!editModal || !deleteModal) {
    console.warn("Habit modals no encontrados");
    return;
  }

  let currentEditId = null;
  let currentDeleteId = null;

  const editName = document.getElementById("editHabitName");
  const editDesc = document.getElementById("editHabitDesc");
  const editGoal = document.getElementById("editHabitGoal");
  const editArea = document.getElementById("editHabitArea");
  const saveEditBtn = document.getElementById("saveEditHabitBtn");
  const cancelEditBtn = document.getElementById("cancelEditHabitBtn");

  if (saveEditBtn) {
    saveEditBtn.addEventListener("click", function() {
      const title = editName.value.trim();
      const description = editDesc.value.trim();
      const goal = parseInt(editGoal.value);
      const areaId = editArea.value ? parseInt(editArea.value) : null;

      if (!title) {
        alert("Titulo requerido");
        return;
      }
      if (goal < 1) {
        alert("Meta debe ser mayor a 0");
        return;
      }

      const payload = { title, description, goal, areaId };

      fetch(`/api/habits/${currentEditId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      })
      .then(r => r.json())
      .then(data => {
        if (data.error) {
          alert("Error: " + data.error);
          return;
        }
        alert("Habito actualizado");
        editModal.classList.add("hidden");
        setTimeout(() => location.reload(), 500);
      })
      .catch(err => alert("Error al actualizar habito"));
    });

    cancelEditBtn.addEventListener("click", () => editModal.classList.add("hidden"));
    editModal.addEventListener("click", e => {
      if (e.target === editModal) editModal.classList.add("hidden");
    });
  }

  const deleteHabitName = document.getElementById("deleteHabitName");
  const confirmDeleteBtn = document.getElementById("confirmDeleteHabitBtn");
  const cancelDeleteBtn = document.getElementById("cancelDeleteHabitBtn");

  if (confirmDeleteBtn) {
    confirmDeleteBtn.addEventListener("click", function() {
      fetch(`/api/habits/${currentDeleteId}`, { method: "DELETE" })
      .then(r => {
        if (!r.ok) throw new Error();
        alert("Habito eliminado");
        deleteModal.classList.add("hidden");
        setTimeout(() => location.reload(), 500);
      })
      .catch(err => alert("Error al eliminar habito"));
    });

    cancelDeleteBtn.addEventListener("click", () => deleteModal.classList.add("hidden"));
    deleteModal.addEventListener("click", e => {
      if (e.target === deleteModal) deleteModal.classList.add("hidden");
    });
  }

  document.querySelectorAll(".habit-menu-btn").forEach(btn => {
    btn.addEventListener("click", function(e) {
      e.stopPropagation();
      const habitId = this.getAttribute("data-habit-id");
      const menu = document.getElementById(`habit-menu-${habitId}`);
      document.querySelectorAll(".habit-menu").forEach(m => {
        if (m !== menu) m.classList.add("hidden");
      });
      menu.classList.toggle("hidden");
    });
  });

  document.querySelectorAll(".edit-habit-btn").forEach(btn => {
    btn.addEventListener("click", function() {
      const habitId = this.getAttribute("data-habit-id");
      currentEditId = habitId;
      const wrapper = this.closest(".habit-item-wrapper");
      const titleEl = wrapper.querySelector(".habit-title");
      editName.value = titleEl.textContent;
      editDesc.value = "";
      editGoal.value = "30";
      editArea.value = "";
      const menu = document.getElementById(`habit-menu-${habitId}`);
      if (menu) menu.classList.add("hidden");
      editModal.classList.remove("hidden");
      editName.focus();
    });
  });

  document.querySelectorAll(".delete-habit-btn").forEach(btn => {
    btn.addEventListener("click", function() {
      const habitId = this.getAttribute("data-habit-id");
      currentDeleteId = habitId;
      const wrapper = this.closest(".habit-item-wrapper");
      const titleEl = wrapper.querySelector(".habit-title");
      deleteHabitName.textContent = titleEl.textContent;
      const menu = document.getElementById(`habit-menu-${habitId}`);
      if (menu) menu.classList.add("hidden");
      deleteModal.classList.remove("hidden");
    });
  });

  document.addEventListener("click", function(e) {
    if (!e.target.closest(".habit-menu-btn") && !e.target.closest(".habit-menu")) {
      document.querySelectorAll(".habit-menu").forEach(m => m.classList.add("hidden"));
    }
  });
}