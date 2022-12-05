package jwtexample.jwtExample.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenInfo {

    private String grantType;  //Bearer 사용
    private String accessToken;
    private String refreshToken;
}
