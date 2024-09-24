package com.ygroupware.ytalk.service;

import com.ygroupware.ytalk.dto.MessageDTO;
import com.ygroupware.ytalk.entity.ChatRooms;
import com.ygroupware.ytalk.entity.MemberExitHistory;
import com.ygroupware.ytalk.entity.Members;
import com.ygroupware.ytalk.entity.Messages;
import com.ygroupware.ytalk.repository.*;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    MessagesRepository messagesRepository;

    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private MemberChatRoomRepository memberChatRoomRepository;

    @Autowired
    private MemberExitHistoryRepository memberExitHistoryRepository;

    /* 대화방 ID로 메시지 목록 조회 */
    public List<MessageDTO> getMessagesByChatRoomId(Long chatRoomId, String currentUserId) {
        MemberExitHistory lastExitHistory = memberExitHistoryRepository
                .findFirstByMemberIdAndChatRoomIdxOrderByExitedAtDesc(currentUserId, chatRoomId);

        LocalDateTime lastExitTime = (lastExitHistory != null) ? lastExitHistory.getExitedAt() : null;

        List<Messages> messages = (lastExitTime != null)
                ? messagesRepository.findByChatRoom_IdxAndCreatedAtAfter(chatRoomId, lastExitTime)
                : messagesRepository.findByChatRoom_Idx(chatRoomId);

        return messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    /* 메시지 저장 메서드 */
    public MessageDTO saveMessage(MessageDTO messageDTO, String senderId, String receiverId) {

        Members sender = membersRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + senderId));
        Members receiver = membersRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found: " + receiverId));

        ChatRooms chatRoom = memberChatRoomRepository.findCommonChatRoomsBetweenMembers(receiverId, senderId);

        Messages message = Messages.builder()
                .messageId(messageDTO.getMessageId())
                .content(StringEscapeUtils.escapeHtml4(messageDTO.getContent()))
                .sender(sender)
                .receiver(receiver)
                .chatRoom(chatRoom)
                .createdAt(LocalDateTime.now())
                .build();

        messagesRepository.save(message);

        return convertToMessageDTO(message);
    }

    /* 읽지 않은 메시지 목록 조회 */
    public List<Messages> getUnreadMessages(String receiverId) {
        return messagesRepository.findByReceiver_IdAndReadFalse(receiverId);
    }

    /* 메시지 읽음 처리 */
    @Transactional
    public void markMessagesAsRead(String currentUserId) {
        messagesRepository.findByReceiver_IdAndReadFalse(currentUserId)
                .forEach(message -> message.setRead(true));
    }

    /* 읽지 않은 메시지 수 조회 */
    public long getUnreadMessagesCount(String receiverId) {
        return messagesRepository.countByReceiverIdAndReadFalse(receiverId);
    }

    /* 메시지 엔티티를 DTO로 변환하는 메서드 */
    private MessageDTO convertToMessageDTO(Messages message) {
        return MessageDTO.builder()
                .messageId(message.getMessageId())
                .content(message.getContent())
                .sender(message.getSender().getId())
                .chatRoomId(message.getChatRoom().getIdx())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
