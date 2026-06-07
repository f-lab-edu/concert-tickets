package com.ticket.concert.application.category;

import com.ticket.concert.application.dto.category.request.CreateCategoryRequest;
import com.ticket.concert.domain.category.entity.Category;
import com.ticket.concert.domain.category.repository.CategoryRepository;
import com.ticket.concert.global.exception.BusinessException;
import com.ticket.concert.global.exception.constant.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void createCategory(CreateCategoryRequest request) {
        validateDuplicateName(request);

        Category category = request.toCategory();
        Category saveCategory = categoryRepository.save(category);
        log.info("[CATEGORY_SAVE] success. categoryId={}", saveCategory.getId());
    }

    private void validateDuplicateName(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY);
        }
    }

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTFOUND_CATEGORY));
    }
}
