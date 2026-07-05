package online.mushu.server.Controller;

import jakarta.annotation.Resource;
import online.mushu.server.Dto.ImageInfDto;
import online.mushu.server.Entity.GoodImages;
import online.mushu.server.Entity.Goods;
import online.mushu.server.Entity.UserProfile;
import online.mushu.server.Service.GoodImagesService;
import online.mushu.server.Service.GoodsService;
import online.mushu.server.Service.UserProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * =======
 * =======
 */

@RequestMapping("/api/image")
public class ImageController {

    @Resource
    GoodImagesService goodImagesService;
    @Resource
    GoodsService goodsService;
    @Resource
    UserProfileService userProfileService;

    private final String basePath = "src/main/resources/static/images/";

    @PostMapping("/commonImage")
    public String commonImage(@RequestParam(name = "images") List<MultipartFile> images,
                              @RequestParam(name = "text") String texts,
                              @RequestParam(name = "goodID") String id) {

        List<ImageInfDto> dtos = new ObjectMapper().readValue(texts, new TypeReference<List<ImageInfDto>>(){});

        if (images.size() != dtos.size()) {
            return "error";
        }

        Goods good = goodsService.getGoodsByID(id);

        for (int i = 0; i < images.size(); i++) {
            String imageID = UUID.randomUUID().toString();
            ImageInfDto dto = dtos.get(i);
            MultipartFile image = images.get(i);

            byte[] f;
            try {
                f = image.getBytes();
            } catch (IOException e) {
                return "failues";
            }

            GoodImages imageEntity = new GoodImages(imageID, good, dto.isVoucher(), dto.getText(), f);

            goodImagesService.saveImage(imageEntity);
        }

        return "success";
    }

    @PostMapping("/voucherImage")
    public String voucherImage(@RequestParam(name = "image") MultipartFile image,
                               @RequestParam(name = "text") String text,
                               @RequestParam(name = "goodID") String imageID) {
        Goods good = goodsService.getGoodsByID(imageID);
        ImageInfDto dto = new ObjectMapper().readValue(text, ImageInfDto.class);
        byte[] f;
        try {
            f = image.getBytes();
        } catch (IOException e) {
            return "failues";
        }

        GoodImages imageEntity = new GoodImages(imageID, good, dto.isVoucher(), dto.getText(), f);
        goodImagesService.saveImage(imageEntity);
        return "success";
    }

    @PostMapping("/changeImage")
    public String changeImage(@RequestParam(name = "images") List<MultipartFile> images,
                              @RequestParam(name = "text") String texts,
                              @RequestParam(name = "goodID") String id) {

        goodImagesService.deleteImage(id);

        List<ImageInfDto> dtos = new ObjectMapper().readValue(texts, new TypeReference<List<ImageInfDto>>(){});

        if (images.size() != dtos.size()) {
            return "error";
        }

        Goods good = goodsService.getGoodsByID(id);

        for (int i = 0; i < images.size(); i++) {
            String imageID = UUID.randomUUID().toString();
            ImageInfDto dto = dtos.get(i);
            MultipartFile image = images.get(i);

            byte[] f;
            try {
                f = image.getBytes();
            } catch (IOException e) {
                return "failues";
            }

            GoodImages imageEntity = new GoodImages(imageID, good, dto.isVoucher(), dto.getText(), f);

            goodImagesService.saveImage(imageEntity);
        }

        return "success";
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

    @GetMapping("/getImagesID")
    public List<String> getImages(@RequestParam(name = "goodID") String goodID) {
        List<GoodImages> imagesID = goodImagesService.getImageID(goodID);
        List<String> images = new ArrayList<>();
        for (GoodImages imageEntity : imagesID) {
            images.add(imageEntity.getId());
        }
        return images;
    }

    @GetMapping("/getImages/{imageID}")
    public ResponseEntity<byte[]> getImage(@PathVariable("imageID") String imageID) {
        GoodImages image = goodImagesService.getImage(imageID);
        byte[] data = image.getImage();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.set("Cache-Control", "max-age=86400");

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/getImageText/{imageID}")
    public String getImageText(@PathVariable("imageID") String imageID) {
        GoodImages image = goodImagesService.getImage(imageID);
        return image.getText();
    }
}
