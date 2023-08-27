package nl.ilovecoding.userservice;


import nl.ilovecoding.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserJpaRepository extends JpaRepository<User,Integer> {
}
