package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.CategoryDto;
import com.backend.backend.domain.dto.RequestCategoryDto;
import com.backend.backend.domain.model.Category;
import com.backend.backend.domain.model.Role;
import com.backend.backend.domain.model.User;
import com.backend.backend.domain.repository.CategoryRepository;
import com.backend.backend.domain.repository.UserRepository;
import com.backend.backend.domain.service.mapper.CategoryMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public AdminCategoryService(CategoryRepository categoryRepository,
                                UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // カテゴリ一覧を取得
    public List<CategoryDto> getCategories(UserDetails userDetails) {
        // loginId取得
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new RuntimeException("Userを取得できませんでした"));
        // 管理者以外がアクセスした場合
        if (loginId == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("権限がありません");
        }
        // カテゴリ一覧を取得
        List<Category> category = categoryRepository.findAll();

        return category.stream().map(CategoryDto::from).toList();
    }

    // カテゴリ追加
    public void addCategory(UserDetails userDetails, RequestCategoryDto dto) {
        // loginId取得
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new RuntimeException("Userを取得できませんでした"));
        // 管理者以外がアクセスした場合
        if (loginId == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("権限がありません");
        }
        // カテゴリを追加
        categoryRepository.save(CategoryMapper.toCategory(dto));
    }

}
