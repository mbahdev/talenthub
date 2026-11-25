
import api from "./api";
import type { Application } from "../types/application";

export interface CreateApplicationRequest {
    missionId: number;
    coverLetter: string;
    proposedPrice: number;
}

const getAll = async (): Promise<Application[]> => {
    const response = await api.get<Application[]>("/applications");
    return response.data;
};

const create = async (
    request: CreateApplicationRequest
): Promise<void> => {
    await api.post("/applications", request);
};

const getByMission
    = async (missionId: number): Promise<Application[]> => {
    const response = await api.get<Application[]>(
        `/applications/mission/${missionId}`
    );
    return response.data;
};

const accept = async (id: number): Promise<void> => {
    await api.patch(`/applications/${id}/accept`);
};

const reject = async (id: number): Promise<void> => {
    await api.patch(`/applications/${id}/reject`);
};

const withdraw = async (id: number): Promise<void> => {
    await api.patch(`/applications/${id}/withdraw`);
};

const applicationService = {
    getAll,
    create,
    getByMission,
    accept,
    reject,
    withdraw
};

export default applicationService;
