package com.finance.ledger.dto;

import lombok.Getter;

@Getter
public class SignupRequest {

    private String email;

    private String password;

    private String nickname;
}
