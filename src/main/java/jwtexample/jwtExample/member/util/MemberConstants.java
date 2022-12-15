package jwtexample.jwtExample.member.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberConstants {
    DUPLICATE(0),
    NOT_DUPLICATE(1),
    PASSWORD_MATCH(1),
    PASSWORD_NOT_MATCH(0);

    private int value;
}
