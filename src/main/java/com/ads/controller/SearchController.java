package com.ads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/search")
public class SearchController {

    @GetMapping("/form")
    public String getSearchForm(@RequestParam String category) {
        switch (category.toLowerCase()) {
            case "cars":
                return "fragments/search-form :: carsForm";
            case "electronics":
                return "fragments/search-form :: electronicsForm";
            case "clothing":
                return "fragments/search-form :: clothingForm";
            case "real-estate":
            default:
                return "fragments/search-form :: realEstateForm";
        }
    }
}