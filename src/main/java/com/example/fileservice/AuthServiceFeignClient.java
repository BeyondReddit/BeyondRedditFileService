package com.example.fileservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(name = "authentication-service")
public interface AuthServiceFeignClient {
    @GetMapping("auth/getCurrentUser")
    Optional<UserInfo> getCurrentUser(@RequestHeader("Authorization") String token);
}
