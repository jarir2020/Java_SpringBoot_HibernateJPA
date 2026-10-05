const state = {
    users: [],
    posts: [],
    selectedPost: null,
    editingId: null
};

const $ = (selector) => document.querySelector(selector);

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, (character) => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;'
    }[character]));
}

async function api(path, options = {}) {
    const response = await fetch(`/api/project3${path}`, {
        ...options,
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
    });
    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
        const error = new Error(data.message || `Request failed (${response.status})`);
        error.fieldErrors = data.fieldErrors || {};
        throw error;
    }
    return data;
}

function showMessage(message, isError = false) {
    const flash = $('#flash');
    flash.textContent = message;
    flash.classList.toggle('error', isError);
    if (message) window.setTimeout(() => { flash.textContent = ''; }, 4500);
}

async function loadWorkspace() {
    try {
        [state.users, state.posts] = await Promise.all([api('/users'), api('/posts')]);
        $('#connection-status').textContent = '● API connected';
        renderUsers();
        renderPosts();
        renderStats();
    } catch (error) {
        $('#connection-status').textContent = '● API unavailable';
        showMessage(error.message, true);
    }
}

function renderStats() {
    $('#post-count').textContent = state.posts.length;
    $('#user-count').textContent = state.users.length;
    $('#published-count').textContent = state.posts.filter((post) => post.published).length;
}

function renderUsers() {
    $('#post-author').innerHTML = state.users.map((user) =>
        `<option value="${user.id}">${escapeHtml(user.displayName)}</option>`).join('');
}

function renderPosts() {
    const list = $('#post-list');
    if (!state.posts.length) {
        list.innerHTML = '<div class="empty-list">No posts match that search.</div>';
        return;
    }
    list.innerHTML = state.posts.map((post) => `
        <article class="post-card">
            <div class="post-meta"><span>${escapeHtml(post.author.displayName)}</span><span class="status ${post.published ? '' : 'draft'}">${post.published ? 'Published' : 'Draft'}</span></div>
            <h3>${escapeHtml(post.title)}</h3>
            <p>${escapeHtml(post.excerpt)}</p>
            <div class="post-actions"><span class="post-meta">${post.commentCount} comment${post.commentCount === 1 ? '' : 's'}</span><button class="open-button" data-open-post="${post.id}">Read entry →</button></div>
        </article>`).join('');
}

async function openPost(id) {
    try {
        state.selectedPost = await api(`/posts/${id}`);
        renderDetail();
    } catch (error) {
        showMessage(error.message, true);
    }
}

function renderDetail() {
    const post = state.selectedPost;
    const panel = $('#detail-panel');
    if (!post) return;
    panel.classList.remove('empty-detail');
    panel.innerHTML = `
        <div class="post-meta"><span>${escapeHtml(post.author.displayName)}</span><span class="status ${post.published ? '' : 'draft'}">${post.published ? 'Published' : 'Draft'}</span></div>
        <h2>${escapeHtml(post.title)}</h2>
        <div class="detail-body">${escapeHtml(post.body)}</div>
        <div class="detail-actions"><button class="button secondary" data-edit-post="${post.id}">Edit</button><button class="button danger" data-delete-post="${post.id}">Delete</button></div>
        <div class="comments-heading"><h3>Conversation</h3><span class="post-meta">${post.comments.length}</span></div>
        <div class="comments">${post.comments.length ? post.comments.map(renderComment).join('') : '<p class="post-meta">Be the first to comment.</p>'}</div>
        <form class="comment-form" id="comment-form">
            <label>Join the conversation<select id="comment-user" required>${state.users.map((user) => `<option value="${user.id}">${escapeHtml(user.displayName)}</option>`).join('')}</select></label>
            <textarea id="comment-body" required minlength="2" maxlength="1000" rows="3" placeholder="Share a useful thought..."></textarea>
            <button class="button primary" type="submit">Add comment</button>
        </form>`;
}

function renderComment(comment) {
    return `<div class="comment"><div class="comment-head"><b>${escapeHtml(comment.author.displayName)}</b><button data-delete-comment="${comment.id}">Remove</button></div><p>${escapeHtml(comment.body)}</p></div>`;
}

