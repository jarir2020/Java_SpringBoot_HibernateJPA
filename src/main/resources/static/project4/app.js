const state = { page: 0, size: 6, totalPages: 0, search: '', category: '', products: [], categories: [], cart: null, orders: [] };
const $ = (selector) => document.querySelector(selector);
const AUTH = `Basic ${btoa('shopper:shopper-password')}`;

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, (character) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[character]));
}

async function api(path, options = {}) {
    const response = await fetch(`/api/project4${path}`, { ...options, headers: { 'Content-Type': 'application/json', Authorization: AUTH, ...(options.headers || {}) } });
    const data = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(data.message || `Request failed (${response.status})`);
    return data;
}

function showMessage(message, error = false) {
    const flash = $('#flash'); flash.textContent = message; flash.classList.toggle('error', error);
    if (message) window.setTimeout(() => { flash.textContent = ''; }, 4500);
}

async function loadCatalog() {
    try {
        const query = new URLSearchParams({ page: state.page, size: state.size });
        if (state.search) query.set('search', state.search);
        if (state.category) query.set('category', state.category);
        const [catalog, cart, orders] = await Promise.all([api(`/products?${query}`), api('/cart'), api('/orders')]);
        state.products = catalog.items; state.totalPages = catalog.totalPages; state.cart = cart; state.orders = orders;
        renderProducts(catalog); renderCart(); renderOrders();
    } catch (error) { showMessage(error.message, true); }
}

async function loadCategories() {
    try {
        state.categories = await api('/categories');
        $('#category').innerHTML = '<option value="">All categories</option>' + state.categories.map((category) => `<option value="${category.name}">${escapeHtml(category.name)}</option>`).join('');
    } catch (error) { showMessage(error.message, true); }
}

function renderProducts(catalog) {
    $('#product-grid').innerHTML = state.products.length ? state.products.map((product) => `
        <article class="product-card">
            <div class="product-image">${escapeHtml(product.name.split(' ').map((word) => word[0]).slice(0, 2).join(''))}</div>
            <div class="product-content"><div class="product-category">${escapeHtml(product.category)}</div><h3>${escapeHtml(product.name)}</h3><p>${escapeHtml(product.description)}</p>
                <div class="product-bottom"><div><div class="price">$${Number(product.price).toFixed(2)}</div><div class="stock">${product.inventoryQuantity} in stock</div></div><button class="button add" data-add="${product.id}" ${product.inventoryQuantity < 1 ? 'disabled' : ''}>Add +</button></div>
            </div>
        </article>`).join('') : '<div class="cart-empty">No products match this search.</div>';
    $('#page-label').textContent = `Page ${catalog.page + 1} of ${Math.max(catalog.totalPages, 1)}`;
    $('#previous-page').disabled = catalog.page === 0;
    $('#next-page').disabled = catalog.page + 1 >= catalog.totalPages;
}

function renderCart() {
    const items = state.cart?.items || [];
    $('#cart-count').textContent = items.reduce((sum, item) => sum + item.quantity, 0);
    $('#cart-heading-count').textContent = `(${items.length})`;
    $('#cart-total').textContent = `$${Number(state.cart?.total || 0).toFixed(2)}`;
    $('#cart-items').innerHTML = items.length ? items.map((item) => `<div class="cart-item"><div><strong>${escapeHtml(item.name)}</strong><br><small>${item.quantity} × $${Number(item.unitPrice).toFixed(2)}</small></div><button class="remove" data-remove="${item.productId}">Remove</button></div>`).join('') : '<div class="cart-empty">Your cart is waiting for a useful object.</div>';
}

function renderOrders() {
    $('#orders').innerHTML = state.orders.length ? state.orders.map((order) => `<div class="order"><strong>Order #${order.id}</strong><span class="order-meta"><span>${order.items.length} item${order.items.length === 1 ? '' : 's'}</span><span>${escapeHtml(order.status)}</span><b>$${Number(order.total).toFixed(2)}</b></span></div>`).join('') : '<div class="order-empty">Complete a checkout to see the order entity and payment record here.</div>';
}

$('#product-grid').addEventListener('click', async (event) => {
    const button = event.target.closest('[data-add]'); if (!button) return;
    try { state.cart = await api('/cart/items', { method: 'POST', body: JSON.stringify({ productId: Number(button.dataset.add), quantity: 1 }) }); renderCart(); showMessage('Added to cart.'); }
    catch (error) { showMessage(error.message, true); }
});

$('#cart-items').addEventListener('click', async (event) => {
    const button = event.target.closest('[data-remove]'); if (!button) return;
    try { state.cart = await api(`/cart/items/${button.dataset.remove}`, { method: 'DELETE' }); renderCart(); showMessage('Removed from cart.'); }
    catch (error) { showMessage(error.message, true); }
});

$('#checkout-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    try { await api('/checkout', { method: 'POST', body: JSON.stringify({ shippingName: $('#shipping-name').value, shippingEmail: $('#shipping-email').value }) }); await loadCatalog(); showMessage('Order placed. Inventory and payment were updated in one transaction.'); }
    catch (error) { showMessage(error.message, true); }
});

$('#search').addEventListener('input', (event) => { state.search = event.target.value; state.page = 0; loadCatalog(); });
$('#category').addEventListener('change', (event) => { state.category = event.target.value; state.page = 0; loadCatalog(); });
$('#previous-page').addEventListener('click', () => { if (state.page > 0) { state.page--; loadCatalog(); } });
$('#next-page').addEventListener('click', () => { if (state.page + 1 < state.totalPages) { state.page++; loadCatalog(); } });
$('#cart-jump').addEventListener('click', () => $('#cart-panel').scrollIntoView({ behavior: 'smooth' }));

loadCategories();
loadCatalog();
