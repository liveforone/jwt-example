package jwtexample.jwtExample.member.repository;

import jwtexample.jwtExample.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);
}
