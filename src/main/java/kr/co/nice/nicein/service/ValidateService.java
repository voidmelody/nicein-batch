package kr.co.nice.nicein.service;


import kr.co.nice.nicein.entity.Employee;
import kr.co.nice.nicein.entity.History;
import kr.co.nice.nicein.entity.ItEmployee;
import kr.co.nice.nicein.repository.CodeRepository;
import kr.co.nice.nicein.repository.EmployeeRepository;
import kr.co.nice.nicein.repository.HistoryRepository;
import kr.co.nice.nicein.repository.ItEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ValidateService {
    private final ItEmployeeRepository itEmployeeRepository;
    private final EmployeeRepository employeeRepository;
    private final HistoryRepository historyRepository;
    private final CodeRepository codeRepository;
    private final LocalDateTime today = LocalDateTime.now();

    public void processCmpEndPeople(){
        List<Employee> todayCmpEndEmployeeList = new ArrayList<>();
        List<ItEmployee> itEmployeeAll = itEmployeeRepository.findAll();
        List<ItEmployee> todayCmpEndItEmployeeList = itEmployeeAll.stream().filter(e -> e.getCmpEnd() != null && e.getCmpEnd().isEqual(today.toLocalDate())).toList();

        for(ItEmployee todayCmpEndItEmployee : todayCmpEndItEmployeeList){
            Employee todayCmpEndEmployee = employeeRepository.findByUserId(todayCmpEndItEmployee.getUserId()).get();

            History history = History.builder()
                    .employee(todayCmpEndEmployee)
                    .editTable("Employee")
                    .editField("status")
                    .preData(todayCmpEndEmployee.getStatus().toString())
                    .chgData("0")
                    .updated_at(today)
                    .editorId("그룹웨어")
                    .fieldName("재직상태")
                    .type(codeRepository.findByCategoryAndValue("type", "퇴사").getCode())
                    .content(todayCmpEndItEmployee.getCmpEndReason())
                    .build();
            historyRepository.save(history);

            todayCmpEndEmployee.setStatus("0");
            todayCmpEndEmployee.setUpdateAt(LocalDateTime.now());
            todayCmpEndEmployeeList.add(todayCmpEndEmployee);
        }
        employeeRepository.saveAll(todayCmpEndEmployeeList);
    }

    public void changeCmpEndPeopleStatus(){
        List<Employee> cmpEndPeople = employeeRepository.findAll().stream().filter(e -> e.getUpdateAt().toLocalDate().isBefore(LocalDate.now())).toList();
        for(Employee employee : cmpEndPeople){
            employee.setStatus("0");
        }
        employeeRepository.saveAll(cmpEndPeople);
    }

}
