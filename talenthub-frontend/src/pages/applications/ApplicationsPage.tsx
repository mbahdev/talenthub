
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import applicationService from "../../services/applicationService";
import missionService from "../../services/missionService";
import authService from "../../services/authService";
import type { Application } from "../../types/application";
import type { Mission } from "../../types/mission";
import styles from "./ApllicationsPage.module.css"
import skillService from "../../services/skillService.ts";

const statusLabels: Record<Application["status"], string> = {
    PENDING: "En attente",
    ACCEPTED: "Acceptée",
    REJECTED: "Refusée",
    WITHDRAWN: "Retirée",
};

function ApplicationsPage() {
    const queryClient = useQueryClient();

    const { data: currentUser } = useQuery({
        queryKey: ["currentUser"],
        queryFn: authService.getCurrentUser,
    });

    const isFreelance = currentUser?.role === "FREELANCE";

    const {
        data: applications = [],
        isLoading: applicationsLoading,
        isError: applicationsError,
    } = useQuery({
        queryKey: ["applications"],
        queryFn: applicationService.getAll,
        enabled: isFreelance,
    });

    const {
        data: missions = [],
        isLoading: missionsLoading,
    } = useQuery({
        queryKey: ["missions"],
        queryFn: missionService.getAll,
        enabled: isFreelance,
    });

    const { data: skills = [] } = useQuery({
        queryKey: ["skills"],
        queryFn: skillService.getAll,
    });

    const withdrawMutation = useMutation({
        mutationFn: applicationService.withdraw,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["applications"] });
        },
    });

    const getMission = (missionId: number): Mission | undefined =>
        missions.find((mission) => mission.id === missionId);

    const handleWithdraw = (applicationId: number) => {
        if (window.confirm("Voulez-vous vraiment retirer cette candidature ?")) {
            withdrawMutation.mutate(applicationId);
        }
    };

    if (!isFreelance && currentUser) {
        return (
            <section className={styles.container}>
                <h1>Mes candidatures</h1>
                <p>Cette page est réservée aux freelances.</p>
            </section>
        );
    }

    if (applicationsLoading || missionsLoading || !currentUser) {
        return <p className={styles.message}>Chargement des candidatures...</p>;
    }

    if (applicationsError) {
        return (
            <p className={styles.error}>
                Erreur lors du chargement de vos candidatures.
            </p>
        );
    }

    return (
        <section className={styles.container}>
            <header className={styles.header}>
                <h1>Mes candidatures</h1>
                <p>Retrouvez ici vos candidatures aux missions.</p>
            </header>

            {applications.length === 0 ? (
                <div className={styles.emptyState}>
                    <h2>Aucune candidature</h2>
                    <p>Vous n'avez pas encore postulé à une mission.</p>
                    <Link to="/missions" className={styles.link}>
                        Découvrir les missions
                    </Link>
                </div>
            ) : (
                <div className={styles.list}>
                    {applications.map((application) => {
                        const mission = getMission(application.missionId);

                        return (
                            <article key={application.id} className={styles.card}>
                                <div className={styles.cardHeader}>
                                    <div>
                                        <h2>
                                            {mission?.title ??
                                                `Mission #${application.missionId}`}
                                        </h2>
                                        <p className={styles.location}>
                                            {mission?.location ?? "Lieu non disponible"}
                                        </p>
                                    </div>

                                    <span
                                        className={`${styles.status} ${
                                            styles[application.status.toLowerCase()]
                                        }`}
                                    >
                                        {statusLabels[application.status]}
                                    </span>
                                </div>

                                {mission && (
                                    <p className={styles.description}>
                                        {mission.description}
                                    </p>
                                )}

                                <div className={styles.skills}>
                                    {mission?.skillIds?.map(skillId => {
                                        const skill = skills.find(item => item.id === skillId);

                                        return skill ? (
                                            <span key={skillId}>{skill.name}</span>
                                        ) : null;
                                    })}
                                </div>

                                <div className={styles.details}>
                                    <span>
                                        Prix proposé :{" "}
                                        <strong>{application.proposedPrice} €</strong>
                                    </span>
                                    {mission && (
                                        <span>
                                            Budget : <strong>{mission.budget} €</strong>
                                        </span>
                                    )}
                                </div>

                                <div className={styles.actions}>
                                    <Link
                                        to={`/missions/${application.missionId}`}
                                        className={styles.link}
                                    >
                                        Voir la mission
                                    </Link>

                                    {application.status === "PENDING" && (
                                        <button
                                            type="button"
                                            className={styles.withdrawButton}
                                            disabled={withdrawMutation.isPending}
                                            onClick={() =>
                                                handleWithdraw(application.id)
                                            }
                                        >
                                            Retirer ma candidature
                                        </button>
                                    )}
                                </div>
                            </article>
                        );
                    })}
                </div>
            )}

            {withdrawMutation.isError && (
                <p className={styles.error}>
                    Impossible de retirer cette candidature.
                </p>
            )}
        </section>
    );
}

export default ApplicationsPage;
