package com.example.fileservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserInfo {
    private Long userId;
    // private List<String> authorities;
    private String authorities;
}
