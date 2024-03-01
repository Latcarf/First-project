document.addEventListener('DOMContentLoaded', function () {
    const csrfToken = document.getElementById('csrf-token').getAttribute('content');
    const csrfHeader = document.getElementById('csrf-header').getAttribute('content');

    document.querySelectorAll('.like-comment-btn').forEach(btn => {
        btn.addEventListener('click', () => toggleLikeOrDislike(btn.getAttribute('data-comment-id'), 'like'));
    });

    document.querySelectorAll('.dislike-comment-btn').forEach(btn => {
        btn.addEventListener('click', () => toggleLikeOrDislike(btn.getAttribute('data-comment-id'), 'dislike'));
    });

    function toggleLikeOrDislike(commentId, action) {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', `/${action}s/comment/${commentId}`, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.setRequestHeader(csrfHeader, csrfToken);
        xhr.onload = function () {
            if (xhr.status === 200) {
                updateLikesAndDislikes(commentId);
            } else if (xhr.status === 403) {
                window.location.href = '/login';
            }
        };
        xhr.onerror = function () {
            console.error('An error occurred during the AJAX request');
        };
        xhr.send();
    }

    function updateLikesAndDislikes(commentId) {
        getLikesCount(commentId);
        getDislikesCount(commentId);
    }

    function getLikesCount(commentId) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', `/likes/count/comment/${commentId}`, true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                const newLikesCount = JSON.parse(xhr.responseText);
                document.getElementById(`likes-count-comment-${commentId}`).textContent = newLikesCount;
            }
        };
        xhr.send();
    }

    function getDislikesCount(commentId) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', `/dislikes/count/comment/${commentId}`, true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                const newDislikesCount = JSON.parse(xhr.responseText);
                document.getElementById(`dislikes-count-comment-${commentId}`).textContent = newDislikesCount;
            }
        };
        xhr.send();
    }
});
