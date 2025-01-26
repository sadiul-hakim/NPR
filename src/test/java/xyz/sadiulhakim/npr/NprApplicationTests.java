package xyz.sadiulhakim.npr;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class NprApplicationTests {

    static ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void contextLoads() {
        modules.verify();
    }

    @Test
    void doc() {
        new Documenter(modules).writeDocumentation();
    }
}
