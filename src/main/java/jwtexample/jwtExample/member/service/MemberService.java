package jwtexample.jwtExample.member.service;

import jwtexample.jwtExample.jwt.JwtTokenProvider;
import jwtexample.jwtExample.jwt.TokenInfo;
import jwtexample.jwtExample.member.dto.MemberRequest;
import jwtexample.jwtExample.member.dto.MemberResponse;
import jwtexample.jwtExample.member.model.Member;
import jwtexample.jwtExample.member.model.Role;
import jwtexample.jwtExample.member.repository.MemberRepository;
import jwtexample.jwtExample.member.util.MemberConstants;
import jwtexample.jwtExample.member.util.MemberMapper;
import jwtexample.jwtExample.member.util.MemberUtils;
import jwtexample.jwtExample.utility.CommonUtils;
import lombok.extern.slf4j.Slf4j;
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

    public int checkDuplicateEmail(String email) {
        Member member = memberRepository.findByEmail(email);

        if (CommonUtils.isNull(member)) {
            return MemberConstants.NOT_DUPLICATE.getValue();
        }
        return MemberConstants.DUPLICATE.getValue();
    }

    public Member getMemberEntity(String email) {
        return memberRepository.findByEmail(email);
    }

    public MemberResponse getMemberByEmail(String email) {
        return MemberMapper.entityToDtoDetail(
                memberRepository.findByEmail(email)
        );
    }

    public List<Member> getAllMemberForAdmin() {
        return memberRepository.findAll();
    }

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
        memberRequest.setNickname(MemberUtils.makeRandomNickname());  //무작위 닉네임 생성

        memberRepository.save(
                MemberMapper.dtoToEntity(memberRequest)
        );
    }

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
