package com.ygroupware.ytalk.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class PartnerDTO {

    private String name;

    private String email;

    private String phone;

}
