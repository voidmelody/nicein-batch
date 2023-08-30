package kr.co.nice.nicein.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private String grantType;
    private String accessToken;
    private String accessTokenExpiresIn;
    private String refreshToken;
}
