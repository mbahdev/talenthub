
import { NavLink } from "react-router-dom";
import {
    LayoutDashboard,
    BriefcaseBusiness,
    FileText,
    Heart,
    UserRound,
} from "lucide-react";
import styles from "./Sidebar.module.css";
import type { CurrentUser } from "../../types/currentUser";

const navigation = [
    { label: "Dashboard", path: "/dashboard", icon: LayoutDashboard },
    { label: "Missions", path: "/missions", icon: BriefcaseBusiness },
    { label: "Mes candidatures", path: "/applications", icon: FileText, role: "FREELANCE" },
    { label: "Favoris", path: "/favorites", icon: Heart },
    { label: "Mon profil", path: "/profile", icon: UserRound },
];

export default function Sidebar() {
    const currentUser: CurrentUser | null = JSON.parse(
        sessionStorage.getItem("currentUser") || "null"
    );

    const filteredNavigation = navigation.filter(
        (item) => !item.role || item.role === currentUser?.role
    );

    return (
        <aside className={styles.sidebar}>
            <div className={styles.logo}>
                Talent<span>Hub</span>
            </div>

            <p className={styles.sectionTitle}>MENU PRINCIPAL</p>

            <nav className={styles.navigation}>
                {filteredNavigation.map(({ label, path, icon: Icon }) => (
                    <NavLink
                        key={path}
                        to={path}
                        end={path === "/"}
                        className={({ isActive }) =>
                            `${styles.navLink} ${isActive ? styles.active : ""}`
                        }
                    >
                        <Icon size={19} />
                        <span>{label}</span>
                    </NavLink>
                ))}
            </nav>
        </aside>
    );
}
