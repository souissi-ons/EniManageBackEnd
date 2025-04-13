package tn.enicarthage.enimanage.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.enimanage.DTO.SalleWithResourcesDTO;
import tn.enicarthage.enimanage.Model.Salle;
import tn.enicarthage.enimanage.service.SalleService;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/salles")
@RequiredArgsConstructor
public class SalleController {

    private final SalleService salleService;

    @PostMapping
    public ResponseEntity<Salle> createSalleWithResources(@RequestBody SalleWithResourcesDTO dto) {
        Salle createdSalle = salleService.createSalleWithRessources(dto);
        return ResponseEntity.ok(createdSalle);
    }

    @GetMapping
    public ResponseEntity<List<SalleWithResourcesDTO>> getAllSalles() {
        List<SalleWithResourcesDTO> salles = salleService.getAllSalles();
        return ResponseEntity.ok(salles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalleWithResourcesDTO> getSalleById(@PathVariable Long id) {
        Optional<SalleWithResourcesDTO> salle = salleService.getSalleById(id);
        return salle.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Salle> updateSalle(@PathVariable Long id, @RequestBody SalleWithResourcesDTO updatedSalle) {
        Optional<Salle> salle = salleService.updateSalle(id, updatedSalle);
        return salle.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalle(@PathVariable Long id) {
        boolean deleted = salleService.deleteSalle(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}