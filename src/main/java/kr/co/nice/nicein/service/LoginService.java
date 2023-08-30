package kr.co.nice.nicein.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kr.co.nice.nicein.dto.LoginRequestDto;
import kr.co.nice.nicein.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.NullServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class LoginService {
    private final ObjectMapper objectMapper;

    @Value("${login.uri}")
    private String loginUri;

    public TokenResponseDto login(LoginRequestDto request) throws Exception{
        RestTemplate restTemplate = new RestTemplate();
        String uriStr = loginUri;
        URI uri = UriComponentsBuilder
                .fromUriString(uriStr)
                .encode()
                .build()
                .toUri();

        if(uri == null){
            throw new NullServiceException(ApiService.class);
        }
        String jsonStr = restTemplate.postForObject(uri, request, String.class);

        TokenResponseDto responseTokenInfo = parseToken(jsonStr);
        return responseTokenInfo;
    }

    public TokenResponseDto parseToken(String jsonStr) throws JsonProcessingException {
        TokenResponseDto responseTokenDto = new TokenResponseDto();
        Map<String, Map<String, String>> tokenInfo = objectMapper.readValue(jsonStr, Map.class);
        Map<String, String> token = tokenInfo.get("token");
        responseTokenDto = objectMapper.convertValue(token, TokenResponseDto.class);
        log.info("Process to parsing Token");
        log.info("Parsing result = {}", responseTokenDto.toString());

        return responseTokenDto;
    }
}
