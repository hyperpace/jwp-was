package http.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHeaderTest {
    private RequestHeader requestHeader;

    @BeforeEach
    public void setUp() {
        requestHeader = RequestHeader.of(Arrays.asList(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*"
        ));
    }

    @Test
    public void getHeaderTest() {
        assertThat(requestHeader.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(requestHeader.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(requestHeader.getHeader("Accept")).isEqualTo("*/*");
    }
}