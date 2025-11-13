function csrfHeaders() {
    const tokenEl = document.querySelector('meta[name="_csrf"]');
    const headerEl = document.querySelector('meta[name="_csrf_header"]');
    const h = new Headers();
    h.append('Content-Type', 'application/x-www-form-urlencoded');
    if (tokenEl && headerEl) {
        h.append(headerEl.content, tokenEl.content);
    }
    return h;
}

function addMembership() {
    const select = document.getElementById('membership-select');
    const membershipId = select.value;
    fetch('/profile/memberships/add', {
        method: 'POST',
        headers: csrfHeaders(),
        body: new URLSearchParams({membershipId})
    }).then(r => r.ok ? r.json() : Promise.reject(r))
        .then(updateMembershipList)
        .catch(() => showMsg('Failed to add membership'));
}

function removeMembership(btn) {
    const membershipId = btn.getAttribute('data-id');
    fetch('/profile/memberships/remove', {
        method: 'POST',
        headers: csrfHeaders(),
        body: new URLSearchParams({membershipId})
    }).then(r => r.ok ? r.json() : Promise.reject(r))
        .then(updateMembershipList)
        .catch(() => showMsg('Failed to remove membership'));
}

function updateMembershipList(payload) {
    const list = document.getElementById('linked-memberships');
    if (!list) return;
    list.innerHTML = '';
    const memberships = payload.memberships || [];
    if (!memberships.length) {
        const li = document.createElement('li');
        li.className = 'list-group-item';
        li.textContent = 'No memberships linked.';
        list.appendChild(li);
        return;
    }
    memberships.forEach(m => {
        const li = document.createElement('li');
        li.className = 'list-group-item';
        li.innerHTML = `<div class="d-flex justify-content-between align-items-center">
                <span><span class="fw-semibold">${m.type || ''}</span> <span class="text-muted"> - ${m.organizationName || ''}</span></span>
                <button class="btn btn-sm btn-outline-danger" data-id="${m.id}" onclick="removeMembership(this)">Remove</button>
            </div>`;
        list.appendChild(li);
    });
    showMsg('Updated successfully');
}

function showMsg(text) {
    const el = document.getElementById('msg');
    if (!el) return;
    el.textContent = text;
    setTimeout(() => el.textContent = '', 2000);
}