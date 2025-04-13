package tn.enicarthage.enimanage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.enimanage.Model.Resource;
import tn.enicarthage.enimanage.repository.ResourceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id).orElse(null);
    }

    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource updateResource(Long id, Resource resourceDetails) {
        Resource resource = resourceRepository.findById(id).orElse(null);
        if (resource != null) {
            resource.setName(resourceDetails.getName());
            resource.setDescription(resourceDetails.getDescription());
            resource.setQuantity(resourceDetails.getQuantity());
            return resourceRepository.save(resource);
        }
        return null;
    }

    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }
}