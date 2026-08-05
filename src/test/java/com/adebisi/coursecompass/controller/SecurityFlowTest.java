package com.adebisi.coursecompass.controller;

import com.adebisi.coursecompass.model.Category;
import com.adebisi.coursecompass.model.Course;
import com.adebisi.coursecompass.model.Difficulty;
import com.adebisi.coursecompass.model.User;
import com.adebisi.coursecompass.model.UserRole;
import com.adebisi.coursecompass.repository.CourseRepository;
import com.adebisi.coursecompass.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityFlowTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void loginPageShowsHelpfulErrorMessage() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(content().string(containsString("We could not sign you in with those credentials.")));
    }

    @Test
    void registerPersistsBcryptEncodedUser() throws Exception {
        long before = userRepository.count();

        mockMvc.perform(post("/register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("fullName", "Grace Hopper")
                        .param("email", "grace@example.com")
                        .param("role", "INSTRUCTOR")
                        .param("password", "SecurePass123!")
                        .param("confirmPassword", "SecurePass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        assertThat(userRepository.count()).isEqualTo(before + 1);
        User savedUser = userRepository.findByEmailIgnoreCase("grace@example.com").orElseThrow();
        assertThat(savedUser.getRole()).isEqualTo(UserRole.INSTRUCTOR);
        assertThat(savedUser.getPassword()).isNotEqualTo("SecurePass123!");
        assertThat(passwordEncoder.matches("SecurePass123!", savedUser.getPassword())).isTrue();
    }

    @Test
    void anonymousUserIsRedirectedFromProtectedCreatePage() throws Exception {
        mockMvc.perform(get("/courses/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void studentCannotCreateCourse() throws Exception {
        mockMvc.perform(post("/courses")
                        .with(SecurityMockMvcRequestPostProcessors.user("student@example.com").roles("STUDENT"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("title", "Unauthorized course")
                        .param("instructor", "Student User")
                        .param("description", "This submission should be rejected because students cannot publish courses.")
                        .param("category", "PROGRAMMING")
                        .param("difficulty", "BEGINNER")
                        .param("durationHours", "8")
                        .param("price", "0"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAreaRequiresAdministratorRole() throws Exception {
        mockMvc.perform(get("/admin")
                        .with(SecurityMockMvcRequestPostProcessors.user("student@example.com").roles("STUDENT")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin@coursecompass.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
    }

    @Test
    void administratorCanEditAndDeleteCourses() throws Exception {
        Course course = new Course();
        course.setTitle("Security Foundations");
        course.setInstructor("Initial Instructor");
        course.setDescription("Learn the basics of application security through practical secure coding exercises.");
        course.setCategory(Category.PROGRAMMING);
        course.setDifficulty(Difficulty.BEGINNER);
        course.setDurationHours(10);
        course.setPrice(new BigDecimal("5000.00"));
        Course saved = courseRepository.save(course);

        mockMvc.perform(post("/courses/{id}/edit", saved.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user("admin@coursecompass.local").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("title", "Security Foundations Updated")
                        .param("instructor", "Updated Instructor")
                        .param("description", "Learn the basics of application security through updated practical secure coding exercises.")
                        .param("category", "PROGRAMMING")
                        .param("difficulty", "INTERMEDIATE")
                        .param("durationHours", "12")
                        .param("price", "7500.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses/" + saved.getId()));

        Course updated = courseRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Security Foundations Updated");
        assertThat(updated.getDifficulty()).isEqualTo(Difficulty.INTERMEDIATE);

        mockMvc.perform(post("/courses/{id}/delete", saved.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user("admin@coursecompass.local").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        assertThat(courseRepository.findById(saved.getId())).isEmpty();
    }
}
