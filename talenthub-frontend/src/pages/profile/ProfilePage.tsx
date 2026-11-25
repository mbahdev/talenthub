
import { useState } from "react";
import type { ReactNode } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import authService from "../../services/authService";
import freelanceService from "../../services/freelanceService";
import skillService from "../../services/skillService";
import api from "../../services/api";

import type { FreelanceForm, ClientForm } from "../../types/Profile";
import styles from "./ProfilePage.module.css";

// Éléments réutilisables

function InfoCard({ label, value, children }: {
    label: string;
    value?: string;
    children?: ReactNode;
}) {
    return <div className={styles.infoCard}>
        <span className={styles.label}>{label}</span>
        {children || <strong>{value}</strong>}
    </div>;
}

function ProfileHeader({ firstName, lastName, email, role }: {
    firstName: string;
    lastName: string;
    email: string;
    role: string;
}) {
    return <section className={styles.profileHeader}>
        <div className={styles.avatar}>
            {(firstName.charAt(0) + lastName.charAt(0)).toUpperCase()}
        </div>
        <div className={styles.identity}>
            <h2>{firstName} {lastName}</h2>
            <p>{email}</p>
            <span className={styles.roleBadge}>{role}</span>
        </div>
    </section>;
}

function PageHeader({ isEditing, onEdit }: {
    isEditing: boolean;
    onEdit: () => void;
}) {
    return <div className={styles.pageHeader}>
        <div>
            <h1>Mon profil</h1>
            <p>Gérez vos informations personnelles et professionnelles.</p>
        </div>
        {!isEditing && <button className={styles.primaryButton} onClick={onEdit}>
            Modifier mon profil
        </button>}
    </div>;
}

function ProfileField({ label, value, onChange, required = false, maxLength, type = "text" }: {
    label: string;
    value: string;
    onChange: (value: string) => void;
    required?: boolean;
    maxLength?: number;
    type?: string;
}) {
    return <label className={styles.field}>
        {label}
        <input
            type={type}
            value={value}
            required={required}
            maxLength={maxLength}
            min={type === "number" ? "0.01" : undefined}
            step={type === "number" ? "0.01" : undefined}
            onChange={event => onChange(event.target.value)}
        />
    </label>;
}

function FormActions({ onCancel, isPending }: {
    onCancel: () => void;
    isPending: boolean;
}) {
    return <div className={styles.formActions}>
        <button type="button" className={styles.secondaryButton} onClick={onCancel}>
            Annuler
        </button>
        <button type="submit" className={styles.primaryButton} disabled={isPending}>
            {isPending ? "Enregistrement..." : "Enregistrer"}
        </button>
    </div>;
}

// Informations personnelles communes aux deux profils
function ProfileInfo({ fullName, email, phone, location }: {
    fullName: string;
    email: string;
    phone?: string | null;
    location?: string | null;
})  {
    return <section className={styles.section}>
        <h3>Informations personnelles</h3>
        <div className={styles.infoGrid}>
            <InfoCard label="Nom complet" value={fullName} />
            <InfoCard label="Adresse e-mail" value={email} />
            <InfoCard label="Téléphone" value={phone || "Non renseigné"} />
            <InfoCard label="Localisation" value={location || "Non renseignée"} />
        </div>
    </section>;
}

