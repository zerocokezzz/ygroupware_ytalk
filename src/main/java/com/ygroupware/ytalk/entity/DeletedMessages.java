package com.ygroupware.ytalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletedMessages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id")
    private String messageId;

    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Members sender;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private DeletedChatRooms deletedChatRoom;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;
}
