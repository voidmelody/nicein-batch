package kr.co.nice.nicein.repository;

import kr.co.nice.nicein.entity.Department;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Transactional
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    @Override
    Optional<Department> findById(String deptId);
}