function ProfilePage() {
    const queryClient = useQueryClient();

    const [isEditing, setIsEditing] = useState(false);
    const [freelanceForm, setFreelanceForm] = useState<FreelanceForm | null>(null);
    const [clientForm, setClientForm] = useState<ClientForm | null>(null);
    const [successMessage, setSuccessMessage] = useState("");

    // Récupération de l'utilisateur connecté
    const { data: currentUser, isLoading: loadingUser, isError: userError } = useQuery({
        queryKey: ["currentUser"],
        queryFn: authService.getCurrentUser,
    });

    // Récupération des profils
    const { data: freelance, isLoading: loadingProfile, isError: freelanceError } = useQuery({
        queryKey: ["freelanceProfile", currentUser?.id],
        queryFn: () => freelanceService.getById(currentUser!.id),
        enabled: currentUser?.role === "FREELANCE",
    });

    const { data: client, isLoading: loadingClient, isError: clientError } = useQuery({
        queryKey: ["clientProfile", currentUser?.id],
        queryFn: async () => (await api.get(`/clients/${currentUser!.id}`)).data,
        enabled: currentUser?.role === "CLIENT",
    });

    const { data: skills = [], isLoading: loadingSkills } = useQuery({
        queryKey: ["skills"],
        queryFn: skillService.getAll,
        enabled: currentUser?.role === "FREELANCE",
    });

    // Mise à jour du profil freelance
    const freelanceMutation = useMutation({
        mutationFn: (data: FreelanceForm) => api.put(`/freelances/${currentUser!.id}`, {
            ...data,
            dailyRate: Number(data.dailyRate),
        }),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ["freelanceProfile", currentUser?.id] });
            setIsEditing(false);
            setFreelanceForm(null);
            setSuccessMessage("Profil mis à jour avec succès.");
        },
    });

    // Mise à jour du profil client
    const clientMutation = useMutation({
        mutationFn: (data: ClientForm) => api.put(`/clients/${currentUser!.id}`, data),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ["clientProfile", currentUser?.id] });
            setIsEditing(false);
            setClientForm(null);
            setSuccessMessage("Profil mis à jour avec succès.");
        },
    });

    // Initialisation du formulaire
    const startEditing = () => {
        setSuccessMessage("");

        if (currentUser?.role === "FREELANCE" && freelance) {
            setFreelanceForm({
                firstName: freelance.firstName,
                lastName: freelance.lastName,
                phone: freelance.phone || "",
                bio: freelance.bio || "",
                dailyRate: String(freelance.dailyRate),
                location: freelance.location || "",
                skillIds: freelance.skillIds || [],
            });
        }

        if (currentUser?.role === "CLIENT" && client) {
            setClientForm({
                firstName: client.firstName,
                lastName: client.lastName,
                phone: client.phone || "",
                companyName: client.companyName || "",
                companyDescription: client.companyDescription || "",
                location: client.location || "",
            });
        }

        setIsEditing(true);
    };

    const cancelEditing = () => {
        setIsEditing(false);
        setFreelanceForm(null);
        setClientForm(null);
        freelanceMutation.reset();
        clientMutation.reset();
    };

    // Chargement et erreurs
    if (loadingUser || loadingProfile || loadingClient || loadingSkills) {
        return <p>Chargement du profil...</p>;
    }

    if (userError || freelanceError || clientError) {
        return <p>Erreur lors du chargement du profil.</p>;
    }

    // PROFIL FREELANCE
    if (currentUser?.role === "FREELANCE" && freelance) {
        const fullName = `${freelance.firstName} ${freelance.lastName}`;
        const associatedSkills = skills.filter(skill => freelance.skillIds?.includes(skill.id));

        return <div className={styles.container}>
            <PageHeader isEditing={isEditing} onEdit={startEditing} />

            {successMessage && <p className={styles.successMessage}>{successMessage}</p>}

            <ProfileHeader
                firstName={freelance.firstName}
                lastName={freelance.lastName}
                email={freelance.email}
                role="Freelance"
            />

            {isEditing && freelanceForm ? (
                <form className={styles.editForm} onSubmit={event => {
                    event.preventDefault();
                    freelanceMutation.mutate(freelanceForm);
                }}>
                    <section className={styles.section}>
                        <h3>Informations personnelles</h3>
                        <div className={styles.formGrid}>
                            <ProfileField label="Prénom" value={freelanceForm.firstName} required maxLength={100}
                                          onChange={value => setFreelanceForm({ ...freelanceForm, firstName: value })} />
                            <ProfileField label="Nom" value={freelanceForm.lastName} required maxLength={100}
                                          onChange={value => setFreelanceForm({ ...freelanceForm, lastName: value })} />
                            <ProfileField label="Téléphone" value={freelanceForm.phone} maxLength={20}
                                          onChange={value => setFreelanceForm({ ...freelanceForm, phone: value })} />
                            <ProfileField label="Localisation" value={freelanceForm.location} maxLength={150}
                                          onChange={value => setFreelanceForm({ ...freelanceForm, location: value })} />
                            <ProfileField label="Tarif journalier (€)" type="number" value={freelanceForm.dailyRate} required
                                          onChange={value => setFreelanceForm({ ...freelanceForm, dailyRate: value })} />
                        </div>

                        <label className={styles.field}>
                            Présentation
                            <textarea rows={5} maxLength={2000} value={freelanceForm.bio}
                                      onChange={event => setFreelanceForm({ ...freelanceForm, bio: event.target.value })} />
                        </label>
                    </section>

                    <section className={styles.section}>
                        <h3>Mes compétences</h3>
                        <div className={styles.skillsList}>
                            {skills.map(skill => (
                                <label key={skill.id} className={styles.skillOption}>
                                    <input
                                        type="checkbox"
                                        checked={freelanceForm.skillIds.includes(skill.id)}
                                        onChange={event => {
                                            const skillIds = event.target.checked
                                                ? [...freelanceForm.skillIds, skill.id]
                                                : freelanceForm.skillIds.filter(id => id !== skill.id);

                                            setFreelanceForm({ ...freelanceForm, skillIds });
                                        }}
                                    />
                                    {skill.name}
                                </label>
                            ))}
                        </div>
                    </section>

                    {freelanceMutation.isError && (
                        <p className={styles.errorMessage}>Erreur lors de la mise à jour du profil.</p>
                    )}

                    <FormActions onCancel={cancelEditing} isPending={freelanceMutation.isPending} />
                </form>
            ) : (
                <>
                    <ProfileInfo fullName={fullName} email={freelance.email}
                                 phone={freelance.phone} location={freelance.location} />

                    <section className={styles.section}>
                        <h3>Informations professionnelles</h3>
                        <InfoCard label="Tarif journalier">
                            <strong className={styles.dailyRate}>{freelance.dailyRate} € / jour</strong>
                        </InfoCard>
                        <InfoCard label="Présentation">
                            <p className={styles.bio}>{freelance.bio || "Aucune présentation renseignée."}</p>
                        </InfoCard>
                    </section>

                    <section className={styles.section}>
                        <h3>Mes compétences</h3>
                        <div className={styles.skillsList}>
                            {associatedSkills.length > 0 ? associatedSkills.map(skill => (
                                <span key={skill.id} className={styles.skillBadge}>{skill.name}</span>
                            )) : <p>Aucune compétence renseignée.</p>}
                        </div>
                    </section>
                </>
            )}
        </div>;
    }

    // PROFIL CLIENT
    if (currentUser?.role === "CLIENT" && client) {
        const fullName = `${client.firstName} ${client.lastName}`;

        return <div className={styles.container}>
            <PageHeader isEditing={isEditing} onEdit={startEditing} />

            {successMessage && <p className={styles.successMessage}>{successMessage}</p>}

            <ProfileHeader
                firstName={client.firstName}
                lastName={client.lastName}
                email={client.email}
                role="Client"
            />

            {isEditing && clientForm ? (
                <form className={styles.editForm} onSubmit={event => {
                    event.preventDefault();
                    clientMutation.mutate(clientForm);
                }}>
                    <section className={styles.section}>
                        <h3>Informations personnelles</h3>
                        <div className={styles.formGrid}>
                            <ProfileField label="Prénom" value={clientForm.firstName} required maxLength={100}
                                          onChange={value => setClientForm({ ...clientForm, firstName: value })} />
                            <ProfileField label="Nom" value={clientForm.lastName} required maxLength={100}
                                          onChange={value => setClientForm({ ...clientForm, lastName: value })} />
                            <ProfileField label="Téléphone" value={clientForm.phone} maxLength={20}
                                          onChange={value => setClientForm({ ...clientForm, phone: value })} />
                            <ProfileField label="Localisation" value={clientForm.location} maxLength={150}
                                          onChange={value => setClientForm({ ...clientForm, location: value })} />
                            <ProfileField label="Nom de l'entreprise" value={clientForm.companyName} required maxLength={150}
                                          onChange={value => setClientForm({ ...clientForm, companyName: value })} />
                        </div>

                        <label className={styles.field}>
                            Présentation de l'entreprise
                            <textarea rows={5} maxLength={2000} value={clientForm.companyDescription}
                                      onChange={event => setClientForm({ ...clientForm, companyDescription: event.target.value })} />
                        </label>
                    </section>

                    {clientMutation.isError && (
                        <p className={styles.errorMessage}>Erreur lors de la mise à jour du profil.</p>
                    )}

                    <FormActions onCancel={cancelEditing} isPending={clientMutation.isPending} />
                </form>
            ) : (
                <>
                    <ProfileInfo fullName={fullName} email={client.email}
                                 phone={client.phone} location={client.location} />

                    <section className={styles.section}>
                        <h3>Informations professionnelles</h3>
                        <InfoCard label="Entreprise" value={client.companyName || "Non renseignée"} />
                        <InfoCard label="Présentation">
                            <p className={styles.bio}>
                                {client.companyDescription || "Aucune présentation renseignée."}
                            </p>
                        </InfoCard>
                    </section>
                </>
            )}
        </div>;
    }

    return <p>Impossible de récupérer votre profil.</p>;
}

export default ProfilePage;
