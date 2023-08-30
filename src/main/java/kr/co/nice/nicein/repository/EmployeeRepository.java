package kr.co.nice.nicein.repository;

import kr.co.nice.nicein.entity.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Override
    List<Employee> findAll();

    Optional<Employee> findByUserId(String userId);
    Optional<Employee> findByUserEmailAndStatus(String email,String status);
}
