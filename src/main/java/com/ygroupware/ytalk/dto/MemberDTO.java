package com.ygroupware.ytalk.dto;

import com.ygroupware.ytalk.entity.ChatRooms;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MemberDTO {

    private String id;

    private String name;

    private String password;

    private String emailAccount;

    private String emailDomain;

    private String role = "ROLE_USER";

    private List<ChatRooms> chatRooms;

    private boolean agreedToTerms = true;

    private boolean agreedToCollections = true;
}
