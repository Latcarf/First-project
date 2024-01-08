document.addEventListener('DOMContentLoaded', function () {
    const csrfToken = document.getElementById('csrf-token').getAttribute('content');
    const csrfHeader = document.getElementById('csrf-header').getAttribute('content');

    document.querySelectorAll('.like-btn').forEach(btn => {
        btn.addEventListener('click', () => toggleLikeOrDislike(btn.getAttribute('data-post-id'), 'like'));
    });

    document.querySelectorAll('.dislike-btn').forEach(btn => {
        btn.addEventListener('click', () => toggleLikeOrDislike(btn.getAttribute('data-post-id'), 'dislike'));
    });

    function toggleLikeOrDislike(postId, action) {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', `/${action}s/post/${postId}`, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.setRequestHeader(csrfHeader, csrfToken);
        xhr.onload = function () {
            if (xhr.status === 200) {
                updateLikesAndDislikes(postId);
            } else if (xhr.status === 403) {
                window.location.href = '/login';
            }
        };
        xhr.onerror = function () {
            console.error('An error occurred during the AJAX request');
        };
        xhr.send();
    }

    function updateLikesAndDislikes(postId) {
        getLikesCount(postId);
        getDislikesCount(postId);
    }

    function getLikesCount(postId) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', `/likes/count/${postId}`, true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                const newLikesCount = JSON.parse(xhr.responseText);
                document.getElementById(`likes-count-${postId}`).textContent = newLikesCount;
            }
        };
        xhr.send();
    }

    function getDislikesCount(postId) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', `/dislikes/count/${postId}`, true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                const newDislikesCount = JSON.parse(xhr.responseText);
                document.getElementById(`dislikes-count-${postId}`).textContent = newDislikesCount;
            }
        };
        xhr.send();
    }
});
