package music_app;
import music_app.Model.Role;
import music_app.Model.User;
import music_app.Services.RoleService;
import music_app.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    public static void main(String []args){

        SpringApplication.run(App.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        if(roleService.rolefindAll().isEmpty()) {
            Role role_Admin = new Role();
            role_Admin.setRole("ADMIN");
            roleService.save(role_Admin);

            Role role_User = new Role();
            role_User.setRole("User");
            roleService.save(role_User);

            User userAmin = new User();
            userAmin.setUsername("Admin");
            userAmin.setPassword("12345");
            userAmin.setEmail("Admin@gmail.com");
            userService.saveAdmin(userAmin);
        }

    }
}
