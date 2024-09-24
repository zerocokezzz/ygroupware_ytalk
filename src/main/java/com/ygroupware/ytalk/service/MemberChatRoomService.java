package com.ygroupware.ytalk.service;

import com.ygroupware.ytalk.dto.ChatRoomDTO;
import com.ygroupware.ytalk.entity.ChatRooms;
import com.ygroupware.ytalk.entity.DeletedChatRooms;
import com.ygroupware.ytalk.entity.MemberChatRoom;
import com.ygroupware.ytalk.entity.Members;
import com.ygroupware.ytalk.repository.ChatRoomsRepository;
import com.ygroupware.ytalk.repository.MemberChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberChatRoomService {

    @Autowired
    private MemberChatRoomRepository memberChatRoomRepository;

    @Autowired
    private ChatRoomsRepository chatRoomsRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberExitHistoryService memberExitHistoryService;

    @Autowired
    private DeletedChatRoomService deletedChatRoomService;

    /* 참여하고 있는 대화방 목록 */
    public List<ChatRoomDTO> getChatRoomsByMemberId(String memberId) {
        return memberChatRoomRepository.findByMemberIdAndExitedFalse(memberId)
                .stream()
                .map(mcr -> new ChatRoomDTO(mcr.getChatRoom().getIdx(), mcr.getChatRoom().getName()))
                .collect(Collectors.toList());
    }

    /* 대화방 열기 또는 생성 */
    public ChatRooms openOrCreateChatRoom(String currentUserId, String partnerId) {
        ChatRooms chatRoom = getCommonChatRooms(currentUserId, partnerId);

        if (chatRoom == null) {
            chatRoom = createChatRoom(currentUserId, partnerId);
        } else {
            MemberChatRoom memberChatRoom = memberChatRoomRepository
                    .findByChatRoom_IdxAndMember_Id(chatRoom.getIdx(), currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("대화방에서 사용자를 찾을 수 없습니다."));
            if (memberChatRoom.isExited()) {
                memberChatRoom.setExited(false);
                memberChatRoomRepository.save(memberChatRoom);
            }
        }

        return chatRoom;
    }

    /* 두 사용자가 공통으로 참여하는 대화방 찾기 */
    public ChatRooms getCommonChatRooms(String currentUserId, String partnerId) {
        return memberChatRoomRepository.findCommonChatRoomsBetweenMembers(currentUserId, partnerId);
    }

    /* 새로운 대화방 생성 */
    private ChatRooms createChatRoom(String currentUserId, String partnerId) {
        Members currentUser = memberService.findMemberById(currentUserId);
        Members partnerUser = memberService.findMemberById(partnerId);

        String chatRoomName = generateChatRoomName(currentUser.getName(), partnerUser.getName());

        ChatRooms newChatRoom = ChatRooms.builder()
                .name(chatRoomName)
                .build();

        newChatRoom = chatRoomsRepository.save(newChatRoom);

        createMemberChatRoom(newChatRoom, currentUser);
        createMemberChatRoom(newChatRoom, partnerUser);

        return newChatRoom;
    }

    /* MemberChatRoom 생성 */
    private void createMemberChatRoom(ChatRooms chatRoom, Members member) {
        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                .member(member)
                .chatRoom(chatRoom)
                .joinedAt(LocalDateTime.now())
                .exited(false)
                .build();
        memberChatRoomRepository.save(memberChatRoom);
    }

    /* 대화방 이름 생성 규칙 */
    private String generateChatRoomName(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + ", " + user2 : user2 + ", " + user1;
    }

    /* 상대방 ID 가져오기 */
    public String getPartnerId(Long chatRoomId, String currentUserId) {
        return memberChatRoomRepository.findByChatRoom_IdxAndMember_IdNot(chatRoomId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("대화방에 상대방이 존재하지 않습니다."))
                .getMember()
                .getId();
    }

    /* 대화방 나가기 */
    public void exitChatRoom(Long chatRoomId, String memberId) {
        MemberChatRoom memberChatRoom = memberChatRoomRepository
                .findByChatRoom_IdxAndMember_Id(chatRoomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("대화방에 사용자가 존재하지 않거나 이미 나갔습니다."));

        memberChatRoom.setExited(true);
        memberChatRoomRepository.save(memberChatRoom);

        memberExitHistoryService.saveMemberExitHistory(memberChatRoom);

        if (allUsersExited(chatRoomId)) {
            deleteChatRoom(chatRoomId);
        }
    }

    /* 모든 사용자가 대화방에서 나갔는지 확인 */
    private boolean allUsersExited(Long chatRoomId) {
        return memberChatRoomRepository.findByChatRoom_IdxAndExitedFalse(chatRoomId).isEmpty();
    }

    /* 대화방 삭제 처리 */
    private void deleteChatRoom(Long chatRoomId) {
        DeletedChatRooms deletedChatRoom = deletedChatRoomService.createDeletedChatRoom(chatRoomId);

        updateMemberChatRooms(chatRoomId, deletedChatRoom);

        deletedChatRoomService.moveMessagesToDeletedTable(chatRoomId, deletedChatRoom);

        deletedChatRoomService.deleteChatRoomAndMessages(chatRoomId);
    }

    /* 대화방에서 사용자의 MemberChatRoom 상태를 업데이트 */
    private void updateMemberChatRooms(Long chatRoomId, DeletedChatRooms deletedChatRoom) {
        memberChatRoomRepository.findByChatRoom_Idx(chatRoomId)
                .forEach(mcr -> {
                    mcr.setDeletedChatRoom(deletedChatRoom);
                    mcr.setChatRoom(null);
                    memberChatRoomRepository.save(mcr);
                });
    }
}

