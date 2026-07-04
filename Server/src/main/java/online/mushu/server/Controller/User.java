package online.mushu.server.Controller;


import jakarta.annotation.Resource;
import online.mushu.server.Dto.RegisterDto;
import online.mushu.server.Dto.ReviseDto;
import online.mushu.server.Entity.UserProfile;
import online.mushu.server.Service.UserProfileService;
import online.mushu.server.Service.UserService;
import online.mushu.server.Vo.RegisterVo;
import online.mushu.server.Vo.ReviseVo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * =======
 * =======
 */
@RestController
@RequestMapping("/api/auth")
public class User {

    @Resource
    UserService userService;
    @Resource
    UserProfileService userProfileService;

    private String basePath = "src/main/resources/static/images/";

    @PostMapping("/register")
    public RegisterVo register(@RequestBody RegisterDto register) {

        int id = register.getId();
        String username = "无名氏";
        String password = register.getPassword();

        online.mushu.server.Entity.User user = new online.mushu.server.Entity.User(id, username, password);
        UserProfile userProfile = new UserProfile(id);
        userService.save(user);
        userProfileService.saveUserProfile(userProfile);

        return RegisterVo.Success(id);
    }

    @PostMapping("/reviseBaseInf")
    public ReviseVo revise(@RequestBody ReviseDto dto) {
        int id = dto.getId();
        String college = dto.getCollege();
        String grade = dto.getGrade();
        String telephone = dto.getTelephone();
        String profile = dto.getProfile();
        boolean certified = dto.isCertified();

        UserProfile userProfile = userProfileService.getUserProfile(id);
        userProfile.setCollege(college);
        userProfile.setGrade(grade);
        userProfile.setTelephone(telephone);
        userProfile.setProfile(profile);

        userProfileService.saveUserProfile(userProfile);

        return new ReviseVo(id, college, grade, telephone, profile, certified);
    }

    @PostMapping("/postImage")
    public String postImage(@RequestParam("image") MultipartFile file, @RequestParam int id) {

        if (file.isEmpty()) {
            return "请上传图片";
        }
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        String fileName = id + suffix;
        String path = basePath + fileName;

        File fileDir = new File(path);

        try {
            file.transferTo(fileDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UserProfile userProfile = userProfileService.getUserProfile(id);
        userProfile.setImage(path);
        userProfileService.saveUserProfile(userProfile);

        return path;

    }

}
