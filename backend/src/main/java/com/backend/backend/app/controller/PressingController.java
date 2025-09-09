package com.backend.backend.app.controller;

import com.backend.backend.domain.model.User;
import com.backend.backend.domain.repository.UserRepository;
import com.backend.backend.domain.service.s3.PresignService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PressingController {

    private final UserRepository userRepository;
    private final PresignService pressingService;

    public PressingController(UserRepository userRepository,
                              PresignService pressingService) {
        this.userRepository = userRepository;
        this.pressingService = pressingService;
    }

    // 署名URLを作成
    @PostMapping("/api/resign/put")
    public Map<String, String> resignPut(@RequestParam String filename, @RequestParam String scope, @AuthenticationPrincipal UserDetails userDetails) {
        // scope: "restaurant-image" | "dish-image" | "dish-model"
        String ext = filename.substring(filename.lastIndexOf('.')+1).toLowerCase();
        String contentType = switch (ext) {
            case "jpg","jpeg" -> "image/jpeg";
            case "png"        -> "image/png";
            case "webp"       -> "image/webp";
            case "glb"        -> "model/gltf-binary";
            case "gltf"       -> "model/gltf+json";
            default           -> "application/octet-stream";
        };
        // loginId取得
        String loginId = userDetails.getUsername();
        // ログインUserを取得
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません: " + loginId));;

        String prefix = switch (scope) {
            case "restaurant-image" -> "restaurants/tmp/%s/images/".formatted(user.getId());
            case "dish-image"       -> "dishes/tmp/%s/images/".formatted(user.getId());
            case "dish-model"       -> "dishes/tmp/%s/models/".formatted(user.getId());
            default -> throw new IllegalArgumentException("scope");
        };
        String key = prefix + java.time.LocalDate.now() + "/" + java.util.UUID.randomUUID() + "." + ext;
        return pressingService.createPutUrl(key, contentType); // {url,key,contentType}
    }

}
