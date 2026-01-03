// Auth.js - Authentication functions for login/register page ONLY

// Show login form
function showLogin() {
    document.getElementById('loginForm').classList.remove('d-none');
    document.getElementById('registerForm').classList.add('d-none');
}

// Show register form
function showRegister() {
    document.getElementById('loginForm').classList.add('d-none');
    document.getElementById('registerForm').classList.remove('d-none');
}

// Handle login
async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const errorDiv = document.getElementById('loginError');

    try {
        showLoading();

        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok && data.token) {
            // Store token and user data
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify({
                id: data.userId,
                username: data.username,
                role: data.role
            }));

            // Redirect to dashboard
            window.location.href = 'dashboard.html';
        } else {
            errorDiv.textContent = data.message || 'Identifiants invalides';
            errorDiv.classList.remove('d-none');
        }
    } catch (error) {
        console.error('Login error:', error);
        errorDiv.textContent = 'Erreur de connexion au serveur';
        errorDiv.classList.remove('d-none');
    } finally {
        hideLoading();
    }

    return false;
}

// Handle register
async function handleRegister(event) {
    event.preventDefault();

    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const fullName = document.getElementById('regFullName').value;
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('regConfirmPassword').value;
    const errorDiv = document.getElementById('registerError');

    // Validate passwords match
    if (password !== confirmPassword) {
        errorDiv.textContent = 'Les mots de passe ne correspondent pas';
        errorDiv.classList.remove('d-none');
        return false;
    }

    try {
        showLoading();

        const response = await fetch('/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username,
                password,
                email,
                fullName,
                role: 'UTILISATEUR'
            })
        });

        const data = await response.json();

        if (response.ok && data.token) {
            // Store token and user data
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify({
                id: data.userId,
                username: data.username,
                role: data.role
            }));

            // Redirect to dashboard
            window.location.href = 'dashboard.html';
        } else {
            errorDiv.textContent = data.message || 'Erreur lors de l\'inscription';
            errorDiv.classList.remove('d-none');
        }
    } catch (error) {
        console.error('Register error:', error);
        errorDiv.textContent = 'Erreur de connexion au serveur';
        errorDiv.classList.remove('d-none');
    } finally {
        hideLoading();
    }

    return false;
}

// Logout function
async function logout() {
    try {
        await apiCall('/auth/logout', { method: 'POST' });
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = 'index.html';
    }
}
