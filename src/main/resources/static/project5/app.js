const state = { company: 'NSTAR', employees: [], leaves: [], dashboard: null };
const $ = (selector) => document.querySelector(selector);
const AUTH = `Basic ${btoa('admin:admin-password')}`;

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, (character) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[character]));
}

function money(value) { return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(value || 0); }

async function api(path, options = {}) {
    const response = await fetch(`/api/project5${path}`, { ...options, headers: { 'Content-Type': 'application/json', Authorization: AUTH, ...(options.headers || {}) } });
    const data = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(data.message || `Request failed (${response.status})`);
    return data;
}

function message(text, error = false) { const flash = $('#flash'); flash.textContent = text; flash.classList.toggle('error', error); if (text) window.setTimeout(() => { flash.textContent = ''; }, 4500); }

async function loadCompanies() {
    const companies = await api('/companies');
    $('#company-select').innerHTML = companies.map((company) => `<option value="${escapeHtml(company.code)}">${escapeHtml(company.name)}</option>`).join('');
    $('#company-select').value = state.company;
}

async function loadData() {
    try {
        const code = encodeURIComponent(state.company);
        const [dashboard, employees, leaves, notifications, audit] = await Promise.all([
            api(`/dashboard?company=${code}`), api(`/employees?company=${code}`), api(`/leave?company=${code}`), api(`/notifications?company=${code}`), api(`/audit?company=${code}`)
        ]);
        state.dashboard = dashboard; state.employees = employees; state.leaves = leaves;
        renderDashboard(dashboard); renderEmployees(employees); renderLeaves(leaves); renderNotifications(notifications); renderAudit(audit);
    } catch (error) { message(error.message, true); }
}

function renderDashboard(data) {
    $('#company-name').textContent = data.company.name; $('#employee-count').textContent = data.employeeCount; $('#present-count').textContent = data.presentToday; $('#remote-count').textContent = data.remoteToday; $('#leave-count').textContent = data.pendingLeaveCount;
    const report = data.latestReport; const payroll = data.latestPayroll;
    $('#payroll-total').textContent = payroll ? money(payroll.totalGross) : '—'; $('#payroll-period').textContent = payroll ? `${payroll.period} · ${payroll.employeeCount} people` : 'No processed run';
    $('#report-net').textContent = report ? money(report.totalNet) : '—'; $('#report-gross').textContent = report ? money(report.totalGross) : '—'; $('#report-deduction').textContent = report ? money(report.totalDeductions) : '—'; $('#report-people').textContent = report ? report.employeeCount : '—'; $('#cache-badge').textContent = report ? (report.cacheHit ? 'cache hit' : 'cache miss') : 'cache —';
}

function renderEmployees(employees) {
    $('#employee-table').innerHTML = employees.length ? employees.map((employee) => `<tr><td><div class="person-name">${escapeHtml(employee.fullName)}</div><div class="person-email">${escapeHtml(employee.email)}</div></td><td>${escapeHtml(employee.jobTitle)}</td><td class="salary">${money(employee.monthlySalary)}</td><td>${escapeHtml(employee.hireDate)}</td></tr>`).join('') : '<tr><td colspan="4" class="empty">No employees in this company.</td></tr>';
    const options = employees.map((employee) => `<option value="${employee.id}">${escapeHtml(employee.fullName)}</option>`).join(''); $('#attendance-employee').innerHTML = options; $('#leave-employee').innerHTML = options;
}

function renderLeaves(leaves) {
    $('#leave-list').innerHTML = leaves.length ? leaves.map((leave) => `<div class="stack-item"><div class="stack-top"><span>${escapeHtml(leave.employeeName)} · ${escapeHtml(leave.leaveType)}</span><span class="status ${leave.status.toLowerCase()}">${escapeHtml(leave.status)}</span></div><p>${escapeHtml(leave.startDate)} → ${escapeHtml(leave.endDate)} · ${escapeHtml(leave.reason)}</p>${leave.status === 'PENDING' ? `<button class="approve" data-approve="${leave.id}">Approve</button>` : ''}</div>`).join('') : '<div class="empty">No leave requests yet.</div>';
}

function renderNotifications(notifications) {
    $('#notification-list').innerHTML = notifications.length ? notifications.map((item) => `<div class="stack-item"><div class="stack-top"><span>${escapeHtml(item.type)}</span><span class="status ${item.status.toLowerCase()}">${escapeHtml(item.status)}</span></div><p>${escapeHtml(item.message)} · ${escapeHtml(item.recipient)}</p></div>`).join('') : '<div class="empty">No notifications have been queued.</div>';
}

function renderAudit(audit) {
    $('#audit-list').innerHTML = audit.length ? audit.map((item) => `<div class="audit-item"><strong>${escapeHtml(item.action)} · ${escapeHtml(item.resourceType)}</strong><span>${escapeHtml(item.actor)} · ${escapeHtml(item.details)}</span></div>`).join('') : '<div class="empty">No audit events yet.</div>';
}

$('#company-select').addEventListener('change', (event) => { state.company = event.target.value; loadData(); });
$('#refresh').addEventListener('click', loadData);

$('#attendance-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    try { await api('/attendance', { method: 'POST', body: JSON.stringify({ employeeId: Number($('#attendance-employee').value), workDate: $('#attendance-date').value, status: $('#attendance-status').value, hours: Number($('#attendance-hours').value) }) }); message('Attendance recorded and added to the audit trail.'); await loadData(); }
    catch (error) { message(error.message, true); }
});

$('#leave-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    try { await api('/leave', { method: 'POST', body: JSON.stringify({ employeeId: Number($('#leave-employee').value), leaveType: $('#leave-type').value, startDate: $('#leave-start').value, endDate: $('#leave-end').value, reason: $('#leave-reason').value }) }); message('Leave request submitted for HR review.'); $('#leave-reason').value = ''; await loadData(); }
    catch (error) { message(error.message, true); }
});

$('#payroll-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    try { await api('/payroll/runs', { method: 'POST', body: JSON.stringify({ companyCode: state.company, period: $('#payroll-period-input').value }) }); message('Payroll processed. The notification dispatcher is delivering the event asynchronously.'); await loadData(); }
    catch (error) { message(error.message, true); }
});

$('#reminder-job').addEventListener('click', async () => { try { const result = await api('/jobs/leave-reminders', { method: 'POST' }); message(`${result.queued} reminder(s) queued for async delivery.`); await loadData(); } catch (error) { message(error.message, true); } });
$('#leave-list').addEventListener('click', async (event) => { const button = event.target.closest('[data-approve]'); if (!button) return; try { await api(`/leave/${button.dataset.approve}/approve`, { method: 'PATCH' }); message('Leave approved and notification queued.'); await loadData(); } catch (error) { message(error.message, true); } });

const today = new Date(); const isoToday = new Date(today.getTime() - today.getTimezoneOffset() * 60000).toISOString().slice(0, 10); $('#attendance-date').value = isoToday; $('#leave-start').value = isoToday; $('#leave-end').value = isoToday; $('#payroll-period-input').value = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;
loadCompanies().then(loadData).catch((error) => message(error.message, true));
