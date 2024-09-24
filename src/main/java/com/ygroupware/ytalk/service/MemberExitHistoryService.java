package com.ygroupware.ytalk.service;

import com.ygroupware.ytalk.entity.MemberChatRoom;
import com.ygroupware.ytalk.entity.MemberExitHistory;
import com.ygroupware.ytalk.repository.MemberExitHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberExitHistoryService {

    private final MemberExitHistoryRepository memberExitHistoryRepository;

    public MemberExitHistoryService(MemberExitHistoryRepository memberExitHistoryRepository) {
        this.memberExitHistoryRepository = memberExitHistoryRepository;
    }

    /* 언제 나갔는지 */
    public void saveMemberExitHistory(MemberChatRoom memberChatRoom){
        MemberExitHistory exitHistory = MemberExitHistory.builder()
                .member(memberChatRoom.getMember())
                .chatRoom(memberChatRoom.getChatRoom())
                .exitedAt(LocalDateTime.now())
                .build();
        memberExitHistoryRepository.save(exitHistory);
    }

    /* 다시 들어오면 */
    public void removeChatRoomReferences(Long chatRoomId) {
        List<MemberExitHistory> exitHistories = memberExitHistoryRepository.findByChatRoom_Idx(chatRoomId);
        for (MemberExitHistory exitHistory : exitHistories) {
            exitHistory.setChatRoom(null);
            memberExitHistoryRepository.save(exitHistory);
        }
    }

}
