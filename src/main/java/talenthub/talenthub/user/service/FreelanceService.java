
package talenthub.talenthub.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.repository.SkillRepository;
import talenthub.talenthub.user.dto.FreelanceRequestDto;
import talenthub.talenthub.user.dto.FreelanceResponseDto;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.exception.FreelanceNotFoundException;
import talenthub.talenthub.user.repository.FreelanceRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreelanceService {

    private final FreelanceRepository freelanceRepository;
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public FreelanceResponseDto findById(Long id) {
        Freelance freelance = freelanceRepository.findById(id)
                .orElseThrow(() -> new FreelanceNotFoundException(id));

        return toResponseDto(freelance);
    }

    @Transactional
    public FreelanceResponseDto update(
            Long id,
            FreelanceRequestDto request) {

        Freelance freelance = freelanceRepository.findById(id)
                .orElseThrow(() -> new FreelanceNotFoundException(id));

        freelance.setFirstName(request.getFirstName());
        freelance.setLastName(request.getLastName());
        freelance.setPhone(request.getPhone());
        freelance.setBio(request.getBio());
        freelance.setDailyRate(request.getDailyRate());
        freelance.setLocation(request.getLocation());

        Set<Skill> skills = request.getSkillIds() == null ? Set.of() : request.getSkillIds()
                .stream()
                .map(skillId -> skillRepository.findById(skillId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Skill not found with id: " + skillId)))
                .collect(Collectors.toSet());

        freelance.setSkills(skills);

        return toResponseDto(freelanceRepository.save(freelance));
    }


    @Transactional(readOnly = true)
    public List<FreelanceResponseDto> findAll() {
        return freelanceRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /*
        toResponseDto() : control the fields sent to the frontend.
     */
    private FreelanceResponseDto toResponseDto(Freelance freelance) {
        FreelanceResponseDto response = new FreelanceResponseDto();

        response.setId(freelance.getId());
        response.setEmail(freelance.getEmail());
        response.setFirstName(freelance.getFirstName());
        response.setLastName(freelance.getLastName());
        response.setPhone(freelance.getPhone());
        response.setBio(freelance.getBio());
        response.setDailyRate(freelance.getDailyRate());
        response.setLocation(freelance.getLocation());

        return response;
    }
}
