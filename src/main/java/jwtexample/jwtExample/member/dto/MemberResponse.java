package jwtexample.jwtExample.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberResponse {

    private Long id;
    private String email;
    private String nickname;

    @Builder
    public MemberResponse(
            Long id,
            String email,
            String nickname
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
