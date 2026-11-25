
import { Bell, Search, LogOut } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

import authService from "../../services/authService";
import freelanceService from "../../services/freelanceService";
import api from "../../services/api";

import styles from "./Header.module.css";

interface HeaderProfile {
    firstName?: string;
    lastName?: string;
}

export default function Header() {
    const navigate = useNavigate();

    // Récupération de l'utilisateur connecté
    const { data: currentUser } = useQuery({
        queryKey: ["headerCurrentUser"],
        queryFn: authService.getCurrentUser,
        retry: false,
    });

    // Récupération de son profil
    const { data: profile } = useQuery<HeaderProfile>({
        queryKey: ["headerProfile", currentUser?.id, currentUser?.role],
        queryFn: async () => {
            if (currentUser?.role === "FREELANCE") {
                const response = await freelanceService.getById(currentUser.id);

                return {
                    firstName: response.firstName,
                    lastName: response.lastName,
                };
            }

            const response = await api.get<HeaderProfile>(
                `/clients/${currentUser!.id}`
            );

            return response.data;
        },
        enabled: !!currentUser,
        retry: false,
    });

    const handleLogout = () => {
        authService.logout();
        navigate("/login", { replace: true });
    };

    const fullName = `${profile?.firstName ?? ""} ${profile?.lastName ?? ""}`.trim();

    const displayName = fullName || currentUser?.email || "Utilisateur";

    const initials = fullName
        ? fullName
            .split(" ")
            .filter(Boolean)
            .map(name => name[0])
            .join("")
            .substring(0, 2)
            .toUpperCase()
        : "U";

    const roleLabel =
        currentUser?.role === "FREELANCE"
            ? "Freelance"
            : currentUser?.role === "CLIENT"
                ? "Client"
                : "";

    return (
        <header className={styles.header}>
            <div className={styles.search}>
                <Search size={18} />
                <input type="text" placeholder="Rechercher..." />
            </div>

            <div className={styles.actions}>
                <button
                    className={styles.notification}
                    aria-label="Notifications"
                >
                    <Bell size={20} />
                </button>

                <div className={styles.user}>
                    <div className={styles.avatar}>{initials}</div>

                    <div>
                        <strong>{displayName}</strong>
                        <span>{roleLabel}</span>
                    </div>
                </div>

                <button
                    className={styles.logout}
                    onClick={handleLogout}
                    title="Se déconnecter"
                    aria-label="Se déconnecter"
                >
                    <LogOut size={19} />
                </button>
            </div>
        </header>
    );
}
