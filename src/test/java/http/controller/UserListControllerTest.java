package http.controller;

import db.DataBase;
import http.common.HttpSession;
import http.common.HttpSessionHandler;
import http.request.HttpRequest;
import http.request.RequestHandler;
import http.response.HttpResponse;
import http.response.ResponseHandler;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.RequestClientTest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class UserListControllerTest {
    private User loginUser = new User("adien", "1234", "aiden", "aiden@naver.com");
    private RequestClientTest requestClient;
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        DataBase.addUser(loginUser);
        DataBase.addUser(new User("roby", "1234", "roby", "roby@naver.com"));
    }

    @Test
    @DisplayName("로그인 성공시 리스트 출력")
    public void loginSuccess() throws Exception {
        HttpSession httpSession = HttpSessionHandler.createSession();
        httpSession.setAttribute("user", loginUser);

        requestClient = RequestClientTest.get("/user/list")
                .setCookie(new HashMap<String, String>() {
                    {
                        put(HttpSession.SESSION_NAME, httpSession.getUuid());
                    }
                });

        InputStream in = new ByteArrayInputStream(requestClient.toString().getBytes(StandardCharsets.UTF_8));
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        HttpRequest httpRequest = RequestHandler.getInstance().create(br);
        HttpResponse httpResponse = ResponseHandler.getInstance().create(httpRequest);

        DefaultController controller = new UserListController();
        controller.doGet(httpRequest, httpResponse);

        assertThat(new String(httpResponse.getBody())).contains("aiden@naver.com");
        assertThat(new String(httpResponse.getBody())).contains("roby@naver.com");
    }

    @Test
    @DisplayName("로그인 실패시 리다이렉트")
    public void loginFail() throws Exception {
        requestClient = RequestClientTest.get("/user/list");

        InputStream in = new ByteArrayInputStream(requestClient.toString().getBytes(StandardCharsets.UTF_8));
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        httpRequest = RequestHandler.getInstance().create(br);
        httpResponse = ResponseHandler.getInstance().create(httpRequest);

        DefaultController controller = new UserListController();
        controller.doGet(httpRequest, httpResponse);

        assertThat(httpResponse.toString()).contains("Location: /index.html");
    }
}