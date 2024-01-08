document.querySelectorAll('.timeago').forEach(el => {
    el.textContent = timeago.format(el.getAttribute('datetime'));
});