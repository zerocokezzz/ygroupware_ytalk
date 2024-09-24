package com.ygroupware.ytalk.repository;

import com.ygroupware.ytalk.entity.ChatRooms;
import com.ygroupware.ytalk.entity.MemberChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

    List<MemberChatRoom> findByMemberIdAndExitedFalse(String id);

    @Query("SELECT m1.chatRoom FROM MemberChatRoom m1 " +
            "JOIN MemberChatRoom m2 ON m1.chatRoom.idx = m2.chatRoom.idx " +
            "WHERE m1.member.id = :firstMemberId AND m2.member.id = :secondMemberId")
    ChatRooms findCommonChatRoomsBetweenMembers(@Param("firstMemberId") String currentUserId,
                                                      @Param("secondMemberId") String partnerId);

    Optional<MemberChatRoom> findByChatRoom_IdxAndMember_IdNot(Long chatRoomId, String currentUserId);

    Optional<MemberChatRoom> findByChatRoom_IdxAndMember_Id(Long chatRoomId, String memberId);

    List<MemberChatRoom> findByChatRoom_IdxAndExitedFalse(Long chatRoomId);

    MemberChatRoom findFirstByChatRoom_Idx(Long chatRoomId);

    List<MemberChatRoom> findByChatRoom_Idx(Long chatRoomId);

}