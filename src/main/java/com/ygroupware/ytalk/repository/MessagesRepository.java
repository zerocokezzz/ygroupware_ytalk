package com.ygroupware.ytalk.repository;

import com.ygroupware.ytalk.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Long> {

    List<Messages> findByChatRoom_Idx(Long chatRoomId);

    List<Messages> findByChatRoom_IdxAndCreatedAtAfter(Long chatRoomId, LocalDateTime lastExitTime);

    List<Messages> findByReceiver_IdAndReadFalse(String receiverId);

    long countByReceiverIdAndReadFalse(String receiverId);

}
