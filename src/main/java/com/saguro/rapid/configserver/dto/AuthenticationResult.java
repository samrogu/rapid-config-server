package com.saguro.rapid.configserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResult {
    private String token;
    private UserInfo userInfo;
}
