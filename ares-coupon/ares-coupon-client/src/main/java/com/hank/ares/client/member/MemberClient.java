package com.hank.ares.client.member;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "ares-member")
public interface MemberClient {
}
