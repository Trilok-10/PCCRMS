// API Configuration
const CONFIG = {
    API_BASE_URL: 'http://localhost:9090',

    // Service endpoints (matching API Gateway routes)
    AUTH_SERVICE: '/api/auth',
    PATIENT_SERVICE: '/api/patients',
    APPOINTMENT_SERVICE: '/api/appointments',
    DOCTOR_SCHEDULE_SERVICE: '/api/appointments/schedules',
    EHR_SERVICE: '/api/ehr',
    PHARMACY_SERVICE: '/api/pharmacy',
    BILLING_SERVICE: '/api/billing',

    // Token key
    TOKEN_KEY: 'authToken',
    USER_KEY: 'userData',

    // Roles
    ROLES: {
        ADMIN: 'ADMIN',
        PATIENT: 'PATIENT',
        DOCTOR: 'DOCTOR',
        RECEPTIONIST: 'RECEPTIONIST',
        PHARMACIST: 'PHARMACIST',
        BILLING_OFFICER: 'BILLING_OFFICER'
    },

    // Appointment Status
    APPOINTMENT_STATUS: {
        BOOKED: 'BOOKED',
        COMPLETED: 'COMPLETED',
        CANCELLED: 'CANCELLED',
        NO_SHOW: 'NO_SHOW'
    },

    // Dispense Status
    DISPENSE_STATUS: {
        PENDING: 'PENDING',
        DISPENSED: 'DISPENSED',
        CANCELLED: 'CANCELLED'
    },

    // Payment Status
    PAYMENT_STATUS: {
        PENDING: 'PENDING',
        PARTIAL: 'PARTIAL',
        PAID: 'PAID'
    },

    // Claim Status
    CLAIM_STATUS: {
        PENDING: 'PENDING',
        APPROVED: 'APPROVED',
        REJECTED: 'REJECTED'
    }
};

// Freeze config to prevent modifications
Object.freeze(CONFIG);

