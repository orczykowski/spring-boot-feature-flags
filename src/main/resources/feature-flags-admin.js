(function() {
    'use strict';

    var API_BASE = window.FF_ADMIN_CONFIG.apiBasePath;

    // ---- Theme ----
    function initTheme() {
        var saved = localStorage.getItem('ff-admin-theme');
        var prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        var theme = saved || (prefersDark ? 'dark' : 'light');
        applyTheme(theme);
    }

    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        document.getElementById('theme-toggle').textContent = theme === 'dark' ? '\u2600' : '\u263E';
    }

    function toggleTheme() {
        var current = document.documentElement.getAttribute('data-theme');
        var next = current === 'dark' ? 'light' : 'dark';
        localStorage.setItem('ff-admin-theme', next);
        applyTheme(next);
    }

    // ---- API ----
    function handleResponse(response) {
        return response.text().then(function(text) {
            if (!response.ok) {
                var msg = 'Request failed (' + response.status + ')';
                if (text) {
                    try { msg = JSON.parse(text).message || msg; } catch(e) {}
                }
                throw new Error(msg);
            }
            return text ? JSON.parse(text) : null;
        });
    }

    function fetchFlags() {
        return fetch(API_BASE).then(handleResponse).then(function(data) {
            return data ? (data.definitions || []) : [];
        });
    }

    function createFlag(name, enabled, entitledUsers) {
        return fetch(API_BASE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: name, enabled: enabled, entitledUsers: entitledUsers })
        }).then(handleResponse);
    }

    function enableFlag(name) {
        return fetch(API_BASE + '/' + encodeURIComponent(name) + '/enable', { method: 'PUT' }).then(handleResponse);
    }

    function disableFlag(name) {
        return fetch(API_BASE + '/' + encodeURIComponent(name) + '/disable', { method: 'PUT' }).then(handleResponse);
    }

    function restrictFlag(name) {
        return fetch(API_BASE + '/' + encodeURIComponent(name) + '/restrict', { method: 'PUT' }).then(handleResponse);
    }

    function deleteFlag(name) {
        return fetch(API_BASE + '/' + encodeURIComponent(name), { method: 'DELETE' }).then(handleResponse);
    }

    function addUser(flagName, userId) {
        return fetch(API_BASE + '/' + encodeURIComponent(flagName) + '/users/' + encodeURIComponent(userId), {
            method: 'POST'
        }).then(handleResponse);
    }

    function removeUser(flagName, userId) {
        return fetch(API_BASE + '/' + encodeURIComponent(flagName) + '/users/' + encodeURIComponent(userId), {
            method: 'DELETE'
        }).then(handleResponse);
    }

    // ---- Toast ----
    function showToast(message, type) {
        var container = document.getElementById('toast-container');
        var toast = document.createElement('div');
        toast.className = 'toast toast-' + (type || 'success');
        toast.textContent = message;
        container.appendChild(toast);
        setTimeout(function() {
            toast.classList.add('toast-removing');
            setTimeout(function() { toast.remove(); }, 200);
        }, 3000);
    }

    // ---- Delete confirmation ----
    var pendingDeleteFlag = null;

    function confirmDelete(flagName) {
        pendingDeleteFlag = flagName;
        document.getElementById('confirm-message').textContent =
            'Are you sure you want to delete "' + flagName + '"? This action cannot be undone.';
        document.getElementById('confirm-dialog').showModal();
    }

    function handleConfirmDelete() {
        if (!pendingDeleteFlag) return;
        var name = pendingDeleteFlag;
        pendingDeleteFlag = null;
        document.getElementById('confirm-dialog').close();
        deleteFlag(name).then(function() {
            showToast('Flag "' + name + '" deleted', 'success');
            loadFlags();
        }).catch(function(e) { showToast(e.message, 'error'); });
    }

    function handleCancelDelete() {
        pendingDeleteFlag = null;
        document.getElementById('confirm-dialog').close();
    }

    // ---- Render ----
    function escapeHtml(str) {
        var div = document.createElement('div');
        div.appendChild(document.createTextNode(str));
        return div.innerHTML;
    }

    function renderFlags(flags) {
        var container = document.getElementById('flags-list');
        document.getElementById('loading').style.display = 'none';

        if (flags.length === 0) {
            container.innerHTML = '<div class="empty-state">No feature flags defined yet. Create one above.</div>';
            return;
        }

        flags.sort(function(a, b) { return a.name.localeCompare(b.name); });

        container.innerHTML = flags.map(function(flag) {
            var stateClass = flag.enabled.toLowerCase();
            var users = flag.entitledUsers || [];

            var usersHtml = '';
            if (flag.enabled === 'RESTRICTED' || users.length > 0) {
                var chips = users.map(function(u) {
                    return '<span class="user-chip">' +
                        escapeHtml(u) +
                        '<button class="user-chip-remove" data-flag="' + escapeHtml(flag.name) + '" data-user="' + escapeHtml(u) + '" title="Remove user">&times;</button>' +
                        '</span>';
                }).join('');

                usersHtml = '<div class="users-section">' +
                    '<div class="users-label">Assigned Users (' + users.length + ')</div>' +
                    '<div class="users-list">' + (chips || '<span style="color:var(--text-muted);font-size:13px">No users assigned</span>') + '</div>' +
                    '<form class="add-user-form" data-flag="' + escapeHtml(flag.name) + '">' +
                        '<input type="text" placeholder="User ID" required>' +
                        '<button type="submit" class="btn btn-sm btn-outline">Add user</button>' +
                    '</form>' +
                    '</div>';
            }

            var enableBtn = flag.enabled !== 'ANYBODY'
                ? '<button class="btn-icon" data-action="enable" data-flag="' + escapeHtml(flag.name) + '" title="Enable for everybody">&#9654;</button>'
                : '';
            var disableBtn = flag.enabled !== 'NOBODY'
                ? '<button class="btn-icon" data-action="disable" data-flag="' + escapeHtml(flag.name) + '" title="Disable for everybody">&#9724;</button>'
                : '';
            var restrictBtn = flag.enabled !== 'RESTRICTED'
                ? '<button class="btn-icon" data-action="restrict" data-flag="' + escapeHtml(flag.name) + '" title="Set to restricted">&#128274;</button>'
                : '';

            return '<div class="flag-card">' +
                '<div class="flag-header">' +
                    '<div class="flag-name-group">' +
                        '<span class="flag-name">' + escapeHtml(flag.name) + '</span>' +
                        '<span class="badge badge-' + stateClass + '">' + escapeHtml(flag.enabled) + '</span>' +
                    '</div>' +
                    '<div class="flag-actions">' +
                        enableBtn + disableBtn + restrictBtn +
                        '<button class="btn-icon danger" data-action="delete" data-flag="' + escapeHtml(flag.name) + '" title="Delete flag">&#128465;</button>' +
                    '</div>' +
                '</div>' +
                usersHtml +
                '</div>';
        }).join('');
    }

    // ---- Event delegation ----
    function handleFlagActions(e) {
        var target = e.target;

        // Enable/Disable/Restrict/Delete buttons
        var action = target.getAttribute('data-action');
        var flagName = target.getAttribute('data-flag');
        if (action && flagName) {
            if (action === 'enable') {
                enableFlag(flagName).then(function() {
                    showToast('Flag "' + flagName + '" enabled', 'success');
                    loadFlags();
                }).catch(function(e) { showToast(e.message, 'error'); });
            } else if (action === 'disable') {
                disableFlag(flagName).then(function() {
                    showToast('Flag "' + flagName + '" disabled', 'success');
                    loadFlags();
                }).catch(function(e) { showToast(e.message, 'error'); });
            } else if (action === 'restrict') {
                restrictFlag(flagName).then(function() {
                    showToast('Flag "' + flagName + '" set to restricted', 'success');
                    loadFlags();
                }).catch(function(e) { showToast(e.message, 'error'); });
            } else if (action === 'delete') {
                confirmDelete(flagName);
            }
            return;
        }

        // Remove user chip
        if (target.classList.contains('user-chip-remove')) {
            var flag = target.getAttribute('data-flag');
            var user = target.getAttribute('data-user');
            removeUser(flag, user).then(function() {
                showToast('User "' + user + '" removed from "' + flag + '"', 'success');
                loadFlags();
            }).catch(function(e) { showToast(e.message, 'error'); });
        }
    }

    function handleAddUser(e) {
        if (e.target.classList.contains('add-user-form') || e.target.closest('.add-user-form')) {
            var form = e.target.closest('.add-user-form');
            if (form && e.type === 'submit') {
                e.preventDefault();
                var flagName = form.getAttribute('data-flag');
                var input = form.querySelector('input');
                var userId = input.value.trim();
                if (!userId) return;
                addUser(flagName, userId).then(function() {
                    showToast('User "' + userId + '" added to "' + flagName + '"', 'success');
                    input.value = '';
                    loadFlags();
                }).catch(function(err) { showToast(err.message, 'error'); });
            }
        }
    }

    // ---- Load ----
    function loadFlags() {
        fetchFlags().then(function(flags) {
            renderFlags(flags);
        }).catch(function(e) {
            document.getElementById('loading').style.display = 'none';
            showToast('Failed to load flags: ' + e.message, 'error');
        });
    }

    // ---- Create ----
    function handleCreate(e) {
        e.preventDefault();
        var name = document.getElementById('flag-name').value.trim();
        var state = document.getElementById('flag-state').value;
        var usersRaw = document.getElementById('flag-users').value.trim();
        var users = usersRaw ? usersRaw.split(',').map(function(u) { return u.trim(); }).filter(function(u) { return u; }) : [];

        createFlag(name, state, users).then(function() {
            showToast('Flag "' + name + '" created', 'success');
            document.getElementById('create-form').reset();
            loadFlags();
        }).catch(function(err) { showToast(err.message, 'error'); });
    }

    // ---- Init ----
    document.addEventListener('DOMContentLoaded', function() {
        initTheme();
        document.getElementById('theme-toggle').addEventListener('click', toggleTheme);
        document.getElementById('create-form').addEventListener('submit', handleCreate);
        document.getElementById('flags-list').addEventListener('click', handleFlagActions);
        document.getElementById('flags-list').addEventListener('submit', handleAddUser);
        document.getElementById('confirm-delete').addEventListener('click', handleConfirmDelete);
        document.getElementById('confirm-cancel').addEventListener('click', handleCancelDelete);
        loadFlags();
    });
})();
