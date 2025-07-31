package com.sismics.docs.rest;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.sismics.docs.core.util.DirectoryUtil;
import com.sismics.util.filter.TokenBasedSecurityFilter;
import com.sismics.util.mime.MimeType;
import com.sismics.util.mime.MimeTypeUtil;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.Assert;
import org.junit.Test;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.zip.ZipInputStream;

/**
 * Exhaustive test of the file resource.
 * 
 * @author bgamard
 */
public class TestFileResource extends BaseJerseyTest {
    /**
     * Test the file resource.
     * 
     * @throws Exception e
     */
    @Test
    public void testFileResource() throws Exception {
        // Login file_resources
        clientUtil.createUser("file_resources");
        String file1Token = clientUtil.login("file_resources");
        
        // Create a document
        String document1Id = clientUtil.createDocument(file1Token);
        
        // Add a file
        String file1Id = clientUtil.addFileToDocument(FILE_PIA_00452_JPG, file1Token, document1Id);
        
        // Add a file
        String file2Id = clientUtil.addFileToDocument(FILE_PIA_00452_JPG, file1Token, document1Id);
        
        // Get the file data
        Response response = target().path("/file/" + file1Id + "/data").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, file1Token)
                .get();
        InputStream is = (InputStream) response.getEntity();
        byte[] fileBytes = ByteStreams.toByteArray(is);
        Assert.assertTrue(fileBytes.length > 0);
        
        // Get the thumbnail data
        response = target().path("/file/" + file1Id + "/data")
                .queryParam("size", "thumb")
                .request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, file1Token)
                .get();
        Assert.assertEquals(Status.OK, Status.fromStatusCode(response.getStatus()));
        is = (InputStream) response.getEntity();
        fileBytes = ByteStreams.toByteArray(is);
        Assert.assertTrue(fileBytes.length > 0);
        
        // Get the content data
        response = target().path("/file/" + file1Id + "/data")
                .queryParam("size", "content")
                .request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, file1Token)
                .get();
        Assert.assertEquals(Status.OK, Status.fromStatusCode(response.getStatus()));

        // Get the web data
        response = target().path("/file/" + file1Id + "/data")
                .queryParam("size", "web")
                .request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, file1Token)
                .get();
        Assert.assertEquals(Status.OK, Status.fromStatusCode(response.getStatus()));
        is = (InputStream) response.getEntity();
        fileBytes = ByteStreams.toByteArray(is);
        Assert.assertTrue(fileBytes.length > 0);
        
        // Check that the files are not readable directly from FS
        Path storedFile = DirectoryUtil.getStorageDirectory().resolve(file1Id);
        Assert.assertEquals(MimeType.DEFAULT, MimeTypeUtil.guessMimeType(storedFile, null));

        // Get all files from a document
        JsonObject json = target().path("/file/list")
                .queryParam("document_id", document1Id)
                .request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, file1Token)
                .get(JsonObject.class);
        JsonArray files = json.getJsonArray("files");
        Assert.assertEquals(2, files.size());
        
        // Delete a file
        response = target().path("/file/" + file1Id).request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, file1Token)
                .delete();
        Assert.assertEquals(Status.OK, Status.fromStatusCode(response.getStatus()));
        
        // Check that the file is deleted
        response = target().path("/file/" + file1Id + "/data").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, file1Token)
                .get();
        Assert.assertEquals(Status.NOT_FOUND, Status.fromStatusCode(response.getStatus()));
        
        // Check that the file is not readable directly from FS
        Assert.assertFalse(Files.exists(storedFile));
    }
    
    /**
     * Test file upload with filename pattern.
     */
    @Test
    public void testFileUploadWithPattern() {
        // Login admin
        String adminToken = adminToken();
        
        // Create a document first
        JsonObject json = target().path("/document").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .put(Entity.form(new MultivaluedHashMap<String, String>() {{
                    add("title", "Test Document");
                    add("language", "eng");
                }}), JsonObject.class);
        
        String documentId = json.getString("id");
        
        // Upload file with filename pattern
        String filenamePattern = "SHEL{number}";
        String fileContent = "Test file content";
        InputStream fileStream = new ByteArrayInputStream(fileContent.getBytes());
        
        MultivaluedMap<String, Object> formData = new MultivaluedHashMap<>();
        formData.add("id", documentId);
        formData.add("file", fileStream);
        formData.add("filenamePattern", filenamePattern);
        
        Response response = target().path("/file").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .put(Entity.entity(formData, MediaType.MULTIPART_FORM_DATA_TYPE));
        
        Assert.assertEquals(200, response.getStatus());
        
        JsonObject fileJson = response.readEntity(JsonObject.class);
        String fileId = fileJson.getString("id");
        String fileName = fileJson.getString("name");
        
        // Verify the filename follows the pattern (should be SHEL1)
        Assert.assertTrue("Filename should start with SHEL", fileName.startsWith("SHEL"));
        Assert.assertTrue("Filename should contain a number", fileName.matches("SHEL\\d+"));
        
        // Upload another file with the same pattern
        fileStream = new ByteArrayInputStream(fileContent.getBytes());
        formData = new MultivaluedHashMap<>();
        formData.add("id", documentId);
        formData.add("file", fileStream);
        formData.add("filenamePattern", filenamePattern);
        
        response = target().path("/file").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .put(Entity.entity(formData, MediaType.MULTIPART_FORM_DATA_TYPE));
        
        Assert.assertEquals(200, response.getStatus());
        
        fileJson = response.readEntity(JsonObject.class);
        String fileName2 = fileJson.getString("name");
        
        // Verify the second filename is SHEL2
        Assert.assertEquals("SHEL2", fileName2);
    }
    
    /**
     * Test file upload without filename pattern (should use original name).
     */
    @Test
    public void testFileUploadWithoutPattern() {
        // Login admin
        String adminToken = adminToken();
        
        // Create a document first
        JsonObject json = target().path("/document").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .put(Entity.form(new MultivaluedHashMap<String, String>() {{
                    add("title", "Test Document");
                    add("language", "eng");
                }}), JsonObject.class);
        
        String documentId = json.getString("id");
        
        // Upload file without filename pattern
        String fileContent = "Test file content";
        InputStream fileStream = new ByteArrayInputStream(fileContent.getBytes());
        
        MultivaluedMap<String, Object> formData = new MultivaluedHashMap<>();
        formData.add("id", documentId);
        formData.add("file", fileStream);
        // No filenamePattern field
        
        Response response = target().path("/file").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, adminToken)
                .put(Entity.entity(formData, MediaType.MULTIPART_FORM_DATA_TYPE));
        
        Assert.assertEquals(200, response.getStatus());
        
        JsonObject fileJson = response.readEntity(JsonObject.class);
        String fileName = fileJson.getString("name");
        
        // Should use a default name (likely based on timestamp or original name)
        Assert.assertNotNull("Filename should not be null", fileName);
        Assert.assertFalse("Filename should not be empty", fileName.isEmpty());
    }
} 