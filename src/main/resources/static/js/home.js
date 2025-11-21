document.addEventListener("DOMContentLoaded", () => {
  const menuLinks = document.querySelectorAll(".menu a");
  const sections = document.querySelectorAll(".section");

  for (const link of menuLinks) {
    link.addEventListener("click", e => {
      const target = link.getAttribute("data-section");

      // ⚙️ Permitir navegación real para las vistas externas
      if (target === "habits" || target === "todos" || target === "profile") {
        return; // No hacemos preventDefault → el navegador sigue el enlace normal
      }

      // 🔒 Bloquear solo los enlaces internos (como Home dinámico)
      e.preventDefault();

      // Cambiar estado activo en menú
      for (const l of menuLinks) l.classList.remove("active");
      link.classList.add("active");

      // Mostrar sección correspondiente
      for (const sec of sections) sec.classList.remove("active");
      document.getElementById(`${target}-section`).classList.add("active");
    });
  }
});

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

// ===== VALIDATE HABIT COMPLETION STATUS ON PAGE LOAD =====
  function validateHabitCompletionStatus() {
    if (!userId) return;

    fetch(`/api/habits/validate-completion-status/${userId}`)
      .then(res => res.json())
      .then(data => {
        if (data.completionStatus) {
          console.log('✅ Estado de hábitos validado:', data.completionStatus);

          // Update checkboxes based on backend state
          Object.entries(data.completionStatus).forEach(([habitId, completed]) => {
            const checkbox = document.querySelector(`.habit-checkbox[data-habit-id="${habitId}"]`);
            if (checkbox) {
              checkbox.checked = completed;
              
              // Update visual state
              const wrapper = checkbox.closest('.habit-item-wrapper') || checkbox.closest('li');
              if (wrapper) {
                if (completed) {
                  wrapper.classList.add('completed');
                } else {
                  wrapper.classList.remove('completed');
                }
              }
            }
          });
        }
      })
      .catch(err => {
        console.error('❌ Error validando estado de hábitos:', err);
      });
  }

  // ===== HABIT COMPLETION LOGIC =====
  const habitCheckboxes = document.querySelectorAll('.habit-checkbox');
  const completeHabitModal = document.getElementById('completeHabitModal');
  const modalHabitTitle = document.getElementById('modalHabitTitle');
  const habitNote = document.getElementById('habitNote');
  const saveCompleteBtn = document.getElementById('saveCompleteBtn');
  const cancelCompleteBtn = document.getElementById('cancelCompleteBtn');

  let currentHabitId = null;
  let currentHabitTitle = null;
  let currentCheckbox = null;

  // ===== CHECKBOX CHANGE HANDLER =====
  habitCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function() {
      currentHabitId = this.getAttribute('data-habit-id');
      currentCheckbox = this;

      // Find habit title
      const wrapper = this.closest('.habit-item-wrapper') || this.closest('li');
      const titleEl = wrapper ? wrapper.querySelector('.habit-title') : null;
      currentHabitTitle = titleEl ? titleEl.textContent : 'Hábito';

      console.log('✅ Habit checkbox changed - HabitId:', currentHabitId, 'Checked:', this.checked);

      if (this.checked) {
        // ✅ CHECKING - Open modal for note
        if (!completeHabitModal || !modalHabitTitle || !habitNote) {
          console.error('❌ Modal elements no encontrados');
          alert('Error: Modal no cargó correctamente');
          this.checked = false;
          return;
        }

        modalHabitTitle.textContent = currentHabitTitle;
        habitNote.value = '';
        completeHabitModal.classList.remove('hidden');
        habitNote.focus();
      } else {
        // ✅ UNCHECKING - Show confirmation
        if (confirm('¿Estás seguro? Esto eliminará tu nota y decrementará la racha.')) {
          fetch(`/api/habits/${currentHabitId}/uncomplete`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' }
          })
          .then(res => res.json())
          .then(data => {
            if (data.error) {
              console.error('❌ Error:', data.error);
              alert('❌ ' + data.error);
              this.checked = true;
              return;
            }
            console.log('✅ Hábito desmarcado:', data);
            alert(`✅ Hábito desmarcado. Racha actual: ${data.streak}`);
            setTimeout(() => location.reload(), 500);
          })
          .catch(err => {
            console.error('❌ Error:', err);
            this.checked = true;
            alert('Error al desmarcar el hábito');
          });
        } else {
          // User cancelled - revert checkbox
          this.checked = true;
        }
      }
    });
  });

  // ===== SAVE COMPLETE BUTTON =====
  if (saveCompleteBtn) {
    saveCompleteBtn.addEventListener('click', function() {
      const note = habitNote.value.trim();

      if (!note) {
        alert('Por favor añade una nota');
        habitNote.focus();
        return;
      }

      const payload = { note: note };

      fetch(`/api/habits/${currentHabitId}/complete`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      .then(res => res.json())
      .then(data => {
        if (data.error) {
          console.error('❌ Error:', data.error);

          if (data.alreadyCompleted) {
            alert('ℹ️ Este hábito ya fue completado hoy');
            if (currentCheckbox) currentCheckbox.checked = true;
          } else {
            alert('❌ Error: ' + data.error);
            if (currentCheckbox) currentCheckbox.checked = false;
          }

          completeHabitModal.classList.add('hidden');
          return;
        }

        console.log('✅ Hábito completado:', data);
        alert('✨ ¡Hábito completado! Racha: ' + data.streak);

        completeHabitModal.classList.add('hidden');
        setTimeout(() => location.reload(), 800);
      })
      .catch(err => {
        console.error('❌ Error de red:', err);
        alert('Error al completar el hábito');
        if (currentCheckbox) currentCheckbox.checked = false;
      });
    });
  }

  // ===== CANCEL COMPLETE BUTTON =====
  if (cancelCompleteBtn) {
    cancelCompleteBtn.addEventListener('click', function() {
      completeHabitModal.classList.add('hidden');
      habitNote.value = '';

      if (currentCheckbox) {
        currentCheckbox.checked = false;
      }

      currentHabitId = null;
      currentCheckbox = null;
    });
  }

  // ===== CLOSE MODAL ON BACKGROUND CLICK =====
  if (completeHabitModal) {
    completeHabitModal.addEventListener('click', function(e) {
      if (e.target === completeHabitModal) {
        this.classList.add('hidden');

        if (currentCheckbox) {
          currentCheckbox.checked = false;
        }
      }
    });
  }


