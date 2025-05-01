package xyz.sadiulhakim.npr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application  {
//    private final ReviewService reviewService;
//    private final VisitorService visitorService;
//
//    public Application(ReviewService reviewService, VisitorService visitorService) {
//        this.reviewService = reviewService;
//        this.visitorService = visitorService;
//    }
//    private final RoleRepo roleRepo;
//    private final UserRepo userRepo;
//    private final PasswordEncoder passwordEncoder;
//
//    public Application(RoleRepo roleRepo, UserRepo userRepo, PasswordEncoder passwordEncoder) {
//        this.roleRepo = roleRepo;
//        this.userRepo = userRepo;
//        this.passwordEncoder = passwordEncoder;
//    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//
//        Visitor visitor = visitorService.getById(2).get();
//        for(int i=1;i<=30;i++){
//            Review review = new Review();
//            review.setReview("Test Review "+i);
//
//            int random = ThreadLocalRandom.current().nextInt(3, 6); // 6 is exclusive
//            review.setRating(random);
//            review.setVisitor(visitor);
//            reviewService.save(review,7);
//        }
//    }

//    @Override
//    public void run(String... args) {
//
//        Role roleAdmin = roleRepo.save(new Role(0, "ROLE_ADMIN", ""));
//        Role save = roleRepo.save(roleAdmin);
//
//        User user = new User(0, "Sadiul Hakim", "sadiulhakim@gmail.com", passwordEncoder.encode("Hakim@123"),
//                "user.svg", save, LocalDateTime.now());
//        userRepo.save(user);
//
//    }
}
