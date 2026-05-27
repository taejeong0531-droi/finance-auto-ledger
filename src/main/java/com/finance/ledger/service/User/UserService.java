package com.finance.ledger.service;

import com.finance.ledger.config.JwtTokenProvider;
import com.finance.ledger.dto.LoginRequest;
import com.finance.ledger.dto.SignupRequest;
import com.finance.ledger.entity.User;
import com.finance.ledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signup(SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getNickName()
        );

        userRepository.save(user);
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) { //사용자가 입력한 비밀번호와 DB에 암호화 저장된 비밀번호가 같은 원본인지 비교
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.createToken(user.getEmail()); //로그인을 성공하면 이메일을 담은 JWT 토큰을 만들어서 준다.
    }

    public String getMyEmail(String token) {
        return jwtTokenProvider.getEmailFromToken(token);
    }
}