package kr.co.nice.nicein.dto;


import kr.co.nice.nicein.vo.AddJobType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String userId;
    private AddJobType addJobType;
    private String loginId;
    private String username;
    private String deptId;
    private String deptName;
    private String companyId;
    private String companyName;
    private String comPhoneNo;
    private String cellPhoneNo;
    private Integer status;
    private String sabun;
    private String userEmail;
    private String ofcLevelId;
    private String ofcLevelName;
    private String positionId;
    private String positionName;
    private String functionId;
    private String functionName;
    private String etcParam1;
    private String regDate;
    private String chgDate;
    private String cefBusinessCategory;
    private String cefMsgYn;
    private String mobileUseYn;
    private String ngCommUseYn;
    private String ngMailUseYn;
    private String ngAppUseYn;
    private String deptFullName;
}
