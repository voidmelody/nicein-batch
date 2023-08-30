package kr.co.nice.nicein.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class TimeBase {

    @Column(name="created_at", updatable = false) // nullable = false;
    private LocalDateTime createAt;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updateAt;

    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createAt = now;
        updateAt = now;
    }

    @PreUpdate
    public void preUpdate(){
        updateAt = LocalDateTime.now();
    }
}
