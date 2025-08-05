package com.ads.model;

import jakarta.persistence.*;

@Entity
@Table(name = "property_photos")
public class PropertyPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "photo_data", columnDefinition = "LONGBLOB")
    private byte[] photoData;

    @Column(name = "photo_type")
    private String photoType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "is_main_photo")
    private boolean mainPhoto = false;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    // Constructors
    public PropertyPhoto() {}

    public PropertyPhoto(byte[] photoData, String photoType, String fileName) {
        this.photoData = photoData;
        this.photoType = photoType;
        this.fileName = fileName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public byte[] getPhotoData() { return photoData; }
    public void setPhotoData(byte[] photoData) { this.photoData = photoData; }

    public String getPhotoType() { return photoType; }
    public void setPhotoType(String photoType) { this.photoType = photoType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public boolean isMainPhoto() { return mainPhoto; }
    public void setMainPhoto(boolean mainPhoto) { this.mainPhoto = mainPhoto; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    // Helper methods
    public boolean hasPhoto() {
        return photoData != null && photoData.length > 0;
    }
}