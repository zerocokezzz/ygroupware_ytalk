package com.ygroupware.ytalk.repository;

import com.ygroupware.ytalk.entity.MemberExitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberExitHistoryRepository extends JpaRepository<MemberExitHistory,Long> {

    MemberExitHistory findFirstByMemberIdAndChatRoomIdxOrderByExitedAtDesc(String memberId, Long chatRoomId);

    List<MemberExitHistory> findByChatRoom_Idx(Long chatRoomId);

}
