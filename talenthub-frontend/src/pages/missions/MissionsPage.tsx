
import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { Heart } from "lucide-react";

import authService from "../../services/authService";
import missionService from "../../services/missionService";
import favoriteService from "../../services/favoriteService";
import skillService from "../../services/skillService";

import type { Mission } from "../../types/mission";
import styles from "./MissionsPage.module.css";

type StatusFilter = "ALL" | "OPEN" | "CLOSED" | "CANCELED";
type RemoteFilter = "ALL" | "REMOTE" | "ONSITE";

function MissionsPage() {
    const [search, setSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState<StatusFilter>("ALL");
    const [remoteFilter, setRemoteFilter] = useState<RemoteFilter>("ALL");

    const authorization = sessionStorage.getItem("authorization");
    const queryClient = useQueryClient();

    const { data: currentUser, isLoading: isUserLoading } = useQuery({
        queryKey: ["currentUser"],
        queryFn: authService.getCurrentUser,
        enabled: Boolean(authorization),
        retry: false,
    });

    const isAuthenticated = Boolean(authorization && currentUser);
    const isClient = currentUser?.role === "CLIENT";
    const isFreelance = currentUser?.role === "FREELANCE";

    const { data: favorites = [] } = useQuery({
        queryKey: ["favorites", currentUser?.id],
        queryFn: () => favoriteService.getByFreelance(currentUser!.id),
        enabled: isFreelance,
    });

    const { data: skills = [] } = useQuery({
        queryKey: ["skills"],
        queryFn: skillService.getAll,
    });

    const favoriteMutation = useMutation({
        mutationFn: (missionId: number) =>
            favoriteService.add(currentUser!.id, missionId),
        onSuccess: () => queryClient.invalidateQueries({
            queryKey: ["favorites", currentUser?.id],
        }),
    });

    const removeFavoriteMutation = useMutation({
        mutationFn: (missionId: number) =>
            favoriteService.remove(currentUser!.id, missionId),
        onSuccess: () => queryClient.invalidateQueries({
            queryKey: ["favorites", currentUser?.id],
        }),
    });

    const {
        data: missions,
        isLoading: isMissionsLoading,
        isError,
        error,
    } = useQuery({
        queryKey: ["missions", currentUser?.role, currentUser?.id],
        queryFn: async () => {
            if (!isAuthenticated) return missionService.getPublicAll();

            const allMissions = await missionService.getAll();

            if (currentUser?.role === "CLIENT") {
                return allMissions.filter(
                    mission => mission.clientId === currentUser.id
                );
            }

            if (currentUser?.role === "FREELANCE") return allMissions;

            return [];
        },
        enabled: !authorization || (!isUserLoading && Boolean(currentUser)),
    });

    const isFavorite = (missionId: number) =>
        favorites.some(favorite => favorite.missionId === missionId);

    const handleFavorite = (missionId: number) => {
        if (isFavorite(missionId)) {
            removeFavoriteMutation.mutate(missionId);
        } else {
            favoriteMutation.mutate(missionId);
        }
    };

    const resetFilters = () => {
        setSearch("");
        setStatusFilter("ALL");
        setRemoteFilter("ALL");
    };

    if (isUserLoading || isMissionsLoading) {
        return <p className={styles.message}>Chargement des missions...</p>;
    }

    if (isError) {
        return (
            <p className={styles.error}>
                Impossible de récupérer les missions :{" "}
                {error instanceof Error ? error.message : "Erreur inconnue"}
            </p>
        );
    }

    const filteredMissions = (missions ?? []).filter(mission => {
        const matchesSearch =
            mission.title.toLowerCase().includes(search.toLowerCase()) ||
            mission.description.toLowerCase().includes(search.toLowerCase());

        const authenticatedMission = isAuthenticated ? mission as Mission : null;

        const matchesStatus =
            !authenticatedMission ||
            statusFilter === "ALL" ||
            authenticatedMission.status === statusFilter;

        const matchesRemote =
            remoteFilter === "ALL" ||
            (remoteFilter === "REMOTE" && mission.remote) ||
            (remoteFilter === "ONSITE" && !mission.remote);

        return matchesSearch && matchesStatus && matchesRemote;
    });

    return (
        <section className={styles.container}>
            <div className={styles.heading}>
                <div>
                    <h1>{isClient ? "Mes missions" : "Explorer les missions"}</h1>
                    <p>
                        {isClient
                            ? "Retrouvez et gérez les missions que vous avez publiées."
                            : "Découvrez les opportunités disponibles sur TalentHub."}
                    </p>
                </div>
                <span className={styles.missionCount}>
                    {filteredMissions.length} mission(s)
                </span>
            </div>

            <div className={styles.filters}>
                <input
                    type="text"
                    className={styles.searchInput}
                    placeholder="Rechercher une mission..."
                    value={search}
                    onChange={event => setSearch(event.target.value)}
                />

                {isAuthenticated && (
                    <select
                        className={styles.filterSelect}
                        value={statusFilter}
                        onChange={event => setStatusFilter(event.target.value as StatusFilter)}
                    >
                        <option value="ALL">Tous les statuts</option>
                        <option value="OPEN">Ouvertes</option>
                        <option value="CLOSED">Terminées</option>
                        <option value="CANCELED">Annulées</option>
                    </select>
                )}

                <select
                    className={styles.filterSelect}
                    value={remoteFilter}
                    onChange={event => setRemoteFilter(event.target.value as RemoteFilter)}
                >
                    <option value="ALL">Tous les lieux</option>
                    <option value="REMOTE">Télétravail</option>
                    <option value="ONSITE">Sur site</option>
                </select>

                <button type="button" className={styles.resetButton} onClick={resetFilters}>
                    Réinitialiser
                </button>
            </div>

            {filteredMissions.length === 0 ? (
                <div className={styles.emptyState}>
                    <h2>Aucune mission trouvée</h2>
                    <p>
                        {missions?.length
                            ? "Aucune mission ne correspond à vos critères."
                            : isClient
                                ? "Vous n'avez publié aucune mission pour le moment."
                                : "Aucune mission disponible pour le moment."}
                    </p>
                    {(missions?.length ?? 0) > 0 && (
                        <button type="button" className={styles.resetButton} onClick={resetFilters}>
                            Effacer les filtres
                        </button>
                    )}
                </div>
            ) : (
                <div className={styles.grid}>
                    {filteredMissions.map(mission => {
                        const authenticatedMission = isAuthenticated ? mission as Mission : null;
                        const status = authenticatedMission?.status;

                        const statusClass = {
                            OPEN: styles.statusOpen,
                            CLOSED: styles.statusClosed,
                            CANCELED: styles.statusCanceled,
                        }[status ?? ""] ?? "";

                        const cardClass = {
                            CLOSED: styles.cardClosed,
                            CANCELED: styles.cardCanceled,
                        }[status ?? ""] ?? "";

                        const missionIsFavorite = isFavorite(mission.id);

                        const missionSkills = authenticatedMission?.skillIds
                            .map(skillId => skills.find(skill => skill.id === skillId))
                            .filter(skill => skill !== undefined) ?? [];

                        return (
                            <article key={mission.id} className={`${styles.card} ${cardClass}`}>
                                <div className={styles.cardTop}>
                                    <div className={styles.cardMeta}>
                                        {authenticatedMission && (
                                            <span className={`${styles.status} ${statusClass}`}>
                                                {authenticatedMission.status}
                                            </span>
                                        )}
                                        {authenticatedMission && (
                                            <span className={styles.budget}>
                                                {authenticatedMission.budget.toLocaleString("fr-FR")} €
                                            </span>
                                        )}
                                    </div>

                                    {isFreelance && (
                                        <button
                                            type="button"
                                            className={`${styles.favoriteButton} ${
                                                missionIsFavorite ? styles.favoriteActive : ""
                                            }`}
                                            onClick={() => handleFavorite(mission.id)}
                                            disabled={
                                                favoriteMutation.isPending ||
                                                removeFavoriteMutation.isPending
                                            }
                                            aria-label={missionIsFavorite
                                                ? "Retirer des favoris"
                                                : "Ajouter aux favoris"}
                                            title={missionIsFavorite
                                                ? "Retirer des favoris"
                                                : "Ajouter aux favoris"}
                                        >
                                            <Heart
                                                size={20}
                                                fill={missionIsFavorite ? "currentColor" : "none"}
                                            />
                                        </button>
                                    )}
                                </div>

                                <h2 className={styles.cardTitle}>{mission.title}</h2>
                                <p className={styles.description}>{mission.description}</p>

                                <div className={styles.details}>
                                    <span>{mission.location}</span>
                                    <span className={styles.separator}>•</span>
                                    <span>{mission.remote ? "Télétravail" : "Sur site"}</span>
                                </div>

                                {missionSkills.length > 0 && (
                                    <div className={styles.skills}>
                                        {missionSkills.map(skill => (
                                            <span key={skill.id}>{skill.name}</span>
                                        ))}
                                    </div>
                                )}

                                <div className={styles.cardFooter}>
                                    <Link to={`/missions/${mission.id}`} className={styles.detailsLink}>
                                        Voir les détails
                                        <span aria-hidden="true"> →</span>
                                    </Link>
                                </div>
                            </article>
                        );
                    })}
                </div>
            )}

            {(favoriteMutation.isError || removeFavoriteMutation.isError) && (
                <p className={styles.error}>
                    Impossible de mettre à jour vos favoris.
                </p>
            )}
        </section>
    );
}

export default MissionsPage;
