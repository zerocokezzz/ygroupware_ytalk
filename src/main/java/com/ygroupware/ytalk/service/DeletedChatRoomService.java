package com.ygroupware.ytalk.service;

import com.ygroupware.ytalk.entity.DeletedChatRooms;
import com.ygroupware.ytalk.entity.DeletedMessages;
import com.ygroupware.ytalk.entity.MemberChatRoom;
import com.ygroupware.ytalk.entity.Messages;
import com.ygroupware.ytalk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeletedChatRoomService {

    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;

    @Autowired
    private DeletedChatRoomsRepository deletedChatRoomsRepository;
    @Autowired
    private MessagesRepository messagesRepository;
    @Autowired
    private DeletedMessagesRepository deletedMessagesRepository;
    @Autowired
    private ChatRoomsRepository chatRoomsRepository;
    @Autowired
    private MemberExitHistoryService memberExitHistoryService;

    public DeletedChatRooms createDeletedChatRoom(Long chatRoomId) {
        MemberChatRoom memberChatRoom = memberChatRoomRepository
                .findFirstByChatRoom_Idx(chatRoomId);

        DeletedChatRooms deletedChatRoom = DeletedChatRooms.builder()
                .chatRoomName(memberChatRoom.getChatRoom().getName())
                .deletedAt(LocalDateTime.now())
                .build();
        deletedChatRoomsRepository.save(deletedChatRoom);

        return deletedChatRoom;
    }

    public void moveMessagesToDeletedTable(Long chatRoomId, DeletedChatRooms deletedChatRoom) {
        List<Messages> messages = messagesRepository.findByChatRoom_Idx(chatRoomId);
        for (Messages message : messages) {
            DeletedMessages deletedMessage = DeletedMessages.builder()
                    .messageId(message.getMessageId())
                    .content(message.getContent())
                    .deletedChatRoom(deletedChatRoom)
                    .deletedAt(LocalDateTime.now())
                    .sender(message.getSender())
                    .build();
            deletedMessagesRepository.save(deletedMessage);
        }
    }

    public void deleteChatRoomAndMessages(Long chatRoomId) {
        memberExitHistoryService.removeChatRoomReferences(chatRoomId);

        List<Messages> messages = messagesRepository.findByChatRoom_Idx(chatRoomId);
        messagesRepository.deleteAll(messages);

        chatRoomsRepository.deleteById(chatRoomId);
    }


}
