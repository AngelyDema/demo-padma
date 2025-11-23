document.addEventListener('DOMContentLoaded', function() {
  console.log("🟢 DOM cargado - inicializando area.js");

  initCreateArea();
  initAreaMenuSystem(); // Renamed to avoid conflicts with habit.js
});

// ============================
//     CREAR AREA
// ============================
function initCreateArea() {
  const areaNameInput = document.getElementById('areaNameInput');
  const createAreaBtn = document.getElementById('createAreaBtn');
  const createAreaModal = document.getElementById('createAreaModal');
  const modalAreaName = document.getElementById('modalAreaName');
  const modalAreaDesc = document.getElementById('modalAreaDesc');
  const saveAreaBtn = document.getElementById('saveAreaBtn');
  const cancelAreaBtn = document.getElementById('cancelAreaBtn');

  if (!createAreaBtn) {
    console.warn("⚠️ No hay botón de crear área");
    return;
  }

  createAreaBtn.addEventListener('click', function() {
    const name = areaNameInput.value.trim();
    if (!name) {
      alert("Ingresa un nombre para el área");
      areaNameInput.focus();
      return;
    }
    modalAreaName.value = name;
    modalAreaDesc.value = "";
    createAreaModal.classList.remove('hidden');
    modalAreaDesc.focus();
  });

  saveAreaBtn.addEventListener('click', function() {
    const name = modalAreaName.value.trim();
    const desc = modalAreaDesc.value.trim();
    const userId = document.body.getAttribute("data-user-id");

    if (!name) {
      alert("El nombre es requerido");
      return;
    }

    const payload = {
      name,
      description: desc,
      users: { userId: parseInt(userId) }
    };

    fetch("/api/areas/createArea", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    })
    .then(r => r.json())
    .then(data => {
      if (data.error) {
        alert("❌ " + data.error);
        return;
      }
      alert("Área creada exitosamente");
      createAreaModal.classList.add('hidden');
      setTimeout(() => location.reload(), 500);
    });
  });

  cancelAreaBtn.addEventListener('click', () => createAreaModal.classList.add('hidden'));

  createAreaModal.addEventListener('click', function(e) {
    if (e.target === createAreaModal) createAreaModal.classList.add('hidden');
  });
}

