
export interface Freelance {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    phone: string | null;
    bio: string;
    dailyRate: number;
    location: string;
    skillIds: number[];
}
