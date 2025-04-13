package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.enicarthage.enimanage.Model.Salle;

import java.util.List;

public interface SalleRepository extends JpaRepository<Salle, Long> {
}