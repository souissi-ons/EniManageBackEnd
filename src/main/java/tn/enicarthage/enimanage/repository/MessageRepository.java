package tn.enicarthage.enimanage.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.enimanage.Model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}