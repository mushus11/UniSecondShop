package online.mushu.server.Controller;


import jakarta.annotation.Resource;
import online.mushu.server.Dto.RegisterDto;
import online.mushu.server.Dto.ReviseDto;
import online.mushu.server.Entity.UserProfile;
import online.mushu.server.Service.UserProfileService;
import online.mushu.server.Service.UserService;
import online.mushu.server.Vo.RegisterVo;
import online.mushu.server.Vo.RegisterVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/register")
    public RegisterVo register(@RequestBody RegisterDto register) {

        int id = register.getId();
        String username = "无名氏";
        String password = register.getPassword();

        online.mushu.server.Entity.User user = new online.mushu.server.Entity.User(id, username, password);
        userService.save(user);

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

        UserProfile userProfile = new UserProfile(id, college, grade, telephone, "", certified, null);
        userProfileService.saveUserProfile(userProfile);

        return new ReviseVo(id, college, grade, telephone, profile, certified);
    }

//    @PostMapping("/postImage")
//    public String postImage() {
//
//    }

}
