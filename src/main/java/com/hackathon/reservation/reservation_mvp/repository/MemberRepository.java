package com.hackathon.reservation.reservation_mvp.repository;

import com.hackathon.reservation.reservation_mvp.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Member} entities.
 *
 * <p>Provides basic CRUD and paging operations on members.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
}