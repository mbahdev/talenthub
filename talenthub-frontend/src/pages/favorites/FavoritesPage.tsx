
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import authService from "../../services/authService";
import favoriteService from "../../services/favoriteService";
import missionService from "../../services/missionService";
import styles from "./FavoritesPage.module.css";

function FavoritesPage() {
    const queryClient = useQueryClient();

    const { data: currentUser } = useQuery({
        queryKey: ["currentUser"],
        queryFn: authService.getCurrentUser,
    });

    const isFreelance = currentUser?.role === "FREELANCE";

    const {
        data: favorites = [],
        isLoading: favoritesLoading,
        isError: favoritesError,
    } = useQuery({
        queryKey: ["favorites", currentUser?.id],
        queryFn: () => favoriteService.getByFreelance(currentUser!.id),
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

    const removeMutation = useMutation({
        mutationFn: (missionId: number) =>
            favoriteService.remove(currentUser!.id, missionId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["favorites"] });
        },
    });

    const handleRemove = (missionId: number) => {
        if (window.confirm("Retirer cette mission de vos favoris ?")) {
            removeMutation.mutate(missionId);
        }
    };

    if (!currentUser || favoritesLoading || missionsLoading) {
        return <p className={styles.message}>Chargement des favoris...</p>;
    }

    if (!isFreelance) {
        return (
            <section className={styles.container}>
                <h1>Mes favoris</h1>
                <p>Cette fonctionnalité est réservée aux freelances.</p>
            </section>
        );
    }

    if (favoritesError) {
        return (
            <p className={styles.error}>
                Erreur lors du chargement de vos favoris.
            </p>
        );
    }

    const favoriteMissions = favorites
        .map((favorite) =>
            missions.find((mission) => mission.id === favorite.missionId)
        )
        .filter((mission) => mission !== undefined);

    return (
        <section className={styles.container}>
            <header className={styles.header}>
                <h1>Mes favoris</h1>
                <p>Retrouvez ici les missions que vous avez ajoutées à vos favoris.</p>
            </header>

            {favorites.length === 0 ? (
                <div className={styles.emptyState}>
                    <h2>Aucun favori</h2>
                    <p>Vous n'avez pas encore ajouté de mission à vos favoris.</p>
                    <Link to="/missions" className={styles.link}>
                        Découvrir les missions
                    </Link>
                </div>
            ) : (
                <div className={styles.grid}>
                    {favoriteMissions.map((mission) => (
                        <article key={mission.id} className={styles.card}>
                            <h2>{mission.title}</h2>

                            <p className={styles.description}>
                                {mission.description}
                            </p>

                            <div className={styles.details}>
                                <span>{mission.location}</span>
                                <span>
                                    {mission.remote ? "À distance" : "Sur site"}
                                </span>
                                <strong>{mission.budget} €</strong>
                            </div>

                            <div className={styles.actions}>
                                <Link
                                    to={`/missions/${mission.id}`}
                                    className={styles.link}
                                >
                                    Voir la mission
                                </Link>

                                <button
                                    type="button"
                                    className={styles.removeButton}
                                    disabled={removeMutation.isPending}
                                    onClick={() => handleRemove(mission.id)}
                                >
                                    Retirer des favoris
                                </button>
                            </div>
                        </article>
                    ))}
                </div>
            )}

            {removeMutation.isError && (
                <p className={styles.error}>
                    Impossible de retirer cette mission des favoris.
                </p>
            )}
        </section>
    );
}

export default FavoritesPage;
