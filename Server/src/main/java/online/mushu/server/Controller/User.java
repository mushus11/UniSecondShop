package online.mushu.server.Controller;


import jakarta.annotation.Resource;
import online.mushu.server.Dto.ChangePasswordDto;
import online.mushu.server.Dto.RegisterDto;
import online.mushu.server.Dto.ReviseDto;
import online.mushu.server.Dto.UserInfDto;
import online.mushu.server.Entity.UserProfile;
import online.mushu.server.Service.UserProfileService;
import online.mushu.server.Service.UserService;
import online.mushu.server.Vo.RegisterVo;
import online.mushu.server.Vo.ReviseVo;
import online.mushu.server.Vo.UserInfVo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * =======
 * =======
 */
@RestController
@RequestMapping("/api/user")
public class User {

    @Resource
    UserService userService;
    @Resource
    UserProfileService userProfileService;

    @PostMapping("/register")
    public RegisterVo register(@RequestBody RegisterDto register) {

        int id = register.getId();
        String username = "无名氏";
        String password = register.getPassword();

        online.mushu.server.Entity.User user = new online.mushu.server.Entity.User(id, username, password);
        UserProfile userProfile = new UserProfile(id);
        userService.save(user);
        userProfileService.saveUserProfile(userProfile);
        System.out.println("注册成功");

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

        System.out.println("信息修改成功");

        return new ReviseVo(id, college, grade, telephone, profile, certified);
    }

//可行
    @PostMapping("/changePassword")
    public int changePassword(@RequestBody ChangePasswordDto dto) {
        int id = dto.getId();
        String newPassword = dto.getNewPassword();

        online.mushu.server.Entity.User user = userService.getUserById(id);
        if (user == null) {
            return 201;
        }
        user.setPassword(newPassword);
        userService.save(user);
        return 200;
    }

//    可行
    @GetMapping("/getUserInf")
    public UserInfVo getUserInf(@RequestBody UserInfDto dto) {
        int id = dto.getId();
        UserProfile userProfile = userProfileService.getUserProfile(id);
        return UserInfVo.builder()
                .id(userProfile.getID())
                .telephone(userProfile.getTelephone())
                .profile(userProfile.getProfile())
                .certified(userProfile.isCertified())
                .college(userProfile.getCollege())
                .grade(userProfile.getGrade())
                .image(userProfile.getImage())
                .build();
    }

}
