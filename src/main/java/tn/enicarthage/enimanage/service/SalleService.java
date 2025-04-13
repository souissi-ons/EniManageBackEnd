package tn.enicarthage.enimanage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.enimanage.DTO.ResourceSalleDTO;
import tn.enicarthage.enimanage.DTO.SalleWithResourcesDTO;
import tn.enicarthage.enimanage.Model.*;
import tn.enicarthage.enimanage.repository.ResourceRepository;
import tn.enicarthage.enimanage.repository.ResourceSalleRepository;
import tn.enicarthage.enimanage.repository.SalleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalleService {
    private final SalleRepository salleRepository;
    private final ResourceSalleRepository resourceSalleRepository;
    private final ResourceRepository resourceRepository;

    @Transactional
    public Salle createSalleWithRessources(SalleWithResourcesDTO dto) {
        Salle salle = new Salle();
        salle.setName(dto.getName());
        salle.setDescription(dto.getDescription());
        salle.setCapacity(dto.getCapacity());
        salle.setBatiment(dto.getBatiment());

    for (ResourceSalleDTO ressourceDTO : dto.getRessources()) {
        ResourceSalle ressourceSalle = new ResourceSalle();
        ressourceSalle.setResource(ressourceDTO.getResource());
        ressourceSalle.setQuantity(ressourceDTO.getQuantity());
        salle.addRessource(ressourceSalle);
    }

     return salleRepository.save(salle);
    }

    public List<SalleWithResourcesDTO> getAllSalles() {
        return salleRepository.findAll().stream()
                .map(salle -> {
                    SalleWithResourcesDTO dto = new SalleWithResourcesDTO();
                    dto.setId(salle.getId());
                    dto.setName(salle.getName());
                    dto.setDescription(salle.getDescription());
                    dto.setCapacity(salle.getCapacity());
                    dto.setBatiment(salle.getBatiment());

                    List<ResourceSalleDTO> resourceDTOs = salle.getRessources().stream()
                            .map(resource -> {
                                ResourceSalleDTO resourceDTO = new ResourceSalleDTO();
                                resourceDTO.setResource(resource.getResource());
                                resourceDTO.setQuantity(resource.getQuantity());
                                return resourceDTO;
                            })
                            .collect(Collectors.toList());

                    dto.setRessources(resourceDTOs);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Get salle by ID
    public Optional<SalleWithResourcesDTO> getSalleById(Long id) {
        return salleRepository.findById(id)
                .map(salle -> {
                    SalleWithResourcesDTO dto = new SalleWithResourcesDTO();
                    dto.setName(salle.getName());
                    dto.setDescription(salle.getDescription());
                    dto.setCapacity(salle.getCapacity());
                    dto.setBatiment(salle.getBatiment());


                    List<ResourceSalleDTO> resourceDTOs = salle.getRessources().stream()
                            .map(resource -> {
                                ResourceSalleDTO resourceDTO = new ResourceSalleDTO();
                                resourceDTO.setResource(resource.getResource());
                                resourceDTO.setQuantity(resource.getQuantity());
                                return resourceDTO;
                            })
                            .collect(Collectors.toList());

                    dto.setRessources(resourceDTOs);
                    return dto;
                });
    }

    // Update salle
    @Transactional
    public Optional<Salle> updateSalle(Long id, SalleWithResourcesDTO updatedSalle) {
        return salleRepository.findById(id)
                .map(salle -> {
                    salle.setName(updatedSalle.getName());
                    salle.setDescription(updatedSalle.getDescription());
                    salle.setCapacity(updatedSalle.getCapacity());
                    salle.setBatiment(updatedSalle.getBatiment());

                    resourceSalleRepository.deleteAll(salle.getRessources());
                    salle.getRessources().clear();


                    for (ResourceSalleDTO resourceDTO : updatedSalle.getRessources()) {
                        ResourceSalle resourceSalle = new ResourceSalle();
                        resourceSalle.setSalle(salle);
                        resourceSalle.setResource(resourceDTO.getResource());
                        resourceSalle.setQuantity(resourceDTO.getQuantity());
                        salle.addRessource(resourceSalle);
                    }

                    return salleRepository.save(salle);
                });
    }

    // Delete salle
    @Transactional
    public boolean deleteSalle(Long id) {
        return salleRepository.findById(id)
                .map(salle -> {
                    salleRepository.delete(salle);
                    return true;
                })
                .orElse(false);
    }


}