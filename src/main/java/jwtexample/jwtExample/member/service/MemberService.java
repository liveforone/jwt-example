package jwtexample.jwtExample.member.service;

import jwtexample.jwtExample.jwt.JwtTokenProvider;
import jwtexample.jwtExample.jwt.TokenInfo;
import jwtexample.jwtExample.member.dto.MemberRequest;
import jwtexample.jwtExample.member.dto.MemberResponse;
import jwtexample.jwtExample.member.model.Member;
import jwtexample.jwtExample.member.model.Role;
import jwtexample.jwtExample.member.repository.MemberRepository;
import jwtexample.jwtExample.utility.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    private static final int DUPLICATE = 0;
    private static final int NOT_DUPLICATE = 1;
    private static final int PASSWORD_MATCH = 1;
    private static final int PASSWORD_NOT_MATCH = 0;

    //== UserResponse builder method ==//
    public MemberResponse dtoBuilder(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }

    //== dto -> entity ==//
    public Member dtoToEntity(MemberRequest member) {
        return Member.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .auth(member.getAuth())
                .nickname(member.getNickname())
                .build();
    }

    //== entity -> dto1 - detail ==//
    public MemberResponse entityToDtoDetail(Member member) {

        if (CommonUtils.isNull(member)) {
            return null;
        }
        return dtoBuilder(member);
    }

    //== 무작위 닉네임 생성 - 숫자 + 문자 ==//
    public String makeRandomNickname() {
        return RandomStringUtils.randomAlphanumeric(5);
    }

    //== 이메일 중복 검증 ==//
    public int checkDuplicateEmail(String email) {
        Member member = memberRepository.findByEmail(email);

        if (CommonUtils.isNull(member)) {
            return NOT_DUPLICATE;
        }
        return DUPLICATE;
    }

    //== 비밀번호 복호화 ==//
    public int checkPasswordMatching(String inputPassword, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(encoder.matches(inputPassword, password)) {
            return PASSWORD_MATCH;
        }
        return PASSWORD_NOT_MATCH;
    }

    public Member getMemberEntity(String email) {
        return memberRepository.findByEmail(email);
    }

    public MemberResponse getMemberByEmail(String email) {
        return entityToDtoDetail(
                memberRepository.findByEmail(email)
        );
    }

    //== 전체 유저 리턴 for admin ==//
    public List<Member> getAllMemberForAdmin() {
        return memberRepository.findAll();
    }

    //== 회원 가입 로직 ==//
    @Transactional
    public void joinUser(MemberRequest memberRequest) {
        //비밀번호 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberRequest.setPassword(passwordEncoder.encode(memberRequest.getPassword()));

        if (Objects.equals(memberRequest.getEmail(), "admin@library.com")) {
            memberRequest.setAuth(Role.ADMIN);
        } else {
            memberRequest.setAuth(Role.MEMBER);
        }
        memberRequest.setNickname(makeRandomNickname());  //무작위 닉네임 생성

        memberRepository.save(
                dtoToEntity(memberRequest)
        );
    }

    //== 로그인 - 세션과 컨텍스트홀더 사용 ==//
    @Transactional
    public TokenInfo login(MemberRequest memberRequest) {

        String email = memberRequest.getEmail();
        String password = memberRequest.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email,
                password
        );
        Authentication authentication = authenticationManagerBuilder
                .getObject()
                .authenticate(authenticationToken);

        return jwtTokenProvider.generateToken(authentication);
    }
    @Transactional
    public void deleteUser(Long userId) {
        memberRepository.deleteById(userId);
    }
}
