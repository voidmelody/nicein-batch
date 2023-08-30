package kr.co.nice.nicein.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class Department extends TimeBase{
    @Id
    private String deptId;

    @ManyToOne
    @JoinColumn(name="company_id")
    private Company company;

    private String deptName;
    private String deptShortName;
    private String treeId;
    private String upTreeId;
    private Integer depth;
    private Integer sortNo;
    private String useYn;
    private String mgrUserId;
    private String deptFullName;

    @Override
    public void prePersist() {
        super.prePersist();
    }

    @Override
    public void preUpdate() {
        super.preUpdate();
    }
}
