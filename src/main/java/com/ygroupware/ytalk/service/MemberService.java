package com.ygroupware.ytalk.service;

import com.ygroupware.ytalk.entity.Members;
import com.ygroupware.ytalk.repository.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemberService {

    @Autowired
    private MembersRepository membersRepository;

    /*ID로 회원을 조회*/
    public Members findMemberById(String memberId) {
        return membersRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));
    }

    /*특정 사용자 ID를 제외한 나머지 사용자 목록 가져오기 (연락처 목록)*/
    public List<Map<String, String>> getAllMemberNamesExcludingCurrentUser(String currentUserId) {
        List<Members> allMembers = membersRepository.findAll();
        return allMembers.stream()
                .filter(member -> !member.getId().equals(currentUserId))
                .map(member -> {
                    Map<String, String> memberMap = new HashMap<>();
                    memberMap.put("id", member.getId());
                    memberMap.put("name", member.getName());
                    return memberMap;
                })
                .collect(Collectors.toList());
    }

}
