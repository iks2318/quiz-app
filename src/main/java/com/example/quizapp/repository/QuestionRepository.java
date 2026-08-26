package com.example.quizapp.repository;

import com.example.quizapp.entity.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Query the foreign-key target explicitly: Question has category.id, not a
    // scalar categoryId property.
    @Query("select q from Question q where q.category.id = :categoryId order by q.id")
    List<Question> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("select q from Question q where q.id = :questionId and q.category.id = :categoryId")
    Question findByIdAndCategoryId(@Param("questionId") Long questionId,
                                   @Param("categoryId") Long categoryId);

    List<Question> findByIdIn(List<Long> ids);

    long countByCategoryId(Long categoryId);

    void deleteByCategoryId(Long categoryId);
}
