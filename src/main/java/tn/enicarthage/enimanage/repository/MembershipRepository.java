package tn.enicarthage.enimanage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.enimanage.Model.Membership;
import tn.enicarthage.enimanage.Model.User;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByClub(User club);
    List<Membership> findByStudent(User student);
    boolean existsByClubAndStudent(User club, User student);
    void deleteByClubAndStudent(User club, User student);
}