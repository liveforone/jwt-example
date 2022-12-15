package jwtexample.jwtExample.utility;

import jakarta.servlet.http.HttpServletRequest;
import jwtexample.jwtExample.jwt.JwtAuthenticationFilter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.List;

public class CommonUtils {

    /*
     * Null 체크 함수
     * Null 이면 true 를 반환
     * Null 이 아니면 false 를 반환
     */
    public static boolean isNull(Object obj) {

        //== 일반 객체 체크 ==//
        if(obj == null) {
            return true;
        }

        //== 문자열 체크 ==//
        if ((obj instanceof String) && (((String)obj).trim().length() == 0)) {
            return true;
        }

        //== 리스트 체크 ==//
        if (obj instanceof List) {
            return ((List<?>)obj).isEmpty();
        }

        return false;
    }

    /*
     * 리다이렉트 ResponseEntity를 만드는 함수
     * input url 은 반드시 '/'로 시작해야한다.
     */
    public static ResponseEntity<String> makeRedirect(String inputUrl, HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String url = "http://localhost:8080" + inputUrl;
        String token = JwtAuthenticationFilter.resolveToken(request);
        httpHeaders.setBearerAuth(token);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                String.class
        );
    }
}
