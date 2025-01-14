package xyz.sadiulhakim.npr;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class NprApplication implements CommandLineRunner {

//    private final RoleRepo roleRepo;
//    private final UserRepo userRepo;
//    private final PasswordEncoder passwordEncoder;
//    private final Logger LOGGER = LoggerFactory.getLogger(NprApplication.class);
//
//    public NprApplication(RoleRepo roleRepo, UserRepo userRepo, PasswordEncoder passwordEncoder) {
//        this.roleRepo = roleRepo;
//        this.userRepo = userRepo;
//        this.passwordEncoder = passwordEncoder;
//    }

    public static void main(String[] args) {
        SpringApplication.run(NprApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

//        Role roleAdmin = roleRepo.save(new Role(0, "ROLE_ADMIN", ""));
//        Role roleAdmin = roleRepo.findByRole("ROLE_ADMIN").orElse(null);
//
//        for (int i = 0; i < 100; i++) {
//            User sadiulHakim = userRepo.save(new User(0, "Test", "test" + i + "@gmail.com",
//                    passwordEncoder.encode("hakim@123"), "default.png", roleAdmin,
//                    LocalDateTime.now()));
//            userRepo.save(sadiulHakim);
//        }
//        LOGGER.info("user {} is created", sadiulHakim.getEmail());
    }
}
