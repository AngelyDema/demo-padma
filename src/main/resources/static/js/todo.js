function filterByDate(date) {
  window.location.href = `/todos/date/${date}`;
}

function toggleTodo(id) {
  fetch(`/api/todos/${id}/toggle`, {
    method: "PUT"
  }).then(() => window.location.reload());
}
