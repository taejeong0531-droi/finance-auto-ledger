package com.finance.ledger.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String secretKey = "finance-auto-ledger-secret-key-finance-auto-ledger";

    public String createToken(String email) { //이메일을 넣어서 토큰 발급

        Date now = new Date();
        Date validity = new Date(now.getTime() + 1000L * 60 * 60);

        SecretKey key = getSecretKey();


        return Jwts.builder()
                .setSubject(email)//토큰 안에 사용자 이메일을 넣는 것
                .setIssuedAt(now) //토큰 발급 시간
                .setExpiration(validity)//토큰 만료시간
                .signWith(key, SignatureAlgorithm.HS256)//서버만 아는 비밀키로 서명
                .compact();
    }

    public String getEmailFromToken(String token) { //토큰을 해석해서 이메일 꺼내기
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}