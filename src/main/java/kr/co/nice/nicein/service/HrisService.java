package kr.co.nice.nicein.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kr.co.nice.nicein.dto.HrisDto;
import kr.co.nice.nicein.dto.LoginRequestDto;
import kr.co.nice.nicein.dto.TokenResponseDto;
import kr.co.nice.nicein.entity.Company;
import kr.co.nice.nicein.entity.Employee;
import kr.co.nice.nicein.entity.ItEmployee;
import kr.co.nice.nicein.repository.CompanyRepository;
import kr.co.nice.nicein.repository.EmployeeRepository;
import kr.co.nice.nicein.repository.ItEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class HrisService {
    private final EmployeeRepository employeeRepository;
    private final ItEmployeeRepository itEmployeeRepository;
    private final CompanyRepository companyRepository;
    private final LoginService loginService;
    private final ObjectMapper objectMapper;

    @Value("${hris.uri}")
    private String hrisUri;

    @Value("${api.passwd}")
    private String passwd;


    public void start(){
        List<Company> hrisCompanyList = companyRepository.findByHrisYn("Y");
        for (Company company : hrisCompanyList) {
            try {
                log.info(company.getCompanyName() + "을 로그인합니다.");
                String gwCd = company.getCompanyId() + "nicein";
                log.info("ID = " + gwCd);
                String accessToken = loginHris(gwCd);
                log.info("accessToken = " + accessToken);
                int totalPageNum = getTotalPageNum(accessToken);
                log.info(company.getCompanyName() + " totalPageNum= " + totalPageNum);
                save(company, accessToken, totalPageNum);
            } catch (HttpClientErrorException e) {
                // 로그인 재시도
                log.error("Login error occurred for company: " + company.getCompanyName());
                String gwCd = company.getCompanyId() + "nicein";
                try {
                    String newAccessToken = loginHris(gwCd);
                    int newTotalPageNum = getTotalPageNum(newAccessToken);
                    save(company, newAccessToken, newTotalPageNum);
                } catch (Exception ex) {
                    log.error("Login retry failed for company: " + company.getCompanyName());
                    // 예외 처리 또는 다른 적절한 조치 수행
                }
            }
        }
    }

    private void save(Company company, String accessToken, int totalPageNum) {
        for (int pageNum = 0; pageNum < totalPageNum; pageNum++) {
            try {
                log.info(company.getCompanyName() + " " + pageNum);
                String responseJsonString = getResponseJsonString(accessToken, pageNum);
                List<HrisDto> hrisDtoList = parseHrisDto(responseJsonString);
                saveHrisValid(hrisDtoList);
                log.info(pageNum + " 저장완료");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 각각 회사 로그인해서 토큰 가져오기.
    public String loginHris(String gwCd){
        try {
            TokenResponseDto tokenDto = loginService.login(new LoginRequestDto(gwCd, passwd));
            String accessToken = tokenDto.getAccessToken();
            return accessToken;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // HRIS 입퇴사 정보 NICEIN에 저장
    public void saveHrisValid(List<HrisDto> hrisDtoList){
        for(HrisDto hrisDto : hrisDtoList){
            String email = hrisDto.getEmail();
            LocalDate niceStart = hrisDto.getNiceStartYmd();
            LocalDate cmpStart = hrisDto.getCompanyStartYmd();
            String employedYn = hrisDto.getEmployedYn();
            LocalDate cmpEnd = hrisDto.getRetireYmd();
            String hrisEmpId = hrisDto.getEmpId();
            String status = employedYn.equals("Y") ? "1" : "0";


            Optional<Employee> optionalEmployee = employeeRepository.findByUserEmailAndStatus(email,"1");
            if(optionalEmployee.isEmpty()){
                continue;
            }
            Employee employee = optionalEmployee.get();
            employee.setHrisId(hrisEmpId);
            employee.setStatus(status);

            employeeRepository.save(employee);
            Optional<ItEmployee> optionalItEmployee = itEmployeeRepository.findById(employee.getUserId());
            if(optionalItEmployee.isEmpty()){
                continue;
            }
            ItEmployee itEmployee = optionalItEmployee.get();
            itEmployee.setNiceStart(niceStart);
            itEmployee.setCmpStart(cmpStart);
            itEmployee.setCmpEnd(cmpEnd);
            itEmployeeRepository.save(itEmployee);
        }
    }

    public String getResponseJsonString(String accessToken,int pageNum){
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder
                .fromUriString(hrisUri + "?page=" + pageNum)
                .encode()
                .build()
                .toUri();
        // AcessToken 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String responseJsonString = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class).getBody();
        return responseJsonString;
    }

    public List<HrisDto> parseHrisDto(String json){
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<HrisDto> hrisDtoList = new ArrayList<>();
        try {
            Map<String, Map<String,Object>> dataMap =objectMapper.readValue(json,Map.class);
            Map<String, Object> data = dataMap.get("data");
            List<Map<String, String>> content = (List<Map<String, String>>) data.get("content");
            for(Map<String,String> user : content){
                HrisDto hrisDto = objectMapper.convertValue(user, HrisDto.class);
                hrisDtoList.add(hrisDto);
            }
            return hrisDtoList;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTotalPageNum(String accessToken){
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String responseJsonString = getResponseJsonString(accessToken, 0);
            Map<String, Map<String,Object>> dataMap =objectMapper.readValue(responseJsonString,Map.class);
            Map<String, Object> data = dataMap.get("data");
            int totalPages = (int)data.get("totalPages");
            return totalPages;
        } catch (HttpClientErrorException e) {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(401));
        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }

}
