
package talenthub.talenthub.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talenthub.talenthub.user.dto.ClientRegistrationRequestDto;
import talenthub.talenthub.user.dto.ClientRequestDto;
import talenthub.talenthub.user.dto.ClientResponseDto;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.exception.ClientNotFoundException;
import talenthub.talenthub.user.exception.EmailAlreadyExistsException;
import talenthub.talenthub.user.repository.ClientRepository;
import talenthub.talenthub.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ClientResponseDto findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id)) ;

        return toResponseDto(client);
    }

    @Transactional
    public ClientResponseDto update(Long id, ClientRequestDto request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPhone(request.getPhone());
        client.setCompanyName(request.getCompanyName());
        client.setCompanyDescription(request.getCompanyDescription());
        client.setLocation(request.getLocation());

        return toResponseDto(clientRepository.save(client));
    }


    @Transactional(readOnly = true)
    public List<ClientResponseDto> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }


    private ClientResponseDto toResponseDto(Client client) {
        ClientResponseDto response = new ClientResponseDto();

        response.setId(client.getId());
        response.setEmail(client.getEmail());
        response.setFirstName(client.getFirstName());
        response.setLastName(client.getLastName());
        response.setPhone(client.getPhone());
        response.setCompanyName(client.getCompanyName());
        response.setCompanyDescription(client.getCompanyDescription());
        response.setLocation(client.getLocation());

        return response;
    }


    @Transactional
    public ClientResponseDto register(ClientRegistrationRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Client client = new Client();

        client.setEmail(request.getEmail());
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPhone(request.getPhone());
        client.setCompanyName(request.getCompanyName());
        client.setCompanyDescription(request.getCompanyDescription());
        client.setLocation(request.getLocation());

        Client savedClient = clientRepository.save(client);

        return toResponseDto(savedClient);
    }


}
