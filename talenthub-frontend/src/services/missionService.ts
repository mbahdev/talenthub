
import api from "./api";
import type { Mission, PublicMission, MissionRequest } from "../types/mission";

const getPublicAll = async (): Promise<PublicMission[]> => {
    const response = await api.get<PublicMission[]>("/missions/public");
    return response.data;
};

const getAll = async (): Promise<Mission[]> => {
    const statuses = ["OPEN", "CLOSED", "CANCELED"];

    const responses = await Promise.all(
        statuses.map(status =>
            api.get<Mission[]>("/missions", {
                params: { status },
            })
        )
    );

    return responses.flatMap(response => response.data);
};

const getById = async (id: number): Promise<Mission> => {
    const response = await api.get<Mission>(`/missions/${id}`);
    return response.data;
};

const deleteMission = async (id: number): Promise<void> => {
    await api.delete(`/missions/${id}`);
};

const update = async (
    id: number,
    request: MissionRequest
): Promise<Mission> => {
    const response = await api.put<Mission>(`/missions/${id}`, request);
    return response.data;
};

const missionService = {
    getPublicAll,
    getAll,
    getById,
    deleteMission,
    update
};

export default missionService;
