package jwtexample.jwtExample.member.controller;

import jwtexample.jwtExample.jwt.TokenInfo;
import jwtexample.jwtExample.member.dto.MemberRequest;
import jwtexample.jwtExample.member.dto.MemberResponse;
import jwtexample.jwtExample.member.model.Member;
import jwtexample.jwtExample.member.model.Role;
import jwtexample.jwtExample.member.service.MemberService;
import jwtexample.jwtExample.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    private static final int NOT_DUPLICATE = 1;
    private static final int PASSWORD_MATCH = 1;

    //== 메인 페이지 ==//
    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("home");
    }

    //== 회원가입 페이지 ==//
    @GetMapping("/member/signup")
    public ResponseEntity<?> signupPage() {
        return ResponseEntity.ok("회원가입페이지");
    }

    //== 회원가입 처리 ==//
    @PostMapping("/member/signup")
    public ResponseEntity<?> signup(@RequestBody MemberRequest memberRequest) {
        int checkEmail = memberService.checkDuplicateEmail(memberRequest.getEmail());

        if (checkEmail != NOT_DUPLICATE) {
            return ResponseEntity
                    .ok("중복되는 이메일이 있어 회원가입이 불가능합니다.");
        }

        String url = "/";
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        memberService.joinUser(memberRequest);
        log.info("회원 가입 성공");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    //== 로그인 페이지 ==//
    @GetMapping("/member/login")
    public ResponseEntity<?> loginPage() {
        return ResponseEntity.ok("로그인 페이지");
    }

    //== 로그인 ==//
    @PostMapping("/member/login")
    public TokenInfo loginPage(
            @RequestBody MemberRequest memberRequest
    ) {
        TokenInfo tokenInfo = memberService.login(memberRequest);
        log.info("로그인 성공!");

        return tokenInfo;
    }

    /*
    로그아웃은 시큐리티 단에서 이루어짐.
    url : /member/logout
    method : POST
     */

    //== 접근 거부 페이지 ==//
    @GetMapping("/member/prohibition")
    public ResponseEntity<?> prohibition() {
        return ResponseEntity
                .ok("접근 권한이 없습니다.");
    }

    @GetMapping("/member/my-page")
    public ResponseEntity<?> myPage(
            Principal principal
    ) {
        MemberResponse member = memberService.getMemberByEmail(principal.getName());

        return ResponseEntity.ok(member);
    }
    //== 어드민 페이지 ==//
    @GetMapping("/admin")
    public ResponseEntity<?> admin(Principal principal) {
        Member member = memberService.getMemberEntity(principal.getName());

        if (!member.getAuth().equals(Role.ADMIN)) {  //권한 검증 - auth = admin check
            log.info("어드민 페이지 접속에 실패했습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        log.info("어드민이 어드민 페이지에 접속했습니다.");
        return ResponseEntity.ok(memberService.getAllMemberForAdmin());
    }

    //== 회원 탈퇴 ==//
    @PostMapping("/member/withdraw")
    public ResponseEntity<?> userWithdraw(
            @RequestBody String password,
            Principal principal
    ) {
        Member member = memberService.getMemberEntity(principal.getName());

        if (CommonUtils.isNull(member)) {
            return ResponseEntity.ok("해당 유저를 조회할 수 없어 탈퇴가 불가능합니다.");
        }

        int checkPassword = memberService.checkPasswordMatching(
                password,
                member.getPassword()
        );

        if (checkPassword != PASSWORD_MATCH) {
            log.info("비밀번호 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
        }

        log.info("회원 : " + member.getId() + " 탈퇴 성공!!");
        memberService.deleteUser(member.getId());

        return ResponseEntity.ok("그동안 서비스를 이용해주셔서 감사합니다.");
    }
}
