package music_app.Controllers;

import music_app.Model.Music;
import music_app.Model.User;
import music_app.Services.MusicService;
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

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;

@Controller
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    MusicService musicService;

    private String uploadFolder="/resources";
    @Autowired
    private MyResourceHttpRequestHandler handler;

    @GetMapping("/Home")
    public String getHome(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userAuth=userService.findUserByEmail(auth.getName());
        if(userService.isAdmin(userAuth)){
            return "redirect:/table";
        }else {
            return "redirect:/index";
        }
    }

    @GetMapping(value = {"/","/index"})
    public ModelAndView index(String search,String select){
        if(search!=null && !search.isEmpty()){
            return new ModelAndView("index","list",
                    musicService.findByTitleAndCategory(search,select));
        }else if (search!=null && search.isEmpty()){
            return new ModelAndView("index","list",
                    musicService.findByCategory(select));
        }else {
            return new ModelAndView("index","list",
                    musicService.findAll());
        }

    }
    @GetMapping("/login")
    public ModelAndView login(){
        return new ModelAndView("login");
    }
    @GetMapping("/signup")
    public ModelAndView signup(){
        return new ModelAndView("signUp","user",new User());
    }
    @GetMapping("/myMusic")
    public ModelAndView getMyMusic(String search,String select){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userAuth=userService.findUserByEmail(auth.getName());
        if(search!=null && !search.isEmpty()){
            return new ModelAndView("MyMusic","list",
                    musicService.findByTitleAndCategoryAndUser(search,select,userAuth));
        }else if (search!=null && search.isEmpty()){
            return new ModelAndView("MyMusic","list",
                    musicService.findByCategoryAndUser(select,userAuth));
        }else {
            return new ModelAndView("MyMusic","list",musicService.findByUser(userAuth));
        }

    }
    @GetMapping("/addMusic")
     public ModelAndView getAddMusic(){
        return new ModelAndView("AddMusic","music",new Music());
    }
    @GetMapping("/updateMusic")
    public ModelAndView getUPdateMusic(@RequestParam("id")int id){
        Music music=musicService.findById(id);
        return new ModelAndView("UpdateMusic","music",music);
    }

    // Return the image from the database using HttpServletResponse
    @GetMapping(value = "/Image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> fromDatabaseAsHttpServResp()throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user=userService.findUserByEmail(auth.getName());
        if(user!=null && user.getImage()!=null){

            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(user.getImage());
        }else {
            byte[] image=convertImageToByte(new File("src/main/resources/static/img/music.jpg"));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                    .body(image);
        }


    }

    @PostMapping("/signup")
    public String postSignUp(@Valid User user, @RequestParam("img") MultipartFile file
            , HttpServletRequest request){
        try {
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

            byte[] imageData = file.getBytes();

            User is_user=userService.findUserByEmail(user.getEmail());
            if(is_user==null){
                user.setImage(imageData);
                userService.saveUser(user);
                return "redirect:/login";
            }else {
                return "redirect:/signup";
            }

         }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }



    }

    @PostMapping("/addMusic")
    public String postAddMusic(@Valid Music music, @RequestParam("fileMusic") MultipartFile file
            , HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userAuth=userService.findUserByEmail(auth.getName());
        try {
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

            byte[] dataMusic = file.getBytes();
            music.setData(dataMusic);
            music.setUser(userAuth);

            musicService.save(music);
        }catch (Exception exception){
            System.err.println(exception.getMessage());
        }

        return "redirect:/myMusic";
    }
    @PostMapping("/updateMusic")
    public String postUpdate(@Valid Music music, @RequestParam("fileMusic") MultipartFile file
            , HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userAuth=userService.findUserByEmail(auth.getName());


        try {
            if(file.getOriginalFilename().isEmpty()) {
                Music music1=musicService.findById(music.getId());
                music.setId(music1.getId());
                music.setUser(userAuth);
                music.setData(music1.getData());
                musicService.save(music);
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

                music.setData(Data);
                music.setUser(userAuth);
                musicService.save(music);
            }

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        return "redirect:/myMusic";
    }

    @GetMapping(value = "/delete/{id}")
    public String Delete(@PathVariable int id) {

       musicService.delete(id);

        return "redirect:/myMusic";
    }

    @GetMapping("/play/{id}")
    public void byterange( @PathVariable("id") Integer id,HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         Music music=musicService.findById(id);
        String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);

        String fileName = music.getTitle();
        String filePath = Paths.get(uploadDirectory, fileName).toString();
        File dir = new File(uploadDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Save the file locally
        File file=new File(filePath+".mp3");
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(music.getData());
        stream.close();

        request.setAttribute(MyResourceHttpRequestHandler.ATTR_FILE,file);
        handler.handleRequest(request, response);

    }

    @GetMapping("/index/download/{id}")
    public String getDownload_index(@PathVariable int id)throws Exception{
       Music music= musicService.findById(id);
        String path=System.getProperty("user.home")+"/Desktop/"+music.getTitle()+".mp3";
        // Save the file locally on Desktop
        File file=new File(path);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(music.getData());
        stream.close();
        return "redirect:/index";

    }
    @GetMapping("/users/download/{id}")
    public String getDownload_MyMusic(@PathVariable int id)throws Exception{
        Music music= musicService.findById(id);
        String path=System.getProperty("user.home")+"/Desktop/"+music.getTitle()+".mp3";
        // Save the file locally on Desktop
        File file=new File(path);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(music.getData());
        stream.close();
        return "redirect:/myMusic";

    }
    //__________________________helper method_______________________
    private byte[] convertImageToByte(File file) throws Exception {

        BufferedImage bImage1 = ImageIO.read(file);
        ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
        ImageIO.write(bImage1, "jpg", bos1);
        byte[] bytes = bos1.toByteArray();
        bos1.flush();
        bos1.close();

        return bytes;
    }
}
