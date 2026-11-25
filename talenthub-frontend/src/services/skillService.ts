import api from "./api";
import type { Skill } from "../types/skill";

const getAll = async (): Promise<Skill[]> => {
    const response = await api.get<Skill[]>("/skills");
    return response.data;
};

const skillService = {
    getAll,
};

export default skillService;