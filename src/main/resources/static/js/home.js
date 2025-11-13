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
