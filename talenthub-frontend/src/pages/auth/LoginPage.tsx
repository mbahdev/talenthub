
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import api from "../../services/api";
import authService from "../../services/authService";
import styles from "./LoginPage.module.css";

function LoginPage() {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        setError("");
        setLoading(true);

        try {
            // 1. Vérification des identifiants
            await api.get("/skills", {
                auth: {
                    username: email.trim(),
                    password,
                },
            });

            // 2. Création de l'en-tête HTTP Basic
            const credentials = `${email.trim()}:${password}`;
            const bytes = new TextEncoder().encode(credentials);
            const binary = Array.from(bytes, byte =>
                String.fromCharCode(byte)
            ).join("");

            const authorization = `Basic ${btoa(binary)}`;

            // 3. Sauvegarde et configuration de l'authentification
            sessionStorage.setItem("authorization", authorization);

            api.defaults.headers.common.Authorization = authorization;

            // 4. Récupération de l'utilisateur connecté
            const currentUser = await authService.getCurrentUser();

            // 5. Conservation de son identité
            sessionStorage.setItem(
                "currentUser",
                JSON.stringify(currentUser)
            );

            // 6. Redirection
            navigate("/missions");

        } catch (err: unknown) {
            delete api.defaults.headers.common.Authorization;

            sessionStorage.removeItem("authorization");
            sessionStorage.removeItem("currentUser");

            if (axios.isAxiosError(err)) {
                if (err.response?.status === 401) {
                    setError("Adresse e-mail ou mot de passe incorrect.");
                } else if (err.response?.status === 403) {
                    setError("Votre compte n'est pas autorisé à accéder à l'application.");
                } else if (!err.response) {
                    setError("Impossible de contacter le serveur. Vérifiez que le backend est démarré.");
                } else {
                    setError("Une erreur est survenue lors de la connexion.");
                }
            } else {
                setError("Une erreur inattendue est survenue.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className={styles.container}>
            <form className={styles.form} onSubmit={handleSubmit}>
                <h1>Bienvenue sur TalentHub</h1>
                <p>Connectez-vous à votre espace professionnel.</p>

                {error && (
                    <div className={styles.error}>
                        {error}
                    </div>
                )}

                <label htmlFor="email">Adresse e-mail</label>
                <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={event => setEmail(event.target.value)}
                    placeholder="votre@email.com"
                    required
                />

                <label htmlFor="password">Mot de passe</label>
                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={event => setPassword(event.target.value)}
                    placeholder="Votre mot de passe"
                    required
                />

                <button type="submit" disabled={loading}>
                    {loading ? "Connexion..." : "Se connecter"}
                </button>

                <div className={styles.registerLink}>
                    <span>Vous n'avez pas encore de compte ?</span>
                    {" "}
                    <Link to="/register">
                        Créer un compte
                    </Link>
                </div>
            </form>
        </div>
    );
}

export default LoginPage;
