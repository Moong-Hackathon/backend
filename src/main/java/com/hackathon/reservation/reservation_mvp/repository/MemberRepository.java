package com.hackathon.reservation.reservation_mvp.repository;

import com.hackathon.reservation.reservation_mvp.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
