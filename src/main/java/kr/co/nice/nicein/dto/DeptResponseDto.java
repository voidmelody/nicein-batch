package kr.co.nice.nicein.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptResponseDto {
    private String deptId;
    private String deptName;
    private String companyId;
    private String companyName;
    private String deptShortName;
    private String treeId;
    private String upTreeId;
    private Integer depth;
    private Integer sortNo;
    private String useYn;
    private String mgrUserId;
    private String deptFullName;
    private String linkDocDeptCode;
    private String linkJeonDeptCode;
}
