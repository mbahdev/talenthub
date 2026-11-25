
import { useEffect, useState } from "react";
import type { ChangeEvent, ComponentProps } from "react";
import { Link, useNavigate } from "react-router-dom";
import { BriefcaseBusiness, Building2 } from "lucide-react";

import authService from "../../services/authService";

import skillService from "../../services/skillService";

import type { Skill } from "../../types/skill";
import type {
    AccountType,
    RegisterForm,
    FreelanceRegistrationRequest,
    ClientRegistrationRequest,
} from "../../types/register";

import styles from "./RegisterPage.module.css";

type FormSubmitEvent = Parameters<
    NonNullable<ComponentProps<"form">["onSubmit"]>
>[0];

const initialForm: RegisterForm = {
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    phone: "",
    location: "",
    bio: "",
    dailyRate: "",
    skillIds: [],
    companyName: "",
    companyDescription: "",
};

export default function RegisterPage() {
    const navigate = useNavigate();

    const [accountType, setAccountType] = useState<AccountType | null>(null);
    const [form, setForm] = useState<RegisterForm>(initialForm);
    const [skills, setSkills] = useState<Skill[]>([]);

    const [loadingSkills, setLoadingSkills] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        if (accountType !== "FREELANCE") {
            return;
        }

        const fetchSkills = async () => {
            try {
                setLoadingSkills(true);
                setError("");

                const data = await skillService.getAll();
                setSkills(data);
            } catch {
                setError("Impossible de charger les compétences.");
            } finally {
                setLoadingSkills(false);
            }
        };

        fetchSkills();
    }, [accountType]);

    const handleChange = (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = event.target;

        setForm((previous) => ({
            ...previous,
            [name]: value,
        }));
    };

    const handleSkillChange = (skillId: number) => {
        setForm((previous) => ({
            ...previous,
            skillIds: previous.skillIds.includes(skillId)
                ? previous.skillIds.filter((id) => id !== skillId)
                : [...previous.skillIds, skillId],
        }));
    };

    const handleSubmit = async (event: FormSubmitEvent) => {
        event.preventDefault();

        if (!accountType) {
            return;
        }

        setSubmitting(true);
        setError("");

        try {
            if (accountType === "FREELANCE") {
                const request: FreelanceRegistrationRequest = {
                    email: form.email,
                    password: form.password,
                    firstName: form.firstName,
                    lastName: form.lastName,
                    phone: form.phone,
                    location: form.location,
                    bio: form.bio,
                    dailyRate: form.dailyRate
                        ? Number(form.dailyRate)
                        : null,
                    skillIds: form.skillIds,
                };

                await authService.registerFreelance(request);
            } else {
                const request: ClientRegistrationRequest = {
                    email: form.email,
                    password: form.password,
                    firstName: form.firstName,
                    lastName: form.lastName,
                    phone: form.phone,
                    location: form.location,
                    companyName: form.companyName,
                    companyDescription: form.companyDescription,
                };

                await authService.registerClient(request);
            }

            navigate("/login", {
                state: {
                    registrationSuccess: true,
                    email: form.email,
                },
            });
        } catch (err) {
            const status = (
                err as { response?: { status?: number } }
            ).response?.status;

            if (status === 409) {
                setError("Cette adresse email est déjà utilisée.");
            } else if (status === 400) {
                setError("Veuillez vérifier les informations saisies.");
            } else if (!status) {
                setError("Impossible de contacter le serveur.");
            } else {
                setError("Une erreur est survenue lors de l'inscription.");
            }
        } finally {
            setSubmitting(false);
        }
    };

    const selectAccountType = (type: AccountType) => {
        setAccountType(type);
        setError("");
    };

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <Link to="/login" className={styles.backLink}>
                    ← Retour à la connexion
                </Link>

                <div className={styles.header}>
                    <h1>Créer un compte</h1>
                    <p>Rejoignez TalentHub et donnez vie à vos projets.</p>
                </div>

                {!accountType ? (
                    <div className={styles.choiceSection}>
                        <h2>Quel type de compte souhaitez-vous créer ?</h2>

                        <div className={styles.choiceGrid}>
                            <button
                                type="button"
                                className={styles.choiceCard}
                                onClick={() => selectAccountType("FREELANCE")}
                            >
                                <BriefcaseBusiness size={30} />
                                <strong>Freelance</strong>
                                <span>Trouvez des missions et développez votre activité.</span>
                            </button>

                            <button
                                type="button"
                                className={styles.choiceCard}
                                onClick={() => selectAccountType("CLIENT")}
                            >
                                <Building2 size={30} />
                                <strong>Client</strong>
                                <span>Trouvez les talents adaptés à vos projets.</span>
                            </button>
                        </div>
                    </div>
                ) : (
                    <>
                        <button
                            type="button"
                            className={styles.changeType}
                            onClick={() => {
                                setAccountType(null);
                                setError("");
                            }}
                        >
                            ← Changer le type de compte
                        </button>

                        <h2 className={styles.formTitle}>
                            Inscription {accountType === "FREELANCE" ? "Freelance" : "Client"}
                        </h2>

                        <form className={styles.form} onSubmit={handleSubmit}>
                            <div className={styles.fieldsGrid}>
                                <div className={styles.field}>
                                    <label htmlFor="firstName">Prénom</label>
                                    <input
                                        id="firstName"
                                        name="firstName"
                                        value={form.firstName}
                                        onChange={handleChange}
                                        required
                                        maxLength={100}
                                    />
                                </div>

                                <div className={styles.field}>
                                    <label htmlFor="lastName">Nom</label>
                                    <input
                                        id="lastName"
                                        name="lastName"
                                        value={form.lastName}
                                        onChange={handleChange}
                                        required
                                        maxLength={100}
                                    />
                                </div>

                                <div className={styles.field}>
                                    <label htmlFor="email">Adresse email</label>
                                    <input
                                        id="email"
                                        name="email"
                                        type="email"
                                        value={form.email}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className={styles.field}>
                                    <label htmlFor="password">Mot de passe</label>
                                    <input
                                        id="password"
                                        name="password"
                                        type="password"
                                        value={form.password}
                                        onChange={handleChange}
                                        required
                                        minLength={8}
                                    />
                                </div>

                                <div className={styles.field}>
                                    <label htmlFor="phone">Téléphone</label>
                                    <input
                                        id="phone"
                                        name="phone"
                                        type="tel"
                                        value={form.phone}
                                        onChange={handleChange}
                                    />
                                </div>

                                <div className={styles.field}>
                                    <label htmlFor="location">Localisation</label>
                                    <input
                                        id="location"
                                        name="location"
                                        value={form.location}
                                        onChange={handleChange}
                                    />
                                </div>
                            </div>

                            {accountType === "FREELANCE" ? (
                                <>
                                    <div className={styles.field}>
                                        <label htmlFor="bio">Présentation</label>
                                        <textarea
                                            id="bio"
                                            name="bio"
                                            value={form.bio}
                                            onChange={handleChange}
                                            rows={4}
                                        />
                                    </div>

                                    <div className={styles.field}>
                                        <label htmlFor="dailyRate">
                                            Tarif journalier (€)
                                        </label>
                                        <input
                                            id="dailyRate"
                                            name="dailyRate"
                                            type="number"
                                            min="0.01"
                                            step="0.01"
                                            value={form.dailyRate}
                                            onChange={handleChange}
                                        />
                                    </div>

                                    <div className={styles.field}>
                                        <label>Compétences</label>

                                        {loadingSkills ? (
                                            <p className={styles.helper}>
                                                Chargement des compétences...
                                            </p>
                                        ) : skills.length > 0 ? (
                                            <div className={styles.skillsGrid}>
                                                {skills.map((skill) => (
                                                    <label
                                                        key={skill.id}
                                                        className={styles.skillOption}
                                                    >
                                                        <input
                                                            type="checkbox"
                                                            checked={form.skillIds.includes(skill.id)}
                                                            onChange={() => handleSkillChange(skill.id)}
                                                        />
                                                        {skill.name}
                                                    </label>
                                                ))}
                                            </div>
                                        ) : (
                                            <p className={styles.helper}>
                                                Aucune compétence disponible.
                                            </p>
                                        )}
                                    </div>
                                </>
                            ) : (
                                <>
                                    <div className={styles.field}>
                                        <label htmlFor="companyName">
                                            Nom de l'entreprise
                                        </label>
                                        <input
                                            id="companyName"
                                            name="companyName"
                                            value={form.companyName}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    <div className={styles.field}>
                                        <label htmlFor="companyDescription">
                                            Présentation de l'entreprise
                                        </label>
                                        <textarea
                                            id="companyDescription"
                                            name="companyDescription"
                                            value={form.companyDescription}
                                            onChange={handleChange}
                                            rows={4}
                                        />
                                    </div>
                                </>
                            )}

                            {error && (
                                <p className={styles.error}>{error}</p>
                            )}

                            <button
                                type="submit"
                                className={styles.submitButton}
                                disabled={submitting || loadingSkills}
                            >
                                {submitting
                                    ? "Création du compte..."
                                    : "Créer mon compte"}
                            </button>
                        </form>
                    </>
                )}

                <p className={styles.loginText}>
                    Vous avez déjà un compte ? <Link to="/login">Se connecter</Link>
                </p>
            </div>
        </div>
    );
}
