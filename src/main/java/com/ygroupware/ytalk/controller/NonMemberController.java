package com.ygroupware.ytalk.controller;

import com.ygroupware.ytalk.dto.MemberDTO;
import com.ygroupware.ytalk.service.NonMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
public class NonMemberController {

    @Autowired
    private NonMemberService nonMemberService;

    @GetMapping("/non-member/pages-login")
    public String login() {
        return "non-member/pages-login";
    }

    @GetMapping("/non-member/pages-register")
    public String registerPage() {
        return "non-member/pages-register";
    }

    /* 회원가입 */
    @PostMapping("/non-member/pages-register")
    public String registerPage(MemberDTO memberDTO) {
        String result = nonMemberService.register(memberDTO);
        if ("success".equals(result)) {
            return "redirect:/non-member/pages-login";
        }
        return "redirect:/non-member/pages-register?error=" + URLEncoder.encode(result, StandardCharsets.UTF_8);
    }

    /* 약관 파일 읽기 유틸리티 */
    private ResponseEntity<String> readFileContent(String filePath, String errorMessage) {
        try {
            Path path = Paths.get(new ClassPathResource(filePath).getURI());
            String content = new String(Files.readAllBytes(path));
            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/non-member/terms")
    public ResponseEntity<String> getTerms() {
        return readFileContent("static/assets/terms.txt", "이용약관을 불러오는 중 오류가 발생했습니다.");
    }

    @GetMapping("/non-member/collections")
    public ResponseEntity<String> getCollections() {
        return readFileContent("static/assets/collections.txt", "개인정보 수집 및 이용 약관을 불러오는 중 오류가 발생했습니다.");
    }

    /* 유효성 검사 유틸리티 */
    private Map<String, String> validateField(String input, String regex, String validMessage, String invalidMessage) {
        Map<String, String> response = new HashMap<>();
        if (input.matches(regex)) {
            response.put("status", "valid");
            response.put("message", validMessage);
        } else {
            response.put("status", "invalid");
            response.put("message", invalidMessage);
        }
        return response;
    }

    /* 이름 유효성 검사 */
    @GetMapping("/non-member/name")
    @ResponseBody
    public Map<String, String> validateName(@RequestParam("name") String name) {
        return validateField(
                name,
                "^[가-힣a-zA-Z]+$",
                "유효한 이름입니다.",
                "이름은 한글 또는 영문으로만 입력 가능합니다."
        );
    }

    /* 이메일 계정 유효성 검사 */
    @GetMapping("/non-member/emailAccount")
    @ResponseBody
    public Map<String, String> validateEmailAccount(@RequestParam("emailAccount") String emailAccount) {
        return validateField(
                emailAccount,
                "^[a-zA-Z0-9]+$",
                "유효한 이메일 계정입니다.",
                "이메일 계정은 영문과 숫자만 입력 가능합니다."
        );
    }

    /* ID 유효성 검사 */
    @GetMapping("/non-member/id")
    @ResponseBody
    public Map<String, String> validateId(@RequestParam("id") String id) {
        return nonMemberService.isIdDuplicate(id)
                ? Map.of("status", "invalid", "message", "이미 사용중인 계정입니다.")
                : validateField(id, "^[a-zA-Z0-9]+$", "유효한 계정입니다.", "계정은 영문과 숫자만 입력 가능합니다.");
    }

    /* 암호 유효성 검사 */
    @GetMapping("/non-member/password")
    @ResponseBody
    public Map<String, String> validatePassword(@RequestParam("password") String password) {
        return validateField(
                password,
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$",
                "유효한 암호입니다.",
                "암호는 영문+숫자+특수문자 조합으로 8~15자리만 입력 가능합니다."
        );
    }
}
