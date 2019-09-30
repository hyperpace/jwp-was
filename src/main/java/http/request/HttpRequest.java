package http.request;

import com.google.common.base.Strings;
import http.common.HttpCookie;
import http.common.HttpMethod;
import http.common.HttpSession;
import http.common.HttpSessionHandler;

import java.io.IOException;

public class HttpRequest {
    public static final String CONTENT_LENGTH_NAME = "Content-Length";
    public static final String CONTENT_TYPE_NAME = "Content-Type";
    public static final String HOST_NAME = "Host";
    public static final String COOKIE_NAME = "Cookie";

    private RequestLine requestLine;
    private RequestHeader requestHeader;
    private RequestBody requestBody;
    private HttpCookie httpCookie;
    private HttpRequestParams httpRequestParams;

    private HttpRequest(RequestLine requestLine,
                        RequestHeader requestHeader,
                        RequestBody requestBody,
                        HttpCookie httpCookie,
                        HttpRequestParams httpRequestParams) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        this.httpCookie = httpCookie;
        this.httpRequestParams = httpRequestParams;
    }

    public static HttpRequest of(RequestLine requestLine,
                                 RequestHeader requestHeader,
                                 RequestBody requestBody) throws IOException {

        HttpCookie httpCookie = HttpCookie.of(requestHeader.getHeader(COOKIE_NAME));

        HttpRequestParams httpRequestParams = HttpRequestParams.of();
        httpRequestParams.putAll(requestLine.getQueryString());
        httpRequestParams.putAll(requestBody.getFormData(requestHeader));

        return new HttpRequest(requestLine, requestHeader, requestBody, httpCookie, httpRequestParams);
    }

    public String getHeader(String headerKey) {
        return requestHeader.getHeader(headerKey);
    }

    public String getUrl() {
        String path = requestLine.getPath();
        String protocol = requestLine.getProtocol();
        String host = requestHeader.getHeader(HOST_NAME);

        return String.format("%s://%s%s", protocol, host, path);
    }

    public String getParameter(String name) {
        return httpRequestParams.getParameter(name);
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public HttpSession getSession() {
        String sessionId = httpCookie.getCookie(HttpSession.SESSION_NAME);
        if (Strings.isNullOrEmpty(sessionId)) {
            HttpSession session = HttpSessionHandler.createSession();
            httpCookie.put(HttpSession.SESSION_NAME, session.getUuid());

            return session;
        }

        return HttpSessionHandler.getSession(sessionId);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean isGet() {
        return requestLine.getMethod().equals(HttpMethod.GET);
    }

    public boolean isPost() {
        return requestLine.getMethod().equals(HttpMethod.POST);
    }
}
