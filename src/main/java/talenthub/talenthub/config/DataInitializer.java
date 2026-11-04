
package talenthub.talenthub.config;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import talenthub.talenthub.user.entity.*;
import talenthub.talenthub.user.repository.UserRepository;
import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.repository.SkillRepository;
import talenthub.talenthub.mission.entity.*;
import talenthub.talenthub.mission.repository.MissionRepository;
import talenthub.talenthub.application.entity.*;
import talenthub.talenthub.application.repository.ApplicationRepository;
import talenthub.talenthub.favorite.entity.Favorite;
import talenthub.talenthub.favorite.repository.FavoriteRepository;

import java.math.BigDecimal;
import java.util.*;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final MissionRepository missionRepository;
    private final ApplicationRepository applicationRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            SkillRepository skillRepository,
            MissionRepository missionRepository,
            ApplicationRepository applicationRepository,
            FavoriteRepository favoriteRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.missionRepository = missionRepository;
        this.applicationRepository = applicationRepository;
        this.favoriteRepository = favoriteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("=== Initialisation des données TalentHub ===");

        // 1. CLIENTS
        Client techNova = createClient(
                "contact@technova.test",
                "ClientTech2026!",
                "Thomas", "Martin",
                "TechNova",
                "Entreprise spécialisée dans les solutions numériques.",
                "Paris"
        );

        Client digitalWorks = createClient(
                "contact@digitalworks.test",
                "ClientDigital2026!",
                "Sophie", "Bernard",
                "DigitalWorks",
                "Agence de développement web et mobile.",
                "Lyon"
        );

        // 2. FREELANCES
        Freelance alex = createFreelance(
                "alex.java@test.com",
                "Freelance2026!",
                "Alex", "Dupont",
                "Développeur backend Java et Spring Boot.",
                450, "Paris"
        );

        Freelance emma = createFreelance(
                "emma.fullstack@test.com",
                "Freelance2026!",
                "Emma", "Petit",
                "Développeuse Full Stack React et Node.js.",
                400, "Lyon"
        );

        Freelance lucas = createFreelance(
                "lucas.frontend@test.com",
                "Freelance2026!",
                "Lucas", "Moreau",
                "Développeur frontend React et TypeScript.",
                350, "Bordeaux"
        );

        // 3. COMPETENCES
        Skill java = createSkill("Java");
        Skill spring = createSkill("Spring Boot");
        Skill react = createSkill("React");
        Skill typescript = createSkill("TypeScript");
        Skill postgres = createSkill("PostgreSQL");
        Skill docker = createSkill("Docker");

        // Compétences des freelances
        alex.setSkills(new HashSet<>(Set.of(java, spring, postgres, docker)));
        emma.setSkills(new HashSet<>(Set.of(java, spring, react, typescript)));
        lucas.setSkills(new HashSet<>(Set.of(react, typescript, docker)));

        userRepository.saveAll(List.of(alex, emma, lucas));

        // 4. MISSIONS
        Mission mission1 = createMission(
                "API de gestion RH",
                "Développement d'une API REST pour gérer les ressources humaines.",
                5000, "Paris", true, MissionStatus.CLOSED,
                techNova, Set.of(java, spring, postgres)
        );

        Mission mission2 = createMission(
                "Application e-commerce",
                "Développement d'une plateforme e-commerce moderne.",
                4000, "Lyon", true, MissionStatus.OPEN,
                techNova, Set.of(react, typescript, docker)
        );

        Mission mission3 = createMission(
                "API de réservation",
                "Création d'une API de réservation avec authentification.",
                6000, "Paris", false, MissionStatus.OPEN,
                techNova, Set.of(java, spring, docker)
        );

        Mission mission4 = createMission(
                "Dashboard analytique",
                "Création d'un tableau de bord de visualisation de données.",
                3500, "Lyon", true, MissionStatus.OPEN,
                digitalWorks, Set.of(react, typescript, postgres)
        );

        Mission mission5 = createMission(
                "Plateforme de recrutement",
                "Développement d'une plateforme de recrutement complète.",
                7000, "Paris", true, MissionStatus.OPEN,
                digitalWorks, Set.of(java, react, postgres)
        );

        Mission mission6 = createMission(
                "Refonte frontend",
                "Modernisation de l'interface utilisateur d'une application.",
                2500, "Bordeaux", true, MissionStatus.OPEN,
                digitalWorks, Set.of(react, typescript)
        );

        // 5. CANDIDATURES
        createApplication(
                alex, mission1,
                "Je suis spécialisé en Java et Spring Boot.",
                4800, ApplicationStatus.ACCEPTED
        );

        createApplication(
                emma, mission1,
                "Je peux également réaliser ce projet.",
                4900, ApplicationStatus.REJECTED
        );

        createApplication(
                emma, mission2,
                "Je suis intéressée par votre projet e-commerce.",
                3900, ApplicationStatus.PENDING
        );

        createApplication(
                lucas, mission2,
                "Je peux prendre en charge le frontend.",
                3600, ApplicationStatus.REJECTED
        );

        createApplication(
                emma, mission3,
                "Mon expérience Spring Boot correspond à votre besoin.",
                5500, ApplicationStatus.PENDING
        );

        createApplication(
                lucas, mission4,
                "Je suis intéressé par votre dashboard.",
                3400, ApplicationStatus.WITHDRAWN
        );

        createApplication(
                alex, mission5,
                "Je peux développer votre backend Java.",
                6800, ApplicationStatus.PENDING
        );

        createApplication(
                emma, mission5,
                "Je peux développer la partie frontend et backend.",
                6500, ApplicationStatus.PENDING
        );

        createApplication(
                lucas, mission6,
                "Je suis spécialisé en React et TypeScript.",
                2400, ApplicationStatus.PENDING
        );

        // 6. FAVORIS
        createFavorite(alex, mission2);
        createFavorite(alex, mission5);
        createFavorite(emma, mission1);
        createFavorite(emma, mission4);
        createFavorite(lucas, mission3);
        createFavorite(lucas, mission6);

        System.out.println("=== Données TalentHub initialisées ===");
    }

    private Client createClient(
            String email, String password,
            String firstName, String lastName,
            String companyName, String description, String location
    ) {
        Optional<User> existing = userRepository.findByEmail(email);

        if (existing.isPresent()) {
            return (Client) existing.get();
        }

        Client client = new Client();
        client.setEmail(email);
        client.setPassword(passwordEncoder.encode(password));
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setCompanyName(companyName);
        client.setCompanyDescription(description);
        client.setLocation(location);

        return userRepository.save(client);
    }

    private Freelance createFreelance(
            String email, String password,
            String firstName, String lastName,
            String bio, double dailyRate, String location
    ) {
        Optional<User> existing = userRepository.findByEmail(email);

        if (existing.isPresent()) {
            return (Freelance) existing.get();
        }

        Freelance freelance = new Freelance();
        freelance.setEmail(email);
        freelance.setPassword(passwordEncoder.encode(password));
        freelance.setFirstName(firstName);
        freelance.setLastName(lastName);
        freelance.setBio(bio);
        freelance.setDailyRate(BigDecimal.valueOf(dailyRate));
        freelance.setLocation(location);

        return userRepository.save(freelance);
    }

    private Skill createSkill(String name) {
        return skillRepository.findAll().stream()
                .filter(skill -> skill.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Skill skill = new Skill();
                    skill.setName(name);
                    skill.setActive(true);
                    return skillRepository.save(skill);
                });
    }

    private Mission createMission(
            String title, String description,
            double budget, String location,
            boolean remote, MissionStatus status,
            Client client, Set<Skill> skills
    ) {
        return missionRepository.findAll().stream()
                .filter(mission -> mission.getTitle().equals(title))
                .findFirst()
                .orElseGet(() -> {
                    Mission mission = new Mission();
                    mission.setTitle(title);
                    mission.setDescription(description);
                    mission.setBudget(BigDecimal.valueOf(budget));
                    mission.setLocation(location);
                    mission.setRemote(remote);
                    mission.setStatus(status);
                    mission.setClient(client);
                    mission.setRequiredSkills(new HashSet<>(skills));

                    return missionRepository.save(mission);
                });
    }

    private void createApplication(
            Freelance freelance, Mission mission,
            String coverLetter, double proposedPrice,
            ApplicationStatus status
    ) {
        boolean exists = applicationRepository.findAll().stream()
                .anyMatch(application ->
                        application.getFreelance().getId().equals(freelance.getId())
                                && application.getMission().getId().equals(mission.getId())
                );

        if (exists) {
            return;
        }

        Application application = new Application();
        application.setFreelance(freelance);
        application.setMission(mission);
        application.setCoverLetter(coverLetter);
        application.setProposedPrice(BigDecimal.valueOf(proposedPrice));
        application.setStatus(status);

        applicationRepository.save(application);
    }

    private void createFavorite(Freelance freelance, Mission mission) {
        boolean exists = favoriteRepository.findAll().stream()
                .anyMatch(favorite ->
                        favorite.getFreelance().getId().equals(freelance.getId())
                                && favorite.getMission().getId().equals(mission.getId())
                );

        if (exists) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setFreelance(freelance);
        favorite.setMission(mission);

        favoriteRepository.save(favorite);
    }
}
