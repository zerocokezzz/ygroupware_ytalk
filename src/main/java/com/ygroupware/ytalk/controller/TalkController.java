package com.ygroupware.ytalk.controller;

import com.ygroupware.ytalk.dto.ChatRoomDTO;
import com.ygroupware.ytalk.dto.MessageDTO;
import com.ygroupware.ytalk.dto.PartnerDTO;
import com.ygroupware.ytalk.entity.ChatRooms;
import com.ygroupware.ytalk.entity.Members;
import com.ygroupware.ytalk.entity.Messages;
import com.ygroupware.ytalk.service.MemberChatRoomService;
import com.ygroupware.ytalk.service.MemberService;
import com.ygroupware.ytalk.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TalkController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberChatRoomService memberChatRoomService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate stompMessagingTemplate;

    /*내 이름을 "나"로 변경하는 유틸 메서드*/
    private String replaceNameWithSelf(String roomName, String currentUserName) {
        return roomName.contains(currentUserName) ? roomName.replace(currentUserName, "나") : roomName;
    }

    /* 주소록 */
    @GetMapping("/talk/partners")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getPartners(Authentication authentication) {
        String id = authentication.getName();
        List<Map<String, String>> memberIdNames = memberService.getAllMemberNamesExcludingCurrentUser(id);
        return memberIdNames == null || memberIdNames.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(memberIdNames);
    }

    /* 주소록 상세 조회 */
    @GetMapping("/partners/{id}")
    @ResponseBody
    public ResponseEntity<PartnerDTO> getPartnerDetails(@PathVariable String id) {
        Members partner = memberService.findMemberById(id);
        return ResponseEntity.ok(PartnerDTO.builder()
                .name(partner.getName())
                .email(partner.getEmail())
                .phone(partner.getPhone())
                .build());
    }

    /* 참여하고 있는 대화방 목록 */
    @GetMapping("/talk/chat-rooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoomDTO>> getChatRooms(Authentication authentication) {
        String id = authentication.getName();
        String currentUserName = memberService.findMemberById(id).getName();

        List<ChatRoomDTO> chatRoomList = memberChatRoomService.getChatRoomsByMemberId(id);

        // "나"로 이름 변경
        chatRoomList.forEach(chatRoom -> chatRoom.setName(replaceNameWithSelf(chatRoom.getName(), currentUserName)));

        return ResponseEntity.ok(chatRoomList);
    }

    /* 대화방 찾기 및 생성 */
    @GetMapping("/chat-room/partner/{partnerId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> openOrCreateChatRoom(@PathVariable String partnerId, Authentication authentication) {
        String currentUserId = authentication.getName();
        ChatRooms chatRoom = memberChatRoomService.openOrCreateChatRoom(currentUserId, partnerId);

        Map<String, Object> response = Map.of("chatRoomId", chatRoom.getIdx());
        return ResponseEntity.ok(response);
    }

    /* 대화 목록 및 partnerId 반환 */
    @GetMapping("/chat-room/messages/{chatRoomId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChatRoomMessages(@PathVariable Long chatRoomId, Authentication authentication) {
        String currentUserId = authentication.getName();

        List<MessageDTO> messageDTOs = messageService.getMessagesByChatRoomId(chatRoomId, currentUserId);
        String partnerId = memberChatRoomService.getPartnerId(chatRoomId, currentUserId);

        ChatRooms chatRooms = memberChatRoomService.getCommonChatRooms(partnerId, currentUserId);
        String chatRoomName = replaceNameWithSelf(chatRooms.getName(), memberService.findMemberById(currentUserId).getName());

        Map<String, Object> response = Map.of(
                "messages", messageDTOs,
                "partnerId", partnerId,
                "roomName", chatRoomName
        );

        return ResponseEntity.ok(response);
    }

    /* 메세지 저장 */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(MessageDTO messageDTO, Authentication authentication, @Header("receiverId") String receiverId) {
        String senderId = authentication.getName();
        messageDTO = messageService.saveMessage(messageDTO, senderId, receiverId);

        String destination = "/topic/chat-room/" + messageDTO.getChatRoomId();
        stompMessagingTemplate.convertAndSend(destination, messageDTO);

        Map<String, String> notification = Map.of(
                "sender", senderId,
                "message", messageDTO.getContent(),
                "chatRoomId", String.valueOf(messageDTO.getChatRoomId())
        );

        stompMessagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    /* 대화방 나가기 */
    @PostMapping("/chat-room/exit/{chatRoomId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> exitChatRoom(@PathVariable Long chatRoomId, @RequestBody Map<String, String> payload) {
        String id = payload.get("userId");
        memberChatRoomService.exitChatRoom(chatRoomId, id);

        return ResponseEntity.ok(Map.of("message", "대화방에서 성공적으로 나갔습니다."));
    }

    /* 읽지 않은 메세지 */
    @GetMapping("/chat-room/unread-messages")
    @ResponseBody
    public ResponseEntity<List<MessageDTO>> getUnreadMessages(Authentication authentication) {
        String currentUserId = authentication.getName();
        List<Messages> unreadMessages = messageService.getUnreadMessages(currentUserId);

        List<MessageDTO> messageDTOs = unreadMessages.stream()
                .map(message -> MessageDTO.builder()
                        .idx(message.getIdx())
                        .messageId(message.getMessageId())
                        .chatRoomId(message.getChatRoom().getIdx())
                        .sender(message.getSender().getId())
                        .receiver(message.getReceiver().getId())
                        .content(message.getContent())
                        .createdAt(message.getCreatedAt())
                        .read(message.isRead())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(messageDTOs);
    }

    /* 메세지 읽음 처리 */
    @PostMapping("/chat-room/messages/mark-as-read")
    @ResponseBody
    public ResponseEntity<Void> markMessagesAsRead(Authentication authentication) {
        messageService.markMessagesAsRead(authentication.getName());
        return ResponseEntity.ok().build();
    }

    /* 읽지 않은 메세지 카운트 */
    @GetMapping("/api/notifications/unread-count")
    public ResponseEntity<Long> getUnreadMessagesCount(Authentication authentication) {
        String id = authentication.getName();
        return ResponseEntity.ok(messageService.getUnreadMessagesCount(id));
    }
}

