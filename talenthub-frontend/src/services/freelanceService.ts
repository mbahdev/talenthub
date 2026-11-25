
import api from "./api";
import type { Freelance } from "../types/freelance";

const getById = async (id: number): Promise<Freelance> => {
    const response = await api.get<Freelance>(`/freelances/${id}`);
    return response.data;
};

const freelanceService = {
    getById,
};

export default freelanceService;
