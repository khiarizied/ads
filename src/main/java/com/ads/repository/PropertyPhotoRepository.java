package com.ads.repository;

import com.ads.model.Property;
import com.ads.model.PropertyPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyPhotoRepository extends JpaRepository<PropertyPhoto, Long> {

    List<PropertyPhoto> findByProperty(Property property);

    List<PropertyPhoto> findByPropertyOrderByDisplayOrder(Property property);

    Optional<PropertyPhoto> findByPropertyAndMainPhotoTrue(Property property);

    @Query("SELECT p FROM PropertyPhoto p WHERE p.property = :property AND p.mainPhoto = true")
    Optional<PropertyPhoto> findMainPhotoByProperty(@Param("property") Property property);

    @Query("SELECT COUNT(p) FROM PropertyPhoto p WHERE p.property = :property")
    long countByProperty(@Param("property") Property property);

    void deleteByProperty(Property property);

    @Query("SELECT p FROM PropertyPhoto p WHERE p.property.id = :propertyId ORDER BY p.displayOrder")
    List<PropertyPhoto> findByPropertyIdOrderByDisplayOrder(@Param("propertyId") Long propertyId);
}