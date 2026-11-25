export interface FreelanceForm {
    firstName: string;
    lastName: string;
    phone: string;
    bio: string;
    dailyRate: string;
    location: string;
    skillIds: number[];
}

export interface ClientForm {
    firstName: string;
    lastName: string;
    phone: string;
    companyName: string;
    companyDescription: string;
    location: string;
}