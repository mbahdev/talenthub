import { useState, type FormEvent } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Link, useNavigate, useParams } from "react-router-dom";

import authService from "../../services/authService";
import missionService from "../../services/missionService";
import applicationService from "../../services/applicationService";

import type { Mission, MissionRequest } from "../../types/mission";

import styles from "./MissionDetailsPage.module.css";
import skillService from "../../services/skillService.ts";

function MissionDetailsPage() {
    const { id } = useParams();
    const missionId = Number(id);

    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const [coverLetter, setCoverLetter] = useState("");
    const [proposedPrice, setProposedPrice] = useState("");

    const [isEditing, setIsEditing] = useState(false);
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [budget, setBudget] = useState("");
    const [location, setLocation] = useState("");
    const [remote, setRemote] = useState(false);
    const [skillIds, setSkillIds] = useState("");

    const authorization = sessionStorage.getItem("authorization");

    const { data: currentUser, isLoading: isUserLoading } = useQuery({
        queryKey: ["currentUser"],
        queryFn: authService.getCurrentUser,
        enabled: Boolean(authorization),
        retry: false,
    });

    const isAuthenticated = Boolean(authorization && currentUser);
    const isFreelance = currentUser?.role === "FREELANCE";
    const isClient = currentUser?.role === "CLIENT";

    const {
        data: mission,
        isLoading,
        isError,
    } = useQuery({
        queryKey: ["mission", missionId],
        queryFn: async () => {
            if (!isAuthenticated) {
                const publicMissions = await missionService.getPublicAll();
                return publicMissions.find(item => item.id === missionId) ?? null;
            }

            return missionService.getById(missionId);
        },
        enabled:
            Number.isInteger(missionId) &&
            missionId > 0 &&
            (!authorization || (!isUserLoading && Boolean(currentUser))),
        retry: false,
    });

    const authenticatedMission =
        mission && "budget" in mission ? mission as Mission : null;

    const isOwner =
        isClient &&
        authenticatedMission !== null &&
        authenticatedMission !== undefined &&
        authenticatedMission.clientId === currentUser?.id;

    const { data: skills = [] } = useQuery({
        queryKey: ["skills"],
        queryFn: skillService.getAll,
    });

    const { data: applications = [] } = useQuery({
        queryKey: ["applications"],
        queryFn: applicationService.getAll,
        enabled: isFreelance,
        retry: false,
    });

    const { data: receivedApplications = [], isLoading: isApplicationsLoading } =
        useQuery({
            queryKey: ["mission-applications", missionId],
            queryFn: () => applicationService.getByMission(missionId),
            enabled: Boolean(isOwner),
            retry: false,
        });

    const hasApplied = applications.some(
        application => application.missionId === missionId
    );

    const applicationMutation = useMutation({
        mutationFn: applicationService.create,
        onSuccess: async () => {
            await queryClient.invalidateQueries({
                queryKey: ["applications"],
            });

            setCoverLetter("");
            setProposedPrice("");
        },
    });

    const deleteMutation = useMutation({
        mutationFn: () => missionService.deleteMission(missionId),
        onSuccess: async () => {
            await queryClient.invalidateQueries({
                queryKey: ["missions"],
            });

            navigate("/missions");
        },
    });

    const updateMutation = useMutation({
        mutationFn: (request: MissionRequest) =>
            missionService.update(missionId, request),
        onSuccess: async () => {
            await queryClient.invalidateQueries({
                queryKey: ["mission", missionId],
            });

            await queryClient.invalidateQueries({
                queryKey: ["missions"],
            });

            setIsEditing(false);
        },
    });

    const acceptMutation = useMutation({
        mutationFn: applicationService.accept,
        onSuccess: async () => {
            await queryClient.invalidateQueries({
                queryKey: ["mission-applications", missionId],
            });

            await queryClient.invalidateQueries({
                queryKey: ["missions"],
            });
        },
    });

    const rejectMutation = useMutation({
        mutationFn: applicationService.reject,
        onSuccess: async () => {
            await queryClient.invalidateQueries({
                queryKey: ["mission-applications", missionId],
            });
        },
    });

    const handleApplication = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        applicationMutation.mutate({
            missionId,
            coverLetter,
            proposedPrice: Number(proposedPrice),
        });
    };

    const handleDelete = () => {
        if (window.confirm("Voulez-vous vraiment supprimer cette mission ?")) {
            deleteMutation.mutate();
        }
    };

    const handleStartEditing = () => {
        if (!authenticatedMission) {
            return;
        }

        setTitle(authenticatedMission.title);
        setDescription(authenticatedMission.description);
        setBudget(String(authenticatedMission.budget));
        setLocation(authenticatedMission.location);
        setRemote(authenticatedMission.remote);
        setSkillIds(authenticatedMission.skillIds.join(", "));

        setIsEditing(true);
    };

    const handleUpdate = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!authenticatedMission) {
            return;
        }

        const request: MissionRequest = {
            title,
            description,
            budget: Number(budget),
            location,
            remote,
            status: authenticatedMission.status,
            clientId: authenticatedMission.clientId,
            skillIds: skillIds
                .split(",")
                .map(value => Number(value.trim()))
                .filter(value => Number.isInteger(value) && value > 0),
        };

        updateMutation.mutate(request);
    };

    if (isUserLoading || isLoading) {
        return <p>Chargement de la mission...</p>;
    }

    if (isError || !mission) {
        return (
            <section className={styles.container}>
                <p className={styles.error}>
                    Impossible de récupérer cette mission.
                </p>
                <Link to="/missions">Retour aux missions</Link>
            </section>
        );
    }

    const statusClass = {
        OPEN: styles.statusOpen,
        CLOSED: styles.statusClosed,
        CANCELED: styles.statusCanceled,
    }[authenticatedMission?.status ?? ""] ?? "";

    return (
        <section className={styles.container}>
            <Link to="/missions" className={styles.backLink}>
                ← Retour aux missions
            </Link>

            <article className={styles.card}>
                <div className={styles.header}>
                    <div>
                        <h1>{mission.title}</h1>
                        <p className={styles.location}>{mission.location}</p>
                    </div>

                    {authenticatedMission && (
                        <span className={`${styles.status} ${statusClass}`}>
                            {authenticatedMission.status}
                        </span>
                    )}
                </div>

                <div className={styles.information}>
                    {authenticatedMission && (
                        <div>
                            <span>Budget</span>
                            <strong>
                                {authenticatedMission.budget.toLocaleString("fr-FR")} €
                            </strong>
                        </div>
                    )}

                    <div>
                        <span>Lieu de travail</span>
                        <strong>
                            {mission.remote ? "Télétravail" : "Sur site"}
                        </strong>
                    </div>
                </div>

                <div className={styles.descriptionSection}>
                    <h2>Description de la mission</h2>
                    <p>{mission.description}</p>
                </div>

                {authenticatedMission && (
                    <div className={styles.skills}>
                        <h2>Compétences requises</h2>
                        <div>
                            {authenticatedMission.skillIds.map(skillId => {
                                const skill = skills.find(item => item.id === skillId);
                                return skill && <span key={skillId}>{skill.name}</span>;
                            })}
                        </div>
                    </div>
                )}

                {!isAuthenticated && (
                    <div className={styles.actionSection}>
                        <p>
                            Connectez-vous pour accéder aux détails
                            et candidater à cette mission.
                        </p>
                        <Link to="/login" className={styles.primaryButton}>
                            Se connecter
                        </Link>
                    </div>
                )}

                {isFreelance && authenticatedMission && (
                    <div className={styles.actionSection}>
                        <h2>Candidater à cette mission</h2>

                        {authenticatedMission.status !== "OPEN" ? (
                            <p>Cette mission n'accepte plus de candidatures.</p>
                        ) : hasApplied ? (
                            <p>Vous avez déjà candidaté à cette mission.</p>
                        ) : (
                            <form onSubmit={handleApplication}>
                                <label htmlFor="coverLetter">
                                    Lettre de motivation
                                </label>
                                <textarea
                                    id="coverLetter"
                                    value={coverLetter}
                                    onChange={event => setCoverLetter(event.target.value)}
                                    required
                                    minLength={10}
                                    rows={6}
                                    placeholder="Présentez votre motivation et votre expérience..."
                                />

                                <label htmlFor="proposedPrice">
                                    Prix proposé (€)
                                </label>
                                <input
                                    id="proposedPrice"
                                    type="number"
                                    min="0.01"
                                    step="0.01"
                                    value={proposedPrice}
                                    onChange={event => setProposedPrice(event.target.value)}
                                    required
                                />

                                {applicationMutation.isError && (
                                    <p className={styles.error}>
                                        Impossible d'envoyer votre candidature.
                                    </p>
                                )}

                                {applicationMutation.isSuccess && (
                                    <p className={styles.success}>
                                        Votre candidature a bien été envoyée.
                                    </p>
                                )}

                                <button
                                    type="submit"
                                    className={styles.primaryButton}
                                    disabled={applicationMutation.isPending}
                                >
                                    {applicationMutation.isPending
                                        ? "Envoi..."
                                        : "Envoyer ma candidature"}
                                </button>
                            </form>
                        )}
                    </div>
                )}

                {isOwner && authenticatedMission && (
                    <div className={styles.actionSection}>
                        <h2>Gestion de la mission</h2>

                        {!isEditing ? (
                            <>
                                <p>Vous êtes propriétaire de cette mission.</p>

                                <button
                                    type="button"
                                    className={styles.primaryButton}
                                    onClick={handleStartEditing}
                                >
                                    Modifier la mission
                                </button>

                                <button
                                    type="button"
                                    className={styles.deleteButton}
                                    onClick={handleDelete}
                                    disabled={deleteMutation.isPending}
                                >
                                    {deleteMutation.isPending
                                        ? "Suppression..."
                                        : "Supprimer la mission"}
                                </button>
                            </>
                        ) : (
                            <form onSubmit={handleUpdate}>
                                <label htmlFor="editTitle">Titre</label>
                                <input
                                    id="editTitle"
                                    value={title}
                                    onChange={event => setTitle(event.target.value)}
                                    required
                                />

                                <label htmlFor="editDescription">Description</label>
                                <textarea
                                    id="editDescription"
                                    value={description}
                                    onChange={event => setDescription(event.target.value)}
                                    required
                                    rows={5}
                                />

                                <label htmlFor="editBudget">Budget (€)</label>
                                <input
                                    id="editBudget"
                                    type="number"
                                    min="0.01"
                                    step="0.01"
                                    value={budget}
                                    onChange={event => setBudget(event.target.value)}
                                    required
                                />

                                <label htmlFor="editLocation">Lieu</label>
                                <input
                                    id="editLocation"
                                    value={location}
                                    onChange={event => setLocation(event.target.value)}
                                    required
                                />

                                <label>
                                    <input
                                        type="checkbox"
                                        checked={remote}
                                        onChange={event => setRemote(event.target.checked)}
                                    />
                                    Télétravail
                                </label>

                                <label htmlFor="editSkills">
                                    Identifiants des compétences (séparés par des virgules)
                                </label>
                                <input
                                    id="editSkills"
                                    value={skillIds}
                                    onChange={event => setSkillIds(event.target.value)}
                                />

                                {updateMutation.isError && (
                                    <p className={styles.error}>
                                        Impossible de modifier la mission.
                                    </p>
                                )}

                                <button
                                    type="submit"
                                    className={styles.primaryButton}
                                    disabled={updateMutation.isPending}
                                >
                                    {updateMutation.isPending
                                        ? "Enregistrement..."
                                        : "Enregistrer les modifications"}
                                </button>

                                <button
                                    type="button"
                                    className={styles.secondaryButton}
                                    onClick={() => setIsEditing(false)}
                                >
                                    Annuler
                                </button>
                            </form>
                        )}

                        {deleteMutation.isError && (
                            <p className={styles.error}>
                                Impossible de supprimer cette mission.
                            </p>
                        )}
                    </div>
                )}

                {isOwner && (
                    <div className={styles.actionSection}>
                        <h2>Candidatures reçues</h2>

                        {isApplicationsLoading ? (
                            <p>Chargement des candidatures...</p>
                        ) : receivedApplications.length === 0 ? (
                            <p>Aucune candidature reçue pour le moment.</p>
                        ) : (
                            <div className={styles.applicationList}>
                                {receivedApplications.map(application => (
                                    <article
                                        key={application.id}
                                        className={styles.applicationCard}
                                    >
                                        <div>
                                            <strong>
                                                Candidat #{application.freelanceId}
                                            </strong>
                                            <span>
                                                {application.proposedPrice.toLocaleString("fr-FR")} €
                                            </span>
                                            <span>
                                                Statut : {application.status}
                                            </span>
                                        </div>

                                        <p>{application.coverLetter}</p>

                                        {application.status === "PENDING" && (
                                            <div className={styles.applicationActions}>
                                                <button
                                                    type="button"
                                                    className={styles.primaryButton}
                                                    disabled={
                                                        acceptMutation.isPending ||
                                                        rejectMutation.isPending
                                                    }
                                                    onClick={() =>
                                                        acceptMutation.mutate(application.id)
                                                    }
                                                >
                                                    Accepter
                                                </button>

                                                <button
                                                    type="button"
                                                    className={styles.deleteButton}
                                                    disabled={
                                                        acceptMutation.isPending ||
                                                        rejectMutation.isPending
                                                    }
                                                    onClick={() =>
                                                        rejectMutation.mutate(application.id)
                                                    }
                                                >
                                                    Refuser
                                                </button>
                                            </div>
                                        )}
                                    </article>
                                ))}
                            </div>
                        )}

                        {(acceptMutation.isError || rejectMutation.isError) && (
                            <p className={styles.error}>
                                Impossible de traiter cette candidature.
                            </p>
                        )}
                    </div>
                )}
            </article>
        </section>
    );
}

export default MissionDetailsPage;