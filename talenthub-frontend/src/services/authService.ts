
import api from "./api";
import type { CurrentUser } from "../types/currentUser";
import type {ClientRegistrationRequest, FreelanceRegistrationRequest} from "../types/register.ts";

const getCurrentUser = async (): Promise<CurrentUser> => {
    const response = await api.get<CurrentUser>("/auth/me");
    return response.data;
};

const logout = (): void => {
    sessionStorage.removeItem("authorization");
    sessionStorage.removeItem("currentUser");

    delete api.defaults.headers.common.Authorization;
};

const registerFreelance = async (
    request: FreelanceRegistrationRequest
): Promise<void> => {
    await api.post("/freelances", request);
};

const registerClient = async (
    request: ClientRegistrationRequest
): Promise<void> => {
    await api.post("/clients", request);
};

const authService = {
    getCurrentUser,
    logout,
    registerClient,
    registerFreelance
};

export default authService;
