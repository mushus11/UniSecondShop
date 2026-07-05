package online.mushu.server.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * =======
 * =======
 */

@RequestMapping("/api/image")
public class ImageController {

    @PostMapping("/commonImage")
    public String commonImage(@RequestParam(name = "images") List<MultipartFile> files,
                              @RequestParam(name = "text") List<String> texts,
                              @RequestParam(name = "userID") int id) {



        return "success";
    }
}
