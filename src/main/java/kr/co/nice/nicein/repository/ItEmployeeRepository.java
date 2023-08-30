package kr.co.nice.nicein.repository;

import kr.co.nice.nicein.entity.ItEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItEmployeeRepository extends JpaRepository<ItEmployee, String> {
}
