
import { useQuery } from "@tanstack/react-query";
import {
    BriefcaseBusiness,
    FileText,
    CheckCircle2,
    Trophy,
    ArrowUpRight,
} from "lucide-react";
import { Link } from "react-router-dom";

import authService from "../../services/authService";
import freelanceService from "../../services/freelanceService";
import api from "../../services/api";

import missionService from "../../services/missionService";
import applicationService from "../../services/applicationService";

import styles from "./DashboardPage.module.css";

interface ProfileData {
    firstName?: string;
    lastName?: string;
    skillIds?: number[];
}

function DashboardPage() {

    // Utilisateur connecté
    const {
        data: currentUser,
        isLoading: currentUserLoading,
    } = useQuery({
        queryKey: ["currentUser"],
        queryFn: authService.getCurrentUser,
    });

    const isFreelance = currentUser?.role === "FREELANCE";
    const isClient = currentUser?.role === "CLIENT";

    // Profil de l'utilisateur connecté
    const {
        data: profile,
        isLoading: profileLoading,
    } = useQuery({
        queryKey: ["dashboard", "profile", currentUser?.id],
        queryFn: async (): Promise<ProfileData> => {

            if (currentUser?.role === "FREELANCE") {
                const freelance = await freelanceService.getById(
                    currentUser.id
                );

                return {
                    firstName: freelance.firstName,
                    lastName: freelance.lastName,
                    skillIds: freelance.skillIds ?? [],
                };
            }

            if (currentUser?.role === "CLIENT") {
                const response = await api.get<ProfileData>(
                    `/clients/${currentUser.id}`
                );

                return response.data;
            }

            throw new Error("Utilisateur non reconnu");
        },
        enabled: !!currentUser,
    });

    // Missions
    const {
        data: missions,
        isLoading: missionsLoading,
        isError: missionsError,
    } = useQuery({
        queryKey: ["dashboard", "missions"],
        queryFn: missionService.getAll,
    });

    // Candidatures : uniquement pour les freelances
    const {
        data: applications,
        isLoading: applicationsLoading,
        isError: applicationsError,
    } = useQuery({
        queryKey: ["dashboard", "applications"],
        queryFn: applicationService.getAll,
        enabled: isFreelance,
    });

    // États globaux
    const isLoading =
        currentUserLoading ||
        missionsLoading ||
        (isFreelance && applicationsLoading);

    const isError =
        missionsError ||
        (isFreelance && applicationsError);

    // Nom de l'utilisateur
    const firstName = profile?.firstName?.trim();

    // Missions appartenant au client connecté
    const clientMissions = (missions ?? []).filter(
        (mission) => mission.clientId === currentUser?.id
    );

    const clientOpenMissions = clientMissions.filter(
        (mission) => mission.status === "OPEN"
    );

    const clientCompletedMissions = clientMissions.filter(
        (mission) => mission.status === "CLOSED"
    );

    const clientCancelledMissions = clientMissions.filter(
        (mission) => mission.status === "CANCELED"
    );

    // Statistiques Freelance
    const availableMissions =
        (missions ?? []).filter(
            (mission) => mission.status === "OPEN"
        ).length;

    const acceptedApplications =
        (applications ?? []).filter(
            (application) => application.status === "ACCEPTED"
        );

    const completedMissions = new Set(
        acceptedApplications
            .filter((application) =>
                missions?.some(
                    (mission) =>
                        mission.id === application.missionId &&
                        mission.status === "CLOSED"
                )
            )
            .map((application) => application.missionId)
    ).size;

    // Missions recommandées
    const freelanceSkillIds = profile?.skillIds ?? [];

    const appliedMissionIds = new Set(
        (applications ?? []).map(
            (application) => application.missionId
        )
    );

    const recommendedMissions = (missions ?? [])
        .filter(
            (mission) =>
                isFreelance &&
                mission.status === "OPEN" &&
                !appliedMissionIds.has(mission.id) &&
                mission.skillIds.some((skillId) =>
                    freelanceSkillIds.includes(skillId)
                )
        )
        .map((mission) => ({
            ...mission,
            matchingSkillsCount: mission.skillIds.filter(
                (skillId) => freelanceSkillIds.includes(skillId)
            ).length,
        }))
        .sort(
            (a, b) =>
                b.matchingSkillsCount - a.matchingSkillsCount
        )
        .slice(0, 3);

    // Statistiques à afficher selon le rôle
    const stats = isFreelance
        ? [
            {
                label: "Missions disponibles",
                value: availableMissions,
                description: "Opportunités à découvrir",
                icon: BriefcaseBusiness,
                color: "orange",
                to: "/missions",
            },
            {
                label: "Mes candidatures",
                value: applications?.length ?? 0,
                description: "Candidatures envoyées",
                icon: FileText,
                color: "blue",
                to: "/applications",
            },
            {
                label: "Candidatures acceptées",
                value: acceptedApplications.length,
                description: "Candidatures retenues",
                icon: CheckCircle2,
                color: "green",
                to: "/applications",
            },
            {
                label: "Missions terminées",
                value: completedMissions,
                description: "Missions clôturées avec succès",
                icon: Trophy,
                color: "purple",
                to: "/applications",
            },
        ]
        : [
            {
                label: "Mes missions",
                value: clientMissions.length,
                description: "Missions créées",
                icon: BriefcaseBusiness,
                color: "orange",
                to: "/missions",
            },
            {
                label: "Missions ouvertes",
                value: clientOpenMissions.length,
                description: "Missions en recherche de freelance",
                icon: FileText,
                color: "blue",
                to: "/missions",
            },
            {
                label: "Missions terminées",
                value: clientCompletedMissions.length,
                description: "Missions clôturées",
                icon: CheckCircle2,
                color: "green",
                to: "/missions",
            },
            {
                label: "Missions annulées",
                value: clientCancelledMissions.length,
                description: "Missions annulées",
                icon: Trophy,
                color: "purple",
                to: "/missions",
            },
        ];

    // Libellés des statuts
    const statusLabels = {
        PENDING: "En attente",
        ACCEPTED: "Acceptée",
        REJECTED: "Refusée",
        WITHDRAWN: "Retirée",
    };

    return (
        <section className={styles.container}>

            {/* En-tête */}
            <header className={styles.pageHeader}>
                <div>
                    <span className={styles.eyebrow}>
                        VOTRE ESPACE
                    </span>

                    <h1>Dashboard</h1>

                    <p>
                        Bienvenue sur votre espace TalentHub.
                    </p>
                </div>
            </header>

            {/* Bienvenue personnalisée */}
            <section className={styles.welcomeCard}>
                <div className={styles.welcomeContent}>

                    <span className={styles.welcomeLabel}>
                        BIENVENUE SUR TALENTHUB
                    </span>

                    <h2>
                        {firstName
                            ? `Bonjour ${firstName}, ravi de vous retrouver !`
                            : "Donnez vie à vos projets professionnels."}
                    </h2>

                    <p>
                        {isFreelance
                            ? "Retrouvez en un coup d'œil vos candidatures et les opportunités qui correspondent à vos compétences."
                            : "Retrouvez en un coup d'œil vos missions et leur évolution."}
                    </p>

                </div>

                <div className={styles.welcomeIcon}>
                    <BriefcaseBusiness
                        size={64}
                        strokeWidth={1.3}
                    />
                </div>
            </section>

            {/* Statistiques */}
            <div className={styles.sectionHeader}>
                <div>
                    <h2>Vue d'ensemble</h2>
                    <p>Les chiffres clés de votre activité</p>
                </div>
            </div>

            {isLoading ? (
                <div className={styles.message}>
                    Chargement de vos statistiques...
                </div>
            ) : isError ? (
                <div className={`${styles.message} ${styles.error}`}>
                    Impossible de récupérer vos statistiques.
                    Vérifiez votre connexion au backend.
                </div>
            ) : (
                <div className={styles.statsGrid}>

                    {stats.map((stat) => {
                        const Icon = stat.icon;

                        return (
                            <Link
                                key={stat.label}
                                to={stat.to}
                                className={styles.statCard}
                            >
                                <div className={styles.cardTop}>
                                    <div
                                        className={`${styles.iconWrapper} ${styles[stat.color]}`}
                                    >
                                        <Icon size={22} />
                                    </div>

                                    <ArrowUpRight
                                        size={19}
                                        className={styles.cardArrow}
                                    />
                                </div>

                                <p className={styles.statValue}>
                                    {String(stat.value).padStart(2, "0")}
                                </p>

                                <h3>{stat.label}</h3>

                                <p className={styles.statDescription}>
                                    {stat.description}
                                </p>
                            </Link>
                        );
                    })}

                </div>
            )}

            {/* Mes missions : Client */}
            {isClient && (
                <section className={styles.contentSection}>

                    <div className={styles.sectionHeader}>
                        <div>
                            <h2>Mes missions</h2>
                            <p>
                                Retrouvez les missions que vous avez publiées
                            </p>
                        </div>

                        <Link
                            to="/missions"
                            className={styles.viewAllLink}
                        >
                            Voir toutes mes missions
                            <ArrowUpRight size={15} />
                        </Link>
                    </div>

                    {missionsLoading ? (
                        <div className={styles.message}>
                            Chargement de vos missions...
                        </div>
                    ) : missionsError ? (
                        <div className={`${styles.message} ${styles.error}`}>
                            Impossible de récupérer vos missions.
                        </div>
                    ) : clientMissions.length === 0 ? (
                        <div className={styles.emptyState}>
                            <BriefcaseBusiness size={28} />

                            <p>
                                Vous n'avez pas encore publié de mission.
                            </p>

                            <Link
                                to="/missions"
                                className={styles.viewAllLink}
                            >
                                Découvrir les missions
                            </Link>
                        </div>
                    ) : (
                        <div className={styles.recommendedGrid}>

                            {clientMissions.slice(0, 3).map((mission) => (
                                <article
                                    key={mission.id}
                                    className={styles.recommendedCard}
                                >
                                    <div className={styles.recommendedCardTop}>
                                        <span className={styles.matchBadge}>
                                            {mission.status === "OPEN"
                                                ? "Ouverte"
                                                : mission.status === "CLOSED"
                                                    ? "Terminée"
                                                    : mission.status === "CANCELED"
                                                        ? "Annulée"
                                                        : mission.status}
                                        </span>
                                    </div>

                                    <h3>{mission.title}</h3>

                                    <p className={styles.missionLocation}>
                                        {mission.location || "Localisation non précisée"}
                                        {" · "}
                                        {mission.remote ? "À distance" : "Sur site"}
                                    </p>

                                    <div className={styles.recommendedCardFooter}>
                                        <strong>
                                            {mission.budget.toLocaleString("fr-FR")} €
                                        </strong>

                                        <Link
                                            to="/missions"
                                            className={styles.missionLink}
                                        >
                                            Consulter
                                            <ArrowUpRight size={15} />
                                        </Link>
                                    </div>
                                </article>
                            ))}

                        </div>
                    )}
                </section>
            )}

            {/* Dernières candidatures : Freelance */}
            {isFreelance && (
                <section className={styles.contentSection}>

                    <div className={styles.sectionHeader}>
                        <div>
                            <h2>Mes candidatures</h2>
                            <p>
                                Suivez l'évolution de vos candidatures
                            </p>
                        </div>

                        <Link
                            to="/applications"
                            className={styles.viewAllLink}
                        >
                            Voir toutes les candidatures
                            <ArrowUpRight size={15} />
                        </Link>
                    </div>

                    {applicationsLoading ? (
                        <div className={styles.message}>
                            Chargement de vos candidatures...
                        </div>
                    ) : applicationsError ? (
                        <div className={`${styles.message} ${styles.error}`}>
                            Impossible de récupérer vos candidatures.
                        </div>
                    ) : !applications || applications.length === 0 ? (
                        <div className={styles.emptyState}>
                            <FileText size={28} />

                            <p>
                                Vous n'avez pas encore envoyé de candidature.
                            </p>

                            <Link
                                to="/missions"
                                className={styles.viewAllLink}
                            >
                                Découvrir les missions
                            </Link>
                        </div>
                    ) : (
                        <div className={styles.applicationsList}>

                            {applications.slice(0, 5).map((application) => {
                                const mission = missions?.find(
                                    (item) => item.id === application.missionId
                                );

                                return (
                                    <article
                                        key={application.id}
                                        className={styles.applicationCard}
                                    >
                                        <div className={styles.applicationIcon}>
                                            <FileText size={20} />
                                        </div>

                                        <div className={styles.applicationInfo}>
                                            <h3>
                                                {mission?.title ??
                                                    `Mission #${application.missionId}`}
                                            </h3>

                                            <p>
                                                Proposition :{" "}
                                                {application.proposedPrice.toLocaleString("fr-FR")} €
                                            </p>
                                        </div>

                                        <span
                                            className={`${styles.applicationStatus} ${
                                                styles[application.status.toLowerCase()]
                                            }`}
                                        >
                                            {statusLabels[application.status]}
                                        </span>
                                    </article>
                                );
                            })}

                        </div>
                    )}

                </section>
            )}

            {/* Missions recommandées : Freelance */}
            {isFreelance && (
                <section className={styles.contentSection}>

                    <div className={styles.sectionHeader}>
                        <div>
                            <h2>Missions recommandées</h2>
                            <p>
                                Des opportunités correspondant à vos compétences
                            </p>
                        </div>

                        <Link
                            to="/missions"
                            className={styles.viewAllLink}
                        >
                            Voir toutes les missions
                        </Link>
                    </div>

                    {profileLoading || missionsLoading || applicationsLoading ? (
                        <div className={styles.emptyState}>
                            Chargement de vos recommandations...
                        </div>
                    ) : !profile || freelanceSkillIds.length === 0 ? (
                        <div className={styles.emptyState}>
                            <BriefcaseBusiness size={26} />

                            <p>
                                Ajoutez des compétences à votre profil
                                pour recevoir des recommandations.
                            </p>
                        </div>
                    ) : recommendedMissions.length === 0 ? (
                        <div className={styles.emptyState}>
                            <BriefcaseBusiness size={26} />

                            <p>
                                Aucune mission ne correspond actuellement
                                à vos compétences.
                            </p>
                        </div>
                    ) : (
                        <div className={styles.recommendedGrid}>

                            {recommendedMissions.map((mission) => (
                                <article
                                    key={mission.id}
                                    className={styles.recommendedCard}
                                >
                                    <div className={styles.recommendedCardTop}>
                                        <span className={styles.matchBadge}>
                                            <CheckCircle2 size={14} />

                                            {mission.matchingSkillsCount} compétence(s)
                                            correspondante(s)
                                        </span>
                                    </div>

                                    <h3>{mission.title}</h3>

                                    <p className={styles.missionLocation}>
                                        {mission.location || "Localisation non précisée"}
                                        {" · "}
                                        {mission.remote ? "À distance" : "Sur site"}
                                    </p>

                                    <div className={styles.recommendedCardFooter}>
                                        <strong>
                                            {mission.budget.toLocaleString("fr-FR")} €
                                        </strong>

                                        <Link
                                            to="/missions"
                                            className={styles.missionLink}
                                        >
                                            Découvrir
                                            <ArrowUpRight size={15} />
                                        </Link>
                                    </div>
                                </article>
                            ))}

                        </div>
                    )}

                </section>
            )}

        </section>
    );
}

export default DashboardPage;
