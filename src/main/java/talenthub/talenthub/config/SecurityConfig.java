
package talenthub.talenthub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

import talenthub.talenthub.security.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider)
            throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth

                        // Inscription : accès public
                        .requestMatchers(HttpMethod.POST, "/api/clients", "/api/freelances")
                        .permitAll()

                        // Missions
                        .requestMatchers(HttpMethod.GET, "/api/missions", "/api/missions/**")
                        .hasAnyRole("CLIENT", "FREELANCE")
                        .requestMatchers("/api/missions/**")
                        .hasRole("CLIENT")

                        // Compétences
                        .requestMatchers(HttpMethod.GET, "/api/skills")
                        .hasAnyRole("CLIENT", "FREELANCE")

                        .requestMatchers(HttpMethod.POST, "/api/skills")
                        .hasAnyRole("CLIENT", "FREELANCE")

                        // Candidatures
                        .requestMatchers(HttpMethod.GET, "/api/applications")
                        .hasRole("FREELANCE")

                        .requestMatchers(HttpMethod.GET, "/api/applications/mission/**")
                        .hasRole("CLIENT")

                        .requestMatchers(HttpMethod.GET, "/api/applications/freelance/**")
                        .hasRole("FREELANCE")

                        .requestMatchers(HttpMethod.GET, "/api/applications/*")
                        .hasAnyRole("CLIENT", "FREELANCE")

                        .requestMatchers(HttpMethod.POST, "/api/applications")
                        .hasRole("FREELANCE")

                        .requestMatchers(HttpMethod.PATCH,
                                "/api/applications/*/accept",
                                "/api/applications/*/reject")
                        .hasRole("CLIENT")

                        .requestMatchers(HttpMethod.PATCH, "/api/applications/*/withdraw")
                        .hasRole("FREELANCE")

                        // Favoris
                        .requestMatchers("/api/favorites/**")
                        .hasRole("FREELANCE")

                        // Profils
                        .requestMatchers("/api/clients/**")
                        .hasRole("CLIENT")

                        .requestMatchers("/api/freelances/**")
                        .hasRole("FREELANCE")

                        // Tout le reste est interdit
                        .anyRequest().denyAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
