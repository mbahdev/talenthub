export interface Mission {
    id: number;
    title: string;
    description: string;
    budget: number;
    location: string;
    remote: boolean;
    status: string;
    clientId: number;
    skillIds: number[];
}

export interface PublicMission {
    id: number;
    title: string;
    description: string;
    location: string;
    remote: boolean;
}

export interface MissionRequest {
    title: string;
    description: string;
    budget: number;
    location: string;
    remote: boolean;
    status: string;
    clientId: number;
    skillIds: number[];
}
