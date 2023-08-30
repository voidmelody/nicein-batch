package kr.co.nice.nicein.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="hr_history")
@Entity
public class History{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Employee employee;

    private String editTable;
    private String editField;
    private String preData;
    private String chgData;
    private LocalDateTime updated_at;
    private String editorId;
    private String fieldName;
    private String type;
    private String content;

}
