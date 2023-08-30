package kr.co.nice.nicein.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="it_employee")
public class ItEmployee extends TimeBase{
    @Id
    private String userId;

    private LocalDate careerStart;
    private LocalDate niceStart;
    private LocalDate cmpStart;
    private LocalDate cmpEnd;

    private String itType;
    private String detail;
    private String license;
    private String note;

    private String cmpEndReason;

    @Override
    public void prePersist() {
        super.prePersist();
    }

    @Override
    public void preUpdate() {
        super.preUpdate();
    }
}
