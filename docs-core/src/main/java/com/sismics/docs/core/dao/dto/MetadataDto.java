package com.sismics.docs.core.dao.dto;

import com.sismics.docs.core.constant.MetadataType;

/**
 * Metadata DTO.
 *
 * @author bgamard 
 */
public class MetadataDto {
    /**
     * Metadata ID.
     */
    private String id;
    
    /**
     * Name.
     */
    private String name;

    /**
     * Type.
     */
    private MetadataType type;

    /**
     * Tag (for categorization or labeling).
     */
    private String tag;

    public String getId() {
        return id;
    }

    public MetadataDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MetadataDto setName(String name) {
        this.name = name;
        return this;
    }

    public MetadataType getType() {
        return type;
    }

    public MetadataDto setType(MetadataType type) {
        this.type = type;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public MetadataDto setTag(String tag) {
        this.tag = tag;
        return this;
    }
}
