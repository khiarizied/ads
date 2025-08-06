package com.ads.service;

import com.ads.dto.PropertyDto;
import com.ads.model.*;
import com.ads.repository.PropertyRepository;
import com.ads.repository.PropertyPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyPhotoRepository propertyPhotoRepository;

    public Property save(PropertyDto propertyDto, User owner) throws IOException {
        Property property = convertToEntity(propertyDto);
        property.setOwner(owner);

        // Save property first
        property = propertyRepository.save(property);

        // Handle photo uploads
        if (propertyDto.getPhotos() != null && !propertyDto.getPhotos().isEmpty()) {
            savePropertyPhotos(property, propertyDto.getPhotos());
        }

        return property;
    }

    public Property update(Long id, PropertyDto propertyDto) throws IOException {
        Optional<Property> existingProperty = propertyRepository.findById(id);
        if (existingProperty.isPresent()) {
            Property property = existingProperty.get();
            updatePropertyFromDto(property, propertyDto);

            // Handle new photo uploads
            if (propertyDto.getPhotos() != null && !propertyDto.getPhotos().isEmpty()) {
                savePropertyPhotos(property, propertyDto.getPhotos());
            }

            return propertyRepository.save(property);
        }
        throw new RuntimeException("Property not found with id: " + id);
    }

    public void delete(Long id) {
        propertyRepository.deleteById(id);
    }

    public Optional<Property> findById(Long id) {
        return propertyRepository.findById(id);
    }

    public List<Property> findAll() {
        return propertyRepository.findAll();
    }

    public Page<Property> findAll(Pageable pageable) {
        return propertyRepository.findAll(pageable);
    }

    public List<Property> findByOwner(User owner) {
        return propertyRepository.findByOwner(owner);
    }

    public Page<Property> findByOwner(User owner, Pageable pageable) {
        return propertyRepository.findByOwner(owner, pageable);
    }

    public Page<Property> findByStatus(PropertyStatus status, Pageable pageable) {
        return propertyRepository.findByStatus(status, pageable);
    }

    public Page<Property> findByType(PropertyType type, Pageable pageable) {
        return propertyRepository.findByType(type, pageable);
    }

    public Page<Property> searchProperties(String keyword, Pageable pageable) {
        return propertyRepository.searchProperties(keyword, pageable);
    }

    public Page<Property> findPropertiesWithFilters(String city, PropertyType type, PropertyStatus status, 
                                                   BigDecimal minPrice, BigDecimal maxPrice, 
                                                   Integer bedrooms, Pageable pageable) {
        return propertyRepository.findPropertiesWithFilters(city, type, status, minPrice, maxPrice, bedrooms, pageable);
    }

    public List<String> getAllCities() {
        return propertyRepository.findAllCities();
    }

    public long countByOwner(User owner) {
        return propertyRepository.countByOwner(owner);
    }

    public void deletePhoto(Long photoId) {
        propertyPhotoRepository.deleteById(photoId);
    }
    
    public Optional<PropertyPhoto> findPhotoById(Long photoId) {
        return propertyPhotoRepository.findById(photoId);
    }
    public boolean deletePhotoById(Long photoId) {
		return propertyPhotoRepository.deleteByIdSafe(photoId);
    	
    	
    	
    }
    private Property convertToEntity(PropertyDto propertyDto) {
        Property property = new Property();
        property.setTitle(propertyDto.getTitle());
        property.setDescription(propertyDto.getDescription());
        property.setPrice(propertyDto.getPrice());
        property.setType(propertyDto.getType());
        property.setStatus(propertyDto.getStatus());
        property.setAddress(propertyDto.getAddress());
        property.setCity(propertyDto.getCity());
        property.setState(propertyDto.getState());
        property.setZipCode(propertyDto.getZipCode());
        property.setBedrooms(propertyDto.getBedrooms());
        property.setBathrooms(propertyDto.getBathrooms());
        property.setArea(propertyDto.getArea());
        property.setYearBuilt(propertyDto.getYearBuilt());
        return property;
    }

    private void updatePropertyFromDto(Property property, PropertyDto propertyDto) {
        property.setTitle(propertyDto.getTitle());
        property.setDescription(propertyDto.getDescription());
        property.setPrice(propertyDto.getPrice());
        property.setType(propertyDto.getType());
        property.setStatus(propertyDto.getStatus());
        property.setAddress(propertyDto.getAddress());
        property.setCity(propertyDto.getCity());
        property.setState(propertyDto.getState());
        property.setZipCode(propertyDto.getZipCode());
        property.setBedrooms(propertyDto.getBedrooms());
        property.setBathrooms(propertyDto.getBathrooms());
        property.setArea(propertyDto.getArea());
        property.setYearBuilt(propertyDto.getYearBuilt());
    }

    private void savePropertyPhotos(Property property, List<MultipartFile> photoFiles) throws IOException {
        int displayOrder = (int) propertyPhotoRepository.countByProperty(property);

        for (int i = 0; i < photoFiles.size(); i++) {
            MultipartFile file = photoFiles.get(i);
            if (file != null && !file.isEmpty()) {
                PropertyPhoto photo = new PropertyPhoto();
                photo.setPhotoData(file.getBytes());
                photo.setPhotoType(file.getContentType());
                photo.setFileName(file.getOriginalFilename());
                photo.setDisplayOrder(displayOrder + i);
                photo.setProperty(property);

                // Set first photo as main photo if no main photo exists
                if (propertyPhotoRepository.findMainPhotoByProperty(property).isEmpty() && i == 0) {
                    photo.setMainPhoto(true);
                }

                propertyPhotoRepository.save(photo);
            }
        }
    }
}