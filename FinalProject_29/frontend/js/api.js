// API Service - Handles all HTTP requests with JWT authentication

const API = {
    // Get authorization headers
    getHeaders() {
        const token = localStorage.getItem(CONFIG.TOKEN_KEY);
        return {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` })
        };
    },

    // Generic fetch wrapper
    async request(endpoint, options = {}) {
        const url = `${CONFIG.API_BASE_URL}${endpoint}`;

        const defaultOptions = {
            headers: this.getHeaders(),
        };

        const finalOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...options.headers
            }
        };

        try {
            const response = await fetch(url, finalOptions);

            // Handle 401 Unauthorized
            if (response.status === 401) {
                localStorage.removeItem(CONFIG.TOKEN_KEY);
                localStorage.removeItem(CONFIG.USER_KEY);
                // Use dynamic path based on current location
                const path = window.location.pathname;
                if (path.includes('/pages/')) {
                    window.location.href = '../../index.html';
                } else {
                    window.location.href = 'index.html';
                }
                throw new Error('Session expired. Please login again.');
            }

            // Handle 403 Forbidden
            if (response.status === 403) {
                throw new Error('You do not have permission to perform this action.');
            }

            // Parse response
            const contentType = response.headers.get('content-type');
            let data;

            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                data = await response.text();
            }

            if (!response.ok) {
                throw new Error(data.message || data || 'An error occurred');
            }

            // Handle ApiResponse wrapper from backend
            // Backend returns: { success: boolean, message: string, data: T }
            if (data && typeof data === 'object' && 'success' in data) {
                if (!data.success) {
                    throw new Error(data.message || 'Request failed');
                }
                return data.data; // Return the actual data
            }

            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    // GET request
    async get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    },

    // POST request
    async post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    // PUT request
    async put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    // DELETE request
    async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    },

    // PATCH request
    async patch(endpoint, data) {
        return this.request(endpoint, {
            method: 'PATCH',
            body: JSON.stringify(data)
        });
    }
};

// Auth Service API calls
const AuthAPI = {
    login(credentials) {
        return API.post(`${CONFIG.AUTH_SERVICE}/login`, credentials);
    },

    register(userData) {
        return API.post(`${CONFIG.AUTH_SERVICE}/register`, userData);
    },

    validateToken() {
        return API.get(`${CONFIG.AUTH_SERVICE}/validate`);
    },

    createUser(userData) {
        return API.post(`${CONFIG.AUTH_SERVICE}/users`, userData);
    },

    getAllUsers() {
        return API.get(`${CONFIG.AUTH_SERVICE}/users`);
    },

    getUserById(id) {
        return API.get(`${CONFIG.AUTH_SERVICE}/users/${id}`);
    },

    getUsersByRole(role) {
        return API.get(`${CONFIG.AUTH_SERVICE}/users/role/${role}`);
    },

    deactivateUser(id) {
        return API.put(`${CONFIG.AUTH_SERVICE}/users/${id}/deactivate`);
    },

    activateUser(id) {
        return API.put(`${CONFIG.AUTH_SERVICE}/users/${id}/activate`);
    },

    resetPassword(id, newPassword) {
        return API.put(`${CONFIG.AUTH_SERVICE}/users/${id}/reset-password?newPassword=${encodeURIComponent(newPassword)}`);
    },

    deleteUser(id) {
        return API.delete(`${CONFIG.AUTH_SERVICE}/users/${id}`);
    }
};

// Patient Service API calls
const PatientAPI = {
    register(patientData) {
        return API.post(CONFIG.PATIENT_SERVICE, patientData);
    },

    updateDemographics(id, data) {
        return API.put(`${CONFIG.PATIENT_SERVICE}/${id}`, data);
    },

    getById(id) {
        return API.get(`${CONFIG.PATIENT_SERVICE}/${id}`);
    },

    getByMrn(mrn) {
        return API.get(`${CONFIG.PATIENT_SERVICE}/mrn/${mrn}`);
    },

    getByUserId(userId) {
        return API.get(`${CONFIG.PATIENT_SERVICE}/user/${userId}`);
    },

    search(query) {
        return API.get(`${CONFIG.PATIENT_SERVICE}/search?keyword=${encodeURIComponent(query)}`);
    },

    getAll() {
        return API.get(CONFIG.PATIENT_SERVICE);
    },

    getActive() {
        return API.get(`${CONFIG.PATIENT_SERVICE}/active`);
    },

    deactivate(id) {
        return API.put(`${CONFIG.PATIENT_SERVICE}/${id}/deactivate`);
    },

    activate(id) {
        return API.put(`${CONFIG.PATIENT_SERVICE}/${id}/activate`);
    },

    delete(id) {
        return API.delete(`${CONFIG.PATIENT_SERVICE}/${id}`);
    }
};

// Appointment Service API calls
const AppointmentAPI = {
    book(appointmentData) {
        return API.post(CONFIG.APPOINTMENT_SERVICE, appointmentData);
    },

    reschedule(id, data) {
        return API.put(`${CONFIG.APPOINTMENT_SERVICE}/${id}/reschedule?newDate=${data.appointmentDate}&newTime=${data.appointmentTime}`);
    },

    cancel(id) {
        return API.put(`${CONFIG.APPOINTMENT_SERVICE}/${id}/cancel`);
    },

    updateStatus(id, status) {
        return API.put(`${CONFIG.APPOINTMENT_SERVICE}/${id}/status?status=${status}`);
    },

    getDoctorSlots(doctorId, date) {
        return API.get(`${CONFIG.APPOINTMENT_SERVICE}/slots?doctorId=${doctorId}&date=${date}`);
    },

    getById(id) {
        return API.get(`${CONFIG.APPOINTMENT_SERVICE}/${id}`);
    },

    getByPatient(patientId) {
        return API.get(`${CONFIG.APPOINTMENT_SERVICE}/patient/${patientId}`);
    },

    getByDoctor(doctorId) {
        return API.get(`${CONFIG.APPOINTMENT_SERVICE}/doctor/${doctorId}`);
    },

    getDoctorToday(doctorId) {
        return API.get(`${CONFIG.APPOINTMENT_SERVICE}/doctor/${doctorId}/today`);
    },

    getAll() {
        return API.get(CONFIG.APPOINTMENT_SERVICE);
    },

    getByDate(date) {
        return API.get(`${CONFIG.APPOINTMENT_SERVICE}/date?date=${date}`);
    }
};

// Doctor Schedule API calls
const DoctorScheduleAPI = {
    create(scheduleData) {
        return API.post(CONFIG.DOCTOR_SCHEDULE_SERVICE, scheduleData);
    },

    update(id, scheduleData) {
        return API.put(`${CONFIG.DOCTOR_SCHEDULE_SERVICE}/${id}`, scheduleData);
    },

    getByDoctor(doctorId) {
        return API.get(`${CONFIG.DOCTOR_SCHEDULE_SERVICE}/doctor/${doctorId}`);
    },

    delete(id) {
        return API.delete(`${CONFIG.DOCTOR_SCHEDULE_SERVICE}/${id}`);
    }
};

// EHR Service API calls
const EhrAPI = {
    createEncounter(encounterData) {
        return API.post(`${CONFIG.EHR_SERVICE}/encounters`, encounterData);
    },



    getPatientHistory(patientId) {
        return API.get(`${CONFIG.EHR_SERVICE}/patients/${patientId}/history`);
    },

    getEncounterById(id) {
        return API.get(`${CONFIG.EHR_SERVICE}/records/${id}`);
    },

    getAllRecords() {
        return API.get(`${CONFIG.EHR_SERVICE}/records`);
    },

    updateEncounter(id, data) {
        return API.put(`${CONFIG.EHR_SERVICE}/encounters/${id}`, data);
    }
};

// Pharmacy Service API calls
const PharmacyAPI = {
    // Prescriptions
    createPrescription(prescriptionData) {
        return API.post(`${CONFIG.PHARMACY_SERVICE}/prescriptions`, prescriptionData);
    },

    dispenseMedication(id, pharmacistId) {
        return API.put(`${CONFIG.PHARMACY_SERVICE}/prescriptions/${id}/dispense?pharmacistId=${pharmacistId}`);
    },

    cancelPrescription(id) {
        return API.put(`${CONFIG.PHARMACY_SERVICE}/prescriptions/${id}/cancel`);
    },

    getPrescriptionById(id) {
        return API.get(`${CONFIG.PHARMACY_SERVICE}/prescriptions/${id}`);
    },

    getPatientPrescriptions(patientId) {
        return API.get(`${CONFIG.PHARMACY_SERVICE}/prescriptions/patient/${patientId}`);
    },

    getPendingPrescriptions() {
        return API.get(`${CONFIG.PHARMACY_SERVICE}/prescriptions/pending`);
    },

    getAllPrescriptions() {
        return API.get(`${CONFIG.PHARMACY_SERVICE}/prescriptions`);
    },

    getPrescriptionsByStatus(status) {
        return API.get(`${CONFIG.PHARMACY_SERVICE}/prescriptions/status/${status}`);
    },



    // Medications
    addMedication(medicationData) {
        return API.post(`${CONFIG.PHARMACY_SERVICE}/medications`, medicationData);
    },

    updateStock(id, quantity) {
        return API.put(`${CONFIG.PHARMACY_SERVICE}/medications/${id}/stock?quantity=${quantity}`);
    },

    deleteMedication(id) {
        return API.delete(`${CONFIG.PHARMACY_SERVICE}/medications/${id}`);
    },

    getMedicationById(id) {
        return API.get(`${CONFIG.PHARMACY_SERVICE}/medications/${id}`);
    },

    searchMedications(query) {
        return API.get(`${CONFIG.PHARMACY_SERVICE}/medications/search?query=${encodeURIComponent(query)}`);
    },

    getAllMedications() {
        return API.get(`${CONFIG.PHARMACY_SERVICE}/medications`);
    },

    getLowStockMedications() {
        return API.get(`${CONFIG.PHARMACY_SERVICE}/medications/low-stock`);
    },

    getExpiringMedications() {
        return API.get(`${CONFIG.PHARMACY_SERVICE}/medications/expiring`);
    },

};

// Billing Service API calls
const BillingAPI = {
    // Invoices
    generateInvoice(invoiceData) {
        return API.post(`${CONFIG.BILLING_SERVICE}/invoices`, invoiceData);
    },

    getInvoiceById(id) {
        return API.get(`${CONFIG.BILLING_SERVICE}/invoices/${id}`);
    },

    getInvoiceByNumber(invoiceNumber) {
        return API.get(`${CONFIG.BILLING_SERVICE}/invoices/number/${invoiceNumber}`);
    },

    getPatientInvoices(patientId) {
        return API.get(`${CONFIG.BILLING_SERVICE}/invoices/patient/${patientId}`);
    },

    getAllInvoices() {
        return API.get(`${CONFIG.BILLING_SERVICE}/invoices`);
    },

    getUnpaidInvoices() {
        return API.get(`${CONFIG.BILLING_SERVICE}/invoices/unpaid`);
    },

    deleteInvoice(invoiceId) {
        return API.delete(`${CONFIG.BILLING_SERVICE}/invoices/${invoiceId}`);
    },

    // Payments
    recordPayment(invoiceId, paymentData) {
        // Backend expects invoiceId in the body, not URL
        return API.post(`${CONFIG.BILLING_SERVICE}/payments`, {
            invoiceId: parseInt(invoiceId),
            amount: paymentData.amount,
            paymentMethod: paymentData.paymentMethod,
            transactionReference: paymentData.referenceNumber || null,
            notes: paymentData.notes || null
        });
    },

    getInvoicePayments(invoiceId) {
        return API.get(`${CONFIG.BILLING_SERVICE}/invoices/${invoiceId}/payments`);
    },

};

