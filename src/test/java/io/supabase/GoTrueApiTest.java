package io.supabase;

import io.supabase.data.dto.*;
import io.supabase.exceptions.*;
import io.supabase.data.dto.Session;
import io.supabase.responses.BaseResponse;
import io.supabase.schemas.User;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

class GoTrueApiTest {
    private static final String url = "http://localhost:9999";
    private static final Map<String, String> headers = new HashMap<>();
    private static GoTrueApi api;

    @BeforeAll
    static void setup() {
        try {
            api = new GoTrueApi(url, headers);
        } catch (UrlNotFoundException e) {
            // should never get here
            Assertions.fail();
        }
    }

    @BeforeEach
    void setupEach() {
        // to ensure that there is nothing specified
        System.clearProperty("gotrue.url");
        System.clearProperty("gotrue.headers");
    }

    @AfterEach
    void tearDown() {
        // to ensure that the tests don't affect each other
        RestTemplate rest = new RestTemplate();
        rest.delete("http://localhost:3000/users");
    }

    @Test
    void constructor_valid() {
        Assertions.assertDoesNotThrow(() -> new GoTrueApi(url, headers));
    }

    @Test
    void constructor_invalid() {
        Assertions.assertThrows(UrlNotFoundException.class, () -> new GoTrueApi(null, null));
        Assertions.assertThrows(UrlNotFoundException.class, () -> new GoTrueApi("", null));
    }

    @Test
    void signUpWithEmail() {
        Session r = null;
        try {
            r = api.signUpWithEmail("email@example.com", "secret");
        } catch (GotrueException e) {
            Assertions.fail();
        }
        Utils.assertSession(r);
    }

    @Test
    void signUpWithEmail_AlreadyExists() {
        try {
            api.signUpWithEmail("email@example.com", "secret");
        } catch (GotrueException e) {
            Assertions.fail();
        }

        GotrueException exception = Assertions.assertThrows(GotrueException.class, () -> api.signUpWithEmail("email@example.com", "secret"));

        // Verify that the reason is UserAlreadyRegistered
        Assertions.assertEquals(FailureHint.Reason.UserAlreadyRegistered, exception.getReason());
    }

    @Test
    void signInWithEmail() {
        Session r = null;
        try {
            // create a user
            api.signUpWithEmail("email@example.com", "secret");

            // login with said user
            r = api.signInWithEmail("email@example.com", "secret");
        } catch (GotrueException e) {
            Assertions.fail();
        }
        Utils.assertSession(r);
    }

    @Test
    void signInWithEmail_wrongPass() {
        // create a user
        try {
            api.signUpWithEmail("email@example.com", "secret");
        } catch (GotrueException e) {
            Assertions.fail();
        }

        // login with said user and check the exception
        GotrueException exception = Assertions.assertThrows(GotrueException.class, () -> api.signInWithEmail("email@example.com", "notSecret"));

        // Verify that the reason is UserBadLogin
        Assertions.assertEquals(FailureHint.Reason.UserBadLogin, exception.getReason());
    }

