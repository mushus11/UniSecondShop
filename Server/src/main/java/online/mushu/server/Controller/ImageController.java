package online.mushu.server.Controller;

import jakarta.annotation.Resource;
import online.mushu.server.Dto.ImageInfDto;
import online.mushu.server.Entity.GoodImages;
import online.mushu.server.Entity.Goods;
import online.mushu.server.Service.GoodImagesService;
import online.mushu.server.Service.GoodsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
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


}
