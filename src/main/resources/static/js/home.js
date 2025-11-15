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

