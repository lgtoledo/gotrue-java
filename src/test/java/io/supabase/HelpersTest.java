package io.supabase;

import io.supabase.data.CircularDependentA;
import io.supabase.data.CircularDependentB;
import io.supabase.exceptions.ApiException;
import io.supabase.utils.Helpers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

class HelpersTest {

    @AfterEach
    void tearDown() {
        // to ensure that the tests dont affect each other
        RestTemplate rest = new RestTemplate();
        rest.delete("http://localhost:3000/users");
    }

    @Test
    void constructor() {
        try {
            Constructor<Helpers> c = Helpers.class.getDeclaredConstructor();
            c.setAccessible(true);
            AtomicReference<Helpers> rUtils = new AtomicReference<>(null);
            Assertions.assertDoesNotThrow(() -> rUtils.set(c.newInstance()));
            Assertions.assertNotNull(rUtils.get());
        } catch (NoSuchMethodException e) {
            Assertions.fail();
        }
    }

    @Test
    void get_headers() {
        Assertions.assertThrows(ApiException.class, () -> Helpers.get(Object.class, new HashMap<>(), "http://smth/"));
    }

    @Test
    void post() {
        // to raise a JsonProcessingException
        CircularDependentA a = new CircularDependentA();
        CircularDependentB b = new CircularDependentB();
        a.setB(b);
        b.setA(a);
        Assertions.assertThrows(ApiException.class, () -> Helpers.post(a, CircularDependentA.class, null, "http://smth/"));
    }


    @Test
    void post_headers() {
        // to raise a JsonProcessingException
        CircularDependentA a = new CircularDependentA();
        CircularDependentB b = new CircularDependentB();
        a.setB(b);
        b.setA(a);
        Assertions.assertThrows(ApiException.class, () -> Helpers.post(a, CircularDependentA.class, new HashMap<>(), "http://smth/"));
    }


    @Test
    void put_json() {
        // to raise a JsonProcessingException
        CircularDependentA a = new CircularDependentA();
        CircularDependentB b = new CircularDependentB();
        a.setB(b);
        b.setA(a);
        Assertions.assertThrows(ApiException.class, () -> Helpers.put(a, CircularDependentA.class, null, "http://smth/"));
    }

    @Test
    void put() {
        // some url that does not exist
        Assertions.assertThrows(ApiException.class, () -> Helpers.put(null, Object.class, null, "http://localhost:1/"));
    }

    @Test
    void toEntity_nulls() {
        try {
            Method m = Helpers.class.getDeclaredMethod("toEntity", String.class, Map.class);
            m.setAccessible(true);
            Assertions.assertDoesNotThrow(() -> m.invoke(null, null, null));
        } catch (NoSuchMethodException e) {
            Assertions.fail();
        }
    }

    @Test
    void toEntity() {
        try {
            Method m = Helpers.class.getDeclaredMethod("toEntity", String.class, Map.class);
            m.setAccessible(true);
            Assertions.assertDoesNotThrow(() -> m.invoke(null, "{\"a\":1}", new HashMap<>()));
        } catch (NoSuchMethodException e) {
            Assertions.fail();
        }
    }
}
