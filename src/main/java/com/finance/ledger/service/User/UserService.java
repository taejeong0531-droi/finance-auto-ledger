package com.finance.ledger.service;

import com.finance.ledger.dto.SignupRequest;
import com.finance.ledger.entity.User;
import com.finance.ledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void signup(SignupRequest request) {

        // 이메일 중복 검사
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        // User 객체 생성
        User user = new User(
                request.getEmail(),
                request.getPassword(),
                request.getNickName()
        );

        // DB 저장
        userRepository.save(user);
    }
}