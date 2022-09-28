package de.damien.funnyplaces;

import de.damien.funnyplaces.accounts.Account;
import de.damien.funnyplaces.comments.Comment;
import de.damien.funnyplaces.images.Image;
import de.damien.funnyplaces.places.Place;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@TestPropertySource("classpath:localtest.properties")
@EnableWebMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RestTest extends RestHelper {

    private final Account account = new Account(
            "KenThompson",
            "ILoveC",
            null,
            null);
    private Place placeData = new Place(
            null,
            "Stein mit Gesicht",
            "Das ist ein Stein mit Gesicht",
            account,
            42.42424242,
            42.12345678,
            null,
            null
    );

    private static String sessionToken = "";
    private static String imageId;
    private static String placeId;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Order(10)
    public void signupCorrectTest() throws Exception {
        mvc.perform(super.signup(account))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(15)
    public void signupErrorUserExistsAlreadyTest() throws Exception {
        Account accTemp = new Account("KenThompson", "MoneroRocks", null, null);
        mvc.perform(super.signup(accTemp))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @Order(20)
    public void loginErrorForbiddenTest() throws Exception {
        Account wrongCredentials = new Account("NikolaT", "MuskSucks", null, null);
        mvc.perform(super.login(wrongCredentials))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(22)
    public void loginErrorAlreadyLoggedInTest() throws Exception {
        Account accTemp = new Account(
                "Damien3",
                "TikTokSucks",
                null,
                null);
        mvc.perform(super.signup(accTemp))
                .andExpect(status().isOk());

        mvc.perform(super.login(accTemp))
                .andExpect(status().isOk());
        mvc.perform(super.login(accTemp))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(30)
    public void loginCorrectTest() throws Exception {
        MvcResult result = mvc.perform(super.login(account))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.hasLength(32)))
                .andReturn();
        RestTest.sessionToken = result.getResponse().getContentAsString();
    }

    @Test
    @Order(33)
    public void logoutCorrectTest() throws Exception {
        Account accTemp = new Account(
                "Damien",
                "TikTokSucks",
                null,
                null);
        mvc.perform(super.signup(accTemp))
                .andExpect(status().isOk());

        MvcResult result = mvc.perform(super.login(accTemp))
                .andExpect(status().isOk())
                .andReturn();
        String tokenTemp = result.getResponse().getContentAsString();

        mvc.perform(super.logout(tokenTemp))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(accTemp.getName()));
    }

    @Test
    @Order(33)
    public void logoutErrorForbiddenTest() throws Exception {
        Account accTemp = new Account(
                "Damien2",
                "TikTokStillSucks",
                null,
                null);
        mvc.perform(super.signup(accTemp))
                .andExpect(status().isOk());

        MvcResult result = mvc.perform(super.login(accTemp))
                .andExpect(status().isOk())
                .andReturn();
        String tokenTemp = result.getResponse().getContentAsString();

        mvc.perform(super.logout("notARealToken"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
    @Order(35)
    public void deleteAccountErrorForbiddenTest() throws Exception {
        mvc.perform(super.deleteAccount("Damien", "FacebookSucks"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(36)
    public void deleteAccountErrorNotFoundTest() throws Exception {
        mvc.perform(super.deleteAccount("OBIS", "ObisIsCool"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(37)
    public void deleteAccountCorrectTest() throws Exception {
        mvc.perform(super.deleteAccount("Damien", "TikTokSucks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Damien"))
                .andReturn();
    }

    @Test
    @Order(40)
    public void postImageCorrectTest() throws Exception {
        MvcResult result = mvc.perform(super.postImage(RestTest.sessionToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        RestTest.imageId = result.getResponse().getContentAsString();
    }

    @Test
    @Order(43)
    public void postImageErrorForbiddenTest() throws Exception {
        mvc.perform(super.postImage("someWrongToken"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
    @Order(50)
    public void getImageCorrectTest() throws Exception {
        mvc.perform(super.getImage(imageId, sessionToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(53)
    public void getImageErrorForbiddenTest() throws Exception {
        mvc.perform(super.getImage(imageId, "SomeWrongTokenAgain"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(55)
    public void getImageErrorNotFoundTest() throws Exception {
        mvc.perform(super.getImage("42", sessionToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(60)
    public void postPlaceCorrectTest() throws Exception {
        Image i = new Image(Long.parseLong(imageId));
        placeData.setImage(i);
        MvcResult result = mvc.perform(super.postPlace(placeData, sessionToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        JSONObject placeResponseJson = new JSONObject(result.getResponse().getContentAsString());
        RestTest.placeId = placeResponseJson.getString("placeId");
    }

    @Test
    @Order(63)
    public void postPlaceErrorForbiddenTest() throws Exception {
        Image i = new Image(Long.parseLong(imageId));
        placeData.setImage(i);
        mvc.perform(super.postPlace(placeData, "WrongTokenNmbr3"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @Order(70)
    public void getPlaceCorrectTest() throws Exception {
        mvc.perform(super.getPlace(placeId, sessionToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", Matchers.is(placeData.getTitle())))
                .andExpect(jsonPath("$.description", Matchers.is(placeData.getDescription())))
                .andExpect(jsonPath("$.creator.name", Matchers.is(placeData.getCreator().getName())))
                .andExpect(jsonPath("$.latitude", Matchers.is(placeData.getLatitude())))
                .andExpect(jsonPath("$.longitude", Matchers.is(placeData.getLongitude())))
                .andExpect(jsonPath("$.image.imageId", Matchers.is(Integer.parseInt(imageId))))
                .andReturn();
    }

    @Test
    @Order(72)
    public void getPlaceErrorForbiddenTest() throws Exception {
        mvc.perform(super.getPlace(placeId, "wrongToken"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(74)
    public void getPlaceErrorNotFoundTest() throws Exception {
        mvc.perform(super.getPlace("6543", sessionToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(80)
    public void patchPlaceCorrectTest() throws Exception {
        Place patchPlace = new Place();
        patchPlace.setTitle("new");
        patchPlace.setDescription("newDesc");
        mvc.perform(super.patchPlace(placeId, patchPlace, sessionToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", Matchers.is(patchPlace.getTitle())))
                .andExpect(jsonPath("$.description", Matchers.is(patchPlace.getDescription())))
                .andExpect(jsonPath("$.creator.name", Matchers.is(placeData.getCreator().getName())))
                .andExpect(jsonPath("$.latitude", Matchers.is(placeData.getLatitude())))
                .andExpect(jsonPath("$.longitude", Matchers.is(placeData.getLongitude())))
                .andExpect(jsonPath("$.image.imageId", Matchers.is(Integer.parseInt(imageId))))
                .andReturn();
    }

    @Test
    @Order(82)
    public void patchPlaceErrorForbiddenTest() throws Exception {
        Place patchPlace = new Place();
        patchPlace.setTitle("new");
        patchPlace.setDescription("newDesc");
        mvc.perform(super.patchPlace(placeId, patchPlace, "wrongToken"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(84)
    public void patchPlaceErrorNotFoundTest() throws Exception {
        Place patchPlace = new Place();
        patchPlace.setTitle("new");
        patchPlace.setDescription("newDesc");
        mvc.perform(super.patchPlace("5432", patchPlace, sessionToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(90)
    public void deletePlaceCorrectTest() throws Exception {
        MvcResult result = mvc.perform(super.postImage(RestTest.sessionToken))
                .andExpect(status().isOk())
                .andReturn();
        String imageIdTemp = result.getResponse().getContentAsString();

        Image i = new Image(Long.parseLong(imageIdTemp));
        Place placeTemp = new Place(
                null,
                "wall",
                "very cool wall",
                account,
                42.42424242,
                42.12345678,
                i,
                null
        );

        MvcResult result2 = mvc.perform(super.postPlace(placeTemp, sessionToken))
                .andExpect(status().isOk())
                .andReturn();
        JSONObject placeResponseJson = new JSONObject(result2.getResponse().getContentAsString());
        String placeIdTemp = placeResponseJson.getString("placeId");

        MvcResult result3 = mvc.perform(super.deletePlace(placeIdTemp, sessionToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", Matchers.is(placeTemp.getTitle())))
                .andExpect(jsonPath("$.description", Matchers.is(placeTemp.getDescription())))
                .andExpect(jsonPath("$.creator.name", Matchers.is(placeTemp.getCreator().getName())))
                .andExpect(jsonPath("$.latitude", Matchers.is(placeTemp.getLatitude())))
                .andExpect(jsonPath("$.longitude", Matchers.is(placeTemp.getLongitude())))
                .andExpect(jsonPath("$.image.imageId", Matchers.is(Integer.parseInt(imageIdTemp))))
                .andReturn();
    }

    @Test
    @Order(92)
    public void deletePlaceErrorNotFoundTest() throws Exception {
        mvc.perform(super.deletePlace("6543", sessionToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(94)
    public void deletePlaceErrorForbiddenTest() throws Exception {
        mvc.perform(super.deletePlace(placeId, "ThisIsWrong"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(100)
    public void getAllPlacesCorrectTest() throws Exception {
        mvc.perform(super.getAllPlaces(sessionToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(105)
    public void getAllPlacesErrorForbiddenTest() throws Exception {
        mvc.perform(super.getAllPlaces("wrongToken"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private Comment comment = new Comment(
            null,
            "very cool place",
            account,
            null
    );
    public static String commentId;

    @Test
    @Order(110)
    public void postCommentCorrectTest() throws Exception {
        comment.setPlace(new Place(Long.parseLong(placeId), null, null, null, null, null, null, null));
        MvcResult result = mvc.perform(super.postComment(comment, sessionToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        RestTest.commentId = result.getResponse().getContentAsString();
    }

    @Test
    @Order(112)
    public void postCommentErrorForbiddenTest() throws Exception {
        comment.setPlace(new Place(Long.parseLong(placeId), null, null, null, null, null, null, null));
        mvc.perform(super.postComment(comment, "wrongHAHAHA"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(120)
    public void getCommentCorrectTest() throws Exception {
        mvc.perform(super.getComment(commentId, sessionToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", Matchers.is(comment.getText())))
                .andExpect(jsonPath("$.writer.name", Matchers.is(comment.getWriter().getName())))
                .andExpect(jsonPath("$.place.placeId", Matchers.is(Integer.parseInt(placeId))));
    }

    @Test
    @Order(122)
    public void getCommentErrorForbiddenTest() throws Exception {
        mvc.perform(super.getComment(commentId, "notToday"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(124)
    public void getCommentErrorNotFoundTest() throws Exception {
        mvc.perform(super.getComment("5678", sessionToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(130)
    public void editCommentCorrectTest() throws Exception {
        Comment commentPatch = new Comment(
                null,
                "new text cool",
                null,
                null
        );
        mvc.perform(super.patchComment(commentId, commentPatch, sessionToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(commentId));

        mvc.perform(super.getComment(commentId, sessionToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", Matchers.is(commentPatch.getText())))
                .andExpect(jsonPath("$.writer.name", Matchers.is(comment.getWriter().getName())))
                .andExpect(jsonPath("$.place.placeId", Matchers.is(Integer.parseInt(placeId))));
    }

    @Test
    @Order(132)
    public void editCommentErrorForbiddenTest() throws Exception {
        Comment commentPatch = new Comment(
                null,
                "new text cool",
                null,
                null
        );
        mvc.perform(super.patchComment(commentId, commentPatch, "nonononooooo"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(134)
    public void editCommentErrorNotFoundTest() throws Exception {
        Comment commentPatch = new Comment(
                null,
                "new text cool",
                null,
                null
        );
        mvc.perform(super.patchComment("8765", commentPatch, sessionToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(136)
    public void deleteCommentErrorForbiddenTest() throws Exception {
        mvc.perform(super.deleteComment(commentId, "TokenButWrong"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(138)
    public void deleteCommentErrorNotFoundTest() throws Exception {
        mvc.perform(super.deleteComment("98765", sessionToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(140)
    public void deleteCommentCorrectTest() throws Exception {
        mvc.perform(super.deleteComment(commentId, sessionToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(150)
    public void getAllCommentByPlaceCorrectTest() throws Exception {
        mvc.perform(super.getAllCommentsByPlaceId(placeId, sessionToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(152)
    public void getAllCommentByPlaceErrorNotFoundTest() throws Exception {
        mvc.perform(super.getAllCommentsByPlaceId("87654", sessionToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(154)
    public void getAllCommentByPlaceErrorForbiddenTest() throws Exception {
        mvc.perform(super.getAllCommentsByPlaceId(placeId, "stillNotCorrect"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
