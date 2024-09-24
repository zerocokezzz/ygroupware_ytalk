package com.ygroupware.ytalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"member", "chatRoom"})
public class MemberChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Members member;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = true)
    private ChatRooms chatRoom;

    @ManyToOne
    @JoinColumn(name = "deleted_chat_room_id", nullable = true)
    private DeletedChatRooms deletedChatRoom;

    @Column(name = "exited", nullable = false)
    private boolean exited = false;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
}