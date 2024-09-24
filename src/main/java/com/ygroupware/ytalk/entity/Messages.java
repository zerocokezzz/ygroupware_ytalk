package com.ygroupware.ytalk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "message_id")
    private String messageId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRooms chatRoom;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Members receiver;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Members sender;

    private String content;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "reply_to_message_id")
    private String replyToMessageId;
    private String attachment;

    @Column(name = "message_type")
    private String messageType;
    private String status;

}
