package com.ads.controller;

import com.ads.dto.PropertyDto;
import com.ads.model.*;
import com.ads.service.PropertyService;
import com.ads.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    // User property dashboard
    @GetMapping("/my-properties")
    public String myProperties(Authentication authentication, Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "6") int size) {
        User currentUser = userService.findByUsername(authentication.getName());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Property> properties = propertyService.findByOwner(currentUser, pageable);

        model.addAttribute("properties", properties);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", properties.getTotalPages());
        model.addAttribute("propertyCount", propertyService.countByOwner(currentUser));

        return "properties/my-properties";
    }

    // Add new property form
    @GetMapping("/add")
    public String addPropertyForm(Model model) {
        model.addAttribute("property", new PropertyDto());
        model.addAttribute("propertyTypes", PropertyType.values());
        model.addAttribute("propertyStatuses", PropertyStatus.values());
        return "properties/add-property";
    }

    // Save new property
    @PostMapping("/add")
    public String addProperty(@Valid @ModelAttribute("property") PropertyDto propertyDto,
                             BindingResult result,
                             @RequestParam("photoFiles") List<MultipartFile> photoFiles,
                             Authentication authentication,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("propertyTypes", PropertyType.values());
            model.addAttribute("propertyStatuses", PropertyStatus.values());
            return "properties/add-property";
        }

        try {
            User currentUser = userService.findByUsername(authentication.getName());
            propertyDto.setPhotos(photoFiles);
            propertyService.save(propertyDto, currentUser);
            return "redirect:/properties/my-properties?success";
        } catch (IOException e) {
            model.addAttribute("uploadError", "Error uploading photos: " + e.getMessage());
            model.addAttribute("propertyTypes", PropertyType.values());
            model.addAttribute("propertyStatuses", PropertyStatus.values());
            return "properties/add-property";
        }
    }

    // Edit property form
    @GetMapping("/edit/{id}")
    public String editPropertyForm(@PathVariable Long id, Authentication authentication, Model model) {
        Optional<Property> property = propertyService.findById(id);
        if (property.isPresent()) {
            User currentUser = userService.findByUsername(authentication.getName());

            // Check if user owns this property
            if (!property.get().getOwner().getId().equals(currentUser.getId())) {
                return "redirect:/properties/my-properties?error=unauthorized";
            }

            PropertyDto propertyDto = convertToDto(property.get());
            model.addAttribute("property", propertyDto);
            model.addAttribute("propertyEntity", property.get());
            model.addAttribute("propertyTypes", PropertyType.values());
            model.addAttribute("propertyStatuses", PropertyStatus.values());
            return "properties/edit-property";
        }
        return "redirect:/properties/my-properties?error=notfound";
    }

    // Update property
    @PostMapping("/edit/{id}")
    public String updateProperty(@PathVariable Long id,
                                @Valid @ModelAttribute("property") PropertyDto propertyDto,
                                BindingResult result,
                                @RequestParam("photoFiles") List<MultipartFile> photoFiles,
                                Authentication authentication,
                                Model model) {
        if (result.hasErrors()) {
            Optional<Property> property = propertyService.findById(id);
            model.addAttribute("propertyEntity", property.orElse(null));
            model.addAttribute("propertyTypes", PropertyType.values());
            model.addAttribute("propertyStatuses", PropertyStatus.values());
            return "properties/edit-property";
        }

        try {
            Optional<Property> existingProperty = propertyService.findById(id);
            if (existingProperty.isPresent()) {
                User currentUser = userService.findByUsername(authentication.getName());

                // Check if user owns this property
                if (!existingProperty.get().getOwner().getId().equals(currentUser.getId())) {
                    return "redirect:/properties/my-properties?error=unauthorized";
                }

                propertyDto.setPhotos(photoFiles);
                propertyService.update(id, propertyDto);
                return "redirect:/properties/my-properties?updated";
            }
        } catch (IOException e) {
            model.addAttribute("uploadError", "Error uploading photos: " + e.getMessage());
            Optional<Property> property = propertyService.findById(id);
            model.addAttribute("propertyEntity", property.orElse(null));
            model.addAttribute("propertyTypes", PropertyType.values());
            model.addAttribute("propertyStatuses", PropertyStatus.values());
            return "properties/edit-property";
        }
        return "redirect:/properties/my-properties?error=notfound";
    }

    // Delete property
    @PostMapping("/delete/{id}")
    public String deleteProperty(@PathVariable Long id, Authentication authentication) {
        Optional<Property> property = propertyService.findById(id);
        if (property.isPresent()) {
            User currentUser = userService.findByUsername(authentication.getName());

            // Check if user owns this property
            if (property.get().getOwner().getId().equals(currentUser.getId())) {
                propertyService.delete(id);
                return "redirect:/properties/my-properties?deleted";
            }
        }
        return "redirect:/properties/my-properties?error=unauthorized";
    }

    // View property details
    @GetMapping("/view/{id}")
    public String viewProperty(@PathVariable Long id, Model model) {
        Optional<Property> property = propertyService.findById(id);
        if (property.isPresent()) {
            model.addAttribute("property", property.get());
            return "properties/view-property";
        }
        return "redirect:/";
    }

    // Public property listings
    @GetMapping("/listings")
    public String listings(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "9") int size,
                          @RequestParam(required = false) String search,
                          @RequestParam(required = false) String city,
                          @RequestParam(required = false) PropertyType type,
                          @RequestParam(required = false) PropertyStatus status,
                          @RequestParam(required = false) BigDecimal minPrice,
                          @RequestParam(required = false) BigDecimal maxPrice,
                          @RequestParam(required = false) Integer bedrooms) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Property> properties;

        if (search != null && !search.trim().isEmpty()) {
            properties = propertyService.searchProperties(search.trim(), pageable);
        } else {
            properties = propertyService.findPropertiesWithFilters(city, type, status, minPrice, maxPrice, bedrooms, pageable);
        }

        model.addAttribute("properties", properties);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", properties.getTotalPages());
        model.addAttribute("cities", propertyService.getAllCities());
        model.addAttribute("propertyTypes", PropertyType.values());
        model.addAttribute("propertyStatuses", PropertyStatus.values());

        // Keep filter values for form
        model.addAttribute("search", search);
        model.addAttribute("selectedCity", city);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("selectedBedrooms", bedrooms);

        return "properties/listings";
    }

    // Get property photo
    @GetMapping("/photo/{photoId}")
    public ResponseEntity<byte[]> getPropertyPhoto(@PathVariable Long photoId) {
        Optional<PropertyPhoto> photo = propertyService.findPhotoById(photoId);
        if (photo.isPresent() && photo.get().hasPhoto()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(photo.get().getPhotoType()));
            return new ResponseEntity<>(photo.get().getPhotoData(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Delete photo
    @PostMapping("/delete-photo/{photoId}")
    public String deletePhoto(@PathVariable Long photoId, Authentication authentication) {
        Optional<PropertyPhoto> photo = propertyService.findPhotoById(photoId);
        if (photo.isPresent()) {
            User currentUser = userService.findByUsername(authentication.getName());

            // Check if user owns this property
            if (photo.get().getProperty().getOwner().getId().equals(currentUser.getId())) {
                Long propertyId = photo.get().getProperty().getId();
                propertyService.deletePhoto(photoId);
                return "redirect:/properties/edit/" + propertyId + "?photoDeleted";
            }
        }
        return "redirect:/properties/my-properties?error=unauthorized";
    }

    private PropertyDto convertToDto(Property property) {
        PropertyDto dto = new PropertyDto();
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPrice(property.getPrice());
        dto.setType(property.getType());
        dto.setStatus(property.getStatus());
        dto.setAddress(property.getAddress());
        dto.setCity(property.getCity());
        dto.setState(property.getState());
        dto.setZipCode(property.getZipCode());
        dto.setBedrooms(property.getBedrooms());
        dto.setBathrooms(property.getBathrooms());
        dto.setArea(property.getArea());
        dto.setYearBuilt(property.getYearBuilt());
        return dto;
    }
}