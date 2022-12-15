package jwtexample.jwtExample.member.dto;

import jwtexample.jwtExample.member.model.Role;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRequest {

    private Long id;
    private String email;
    private String password;
    private Role auth;
    private String nickname;
}
