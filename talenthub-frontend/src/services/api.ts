
import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

// Restauration de l'authentification après un refresh
const savedAuthorization = sessionStorage.getItem("authorization");

if (savedAuthorization) {
    api.defaults.headers.common.Authorization = savedAuthorization;
}

export default api;
