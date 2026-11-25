
export type AccountType = "FREELANCE" | "CLIENT";

export interface RegisterForm {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phone: string;
    location: string;
    bio: string;
    dailyRate: string;
    skillIds: number[];
    companyName: string;
    companyDescription: string;
}

export interface FreelanceRegistrationRequest {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phone: string;
    location: string;
    bio: string;
    dailyRate: number | null;
    skillIds: number[];
}

export interface ClientRegistrationRequest {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phone: string;
    location: string;
    companyName: string;
    companyDescription: string;
}