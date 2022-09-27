package de.damien.funnyplaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.damien.funnyplaces.accounts.Account;
import de.damien.funnyplaces.comments.Comment;
import de.damien.funnyplaces.places.Place;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class RestHelper {

    static MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", StandardCharsets.UTF_8);
    private final String BASE_URL = "/api/v1";


    private RequestBuilder genericGet(String path, String token) {
        return MockMvcRequestBuilders
                .get(BASE_URL + path)
                .locale(Locale.ENGLISH)
                .accept(RestHelper.MEDIA_TYPE_JSON_UTF8)
                .contentType(RestHelper.MEDIA_TYPE_JSON_UTF8)
                .header("token", token);
    }

    private RequestBuilder genericGet(String path) {
        return MockMvcRequestBuilders
                .get(BASE_URL + path)
                .locale(Locale.ENGLISH)
                .accept(RestHelper.MEDIA_TYPE_JSON_UTF8)
                .contentType(RestHelper.MEDIA_TYPE_JSON_UTF8);
    }


    private RequestBuilder genericPost(String path, String content, String token) {
        return MockMvcRequestBuilders
                .post(BASE_URL + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("token", token);
    }

    private RequestBuilder genericPost(String path, String content) {
        return MockMvcRequestBuilders
                .post(BASE_URL + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    private RequestBuilder genericPostToken(String path, String token) {
        return MockMvcRequestBuilders
                .post(BASE_URL + path)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", token);
    }


    private RequestBuilder genericPatch(String path, String content, String token) {
        return MockMvcRequestBuilders
                .patch(BASE_URL + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .header("token", token);

    }

    private RequestBuilder genericDelete(String path, String token) {
        return MockMvcRequestBuilders
                .delete(BASE_URL + path)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", token);

    }

    //ACCOUNT
    public RequestBuilder signup(Account account) throws JsonProcessingException {
        return genericPost(
                "/signup",
                new ObjectMapper().writeValueAsString(account));
    }


    public RequestBuilder login(Account account) throws JsonProcessingException {
        return genericPost(
                "/login",
                new ObjectMapper().writeValueAsString(account));
    }

    public RequestBuilder logout(String token) {
        return genericPostToken(
                "/logout",
                token);
    }

    public RequestBuilder deleteAccount(String name, String password) {
        return MockMvcRequestBuilders
                .delete(BASE_URL + "/accounts/" + name)
                .contentType(MediaType.APPLICATION_JSON)
                .header("password", password);
    }


    //IMAGE
    public RequestBuilder postImage(String token) throws IOException {
        FileInputStream fis = new FileInputStream("src/test/java/de/damien/funnyplaces/test_image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", fis);

        HashMap<String, String> contentTypeParams = new HashMap<String, String>();
        contentTypeParams.put("boundary", "265001916915724");
        MediaType mediaType = new MediaType("multipart", "form-data", contentTypeParams);

        return MockMvcRequestBuilders
                .fileUpload(BASE_URL + "/images")
                .file(image)
                .contentType(mediaType)
                .characterEncoding(StandardCharsets.UTF_8)
                .header("token", token);
    }

    public RequestBuilder getImage(String id, String token) {
        return genericGet(
                "/images/" + id,
                token);
    }


    //PLACE
    public RequestBuilder postPlace(Place place, String token) throws JsonProcessingException {
        return genericPost(
                "/places",
                new ObjectMapper().writeValueAsString(place),
                token);
    }

    public RequestBuilder getPlace(String id, String token) {
        return genericGet(
                "/places/" + id,
                token);
    }

    public RequestBuilder getAllPlaces(String token) {
        return genericGet(
                "/places",
                token);
    }

    public RequestBuilder getAllCommentsByPlaceId(String id, String token) {
        return genericGet(
                "/places/" + id + "/comments",
                token);
    }

    public RequestBuilder deletePlace(String id, String token) {
        return genericDelete(
                "/places/" + id,
                token);
    }

    public RequestBuilder patchPlace(String id, Place place, String token) throws JsonProcessingException {
        return genericPatch(
                "/places/" + id,
                new ObjectMapper().writeValueAsString(place),
                token);
    }


    //COMMENT
    public RequestBuilder postComment(Comment comment, String token) throws JsonProcessingException {
        return genericPost(
                "/comments",
                new ObjectMapper().writeValueAsString(comment),
                token);
    }

    public RequestBuilder getComment(String id, String token) {
        return genericGet(
                "/comments/" + id,
                token);
    }

    public RequestBuilder deleteComment(String id, String token) {
        return genericDelete(
                "/comments/" + id,
                token);
    }

    public RequestBuilder patchComment(String id, Comment comment, String token) throws JsonProcessingException {
        return genericPatch(
                "/comments/" + id,
                new ObjectMapper().writeValueAsString(comment),
                token);
    }

}