// ============================
//     MENÚ ⋮ PARA AREAS
// ============================
function initAreaMenuSystem() {
  const editModal = document.getElementById("editAreaModal");
  const deleteModal = document.getElementById("deleteAreaModal");

  let currentEditId = null;
  let currentDeleteId = null;

  // Get all menu buttons
  const menuButtons = document.querySelectorAll(".area-menu-btn");
  
  if (menuButtons.length === 0) {
    console.warn("⚠️ No area menu buttons found");
    return;
  }

  console.log(`✅ Found ${menuButtons.length} area menu buttons`);

  // ============================
  //     TOGGLE MENU ON CLICK
  // ============================
  menuButtons.forEach(btn => {
    btn.addEventListener("click", function(e) {
      e.preventDefault();
      e.stopPropagation();

      const areaId = this.getAttribute("data-area-id");
      const menu = document.getElementById(`areamenu-${areaId}`);

      if (!menu) {
        console.error(`❌ Menu not found: areamenu-${areaId}`);
        return;
      }

      // Close all other menus first
      document.querySelectorAll(".area-item-menu").forEach(m => {
        if (m !== menu) m.classList.add("hidden");
      });
      
      // Remove active state from all buttons
      document.querySelectorAll(".area-menu-btn").forEach(b => {
        if (b !== this) b.classList.remove("active");
      });

      // Toggle this menu
      const isHidden = menu.classList.toggle("hidden");
      
      // Toggle active state on button
      this.classList.toggle("active", !isHidden);

      console.log(`📋 Menu ${areaId} is now ${isHidden ? 'hidden' : 'visible'}`);
    });
  });

  // ============================
  //     EDIT BUTTON HANDLERS
  // ============================
  document.querySelectorAll(".edit-area-btn").forEach(btn => {
    btn.addEventListener("click", function(e) {
      e.stopPropagation();
      
      const areaId = this.getAttribute("data-area-id");
      currentEditId = areaId;

      // Find the area name from the link
      const wrapper = this.closest("li");
      const nameEl = wrapper?.querySelector("a");
      
      if (!nameEl) {
        console.error("❌ Could not find area name element");
        return;
      }

      const editName = document.getElementById("editAreaName");
      const editDesc = document.getElementById("editAreaDesc");
      
      if (editName) editName.value = nameEl.textContent.trim();
      if (editDesc) editDesc.value = "";

      // Close the dropdown menu
      closeAllAreaMenus();

      // Show edit modal
      if (editModal) editModal.classList.remove("hidden");
    });
  });

  // ============================
  //     DELETE BUTTON HANDLERS
  // ============================
  document.querySelectorAll(".delete-area-btn").forEach(btn => {
    btn.addEventListener("click", function(e) {
      e.stopPropagation();
      
      const areaId = this.getAttribute("data-area-id");
      currentDeleteId = areaId;

      // Close the dropdown menu
      closeAllAreaMenus();

      // Show delete modal
      if (deleteModal) deleteModal.classList.remove("hidden");
    });
  });

  // ============================
  //     SAVE EDIT HANDLER
  // ============================
  const saveEditBtn = document.getElementById("saveEditAreaBtn");
  const cancelEditBtn = document.getElementById("cancelEditAreaBtn");
  const editName = document.getElementById("editAreaName");
  const editDesc = document.getElementById("editAreaDesc");

  if (saveEditBtn) {
    saveEditBtn.addEventListener("click", function() {
      const name = editName?.value.trim();
      const desc = editDesc?.value.trim() || "";

      if (!name) {
        alert("El nombre es requerido");
        return;
      }

      fetch(`/api/areas/id/${currentEditId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, description: desc })
      })
      .then(r => r.json())
      .then(data => {
        if (data.error) {
          alert("❌ " + data.error);
          return;
        }
        alert("Área actualizada");
        editModal.classList.add("hidden");
        setTimeout(() => location.reload(), 500);
      });
    });
  }

  if (cancelEditBtn) {
    cancelEditBtn.addEventListener("click", () => editModal?.classList.add("hidden"));
  }

  if (editModal) {
    editModal.addEventListener("click", e => {
      if (e.target === editModal) editModal.classList.add("hidden");
    });
  }

  // ============================
  //     DELETE CONFIRM HANDLER
  // ============================
  const confirmDeleteBtn = document.getElementById("confirmDeleteAreaBtn");
  const cancelDeleteBtn = document.getElementById("cancelDeleteAreaBtn");

  if (confirmDeleteBtn) {
    confirmDeleteBtn.addEventListener("click", function() {
      fetch(`/api/areas/id/${currentDeleteId}`, { method: "DELETE" })
      .then(r => {
        if (!r.ok) throw new Error();
        alert("Área eliminada");
        deleteModal.classList.add("hidden");
        setTimeout(() => location.reload(), 500);
      })
      .catch(() => alert("Error al eliminar área"));
    });
  }

  if (cancelDeleteBtn) {
    cancelDeleteBtn.addEventListener("click", () => deleteModal?.classList.add("hidden"));
  }

  if (deleteModal) {
    deleteModal.addEventListener("click", e => {
      if (e.target === deleteModal) deleteModal.classList.add("hidden");
    });
  }

  // ============================
  //     CLOSE MENUS ON OUTSIDE CLICK
  // ============================
  document.addEventListener("click", function(e) {
    if (!e.target.closest(".area-menu-btn") && !e.target.closest(".area-item-menu")) {
      closeAllAreaMenus();
    }
  });

  // Close on Escape key
  document.addEventListener("keydown", function(e) {
    if (e.key === "Escape") {
      closeAllAreaMenus();
    }
  });
}

// Helper function to close all menus
function closeAllAreaMenus() {
  document.querySelectorAll(".area-item-menu").forEach(m => m.classList.add("hidden"));
  document.querySelectorAll(".area-menu-btn").forEach(b => b.classList.remove("active"));
}