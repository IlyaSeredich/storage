package com.cloud.cloudstorage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CloudStorageApplicationTests extends BaseIntegrationTest {

    @Test
    void contextLoads() {
    }

}
