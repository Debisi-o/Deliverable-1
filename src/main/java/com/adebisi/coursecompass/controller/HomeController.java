package com.adebisi.coursecompass.controller;

import com.adebisi.coursecompass.repository.CourseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CourseRepository courseRepository;

    public HomeController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("courseCount", courseRepository.count());
        model.addAttribute("latestCourses", courseRepository.findAll(
                PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent());
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
