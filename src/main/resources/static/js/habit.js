// ===== ESPERAR A QUE TODO EL DOM CARGUE =====
document.addEventListener('DOMContentLoaded', function() {
  console.log('🟢 Habits.js cargado y DOM listo');
  
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
function resetCheckboxesAtMidnight() {
  const lastResetDate = localStorage.getItem('lastResetDate');
  const today = new Date().toISOString().split('T')[0]; // Formato: YYYY-MM-DD

  if (lastResetDate !== today) {
    console.log('🔄 Reseteando hábitos - nuevo día detectado');
    
    // Desmarcar todos los checkboxes
    document.querySelectorAll('.habit-checkbox').forEach(checkbox => {
      checkbox.checked = false;
    });
    
    // Guardar que ya reseteamos hoy
    localStorage.setItem('lastResetDate', today);
    location.reload(); // Opcional: recargar página
  }
}

// Llamar al cargar la página
resetCheckboxesAtMidnight();

// También verificar cada hora por si acaso
setInterval(resetCheckboxesAtMidnight, 3600000); // Cada hora


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

  // ===== CREAR ÁREA =====
  const areaNameInput = document.getElementById('areaNameInput');
  const createAreaBtn = document.getElementById('createAreaBtn');
  const createAreaModal = document.getElementById('createAreaModal');
  const modalAreaName = document.getElementById('modalAreaName');
  const modalAreaDesc = document.getElementById('modalAreaDesc');
  const saveAreaBtn = document.getElementById('saveAreaBtn');
  const cancelAreaBtn = document.getElementById('cancelAreaBtn');

  if (createAreaBtn && createAreaModal) {
    console.log('✅ Elementos de área encontrados');

    createAreaBtn.addEventListener('click', function() {
      const areaName = areaNameInput.value.trim();
      
      console.log('📍 Click en crear área');
      console.log('📋 Nombre ingresado:', areaName);

      if (!areaName) {
        alert('Por favor ingresa un nombre para el área');
        areaNameInput.focus();
        return;
      }

      modalAreaName.value = areaName;
      modalAreaDesc.value = '';
      
      createAreaModal.classList.remove('hidden');
      modalAreaDesc.focus();
    });

    saveAreaBtn.addEventListener('click', function() {
      const name = modalAreaName.value.trim();
      const description = modalAreaDesc.value.trim();

      if (!name) {
        alert('El nombre del área es requerido');
        modalAreaName.focus();
        return;
      }

      const userId = document.querySelector('body').getAttribute('data-user-id');

      if (!userId) {
        console.error('❌ userId no encontrado');
        alert('Error: usuario no identificado');
        return;
      }

      const payload = {
        name: name,
        description: description,
        users: {
          userId: parseInt(userId)
        }
      };

      console.log('📤 Enviando área:', payload);

      fetch('/api/areas/createArea', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      .then(res => res.json())
      .then(data => {
        if (data.error || typeof data === 'string') {
          console.error('❌ Error:', data);
          alert('❌ Error: ' + (data.error || data));
          return;
        }
        console.log('✅ Área creada:', data);
        alert('✅ ¡Área creada exitosamente!');
        
        createAreaModal.classList.add('hidden');
        areaNameInput.value = '';
        modalAreaName.value = '';
        modalAreaDesc.value = '';
        
        setTimeout(() => location.reload(), 500);
      })
      .catch(err => {
        console.error('❌ Error de red:', err);
        alert('Error al crear el área');
      });
    });

    cancelAreaBtn.addEventListener('click', function() {
      createAreaModal.classList.add('hidden');
      modalAreaName.value = '';
      modalAreaDesc.value = '';
    });

    createAreaModal.addEventListener('click', function(e) {
      if (e.target === createAreaModal) {
        createAreaModal.classList.add('hidden');
      }
    });
  }

  // ===== COMPLETAR HÁBITO =====
  // ===== COMPLETAR HÁBITO =====
  const habitCheckboxes = document.querySelectorAll('.habit-checkbox');
  const completeHabitModal = document.getElementById('completeHabitModal');
  const modalHabitTitle = document.getElementById('modalHabitTitle');
  const habitNote = document.getElementById('habitNote');
  const saveCompleteBtn = document.getElementById('saveCompleteBtn');
  const cancelCompleteBtn = document.getElementById('cancelCompleteBtn');

  let currentHabitId = null;
  let currentHabitTitle = null;
  let currentCheckbox = null;

  if (!completeHabitModal) {
    console.warn('⚠️ completeHabitModal no encontrado - esperando que cargue...');
  }

  habitCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function() {
      currentHabitId = this.getAttribute('data-habit-id');
      currentCheckbox = this;
      
      const wrapper = this.closest('.habit-item-wrapper');
      if (!wrapper) {
        console.error('❌ No se encontró .habit-item-wrapper');
        return;
      }
      
      const titleEl = wrapper.querySelector('.habit-title');
      if (!titleEl) {
        console.error('❌ No se encontró .habit-title');
        return;
      }
      
      currentHabitTitle = titleEl.textContent;

      console.log('✅ Checkbox changed - HabitId:', currentHabitId, 'Checked:', this.checked);

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
            console.log('📊 Nueva racha:', data.streak);
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
          
          // ✅ NEW: Handle already completed
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

  if (cancelCompleteBtn) {
    cancelCompleteBtn.addEventListener('click', function() {
      completeHabitModal.classList.add('hidden');
      habitNote.value = '';
      
      // ✅ FIXED: Properly reset checkbox
      if (currentCheckbox) {
        currentCheckbox.checked = false;
      }
      
      currentHabitId = null;
      currentCheckbox = null;
    });
  }

  if (completeHabitModal) {
    completeHabitModal.addEventListener('click', function(e) {
      if (e.target === completeHabitModal) {
        this.classList.add('hidden');
        
        // ✅ FIXED: Reset checkbox when closing modal
        if (currentCheckbox && !currentCheckbox.dataset.wasCompleted) {
          currentCheckbox.checked = false;
        }
      }
    });
  }
});
