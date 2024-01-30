document.getElementById('comment-form').addEventListener('submit', function(e) {
    e.preventDefault();

    var postId = this.action.split('/').pop();
    var commentText = document.getElementById('comment-text').value;

    fetch('/create-comment/' + postId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.getElementById('csrf-token').content
        },
        body: JSON.stringify({ text: commentText })
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            addCommentToPage(data);
            document.getElementById('comment-text').value = '';
        })
        .catch(error => console.error('Error:', error));
});

const currentUserName = document.getElementById('current-user-email').textContent;
function addCommentToPage(comment) {
    var commentsList = document.querySelector('.comments-list');
    var newComment = document.createElement('li');
    newComment.innerHTML = `
        <h5>
            <p>
                <a href="${comment.userDTO.email === currentUserName ? '/account' : '/user/' + comment.userDTO.id}">
                    ${comment.userDTO.name}
                </a>
            </p>
            <span class="timeago" datetime="${comment.createdCommentDateTime}"></span>
        </h5>
        <h3><p>${comment.text}</p></h3>
    `;
    commentsList.appendChild(newComment);
    timeago().render(newComment.querySelectorAll('.timeago'));

}


