package kr.co.nice.nicein.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HrisDto {
    private LocalDate niceStartYmd;
    private LocalDate companyStartYmd;
    private LocalDate retireYmd;
    private String email;
    private String employedYn;
    private String empId;
}
