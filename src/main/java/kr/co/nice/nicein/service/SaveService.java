package kr.co.nice.nicein.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kr.co.nice.nicein.entity.*;
import kr.co.nice.nicein.repository.*;
import kr.co.nice.nicein.vo.AddJobType;
import kr.co.nice.nicein.dto.DeptResponseDto;
import kr.co.nice.nicein.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SaveService {
    private final ObjectMapper objectMapper;
    private final CodeRepository codeRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final HistoryRepository historyRepository;
    private final ItEmployeeRepository itEmployeeRepository;

    private final String YES = "Y";

    List<Employee> existedEmployeeList = new ArrayList<>(); // 기존 DB의 조직도 정보
    List<Employee> updatedEmployeeList = new ArrayList<>();



    public List<UserResponseDto> parseEmployeeDataAndSave(String jsonStr) throws JsonProcessingException, IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Map<String, List<Map<String, String>>> dataMap = objectMapper.readValue(jsonStr, Map.class);
        List<Map<String, String>> Info = dataMap.get("data");
        existedEmployeeList = employeeRepository.findAll();
        List<UserResponseDto> responseUserDtoList = new ArrayList<>();
        List<History> historyList = new ArrayList<>();
        List<Employee> employeeList = new ArrayList<>();

        // 새로운 조직도 정보
        for (Map<String, String> user : Info) {
            // Parsing.
            UserResponseDto userData = objectMapper.convertValue(user, UserResponseDto.class);
            // BASIC이 아닌 경우 (겸직 등)는 DB에 저장하지 않게 결정.
            if(!userData.getAddJobType().equals(AddJobType.BASIC)){
                continue;
            }

            // 사용하지 않는 회사인 경우 저장하지 않음.
            String companyId = userData.getCompanyId();
            Company c = companyRepository.findById(companyId).get();
            if(!c.getUseYn().equals("Y")){
                continue;
            }

            // 조직도 부서에 없으면 저장하지 않음.
            String deptId = userData.getDeptId();
            Optional<Department> optionalDepartment = departmentRepository.findById(deptId);
            if(optionalDepartment.isEmpty()){
                continue;
            }

            responseUserDtoList.add(userData);

            Department department = departmentBuilder(userData);
            Company company = companyBuilder(userData);
            Employee employee = objectMapper.convertValue(userData, Employee.class);

            Optional<Employee> existedEmployee = employeeRepository.findByUserId(employee.getUserId());

            // deptFullName Employee쪽으로.
            employee.setEmployeeDeptFullName(userData.getDeptFullName());
            department.setDeptFullName(userData.getDeptFullName());
            employee.setCompany(company);
            employee.setDepartment(department);

            // itYn 설정
            if(itEmployeeRepository.findById(employee.getUserId()).isPresent()){
                employee.setItYn("Y");
            }


            // DB 저장 시작
            if(optionalDepartment.isPresent()){
                optionalDepartment.get().setDeptFullName(department.getDeptFullName());
            }
            employeeList.add(employee);

            // 신규 인원일 경우
            if(existedEmployee.isEmpty()){
                History history = History.builder()
                        .employee(employee)
                        .editTable("Employee")
                        .updated_at(LocalDateTime.now())
                        .editorId("그룹웨어")
                        .type(codeRepository.findByCategoryAndValue("type", "신규").getCode())
                        .content(employee.getEmployeeDeptFullName() + " " + employee.getUsername())
                        .build();
                historyList.add(history);
            }

            // 부서명이 달라진 경우.
            if(existedEmployee.isPresent()){
                if(existedEmployee.get().getEmployeeDeptFullName() != null && userData.getDeptFullName()!=null){
                    if(!existedEmployee.get().getEmployeeDeptFullName().equals(userData.getDeptFullName())){
                        History history = History.builder()
                                .employee(employee)
                                .editTable("Employee")
                                .editField("employee_dept_full_name")
                                .preData(existedEmployee.get().getEmployeeDeptFullName())
                                .chgData(userData.getDeptFullName())
                                .updated_at(LocalDateTime.now())
                                .editorId("그룹웨어")
                                .fieldName("부서명")
                                .type(codeRepository.findByCategoryAndValue("type", "변경").getCode())
                                .content("부서명: " + existedEmployee.get().getEmployeeDeptFullName() + "→" + userData.getDeptFullName())
                                .build();
                       historyList.add(history);
                    }
                }
            }
        }
        employeeRepository.saveAll(employeeList);
        historyRepository.saveAll(historyList);
        return responseUserDtoList;
    }

    public List<DeptResponseDto> parseDeptDataAndSave(String jsonStr) throws JsonProcessingException{
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<DeptResponseDto> responseDeptDtoList = new ArrayList<>();
        List<Department> departmentList = new ArrayList<>();

        Map<String, List<Map<String, String>>> dataMap = objectMapper.readValue(jsonStr, Map.class);
        List<Map<String, String>> deptInfo = dataMap.get("data");

        for(Map<String, String> dept : deptInfo){
            // Parsing.
            DeptResponseDto deptData = objectMapper.convertValue(dept, DeptResponseDto.class);
            responseDeptDtoList.add(deptData);

            Department department = objectMapper.convertValue(deptData, Department.class);

            Company company = companyRepository.findById(deptData.getCompanyId()).get();
            department.setCompany(company);

            departmentList.add(department);
        }
        departmentRepository.saveAll(departmentList);
        return responseDeptDtoList;
    }

    public void saveManagerYn(){
        List<Department> departmentList = departmentRepository.findAll().stream().filter(d -> d.getMgrUserId() != null).toList();
        List<Employee> managerList = new ArrayList<>();
        for(Department department : departmentList){
            Optional<Employee> manager = employeeRepository.findByUserId(department.getMgrUserId());
            if(manager.isEmpty()){
                continue;
            }
            manager.get().setManagerYn(YES);
            managerList.add(manager.get());
        }
        employeeRepository.saveAll(managerList);
    }

    public Department departmentBuilder(UserResponseDto userData){
        Department department = Department.builder()
                .deptId(userData.getDeptId())
                .deptName(userData.getDeptName())
                .build();
        return department;
    }

    public Company companyBuilder(UserResponseDto userData){
        Company company = Company.builder()
                .companyId(userData.getCompanyId())
                .companyName(userData.getCompanyName())
                .build();
        return company;
    }
}
