package talenthub.talenthub.mission.service;

import org.springframework.stereotype.Service;
import talenthub.talenthub.mission.dto.MissionRequestDto;
import talenthub.talenthub.mission.dto.MissionResponseDto;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.mission.entity.MissionStatus;
import talenthub.talenthub.mission.exception.MissionNotFoundException;
import talenthub.talenthub.mission.repository.MissionRepository;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.user.repository.ClientRepository;
import talenthub.talenthub.skill.repository.SkillRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MissionService {

    private final MissionRepository missionRepository;
    private final ClientRepository clientRepository;
    private final SkillRepository skillRepository;

    public MissionService(MissionRepository missionRepository, 
                          ClientRepository clientRepository, SkillRepository skillRepository) {
        this.missionRepository = missionRepository;
        this.clientRepository = clientRepository;
        this.skillRepository = skillRepository;
    }

    public List<MissionResponseDto> findByStatus(MissionStatus status) {
        return missionRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDto)
                .toList() ;
    }

    public MissionResponseDto findById(Long id) {
        Mission mission = missionRepository.findById(id).orElseThrow(() -> new MissionNotFoundException(id));
        return toResponseDto(mission);
    }

    public MissionResponseDto create(MissionRequestDto missionRequestDto) {
        Client client = clientRepository.findById(missionRequestDto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        Set<Skill> skills = new HashSet<>(skillRepository.findAllById(missionRequestDto.getSkillIds()));

        Mission mission = new Mission();

        toUpdateMissionData(missionRequestDto, client, skills, mission);

        return toResponseDto(missionRepository.save(mission));
    }

    public MissionResponseDto update(Long id, MissionRequestDto missionRequestDto) {

        Client client = clientRepository.findById(missionRequestDto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Set<Skill> skills = new HashSet<>(skillRepository.findAllById(missionRequestDto.getSkillIds()));

        Mission  mission = missionRepository.findById(id).orElseThrow(() -> new MissionNotFoundException(id));

        toUpdateMissionData(missionRequestDto, client, skills, mission);

        return toResponseDto(missionRepository.save(mission));
    }

    public void delete(Long id) {
        if (!missionRepository.existsById(id)) {
            throw new MissionNotFoundException(id);
        }
        missionRepository.deleteById(id);
    }

    private MissionResponseDto toResponseDto(Mission mission) {
        MissionResponseDto dto = new MissionResponseDto();

        dto.setId(mission.getId());
        dto.setTitle(mission.getTitle());
        dto.setDescription(mission.getDescription());
        dto.setBudget(mission.getBudget());
        dto.setLocation(mission.getLocation());
        dto.setRemote(mission.isRemote());
        dto.setStatus(mission.getStatus());

        dto.setClientId(mission.getClient().getId());
        dto.setSkillIds(
                mission.getRequiredSkills()
                        .stream()
                        .map(Skill::getId)
                        .collect(Collectors.toSet())
        );
        return dto;
    }

    private void toUpdateMissionData(MissionRequestDto missionRequestDto,
                                   Client client, Set<Skill> skills, Mission mission) {
        mission.setTitle(missionRequestDto.getTitle());
        mission.setDescription(missionRequestDto.getDescription());
        mission.setBudget(missionRequestDto.getBudget());
        mission.setLocation(missionRequestDto.getLocation());
        mission.setRemote(missionRequestDto.isRemote());
        mission.setStatus(missionRequestDto.getStatus());

        mission.setClient(client);
        mission.setRequiredSkills(skills);
    }


}