    @Test
    void signOut() {
        // create a user to get a valid JWT
        Session r = null;
        try {
            r = api.signUpWithEmail("email@example.com", "secret");
        } catch (GotrueException e) {
            Assertions.fail();
        }
        String jwt = r.getAccessToken();

        BaseResponse response = Assertions.assertDoesNotThrow(() -> api.signOut(jwt));

        // Verify that the response is not null and that the status code is NO_CONTENT (204)
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getResponseMessage().getStatusCode());
    }

    @Test
    void signOut_invalidJWT() {
        String jwt = "somethingThatIsNotAValidJWT";

        GotrueException exception = Assertions.assertThrows(GotrueException.class, () -> api.signOut(jwt));
        // Verify that the reason is AdminTokenRequired
        Assertions.assertEquals(FailureHint.Reason.AdminTokenRequired, exception.getReason());
    }

    @Test
    void getUser() {
        User user = null;
        try {
            // create a user to get a valid JWT
            Session r = api.signUpWithEmail("email@example.com", "secret");

            String jwt = r.getAccessToken();

            user = api.getUser(jwt);
        } catch (GotrueException e) {
            Assertions.fail();
        }
        Utils.assertUserDto(user);
        Assertions.assertNotNull(user.getUserMetadata());
    }

    @Test
    void getUser_invalidJWT() {
        String jwt = "somethingThatIsNotAValidJWT";
        GotrueException exception = Assertions.assertThrows(GotrueException.class, () -> api.getUser(jwt));
        // Verify that the reason is AdminTokenRequired
        Assertions.assertEquals(FailureHint.Reason.AdminTokenRequired, exception.getReason());
    }

    @Test
    void refreshAccessToken() {
        Session r = null;
        Session a = null;
        try {
            // create a user to get a valid refreshToken
            r = api.signUpWithEmail("email@example.com", "secret");

            String token = r.getRefreshToken();

            a = api.refreshAccessToken(token);
        } catch (GotrueException e) {
            Assertions.fail();
        }
        Utils.assertSession(a);
        Assertions.assertNotEquals(r.getAccessToken(), a.getAccessToken());
        Assertions.assertNotEquals(r.getRefreshToken(), a.getRefreshToken());
    }

    @Test
    void refreshAccessToken_invalidToken() {
        String token = "noValidToken";
        GotrueException exception = Assertions.assertThrows(GotrueException.class, () -> api.refreshAccessToken(token));

        // Verify that the reason is InvalidRefreshToken
        Assertions.assertEquals(FailureHint.Reason.InvalidRefreshToken, exception.getReason());
    }

    @Test
    void updateUser_email() {
        UserAttributesDto attr = null;
        User user = null;
        try {
            // create a user
            Session r = api.signUpWithEmail("email@example.com", "secret");

            attr = new UserAttributesDto();
            attr.setEmail("newemail@example.com");

            user = api.updateUser(r.getAccessToken(), attr);
        } catch (GotrueException e) {
            Assertions.fail();
        }
        Utils.assertUserUpdated(user);
        Assertions.assertEquals(user.getNewEmail(), attr.getEmail());
        Assertions.assertNotNull(user.getUserMetadata());
    }

    @Test
    void updateUser_password() {
        User user = null;
        try {
            // create a user
            Session r = api.signUpWithEmail("email@example.com", "secret");

            UserAttributesDto attr = new UserAttributesDto();
            attr.setPassword("pass12");

            user = api.updateUser(r.getAccessToken(), attr);
        } catch (GotrueException e) {
            Assertions.fail();
        }
        // normal assert because there is no new email attribute
        Utils.assertUserDto(user);
        Assertions.assertNotNull(user.getUserMetadata());

        // login with the new password
        Session s = null;
        try {
            s = api.signInWithEmail("email@example.com", "pass12");
        } catch (GotrueException e) {
            Assertions.fail();
        }
        Utils.assertSession(s);
    }

    @Test
    void getUrlForProvider() {
        String url = api.getUrlForProvider("Github");
        Assertions.assertNotNull(url);
        Assertions.assertTrue(url.endsWith("/authorize?provider=Github"));
    }

    @Test
    void getSettings() {
        Settings s = null;
        try {
            s = api.getSettings();
        } catch (GotrueException e) {
            Assertions.fail();
        }
        Utils.assertSettings(s);
    }

    @Test
    void recoverPassword() {
        Session r = null;
        try {
            // create a user
            r = api.signUpWithEmail("email@example.com", "secret");
        } catch (GotrueException e) {
            Assertions.fail();
        }
        final Session finalR = r;
        // send recovery link to user
        Assertions.assertDoesNotThrow(() -> api.recoverPassword(finalR.getUser().getEmail()));
    }

    @Test
    void recoverPassword_no_user() {
        try {
            api.recoverPassword("email@example.com");
            // should throw an exception
            Assertions.fail();
        } catch (ApiException e) {
            // there is no user with the given email
            Assertions.assertTrue(e.getCause().getMessage().startsWith("404 Not Found"));
        }
    }

    @Test
    void magicLink() {
        Session r = null;
        try {
            // create a user
            r = api.signUpWithEmail("email@example.com", "secret");
        } catch (GotrueException e) {
            Assertions.fail();
        }
        final Session finalR = r;
        // send recovery link to user
        Assertions.assertDoesNotThrow(() -> api.magicLink(finalR.getUser().getEmail()));
    }

    @Test
    void magicLink_no_user() {
        // there does not already have to be a user registered with the email
        Assertions.assertDoesNotThrow(() -> api.magicLink("email@example.com"));
    }
}
