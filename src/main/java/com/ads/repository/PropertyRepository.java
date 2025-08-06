package com.ads.repository;

import com.ads.model.Property;
import com.ads.model.PropertyStatus;
import com.ads.model.PropertyType;
import com.ads.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwner(User owner);

    Page<Property> findByOwner(User owner, Pageable pageable);

    List<Property> findByStatus(PropertyStatus status);

    Page<Property> findByStatus(PropertyStatus status, Pageable pageable);

    List<Property> findByType(PropertyType type);

    Page<Property> findByType(PropertyType type, Pageable pageable);

    List<Property> findByStatusAndType(PropertyStatus status, PropertyType type);

    Page<Property> findByStatusAndType(PropertyStatus status, PropertyType type, Pageable pageable);

    @Query("SELECT p FROM Property p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Property> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM Property p WHERE p.city = :city")
    List<Property> findByCity(@Param("city") String city);

    Page<Property> findByCity(String city, Pageable pageable);

    @Query("SELECT p FROM Property p WHERE " +
           "(:city IS NULL OR p.city = :city) AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:bedrooms IS NULL OR p.bedrooms >= :bedrooms)")
    Page<Property> findPropertiesWithFilters(
            @Param("city") String city,
            @Param("type") PropertyType type,
            @Param("status") PropertyStatus status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("bedrooms") Integer bedrooms,
            Pageable pageable);

    @Query("SELECT p FROM Property p WHERE " +
           "p.title LIKE %:keyword% OR p.description LIKE %:keyword% OR p.address LIKE %:keyword% OR p.city LIKE %:keyword%")
    Page<Property> searchProperties(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Property p WHERE p.owner = :owner")
    long countByOwner(@Param("owner") User owner);

    @Query("SELECT DISTINCT p.city FROM Property p ORDER BY p.city")
    List<String> findAllCities();
    
    
    default boolean deleteByIdSafe(Long id) {
        if (existsById(id)) {
            deleteById(id); // from JpaRepository
            return true;
        }
        return false;
    }

    // Optional: if you're using Spring Data JPA 2.5+, existsById is available
    boolean existsById(Long id);
}