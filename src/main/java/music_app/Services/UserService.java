package music_app.Services;

import music_app.Model.Role;
import music_app.Model.User;
import music_app.Repositories.RoleRepository;
import music_app.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service("UserService")
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Transactional
    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(2);
        Role userRole = roleRepository.findByRole("User");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }
    @Transactional
    public User saveAdmin(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }
    @Transactional
    public void delete(User user){
        userRepository.delete(user);
    }
    @Transactional
    public User findById(int id){
        return userRepository.findById(id);
    }

    @Transactional
    public List<User> findAllUsers(){
        return userRepository.findByActive(2);
    }
    @Transactional
    public List<User> findByUsername(String username){
        return userRepository.findByUsernameAndActive(username,2);
    }


    public boolean isAdmin(User user){


        if (user.getActive()==1) {
            return true;
        } else {
            return false;
        }
    }


}