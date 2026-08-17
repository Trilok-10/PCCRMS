// Utility Functions

const Utils = {
    // Extract error message from various error formats
    // Handles: string, Error object, API error object, nested errors
    extractErrorMessage(error, fallback = 'An error occurred') {
        if (!error) return fallback;

        // If it's a simple string
        if (typeof error === 'string') return error;

        // If error.message is a string
        if (error.message && typeof error.message === 'string') {
            return error.message;
        }

        // If error.message is an object (e.g., validation errors)
        if (error.message && typeof error.message === 'object') {
            const messages = [];
            for (const [field, msg] of Object.entries(error.message)) {
                const fieldName = field.replace(/([A-Z])/g, ' $1').toLowerCase().trim();
                const capitalizedField = fieldName.charAt(0).toUpperCase() + fieldName.slice(1);
                messages.push(`${capitalizedField}: ${msg}`);
            }
            return messages.join(', ') || fallback;
        }

        // If it has an errors array
        if (error.errors && Array.isArray(error.errors)) {
            return error.errors.map(e =>
                typeof e === 'string' ? e : (e.message || e.defaultMessage || JSON.stringify(e))
            ).join(', ');
        }

        // If it has error property
        if (error.error && typeof error.error === 'string') {
            return error.error;
        }

        // Last resort - try to stringify but avoid [object Object]
        try {
            const str = JSON.stringify(error);
            if (str && str !== '{}') return str;
        } catch (e) {
            // Ignore stringify errors
        }

        return fallback;
    },

    // Format date to YYYY-MM-DD
    formatDate(date) {
        if (!date) return '';
        const d = new Date(date);
        return d.toISOString().split('T')[0];
    },

    // Format date for display (e.g., "Jan 15, 2024")
    formatDateDisplay(date) {
        if (!date) return '';
        const d = new Date(date);
        return d.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    },

    // Format time (e.g., "09:30 AM")
    formatTime(time) {
        if (!time) return '';
        const [hours, minutes] = time.split(':');
        const h = parseInt(hours);
        const ampm = h >= 12 ? 'PM' : 'AM';
        const hour12 = h % 12 || 12;
        return `${hour12}:${minutes} ${ampm}`;
    },

    // Format currency
    formatCurrency(amount) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount || 0);
    },

    // Show toast notification
    showToast(message, type = 'info') {
        const toastContainer = document.getElementById('toast-container') || this.createToastContainer();

        const toast = document.createElement('div');
        toast.className = `toast show align-items-center text-white bg-${this.getToastColor(type)} border-0`;
        toast.setAttribute('role', 'alert');
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    <i class="${this.getToastIcon(type)} me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        `;

        toastContainer.appendChild(toast);

        // Auto remove after 4 seconds
        setTimeout(() => {
            toast.remove();
        }, 4000);
    },

    createToastContainer() {
        const container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
        return container;
    },

    getToastColor(type) {
        const colors = {
            success: 'success',
            error: 'danger',
            warning: 'warning',
            info: 'primary'
        };
        return colors[type] || 'primary';
    },

    getToastIcon(type) {
        const icons = {
            success: 'bi bi-check-circle-fill',
            error: 'bi bi-x-circle-fill',
            warning: 'bi bi-exclamation-triangle-fill',
            info: 'bi bi-info-circle-fill'
        };
        return icons[type] || 'bi bi-info-circle-fill';
    },

    // Show loading spinner
    showLoading(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.innerHTML = `
                <div class="d-flex justify-content-center align-items-center py-5">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            `;
        }
    },

    // Hide loading spinner
    hideLoading(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.innerHTML = '';
        }
    },

    // Show confirmation modal
    async confirm(title, message) {
        return new Promise((resolve) => {
            const modalHtml = `
                <div class="modal fade" id="confirmModal" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">${title}</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <p>${message}</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="confirmNo">Cancel</button>
                                <button type="button" class="btn btn-primary" id="confirmYes">Confirm</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;

            // Remove existing modal if any
            const existingModal = document.getElementById('confirmModal');
            if (existingModal) existingModal.remove();

            document.body.insertAdjacentHTML('beforeend', modalHtml);
            const modal = new bootstrap.Modal(document.getElementById('confirmModal'));
            modal.show();

            document.getElementById('confirmYes').onclick = () => {
                modal.hide();
                resolve(true);
            };

            document.getElementById('confirmNo').onclick = () => {
                modal.hide();
                resolve(false);
            };

            document.getElementById('confirmModal').addEventListener('hidden.bs.modal', () => {
                document.getElementById('confirmModal').remove();
            });
        });
    },

    // Get status badge HTML
    getStatusBadge(status, type = 'default') {
        const badges = {
            // Appointment status
            BOOKED: 'bg-primary',
            COMPLETED: 'bg-success',
            CANCELLED: 'bg-danger',
            NO_SHOW: 'bg-warning text-dark',
            // Dispense status
            PENDING: 'bg-warning text-dark',
            DISPENSED: 'bg-success',
            // Payment status
            UNPAID: 'bg-warning text-dark',
            PAID: 'bg-success',
            PARTIAL: 'bg-info',
            // Claim status
            SUBMITTED: 'bg-primary',
            APPROVED: 'bg-success',
            REJECTED: 'bg-danger',
            // User status
            ACTIVE: 'bg-success',
            INACTIVE: 'bg-secondary'
        };

        return `<span class="badge ${badges[status] || 'bg-secondary'}">${status}</span>`;
    },

    // Get role badge
    getRoleBadge(role) {
        const colors = {
            ADMIN: 'bg-danger',
            DOCTOR: 'bg-primary',
            PATIENT: 'bg-success',
            RECEPTIONIST: 'bg-info',
            PHARMACIST: 'bg-warning text-dark',
            BILLING_OFFICER: 'bg-secondary'
        };
        return `<span class="badge ${colors[role] || 'bg-secondary'}">${role}</span>`;
    },

    // Debounce function for search inputs
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    // Validate email (must end with .com)
    isValidEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email) && email.toLowerCase().endsWith('.com');
    },

    // Validate phone number
    isValidPhone(phone) {
        const re = /^\+?[\d\s-]{10,}$/;
        return re.test(phone);
    },

    // Generate MRN (for display purposes)
    generateMRN() {
        const timestamp = Date.now().toString(36).toUpperCase();
        const random = Math.random().toString(36).substring(2, 6).toUpperCase();
        return `MRN-${timestamp}-${random}`;
    },

    // Calculate age from date of birth
    calculateAge(dob) {
        if (!dob) return '';
        const today = new Date();
        const birthDate = new Date(dob);
        let age = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    },

    // Get today's date in YYYY-MM-DD format
    getToday() {
        return new Date().toISOString().split('T')[0];
    },

    // Parse URL parameters
    getUrlParams() {
        const params = new URLSearchParams(window.location.search);
        const result = {};
        for (const [key, value] of params) {
            result[key] = value;
        }
        return result;
    },

    // Set button loading state
    setButtonLoading(button, loading) {
        if (loading) {
            button.disabled = true;
            button.dataset.originalText = button.innerHTML;
            button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Loading...';
        } else {
            button.disabled = false;
            button.innerHTML = button.dataset.originalText || button.innerHTML;
        }
    },

    // Escape HTML to prevent XSS
    escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    // Truncate text
    truncate(text, length = 50) {
        if (!text) return '';
        return text.length > length ? text.substring(0, length) + '...' : text;
    },

    // Empty state HTML
    getEmptyState(message = 'No data found', icon = 'bi-inbox') {
        return `
            <div class="text-center py-5 text-muted">
                <i class="bi ${icon}" style="font-size: 3rem;"></i>
                <p class="mt-3">${message}</p>
            </div>
        `;
    },

    // Error state HTML
    getErrorState(message = 'Something went wrong') {
        return `
            <div class="text-center py-5 text-danger">
                <i class="bi bi-exclamation-triangle" style="font-size: 3rem;"></i>
                <p class="mt-3">${message}</p>
            </div>
        `;
    }
};