function resetComposer() {
    state.editingId = null;
    $('#composer-title').textContent = 'Publish an idea';
    $('#post-submit').textContent = 'Publish post';
    $('#cancel-edit').classList.add('hidden');
    $('#post-form').reset();
    $('#post-published').checked = true;
}

function startEditing(post) {
    state.editingId = post.id;
    $('#composer-title').textContent = 'Edit your idea';
    $('#post-submit').textContent = 'Save changes';
    $('#cancel-edit').classList.remove('hidden');
    $('#post-title').value = post.title;
    $('#post-body').value = post.body;
    $('#post-author').value = post.author.id;
    $('#post-published').checked = post.published;
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function submitUser(event) {
    event.preventDefault();
    const form = event.currentTarget;
    try {
        await api('/users', { method: 'POST', body: JSON.stringify({
            displayName: $('#user-name').value,
            email: $('#user-email').value
        }) });
        form.reset();
        await loadWorkspace();
        showMessage('Author created.');
    } catch (error) { showMessage(error.message, true); }
}

async function submitPost(event) {
    event.preventDefault();
    const payload = {
        title: $('#post-title').value,
        body: $('#post-body').value,
        authorId: Number($('#post-author').value),
        published: $('#post-published').checked
    };
    try {
        const wasEditing = Boolean(state.editingId);
        const path = wasEditing ? `/posts/${state.editingId}` : '/posts';
        const method = wasEditing ? 'PUT' : 'POST';
        const post = await api(path, { method, body: JSON.stringify(payload) });
        resetComposer();
        await loadWorkspace();
        await openPost(post.id);
        showMessage(wasEditing ? 'Post updated.' : 'Post published.');
    } catch (error) { showMessage(error.message, true); }
}

async function submitComment(event) {
    event.preventDefault();
    const postId = state.selectedPost.id;
    try {
        await api(`/posts/${postId}/comments`, { method: 'POST', body: JSON.stringify({
            userId: Number($('#comment-user').value), body: $('#comment-body').value
        }) });
        await loadWorkspace();
        await openPost(postId);
        showMessage('Comment added.');
    } catch (error) { showMessage(error.message, true); }
}

$('#user-form').addEventListener('submit', submitUser);
$('#post-form').addEventListener('submit', submitPost);
$('#cancel-edit').addEventListener('click', resetComposer);
$('#search').addEventListener('input', async (event) => {
    try { state.posts = await api(`/posts?search=${encodeURIComponent(event.target.value)}`); renderPosts(); renderStats(); }
    catch (error) { showMessage(error.message, true); }
});

$('#post-list').addEventListener('click', (event) => {
    const button = event.target.closest('[data-open-post]');
    if (button) openPost(button.dataset.openPost);
});

$('#detail-panel').addEventListener('click', async (event) => {
    const editButton = event.target.closest('[data-edit-post]');
    const deleteButton = event.target.closest('[data-delete-post]');
    const deleteCommentButton = event.target.closest('[data-delete-comment]');
    if (editButton && state.selectedPost) startEditing(state.selectedPost);
    if (deleteButton && state.selectedPost && window.confirm('Delete this post and its comments?')) {
        try { await api(`/posts/${state.selectedPost.id}`, { method: 'DELETE' }); state.selectedPost = null; await loadWorkspace(); $('#detail-panel').className = 'panel detail-panel empty-detail'; $('#detail-panel').innerHTML = '<div class="empty-icon">↗</div><h2>Select a post</h2><p>Open a post to read the full entry, edit it, or join the conversation.</p>'; showMessage('Post deleted.'); }
        catch (error) { showMessage(error.message, true); }
    }
    if (deleteCommentButton) {
        try { await api(`/comments/${deleteCommentButton.dataset.deleteComment}`, { method: 'DELETE' }); await openPost(state.selectedPost.id); showMessage('Comment removed.'); }
        catch (error) { showMessage(error.message, true); }
    }
});

$('#detail-panel').addEventListener('submit', (event) => {
    if (event.target.id === 'comment-form') submitComment(event);
});

loadWorkspace();
