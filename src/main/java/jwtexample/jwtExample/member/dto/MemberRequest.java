package jwtexample.jwtExample.member.dto;

import jwtexample.jwtExample.member.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberRequest {

    private Long id;
    private String email;
    private String password;
    private Role auth;
    private String nickname;
}
