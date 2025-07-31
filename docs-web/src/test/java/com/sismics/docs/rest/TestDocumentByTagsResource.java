package com.sismics.docs.rest;

import com.sismics.util.filter.TokenBasedSecurityFilter;
import org.junit.Assert;
import org.junit.Test;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Test the document by tags resource.
 */
public class TestDocumentByTagsResource extends BaseJerseyTest {
    
    /**
     * Test getting documents grouped by metadata tags.
     */
    @Test
    public void testGetDocumentsByTags() {
        // Login admin
        String adminToken = adminToken();
        
        // Create a metadata with tag
        JsonObject metadataJson = target().path("/metadata").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .put(Entity.form(new Form()
                        .param("name", "Test Metadata")
                        .param("type", "TEXT")
                        .param("tag", "TEST_TAG")), JsonObject.class);
        
        String metadataId = metadataJson.getString("id");
        
        // Create a document
        JsonObject documentJson = target().path("/document").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .put(Entity.form(new Form()
                        .param("title", "Test Document")
                        .param("language", "eng")
                        .param("metadata_id", metadataId)
                        .param("metadata_value", "Test Value")), JsonObject.class);
        
        String documentId = documentJson.getString("id");
        
        // Get documents by tags
        JsonObject response = target().path("/document/by-tags").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .get(JsonObject.class);
        
        // Verify response structure
        Assert.assertTrue("Response should contain tags array", response.containsKey("tags"));
        JsonArray tags = response.getJsonArray("tags");
        Assert.assertTrue("Should have at least one tag", tags.size() > 0);
        
        // Find our test tag
        boolean foundTestTag = false;
        for (int i = 0; i < tags.size(); i++) {
            JsonObject tag = tags.getJsonObject(i);
            if ("TEST_TAG".equals(tag.getString("tag"))) {
                foundTestTag = true;
                Assert.assertEquals("Test Metadata", tag.getString("metadata_name"));
                Assert.assertEquals(metadataId, tag.getString("metadata_id"));
                Assert.assertTrue("Should have at least one document", tag.getInt("document_count") > 0);
                
                // Check documents array
                JsonArray documents = tag.getJsonArray("documents");
                Assert.assertTrue("Should have documents", documents.size() > 0);
                
                // Verify our document is in the list
                boolean foundDocument = false;
                for (int j = 0; j < documents.size(); j++) {
                    JsonObject document = documents.getJsonObject(j);
                    if (documentId.equals(document.getString("id"))) {
                        foundDocument = true;
                        break;
                    }
                }
                Assert.assertTrue("Our document should be in the list", foundDocument);
                break;
            }
        }
        
        Assert.assertTrue("Test tag should be found", foundTestTag);
    }
    
    /**
     * Test getting documents by tags with no metadata tags.
     */
    @Test
    public void testGetDocumentsByTagsNoTags() {
        // Login admin
        String adminToken = adminToken();
        
        // Get documents by tags
        JsonObject response = target().path("/document/by-tags").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .get(JsonObject.class);
        
        // Verify response structure
        Assert.assertTrue("Response should contain tags array", response.containsKey("tags"));
        JsonArray tags = response.getJsonArray("tags");
        
        // Should return empty array if no metadata tags exist
        Assert.assertNotNull("Tags array should not be null", tags);
    }
} 