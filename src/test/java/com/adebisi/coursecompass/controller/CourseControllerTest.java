package com.adebisi.coursecompass.controller;

import com.adebisi.coursecompass.model.Course;
import com.adebisi.coursecompass.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired CourseRepository repository;

    @Test
    void listRendersAndSupportsFilters() throws Exception {
        mockMvc.perform(get("/courses")
                        .param("category", "PROGRAMMING")
                        .param("difficulty", "BEGINNER")
                        .param("sort", "title")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/list"))
                .andExpect(model().attributeExists("coursePage"));
    }

    @Test
    void invalidCourseIsRejectedWithPlainLanguageErrors() throws Exception {
        long before = repository.count();
        mockMvc.perform(post("/courses")
                        .param("title", "")
                        .param("instructor", "")
                        .param("description", "Too short")
                        .param("category", "")
                        .param("difficulty", "")
                        .param("durationHours", "0")
                        .param("price", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/form"))
                .andExpect(model().attributeHasFieldErrors("course", "title", "instructor",
                        "description", "category", "difficulty", "durationHours", "price"));
        assertThat(repository.count()).isEqualTo(before);
    }

    @Test
    void validCoursePersistsAndRedirectsToItsDetails() throws Exception {
        long before = repository.count();
        mockMvc.perform(post("/courses")
                        .param("title", "Test-Driven Spring")
                        .param("instructor", "Ada James")
                        .param("description", "Build reliable Spring applications through focused automated tests.")
                        .param("category", "PROGRAMMING")
                        .param("difficulty", "INTERMEDIATE")
                        .param("durationHours", "12")
                        .param("price", "15000.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/courses/*"));

        assertThat(repository.count()).isEqualTo(before + 1);
        Course saved = repository.findAll().stream()
                .filter(course -> course.getTitle().equals("Test-Driven Spring"))
                .findFirst().orElseThrow();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
