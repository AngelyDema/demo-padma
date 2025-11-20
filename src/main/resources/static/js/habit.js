document.addEventListener('DOMContentLoaded', function() {
  console.log('🟢 Habits.js cargado');
  
  const habitTitle = document.getElementById('habitTitle');
  const habitForm = document.getElementById('habitForm');
  const toggleHabitMoreBtn = document.getElementById('toggleHabitMoreBtn');
  const cancelHabitBtn = document.getElementById('cancelHabitBtn');
  
  const habitDesc = document.getElementById('habitDesc');
  const habitGoal = document.getElementById('habitGoal');
  const habitArea = document.getElementById('habitArea');
  const habitCompleted = document.getElementById('habitCompleted');

  if (!habitTitle || !habitForm) {
    console.error('❌ Elementos del formulario no encontrados');
    return;
  }

  console.log('✅ Formulario encontrado');

  // ===== EXPANDIR/CONTRAER FORMULARIO =====
  toggleHabitMoreBtn.addEventListener('click', function() {
    console.log('📍 Toggle formulario');
    habitForm.classList.toggle('hidden');
    
    // Auto-focus en la descripción cuando se expande
    if (!habitForm.classList.contains('hidden')) {
      habitDesc.focus();
    }
  });

  // ===== CANCELAR =====
  cancelHabitBtn.addEventListener('click', function() {
    console.log('❌ Cancelar formulario');
    habitForm.classList.add('hidden');
    habitForm.reset();
    habitTitle.value = '';
    habitTitle.focus();
  });

  // ===== CREAR HÁBITO =====
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

    // Obtener userId
    const userId = document.querySelector('body').getAttribute('data-user-id') || 
                   document.querySelector('[name="userId"]')?.value;

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
      
      // Limpiar y contraer
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

  // Click en el botón "+" del sidebar
  createAreaBtn.addEventListener('click', function() {
    const areaName = areaNameInput.value.trim();
    
    console.log('📍 Click en crear área');
    console.log('📋 Nombre ingresado:', areaName);

    if (!areaName) {
      alert('Por favor ingresa un nombre para el área');
      areaNameInput.focus();
      return;
    }

    // Prellenar el nombre en el modal
    modalAreaName.value = areaName;
    modalAreaDesc.value = '';
    
    // Abrir modal
    createAreaModal.classList.remove('hidden');
    modalAreaDesc.focus();
  });

  // Guardar área desde el modal
  saveAreaBtn.addEventListener('click', function() {
    const name = modalAreaName.value.trim();
    const description = modalAreaDesc.value.trim();

    if (!name) {
      alert('El nombre del área es requerido');
      modalAreaName.focus();
      return;
    }

    // Obtener userId
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
      
      // Cerrar modal y limpiar
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

  // Cancelar
  cancelAreaBtn.addEventListener('click', function() {
    createAreaModal.classList.add('hidden');
    modalAreaName.value = '';
    modalAreaDesc.value = '';
  });

  // Cerrar modal al hacer click fuera
  createAreaModal.addEventListener('click', function(e) {
    if (e.target === createAreaModal) {
      createAreaModal.classList.add('hidden');
    }
  });
}

// ===== COMPLETAR HÁBITO =====
document.addEventListener('DOMContentLoaded', function() {
  const habitCheckboxes = document.querySelectorAll('.habit-checkbox');
  const completeHabitModal = document.getElementById('completeHabitModal');
  const modalHabitTitle = document.getElementById('modalHabitTitle');
  const habitNote = document.getElementById('habitNote');
  const saveCompleteBtn = document.getElementById('saveCompleteBtn');
  const cancelCompleteBtn = document.getElementById('cancelCompleteBtn');

  let currentHabitId = null;
  let currentHabitTitle = null;

  // ✅ Escuchar cambios en checkboxes
  habitCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function() {
      currentHabitId = this.getAttribute('data-habit-id');
      currentHabitTitle = this.closest('.habit-item')
                            .querySelector('.habit-title').textContent;

      console.log('✅ Checkbox changed - HabitId:', currentHabitId, 'Checked:', this.checked);

      if (this.checked) {
        // ✅ Si se MARCA → Abrir modal para agregar nota
        modalHabitTitle.textContent = currentHabitTitle;
        habitNote.value = '';
        completeHabitModal.classList.remove('hidden');
        habitNote.focus();
      } else {
        // ✅ Si se DESMARCA → Llamar endpoint uncomplete
        fetch(`/api/habits/${currentHabitId}/uncomplete`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' }
        })
        .then(res => res.json())
        .then(data => {
          console.log('✅ Hábito desmarcado:', data);
          console.log('📊 Nueva racha:', data.streak);
          setTimeout(() => location.reload(), 500);
        })
        .catch(err => {
          console.error('❌ Error:', err);
          this.checked = true; // Revertir si hay error
          alert('Error al desmarcar el hábito');
        });
      }
    });
  });

  // ✅ GUARDAR COMPLETACIÓN con nota
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
        alert('❌ Error: ' + data.error);
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
    });
  });

  // ✅ CANCELAR
  cancelCompleteBtn.addEventListener('click', function() {
    completeHabitModal.classList.add('hidden');
    currentHabitId = null;
    habitNote.value = '';
    
    // ✅ Desmarcar el checkbox también
    const checkbox = document.querySelector(`[data-habit-id="${currentHabitId}"]`);
    if (checkbox) checkbox.checked = false;
  });

  // ✅ Cerrar modal al click fuera
  completeHabitModal.addEventListener('click', function(e) {
    if (e.target === completeHabitModal) {
      this.classList.add('hidden');
    }
  });
});



