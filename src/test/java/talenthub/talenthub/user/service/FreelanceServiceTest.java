
package talenthub.talenthub.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.repository.SkillRepository;
import talenthub.talenthub.user.dto.FreelanceRegistrationRequestDto;
import talenthub.talenthub.user.dto.FreelanceRequestDto;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.exception.EmailAlreadyExistsException;
import talenthub.talenthub.user.exception.FreelanceNotFoundException;
import talenthub.talenthub.user.repository.FreelanceRepository;
import talenthub.talenthub.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FreelanceServiceTest {

    @Mock
    private FreelanceRepository freelanceRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private FreelanceService freelanceService;

    @Test
    void shouldFindFreelanceById() {
        Freelance freelance = createFreelance(1L);

        when(freelanceRepository.findById(1L)).thenReturn(Optional.of(freelance));

        var response = freelanceService.findById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("freelance@test.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getSkillIds()).containsExactly(1L);

        verify(freelanceRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenFreelanceDoesNotExist() {
        when(freelanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> freelanceService.findById(1L))
                .isInstanceOf(FreelanceNotFoundException.class);

        verify(freelanceRepository).findById(1L);
    }

    @Test
    void shouldUpdateFreelanceSuccessfully() {
        Freelance freelance = createFreelance(1L);
        Skill skill = createSkill(2L);

        FreelanceRequestDto request = new FreelanceRequestDto();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setPhone("0600000000");
        request.setBio("New bio");
        request.setLocation("Paris");
        request.setSkillIds(Set.of(2L));

        when(freelanceRepository.findById(1L)).thenReturn(Optional.of(freelance));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill));
        when(freelanceRepository.save(any(Freelance.class))).thenAnswer(invocation ->
                invocation.getArgument(0));

        var response = freelanceService.update(1L, request);

        assertThat(response.getFirstName()).isEqualTo("Jane");
        assertThat(response.getLastName()).isEqualTo("Smith");
        assertThat(response.getPhone()).isEqualTo("0600000000");
        assertThat(response.getBio()).isEqualTo("New bio");
        assertThat(response.getLocation()).isEqualTo("Paris");
        assertThat(response.getSkillIds()).containsExactly(2L);

        verify(freelanceRepository).save(freelance);
    }

    @Test
    void shouldClearSkillsWhenSkillIdsAreNullDuringUpdate() {
        Freelance freelance = createFreelance(1L);

        FreelanceRequestDto request = new FreelanceRequestDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSkillIds(null);

        when(freelanceRepository.findById(1L)).thenReturn(Optional.of(freelance));
        when(freelanceRepository.save(any(Freelance.class))).thenAnswer(invocation ->
                invocation.getArgument(0));

        var response = freelanceService.update(1L, request);

        assertThat(response.getSkillIds()).isEmpty();
        assertThat(freelance.getSkills()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingFreelance() {
        FreelanceRequestDto request = new FreelanceRequestDto();

        when(freelanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> freelanceService.update(1L, request))
                .isInstanceOf(FreelanceNotFoundException.class);

        verify(freelanceRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithNonExistingSkill() {
        Freelance freelance = createFreelance(1L);

        FreelanceRequestDto request = new FreelanceRequestDto();
        request.setSkillIds(Set.of(99L));

        when(freelanceRepository.findById(1L)).thenReturn(Optional.of(freelance));
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> freelanceService.update(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Skill not found with id: 99");

        verify(freelanceRepository, never()).save(any());
    }

    @Test
    void shouldFindAllFreelances() {
        Freelance freelance1 = createFreelance(1L);
        Freelance freelance2 = createFreelance(2L);
        freelance2.setEmail("freelance2@test.com");

        when(freelanceRepository.findAll()).thenReturn(List.of(freelance1, freelance2));

        var responses = freelanceService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(response -> response.getId())
                .containsExactly(1L, 2L);

        verify(freelanceRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoFreelancesExist() {
        when(freelanceRepository.findAll()).thenReturn(List.of());

        var responses = freelanceService.findAll();

        assertThat(responses).isEmpty();

        verify(freelanceRepository).findAll();
    }

    @Test
    void shouldRegisterFreelanceSuccessfullyWithSkills() {
        FreelanceRegistrationRequestDto request = createRegistrationRequest();
        Skill skill = createSkill(1L);

        when(userRepository.existsByEmail("freelance@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        when(freelanceRepository.save(any(Freelance.class))).thenAnswer(invocation -> {
            Freelance freelance = invocation.getArgument(0);
            freelance.setId(1L);
            return freelance;
        });

        var response = freelanceService.register(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("freelance@test.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getSkillIds()).containsExactly(1L);

        verify(userRepository).existsByEmail("freelance@test.com");
        verify(passwordEncoder).encode("password123");
        verify(skillRepository).findById(1L);
        verify(freelanceRepository).save(any(Freelance.class));
    }

    @Test
    void shouldRegisterFreelanceWithoutSkills() {
        FreelanceRegistrationRequestDto request = createRegistrationRequest();
        request.setSkillIds(null);

        when(userRepository.existsByEmail("freelance@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        when(freelanceRepository.save(any(Freelance.class))).thenAnswer(invocation -> {
            Freelance freelance = invocation.getArgument(0);
            freelance.setId(1L);
            return freelance;
        });

        var response = freelanceService.register(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSkillIds()).isEmpty();

        verify(skillRepository, never()).findById(any());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringWithNonExistingSkill() {
        FreelanceRegistrationRequestDto request = createRegistrationRequest();
        request.setSkillIds(Set.of(99L));

        when(userRepository.existsByEmail("freelance@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> freelanceService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Skill not found with id: 99");

        verify(freelanceRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringWithExistingEmail() {
        FreelanceRegistrationRequestDto request = createRegistrationRequest();

        when(userRepository.existsByEmail("freelance@test.com")).thenReturn(true);

        assertThatThrownBy(() -> freelanceService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(passwordEncoder, never()).encode(anyString());
        verify(freelanceRepository, never()).save(any());
    }

    private Freelance createFreelance(Long id) {
        Freelance freelance = new Freelance();
        freelance.setId(id);
        freelance.setEmail("freelance@test.com");
        freelance.setFirstName("John");
        freelance.setLastName("Doe");
        freelance.setPhone("0612345678");
        freelance.setBio("Java developer");
        freelance.setLocation("Paris");
        freelance.setSkills(new java.util.HashSet<>(Set.of(createSkill(1L))));
        return freelance;
    }

    private Skill createSkill(Long id) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setName("Java");
        return skill;
    }

    private FreelanceRegistrationRequestDto createRegistrationRequest() {
        FreelanceRegistrationRequestDto request = new FreelanceRegistrationRequestDto();
        request.setEmail("freelance@test.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("0612345678");
        request.setBio("Java developer");
        request.setLocation("Paris");
        request.setSkillIds(Set.of(1L));
        return request;
    }
}
