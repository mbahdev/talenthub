export interface Application {
    id: number;
    coverLetter: string;
    proposedPrice: number;
    status: "PENDING" | "ACCEPTED" | "REJECTED" | "WITHDRAWN";
    freelanceId: number;
    missionId: number;
}
