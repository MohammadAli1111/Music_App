package music_app.Controllers;

import music_app.Model.Music;
import music_app.Model.Role;
import music_app.Model.User;
import music_app.Services.MusicService;
import music_app.Services.RoleService;
import music_app.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;

@Controller
@RequestMapping("/")
public class AdminController {
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    MusicService musicService;
    private String uploadFolder="/resources";

    @GetMapping("table")
    public ModelAndView getTable(String search){
        if (search!=null){
            return new ModelAndView("MusicTable","list"
                    ,musicService.findByIsdisplayAndTitle(false,search));
            }else {
            return new ModelAndView("MusicTable","list",musicService.findByIsdisplay(false));
        }

    }
    @GetMapping("check/{id}")
    public String getCheck(@PathVariable int id){
        Music music=musicService.findById(id);
        music.setIsdisplay(true);
        musicService.save(music);
        return "redirect:/table";
    }
    @GetMapping("delete/music/{id}")
    public String getDeleteMusic(@PathVariable int id){
        musicService.delete(id);
        return "redirect:/table";
    }

    @GetMapping("users")
    public ModelAndView getUsers(String search){
            if(search!=null){
                return new ModelAndView("Users","list",userService.findByUsername(search));
            }else {
                return new ModelAndView("Users","list",userService.findAllUsers());
            }

    }
    @GetMapping(value = "/delete/user/{id}")
    public String Delete(@PathVariable int id) {
        User user=userService.findById(id);
        userService.delete(user);
        Role role_User = new Role();
        role_User.setRole("User");
        roleService.save(role_User);
        return "redirect:/users";
    }

    @GetMapping("profile")
    public ModelAndView getPofile(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userAuth=userService.findUserByEmail(auth.getName());
        return new ModelAndView("Profile","user",userAuth);
    }
    @PostMapping(value = "profile",params = "save")
    public String postProfile(@Valid User user, @RequestParam("fileAdmin") MultipartFile file
            , HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userAuth=userService.findUserByEmail(auth.getName());


        try {
            if(file.getOriginalFilename().isEmpty()) {
               User user1=userService.findById(user.getId());
               user.setImage(user1.getImage());
               userService.saveAdmin(user);
            }else{
                String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);

                String fileName = file.getOriginalFilename();
                String filePath = Paths.get(uploadDirectory, fileName).toString();
                File dir = new File(uploadDirectory);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // Save the file locally
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                stream.write(file.getBytes());
                stream.close();

                byte[] Data = file.getBytes();

                user.setImage(Data);
                userService.saveAdmin(user);
            }

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return "redirect:/myMusic";
    }
    @PostMapping(value = "profile",params = "delete")
    public String postProfile_Detete(@Valid User user){

        userService.delete(user);

        User userAmin = new User();
        userAmin.setUsername("Admin");
        userAmin.setPassword("12345");
        userAmin.setEmail("Admin@gmail.com");
        userService.saveAdmin(userAmin);

        return "redirect:/index";
    }


    // Return the image from the user using HttpServletResponse
    @GetMapping(value = "/Image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> displayuserImage(@PathVariable int id)throws Exception {
        byte[] Data =userService.findById(id).getImage();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                .body(Data);

    }

}
