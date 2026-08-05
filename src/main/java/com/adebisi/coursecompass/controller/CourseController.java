package com.adebisi.coursecompass.controller;

import com.adebisi.coursecompass.model.Category;
import com.adebisi.coursecompass.model.Course;
import com.adebisi.coursecompass.model.Difficulty;
import com.adebisi.coursecompass.repository.CourseRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "newest", "createdAt",
            "title", "title",
            "price", "price",
            "duration", "durationHours"
    );
    private static final Set<String> DIRECTIONS = Set.of("asc", "desc");

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @ModelAttribute
    void addOptions(Model model) {
        model.addAttribute("categories", Category.values());
        model.addAttribute("difficulties", Difficulty.values());
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        String safeSort = SORT_FIELDS.containsKey(sort) ? sort : "newest";
        String safeDirection = DIRECTIONS.contains(direction.toLowerCase()) ? direction.toLowerCase() : "desc";
        Sort.Direction sortDirection = Sort.Direction.fromString(safeDirection);
        Pageable pageable = PageRequest.of(Math.max(page, 0), 6,
                Sort.by(sortDirection, SORT_FIELDS.get(safeSort)).and(Sort.by("id")));

        Page<Course> courses;
        if (category != null && difficulty != null) {
            courses = courseRepository.findByCategoryAndDifficulty(category, difficulty, pageable);
        } else if (category != null) {
            courses = courseRepository.findByCategory(category, pageable);
        } else if (difficulty != null) {
            courses = courseRepository.findByDifficulty(difficulty, pageable);
        } else {
            courses = courseRepository.findAll(pageable);
        }

        if (courses.getTotalPages() > 0 && page >= courses.getTotalPages()) {
            return "redirect:/courses";
        }

        model.addAttribute("coursePage", courses);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("selectedSort", safeSort);
        model.addAttribute("selectedDirection", safeDirection);
        return "courses/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public String createForm(Model model) {
        if (!model.containsAttribute("course")) {
            model.addAttribute("course", new Course());
        }
        populateFormAttributes(model, "Add a course",
                "Give learners enough information to decide whether this course fits their goals.",
                "Publish course", "/courses");
        return "courses/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public String create(@Valid @ModelAttribute Course course, BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormAttributes(model, "Add a course",
                    "Give learners enough information to decide whether this course fits their goals.",
                    "Publish course", "/courses");
            return "courses/form";
        }
        Course saved = courseRepository.save(course);
        redirectAttributes.addFlashAttribute("successMessage",
                "Course added successfully. It is now available in the catalogue.");
        return "redirect:/courses/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        model.addAttribute("course", course);
        return "courses/details";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        model.addAttribute("course", course);
        populateFormAttributes(model, "Edit course",
                "Update this course record from the administrative workspace.",
                "Save changes", "/courses/" + id + "/edit");
        return "courses/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("course") Course course,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        if (bindingResult.hasErrors()) {
            populateFormAttributes(model, "Edit course",
                    "Update this course record from the administrative workspace.",
                    "Save changes", "/courses/" + id + "/edit");
            return "courses/form";
        }

        existing.setTitle(course.getTitle());
        existing.setInstructor(course.getInstructor());
        existing.setDescription(course.getDescription());
        existing.setCategory(course.getCategory());
        existing.setDifficulty(course.getDifficulty());
        existing.setDurationHours(course.getDurationHours());
        existing.setPrice(course.getPrice());
        courseRepository.save(existing);

        redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        return "redirect:/courses/" + existing.getId();
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        courseRepository.delete(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        return "redirect:/admin";
    }

    private void populateFormAttributes(Model model, String formTitle, String formIntro,
                                        String submitLabel, String formAction) {
        model.addAttribute("formTitle", formTitle);
        model.addAttribute("formIntro", formIntro);
        model.addAttribute("submitLabel", submitLabel);
        model.addAttribute("formAction", formAction);
    }
}
