package com.wjp.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
class UserCenterApplicationTests {

    @Test
    void testDigest() {
        String result = DigestUtils.md5DigestAsHex(("abcd" + "password").getBytes());
        System.out.println(result);
    }
    @Test
    void contextLoads() {
    }

}
