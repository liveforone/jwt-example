package jwtexample.jwtExample.member.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MemberUtils {

    /*
    * 무작위 닉네임 생성
    * 리턴타입 : String 숫자 + String 문자
     */
    public static String makeRandomNickname() {
        return RandomStringUtils.randomAlphanumeric(5);
    }

    /*
    * 비밀 번호 체크함수
     */
    public static int checkPasswordMatching(String inputPassword, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(encoder.matches(inputPassword, password)) {
            return MemberConstants.PASSWORD_MATCH.getValue();
        }
        return MemberConstants.PASSWORD_NOT_MATCH.getValue();
    }
}
