package kr.co.nice.nicein.repository;

import kr.co.nice.nicein.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeRepository extends JpaRepository<Code,Long> {

    Code findByCategoryAndValue(String category,String value);
}
