import api from "./api";
import type { Favorite } from "../types/favorite";

const getByFreelance = async (freelanceId: number): Promise<Favorite[]> => {
    const response = await api.get<Favorite[]>(
        `/favorites/freelance/${freelanceId}`
    );
    return response.data;
};

const remove = async (
    freelanceId: number,
    missionId: number
): Promise<void> => {
    await api.delete(
        `/favorites/freelance/${freelanceId}/mission/${missionId}`
    );
};

const add = async (
    freelanceId: number,
    missionId: number
): Promise<void> => {
    await api.post("/favorites", {
        freelanceId,
        missionId,
    });
};

const favoriteService = {
    add,
    getByFreelance,
    remove,
};

export default favoriteService;