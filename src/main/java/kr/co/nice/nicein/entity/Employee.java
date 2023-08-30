package kr.co.nice.nicein.entity;

import kr.co.nice.nicein.vo.AddJobType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class Employee extends TimeBase{
    @Id
    private String userId;

    @Enumerated(value=EnumType.STRING)
    private AddJobType addJobType;

    private String loginId;
    private String username;

    @ManyToOne
    @JoinColumn(name="dept_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name="company_id")
    private Company company;

    private String status;
    private String sabun;
    private String cefBusinessCategory;
    private String cellPhoneNo;
    private String comPhoneNo;
    private String userEmail;
    private String ofcLevelName;
    private String positionName;
    private String functionName;
    private String employeeDeptFullName;

    private String managerYn;
    private String itYn;
    private String hrisId;

    @Override
    public void prePersist() {
        super.prePersist();
    }

    @Override
    public void preUpdate() {
        super.preUpdate();
    }

}
