package io.supabase;

import io.supabase.data.dto.*;
import io.supabase.exceptions.ApiException;
import io.supabase.exceptions.GotrueException;
import io.supabase.exceptions.UrlNotFoundException;
import io.supabase.data.dto.Session;
import io.supabase.responses.BaseResponse;
import io.supabase.schemas.User;
import io.supabase.utils.Helpers;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class GoTrueApi {
    protected String url;
    protected Map<String, String> headers;

    protected GoTrueApi(String url, Map<String, String> headers) throws UrlNotFoundException {
        if (url == null || url.isEmpty()) {
            throw new UrlNotFoundException();
        }
        this.url = url;
        this.headers = headers;
    }

    /**
     * Send a magic-link to a given email.
     *
     * @param email the email the link should be sent to.
     * @throws GotrueException if the underlying http request throws an error of any kind.
     */
    public BaseResponse magicLink(String email) throws GotrueException {
        String urlMagicLink = String.format("%s/magiclink", url);

        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(email);

        return Helpers.makeRequest(HttpMethod.POST, urlMagicLink, emailDto, headers);
    }

    /**
     * Send a password-recovery link to a given email.
     *
     * @param email the email a recovery link should be sent to.
     * @throws GotrueException if the underlying http request throws an error of any kind.
     */
    public BaseResponse recoverPassword(String email) throws GotrueException {
        String urlRecover = String.format("%s/recover", url);

        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(email);

        return Helpers.makeRequest(HttpMethod.POST, urlRecover, emailDto, headers);
    }

    /**
     * Get the settings from the gotrue server.
     *
     * @return settings from the gotrue server.
     * @throws GotrueException if the underlying http request throws an error of any kind.
     */
    public Settings getSettings() throws GotrueException {
        String urlSettings = String.format("%s/settings", url);

        return Helpers.makeRequest(HttpMethod.GET, urlSettings, null, headers, Settings.class);
    }

    /**
     * Generates the relevant login URL for a third-party provider.
     *
     * @param provider One of the providers supported by GoTrue.
     * @return the url for the given provider
     */
    public String getUrlForProvider(String provider) {
        return String.format("%s/authorize?provider=%s", url, provider);
    }

    /**
     * Update a user.
     *
     * @param jwt   A valid JWT.
     * @param attributes The data you want to update
     * @return details of the updated user.
     * @throws GotrueException if the underlying http request throws an error of any kind.
     */
    public User updateUser(String jwt, UserAttributesDto attributes) throws GotrueException {
        String urlUser = String.format("%s/user", url);

        return Helpers.makeRequest(HttpMethod.PUT, urlUser, attributes, headersWithJWT(jwt), User.class);
    }

    // TODO: Ver porqué en C# también se pasa como parámetro el AccessToken
    /**
     * Generates a new JWT
     *
     * @param refreshToken A valid refresh token that was returned on login.
     * @return The updated information with the refreshed token
     * @throws GotrueException if the underlying http request throws an error of any kind.
     */
    public Session refreshAccessToken(String refreshToken) throws GotrueException {
        String urlToken = String.format("%s/token?grant_type=refresh_token", url);

        RefreshTokenDto refreshTokenDto = new RefreshTokenDto();
        refreshTokenDto.setRefreshToken(refreshToken);

        return Helpers.makeRequest(HttpMethod.POST, urlToken, refreshTokenDto, headers, Session.class);
    }

    /**
     * Gets details about the user.
     *
     * @param jwt A valid, logged-in JWT.
     * @return UserDto details about the user.
     * @throws GotrueException if the underlying http request throws an error of any kind.
     */
    public User getUser(String jwt) throws GotrueException {
        String urlUser = String.format("%s/user", url);

        return Helpers.makeRequest(HttpMethod.GET, urlUser, null, headersWithJWT(jwt), User.class);
    }

    // TODO: Test this method
    /**
     * Get user details by Id
     * @param jwt A valid JWT. Must be a full-access API Key (e.g. service_role key)
     * @param userId The user ID to get details for
     * @return User
     */
    public User getUserById(String jwt, String userId) throws GotrueException {
        String urlUser = String.format("%s/admin/users/%s", url, userId);

        return Helpers.makeRequest(HttpMethod.GET, urlUser, null, headersWithJWT(jwt), User.class);
    }

    // TODO: Ver lo de SignOutScope (Global, Local, others)
    /**
     * Removes a logged-in session.
     *
     * @param jwt A valid, logged-in JWT.
     * @throws GotrueException if the underlying http request throws an error of any kind.
     */
    public BaseResponse signOut(String jwt) throws GotrueException {
        String urlLogout = String.format("%s/logout", url);

        return Helpers.makeRequest(HttpMethod.POST, urlLogout, null, headersWithJWT(jwt));
    }


    /**
     * Logs in an existing user using their email address.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @return Details about the authentication.
     * @throws GotrueException if the underlying http request throws an error of any kind.
     */
    public Session signInWithEmail(String email, String password) throws GotrueException {
        String urlToken = String.format("%s/token?grant_type=password", url);

        CredentialsDto credentials = new CredentialsDto();
        credentials.setEmail(email);
        credentials.setPassword(password);

        return Helpers.makeRequest(HttpMethod.POST, urlToken, credentials, headers, Session.class);
    }


    /**
     * Creates a new user using their email address.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @return Details about the authentication.
     * @throws GotrueException if the underlying http request throws an error of any kind.
     */
    public Session signUpWithEmail(String email, String password) throws GotrueException {
        CredentialsDto credentials = new CredentialsDto();
        credentials.setEmail(email);
        credentials.setPassword(password);

        String urlSignup = String.format("%s/signup", url);

        return Helpers.makeRequest(HttpMethod.POST, urlSignup, credentials, headers, Session.class);
    }


    /**
     * Get the default headers plus the Authorization header.
     *
     * @param jwt the token to be added to the headers.
     * @return the default headers plus the Authorization header.
     */
    private Map<String, String> headersWithJWT(String jwt) {
        Map<String, String> newHeaders = new HashMap<>(headers);
        newHeaders.put("Authorization", String.format("Bearer %s", jwt));

        return newHeaders;
    }
}
