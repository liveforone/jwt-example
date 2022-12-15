package jwtexample.jwtExample.member.util;

import jwtexample.jwtExample.member.dto.MemberRequest;
import jwtexample.jwtExample.member.dto.MemberResponse;
import jwtexample.jwtExample.member.model.Member;
import jwtexample.jwtExample.utility.CommonUtils;

public class MemberMapper {

    //== dto -> entity ==//
    public static Member dtoToEntity(MemberRequest member) {
        return Member.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .auth(member.getAuth())
                .nickname(member.getNickname())
                .build();
    }

    //== UserResponse builder method ==//
    private static MemberResponse dtoBuilder(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }

    //== entity -> dto1 - detail ==//
    public static MemberResponse entityToDtoDetail(Member member) {

        if (CommonUtils.isNull(member)) {
            return null;
        }
        return MemberMapper.dtoBuilder(member);
    }
}
