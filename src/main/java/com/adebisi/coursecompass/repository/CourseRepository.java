package com.adebisi.coursecompass.repository;

import com.adebisi.coursecompass.model.Category;
import com.adebisi.coursecompass.model.Course;
import com.adebisi.coursecompass.model.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    Page<Course> findByCategory(Category category, Pageable pageable);

    Page<Course> findByDifficulty(Difficulty difficulty, Pageable pageable);

    Page<Course> findByCategoryAndDifficulty(Category category, Difficulty difficulty, Pageable pageable);
}
