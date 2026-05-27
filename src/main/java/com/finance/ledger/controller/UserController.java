package com.finance.ledger.controller;

import com.finance.ledger.dto.LoginRequest;
import com.finance.ledger.dto.SignupRequest;
import com.finance.ledger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request){
        userService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request){
        String token = userService.login(request);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("토큰이 없습니다.");
        }

        String token = authorizationHeader.replace("Bearer ", "");
        String email = userService.getMyEmail(token);

        return ResponseEntity.ok(email);
    }
}
