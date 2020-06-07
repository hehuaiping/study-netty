package com.soulmate.netty;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StudyNettyApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void print() {
        System.out.println(1024 << 1);
        System.out.println(1024 << 10);
        System.out.println(1024 * 1024);
    }
}
