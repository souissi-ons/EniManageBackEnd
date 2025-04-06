package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.enimanage.Model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
