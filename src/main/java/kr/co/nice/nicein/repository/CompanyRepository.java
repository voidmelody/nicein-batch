package kr.co.nice.nicein.repository;

import kr.co.nice.nicein.entity.Company;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    @Override
    Optional<Company> findById(String companyId);

    List<Company> findByHrisYn(String yn);
}
