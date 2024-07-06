package io.supabase.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.supabase.exceptions.ApiException;
import io.supabase.exceptions.FailureHint;
import io.supabase.exceptions.GotrueException;
import io.supabase.responses.BaseResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.*;

import java.util.HashMap;
import java.util.Map;

public class RestUtils {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final RestTemplate rest = new RestTemplate();


    private RestUtils() {
    }

    public static <T> T makeRequest(HttpMethod method, String url, Object data, Map<String, String> headers, Class<T> responseType) throws GotrueException {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            headers.forEach(httpHeaders::add);
            HttpEntity<String> entity = new HttpEntity<>(data != null ? mapper.writeValueAsString(data) : null, httpHeaders);
            ResponseEntity<String> responseEntity = rest.exchange(url, method, entity, String.class);

            // Verify if the request was successful
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                GotrueException e = new GotrueException(responseEntity.getBody() != null ? responseEntity.getBody() : "Request Failed");
                e.setContent(responseEntity.getBody());
                e.setStatusCode(responseEntity.getStatusCode().value());
                e.setResponse(responseEntity);
                e.addReason();

                throw e;
            }

            return responseEntity.getBody() != null ? mapper.readValue(responseEntity.getBody(), responseType) : null;

        } catch (HttpClientErrorException e) { // 4xx
            GotrueException gotrueException = new GotrueException("Request Failed" + e.getMessage());
            gotrueException.setContent(e.getResponseBodyAsString());
            gotrueException.setStatusCode(e.getStatusCode().value());
            gotrueException.setResponse(new ResponseEntity<>(e.getResponseBodyAsString(), e.getResponseHeaders(), e.getStatusCode()));
            gotrueException.addReason();
            throw gotrueException;
        } catch (HttpServerErrorException e) { // 5xx
            GotrueException gotrueException = new GotrueException("Server error: " + e.getMessage(), FailureHint.Reason.Offline, e);
            gotrueException.setStatusCode(e.getStatusCode().value());
            throw gotrueException;
        } catch (JsonProcessingException e) {
            System.out.println("Excepción 2:  \n" + e.getMessage());
            throw new GotrueException("Error processing JSON", FailureHint.Reason.Unknown, e);
        } catch (Exception e) {
            System.out.println("Excepción 3:  \n" + e.getMessage());
            throw new GotrueException("Request Failed", FailureHint.Reason.Unknown, e);
        }
    }

    public static BaseResponse makeRequest(HttpMethod method, String url, Object data, Map<String, String> headers) throws GotrueException {
        try {
            // Create and configure the HTTP headers
            HttpHeaders httpHeaders = new HttpHeaders();
            headers.forEach(httpHeaders::add);

            // Create the HTTP entity with the body data and headers
            HttpEntity<String> entity = new HttpEntity<>(data != null ? mapper.writeValueAsString(data) : null, httpHeaders);

            // Send the request using RestTemplate
            ResponseEntity<String> responseEntity = rest.exchange(url, method, entity, String.class);

            // Verify if the request was successful
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                GotrueException e = new GotrueException(responseEntity.getBody() != null ? responseEntity.getBody() : "Request Failed");
                e.setContent(responseEntity.getBody());
                e.setStatusCode(responseEntity.getStatusCode().value());
                e.setResponse(responseEntity);
                e.addReason();
                
                throw e;
            }

            // Return the response encapsulated in a BaseResponse object
            return new BaseResponse(responseEntity.getBody(), responseEntity);

        } catch (HttpClientErrorException e) { // 4xx
            GotrueException gotrueException = new GotrueException("Request Failed" + e.getMessage());
            gotrueException.setContent(e.getResponseBodyAsString());
            gotrueException.setStatusCode(e.getStatusCode().value());
            gotrueException.setResponse(new ResponseEntity<>(e.getResponseBodyAsString(), e.getResponseHeaders(), e.getStatusCode()));
            gotrueException.addReason();
            throw gotrueException;
        } catch (HttpServerErrorException e) { // 5xx
            GotrueException gotrueException = new GotrueException("Server error: " + e.getMessage(), FailureHint.Reason.Offline, e);
            gotrueException.setStatusCode(e.getStatusCode().value());
            throw gotrueException;
        } catch (JsonProcessingException e) {
            System.out.println("Excepción 2:  \n" + e.getMessage());
            throw new GotrueException("Error processing JSON", FailureHint.Reason.Unknown, e);
        } catch (Exception e) {
            System.out.println("Excepción 3:  \n" + e.getMessage());
            throw new GotrueException("Request Failed", FailureHint.Reason.Unknown, e);
        }
    }


    // -----------------------------------------------------------------------------------

    /**
     * Sends a Put request.
     *
     * @param body          the body of the request, will be parsed to json.
     * @param responseClass the class of the response.
     * @param headers       the headers that will be sent with the request.
     * @param url           the url the request will be sent to.
     * @param <R>           the type of the response.
     * @return the response of the request parsed from json to R.
     * @throws ApiException if an Exception is thrown.
     */
    public static <R> R put(Object body, Class<R> responseClass, Map<String, String> headers, String url) throws ApiException {
        try {
            HttpEntity<String> entity = toEntity(body, headers);
            return rest.exchange(url, HttpMethod.PUT, entity, responseClass).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new ApiException("Put failed", e);
        } catch (JsonProcessingException e) {
            throw new ApiException("Object mapping failed", e);
        }
    }

    /**
     * Sends a Get request.
     *
     * @param responseClass the class of the response.
     * @param headers       the headers that will be sent with the request.
     * @param url           the url the request will be sent to.
     * @param <R>           the type of the response.
     * @return the response of the request parsed from json to R.
     * @throws ApiException if an Exception is thrown.
     */
    public static <R> R get(Class<R> responseClass, Map<String, String> headers, String url) throws ApiException {
        try {
            HttpEntity<String> entity = toEntity(headers);
            ResponseEntity<R> res = rest.exchange(url, HttpMethod.GET, entity, responseClass);
            return res.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new ApiException("Get failed", e);
        }
    }


    /**
     * Sends a Post request.
     *
     * @param headers the headers that will be sent with the request.
     * @param url     the url the request will be sent to.
     * @throws ApiException if an Exception is thrown.
     */
    public static void post(Map<String, String> headers, String url) throws ApiException {
        try {
            HttpEntity<String> entity = toEntity(headers);
            rest.postForObject(url, entity, Void.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new ApiException("Post failed", e);
        }
    }

    /**
     * Sends a Post request.
     *
     * @param body    the body of the request, will be parsed to json.
     * @param headers the headers that will be sent with the request.
     * @param url     the url the request will be sent to.
     * @throws ApiException if an Exception is thrown.
     */
    public static void post(Object body, Map<String, String> headers, String url) throws ApiException {
        post(body, Void.class, headers, url);
    }

    /**
     * Sends a Post request.
     *
     * @param body          the body of the request, will be parsed to json.
     * @param responseClass the class of the response.
     * @param headers       the headers that will be sent with the request.
     * @param url           the url the request will be sent to.
     * @param <R>           the type of the response.
     * @return the response of the request parsed from json to R.
     * @throws ApiException if an Exception is thrown.
     */
    public static <R> R post(Object body, Class<R> responseClass, Map<String, String> headers, String url) throws ApiException {
        try {
            HttpEntity<String> entity = toEntity(body, headers);
            return rest.postForObject(url, entity, responseClass);
        } catch (RestClientResponseException | ResourceAccessException e) {
            throw new ApiException("Post failed", e);
        } catch (JsonProcessingException e) {
            throw new ApiException("Object mapping failed", e);
        }
    }

    private static HttpEntity<String> toEntity(Object object, Map<String, String> headers) throws JsonProcessingException {
        return toEntity(mapper.writeValueAsString(object), headers);
    }

    private static HttpEntity<String> toEntity(Map<String, String> headers) {
        return toEntity(null, headers);
    }

    private static HttpEntity<String> toEntity(String jsonBody, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers = (headers != null) ? headers : new HashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }
        return new HttpEntity<>(jsonBody, httpHeaders);
    }
}
