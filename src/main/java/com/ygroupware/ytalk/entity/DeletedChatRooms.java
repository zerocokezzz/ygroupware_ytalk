package com.ygroupware.ytalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deleted_chat_rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletedChatRooms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatRoomName;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "deletedChatRoom", cascade = CascadeType.ALL)
    private List<MemberChatRoom> memberChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "deletedChatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeletedMessages> deletedMessages = new ArrayList<>();
}
