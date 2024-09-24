package com.ygroupware.ytalk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Members {

    @Id
    @NotNull
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String password;

    @NotNull
    private String email;

    private String phone;

    @NotNull
    private String role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberChatRoom> memberChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Agreement> agreements = new ArrayList<>();

}
