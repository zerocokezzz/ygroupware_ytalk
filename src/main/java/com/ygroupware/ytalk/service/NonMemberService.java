package com.ygroupware.ytalk.service;

import com.ygroupware.ytalk.dto.MemberDTO;
import com.ygroupware.ytalk.entity.Agreement;
import com.ygroupware.ytalk.entity.Members;
import com.ygroupware.ytalk.repository.MembersRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class NonMemberService {

    private final MembersRepository membersRepository;
    private final PasswordEncoder passwordEncoder;

    public NonMemberService(MembersRepository membersRepository, PasswordEncoder passwordEncoder) {
        this.membersRepository = membersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ID 중복 체크 메소드 */
    public boolean isIdDuplicate(String inputId) {
        return membersRepository.existsById(inputId);
    }

    /* 회원가입 */
    public String register(MemberDTO memberDTO) {

        String inputId = StringEscapeUtils.escapeHtml4(memberDTO.getId());
        String inputEmail = StringEscapeUtils.escapeHtml4(memberDTO.getEmailAccount() + "@" + memberDTO.getEmailDomain());
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());

        Members members = Members.builder()
                .id(inputId)
                .email(inputEmail)
                .name(memberDTO.getName())
                .password(encodedPassword)
                .role(memberDTO.getRole())
                .agreements(new ArrayList<>())
                .build();

        addAgreement(members, memberDTO.isAgreedToTerms(), "terms");
        addAgreement(members, memberDTO.isAgreedToCollections(), "collections");

        membersRepository.save(members);

        return "success";
    }

    /* Agreement 추가 */
    private void addAgreement(Members member, boolean isAgreed, String agreementType) {
        if (isAgreed) {
            Agreement agreement = Agreement.builder()
                    .member(member)
                    .agreedAt(LocalDateTime.now())
                    .agreementType(agreementType)
                    .build();
            member.getAgreements().add(agreement);
        }
    }
}
