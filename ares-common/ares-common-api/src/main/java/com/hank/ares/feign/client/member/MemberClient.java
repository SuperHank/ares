package com.hank.ares.feign.client.member;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "ares-member")
public interface MemberClient {
}
