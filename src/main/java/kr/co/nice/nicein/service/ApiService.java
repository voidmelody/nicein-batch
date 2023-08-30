package kr.co.nice.nicein.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import kr.co.nice.nicein.dto.LoginRequestDto;
import kr.co.nice.nicein.dto.DeptResponseDto;
import kr.co.nice.nicein.dto.TokenResponseDto;
import kr.co.nice.nicein.dto.UserResponseDto;
import kr.co.nice.nicein.entity.Employee;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.NullServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Getter
@Setter
@Service
public class ApiService {

    private final ObjectMapper objectMapper;
    private final LoginService loginService;
    private final SaveService saveService;

    List<Employee> existedEmployeeList = new ArrayList<>(); // 기존 DB의 조직도 정보
    List<Employee> updatedEmployeeList = new ArrayList<>();

    private String accessToken;
    private String refreshToken;

    @Value("${api.gwcd}")
    private String gwcd;
    @Value("${api.passwd}")
    private String passwd;

    @Value("${gwUser.uri}")
    private String gwUserUri;
    @Value("${gwDept.uri}")
    private String gwDeptUri;

    @PostConstruct
    public void init() throws Exception{
        TokenResponseDto responseToken = loginService.login(new LoginRequestDto(gwcd, passwd));
        accessToken = responseToken.getAccessToken();
        refreshToken = responseToken.getRefreshToken();
    }

    public List<UserResponseDto> getUserApi() throws Exception{
        String responseJsonString = getResponseJsonString(gwUserUri);
        List<UserResponseDto> responseList = saveService.parseEmployeeDataAndSave(responseJsonString);
        return responseList;
    }

    public List<DeptResponseDto> getDeptApi() throws Exception{
        String responseJsonString = getResponseJsonString(gwDeptUri);
        List<DeptResponseDto> responseList = saveService.parseDeptDataAndSave(responseJsonString);
        return responseList;
    }

    public String getResponseJsonString(String uriStr) throws Exception{
        RestTemplate restTemplate = new RestTemplate();
        if(!uriStr.equals(gwUserUri) && !uriStr.equals(gwDeptUri)) {
            throw new Exception("uri가 올바르지 않습니다.");
        }
        URI uri = UriComponentsBuilder
                .fromUriString(uriStr)
                .encode()
                .build()
                .toUri();
        if(uri == null){
            throw new NullServiceException(ApiService.class);
        }
        // AcessToken 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String responseJsonString = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class).getBody();
        return responseJsonString;
    }
}
