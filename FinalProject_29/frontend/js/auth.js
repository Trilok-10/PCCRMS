// Authentication Module

// Helper to get base path for redirects
function getBasePath() {
    const path = window.location.pathname;
    if (path.includes('/pages/')) {
        return '../../';
    }
    return './';
}

// Helper to get pages path for dashboard redirects
function getPagesPath() {
    const path = window.location.pathname;
    if (path.includes('/pages/')) {
        // Already in pages, go up two levels then into pages
        return '../../pages/';
    }
    // From root level (index.html), go into pages
    return './pages/';
}

const Auth = {
    // Store user data and token
    setSession(token, user) {
        localStorage.setItem(CONFIG.TOKEN_KEY, token);
        localStorage.setItem(CONFIG.USER_KEY, JSON.stringify(user));
    },

    // Get current token
    getToken() {
        return localStorage.getItem(CONFIG.TOKEN_KEY);
    },

    // Get current user
    getUser() {
        const userData = localStorage.getItem(CONFIG.USER_KEY);
        return userData ? JSON.parse(userData) : null;
    },

    // Check if user is logged in
    isLoggedIn() {
        return !!this.getToken();
    },

    // Get user role
    getRole() {
        const user = this.getUser();
        return user ? user.role : null;
    },

    // Get user ID
    getUserId() {
        const user = this.getUser();
        return user ? user.userId : null;
    },

    // Get user name
    getUserName() {
        const user = this.getUser();
        return user ? user.fullName : null;
    },

    // Logout - clear storage only
    logout() {
        localStorage.removeItem(CONFIG.TOKEN_KEY);
        localStorage.removeItem(CONFIG.USER_KEY);
        localStorage.removeItem('patientId');
    },

    // Logout and redirect to login page
    logoutAndRedirect() {
        this.logout();
        window.location.href = getBasePath() + 'index.html';
    },

    // Login
    async login(email, password) {
        try {
            const response = await AuthAPI.login({ email, password });

            if (response.token) {
                this.setSession(response.token, {
                    userId: response.userId,
                    email: response.email,
                    fullName: response.fullName,
                    role: response.role
                });
                return { success: true, role: response.role };
            }
            return { success: false, message: 'Invalid response from server' };
        } catch (error) {
            return { success: false, message: error.message || 'Login failed' };
        }
    },

    // Register (Patient self-registration)
    async register(userData) {
        try {
            const response = await AuthAPI.register(userData);
            return { success: true, message: 'Registration successful! Please login.' };
        } catch (error) {
            return { success: false, message: error.message || 'Registration failed' };
        }
    },

    // Check role access
    hasRole(requiredRoles) {
        const userRole = this.getRole();
        if (!userRole) return false;

        if (Array.isArray(requiredRoles)) {
            return requiredRoles.includes(userRole);
        }
        return userRole === requiredRoles;
    },

    // Redirect based on role
    redirectToDashboard() {
        const role = this.getRole();
        const basePath = getPagesPath();

        switch (role) {
            case CONFIG.ROLES.ADMIN:
                window.location.href = `${basePath}admin/dashboard.html`;
                break;
            case CONFIG.ROLES.PATIENT:
                window.location.href = `${basePath}patient/dashboard.html`;
                break;
            case CONFIG.ROLES.DOCTOR:
                window.location.href = `${basePath}doctor/dashboard.html`;
                break;
            case CONFIG.ROLES.RECEPTIONIST:
                window.location.href = `${basePath}receptionist/dashboard.html`;
                break;
            case CONFIG.ROLES.PHARMACIST:
                window.location.href = `${basePath}pharmacist/dashboard.html`;
                break;
            case CONFIG.ROLES.BILLING_OFFICER:
                window.location.href = `${basePath}billing_officer/dashboard.html`;
                break;
            default:
                window.location.href = getBasePath() + 'index.html';
        }
    },

    // Check authentication and redirect if not logged in
    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = getBasePath() + 'index.html';
            return false;
        }
        return true;
    },

    // Check role and redirect if not authorized
    requireRole(allowedRoles) {
        if (!this.requireAuth()) return false;

        if (!this.hasRole(allowedRoles)) {
            Utils.showToast('Access Denied', 'error');
            this.redirectToDashboard();
            return false;
        }
        return true;
    }
};

// Global handleLogout function for onclick handlers
function handleLogout() {
    Auth.logoutAndRedirect();
}

// Initialize auth check on page load
document.addEventListener('DOMContentLoaded', () => {
    // Skip auth check for login and register pages
    const currentPage = window.location.pathname;
    if (currentPage.includes('index.html') || currentPage.includes('register.html') || currentPage.endsWith('/frontend/')) {
        return;
    }

    // Check if user is authenticated
    if (!Auth.isLoggedIn()) {
        window.location.href = getBasePath() + 'index.html';
    }
});

