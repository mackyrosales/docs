package com.sismics.docs.rest;

import com.sismics.util.filter.TokenBasedSecurityFilter;
import org.junit.Assert;
import org.junit.Test;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

/**
 * Test the metadata resource.
 * 
 * @author bgamard
 */
public class TestMetadataResource extends BaseJerseyTest {
    /**
     * Test the metadata resource.
     */
    @Test
    public void testMetadataResource() {
        // Login admin
        String adminToken = adminToken();

        // Create a metadata with tag
        JsonObject json = target().path("/metadata").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .put(Entity.form(new Form()
                        .param("name", "Test Metadata")
                        .param("type", "STRING")
                        .param("tag", "test-tag")), JsonObject.class);
        String metadataId = json.getString("id");
        Assert.assertEquals("Test Metadata", json.getString("name"));
        Assert.assertEquals("STRING", json.getString("type"));
        Assert.assertEquals("test-tag", json.getString("tag"));

        // Get the metadata list
        json = target().path("/metadata").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .get(JsonObject.class);
        Assert.assertEquals(1, json.getJsonArray("metadata").size());
        JsonObject metadata = json.getJsonArray("metadata").getJsonObject(0);
        Assert.assertEquals(metadataId, metadata.getString("id"));
        Assert.assertEquals("Test Metadata", metadata.getString("name"));
        Assert.assertEquals("STRING", metadata.getString("type"));
        Assert.assertEquals("test-tag", metadata.getString("tag"));

        // Update the metadata tag
        json = target().path("/metadata").path(metadataId).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .post(Entity.form(new Form()
                        .param("name", "Updated Metadata")
                        .param("tag", "updated-tag")), JsonObject.class);
        Assert.assertEquals("Updated Metadata", json.getString("name"));
        Assert.assertEquals("updated-tag", json.getString("tag"));

        // Verify the update in the list
        json = target().path("/metadata").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .get(JsonObject.class);
        metadata = json.getJsonArray("metadata").getJsonObject(0);
        Assert.assertEquals("Updated Metadata", metadata.getString("name"));
        Assert.assertEquals("updated-tag", metadata.getString("tag"));

        // Test metadata with empty tag
        json = target().path("/metadata").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .put(Entity.form(new Form()
                        .param("name", "No Tag Metadata")
                        .param("type", "INTEGER")), JsonObject.class);
        Assert.assertEquals("No Tag Metadata", json.getString("name"));
        Assert.assertEquals("", json.getString("tag"));

        // Delete the metadata
        Response response = target().path("/metadata").path(metadataId).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .delete();
        Assert.assertEquals(200, response.getStatus());

        // Verify deletion
        json = target().path("/metadata").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .get(JsonObject.class);
        Assert.assertEquals(1, json.getJsonArray("metadata").size());
    }
}
