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

document.addEventListener("DOMContentLoaded", async () => {
  const response = await fetch("/api/home/data");
  const data = await response.json();

  if (data.error) {
    console.error(data.error);
    return;
  }

  const today = new Date().toISOString().split("T")[0];

  const todosToday = data.todos.filter(todo => todo.date === today);
  const habitsToday = data.habits.filter(habit => habit.date === today);

  renderTodos(todosToday);
  renderHabits(habitsToday);
});

document.addEventListener("DOMContentLoaded", async () => {
  const homeSection = document.querySelector("#home-section");
  const todosSection = document.querySelector("#todos-section");
  const habitsSection = document.querySelector("#habits-section");

  const today = new Date().toISOString().split("T")[0];

  try {
    const response = await fetch("/api/home/data");
    const data = await response.json();

    if (data.error) {
      homeSection.innerHTML += `<p class="error-msg">${data.error}</p>`;
      return;
    }

    const todosToday = data.todos.filter(todo => todo.date === today);
    const habitsToday = data.habits.filter(habit => habit.date === today);

    renderSummary(todosToday, habitsToday);
    renderTodosAll(data.todos);
    renderHabitsAll(data.habits);

  } catch (error) {
    console.error("Error al cargar datos del Home:", error);
    homeSection.innerHTML += `<p class="error-msg">No se pudieron cargar los datos 🌧️</p>`;
  }

  // 🟢 Render resumen del día en el Home
  function renderSummary(todos, habits) {
    const summaryDiv = document.createElement("div");
    summaryDiv.classList.add("summary-container");

    const todosHTML =
      todos.length > 0
        ? todos
            .map(
              (todo) => `
          <div class="item-card todo-card">
            <div class="item-header">
              <h3>${todo.title}</h3>
              <span class="status ${todo.completed ? "done" : "pending"}">
                ${todo.completed ? "✔" : "⏳"}
              </span>
            </div>
            <p class="item-desc">${todo.description || "Sin descripción"}</p>
          </div>`
            )
            .join("")
        : `<p class="empty-msg">No tienes tareas para hoy 🌿</p>`;

    const habitsHTML =
      habits.length > 0
        ? habits
            .map(
              (habit) => `
          <div class="item-card habit-card">
            <div class="item-header">
              <h3>${habit.name}</h3>
              <span class="streak">🔥 ${habit.streak || 0}</span>
            </div>
            <p class="item-desc">${habit.description || "Sin descripción"}</p>
          </div>`
            )
            .join("")
        : `<p class="empty-msg">No tienes hábitos para hoy 🌸</p>`;

    summaryDiv.innerHTML = `
      <h3>🌼 Tareas de hoy</h3>
      <div class="summary-list">${todosHTML}</div>

      <h3>🌻 Hábitos de hoy</h3>
      <div class="summary-list">${habitsHTML}</div>
    `;

    homeSection.appendChild(summaryDiv);
  }

  // 🟢 Render de todas las ToDo (página completa)
  function renderTodosAll(todos) {
    todosSection.innerHTML = `
      <h2>Todas tus tareas</h2>
      ${
        todos.length > 0
          ? todos
              .map(
                (todo) => `
            <div class="item-card todo-card">
              <div class="item-header">
                <h3>${todo.title}</h3>
                <span class="status ${todo.completed ? "done" : "pending"}">
                  ${todo.completed ? "✔" : "⏳"}
                </span>
              </div>
              <p class="item-desc">${todo.description || "Sin descripción"}</p>
              <p class="item-date">📅 ${todo.date}</p>
            </div>`
              )
              .join("")
          : `<p class="empty-msg">Aún no has creado tareas 🪴</p>`
      }
    `;
  }

  // 🟢 Render de todos los hábitos (página completa)
  function renderHabitsAll(habits) {
    habitsSection.innerHTML = `
      <h2>Tus hábitos</h2>
      ${
        habits.length > 0
          ? habits
              .map(
                (habit) => `
            <div class="item-card habit-card">
              <div class="item-header">
                <h3>${habit.name}</h3>
                <span class="streak">🔥 ${habit.streak || 0}</span>
              </div>
              <p class="item-desc">${habit.description || "Sin descripción"}</p>
            </div>`
              )
              .join("")
          : `<p class="empty-msg">No has agregado hábitos aún 🌷</p>`
      }
    `;
  }
});


