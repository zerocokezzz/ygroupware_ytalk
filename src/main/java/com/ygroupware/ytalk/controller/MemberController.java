package com.ygroupware.ytalk.controller;

import com.ygroupware.ytalk.entity.Members;
import com.ygroupware.ytalk.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        String id = authentication.getName();
        model.addAttribute("currentUserId", id);
        return "index";
    }

    /* 메신저 접속 */
    @GetMapping("/talk/talk-index")
    public String initializeTalkPage(Model model, Authentication authentication) {
        String id = authentication.getName();
        model.addAttribute("currentUserId", id);
        return "talk/talk-index";
    }

    /* 내정보 */
    @GetMapping("/yet/my-profile")
    public String myProfile(Model model, Authentication authentication) {
        String id = authentication.getName();

        Members members = memberService.findMemberById(id);

        model.addAttribute("myProfile", members);

        return "yet/my-profile";
    }

    /* 개발 전 */
    @GetMapping("/yet/users-profile")
    public String usersProfile(Model model, Authentication authentication) {
        String id = authentication.getName();
        model.addAttribute("currentUserId", id);
        return "yet/users-profile";
    }

}
