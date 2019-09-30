package http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static class RequestHandlerHolder {
        private static final RequestHandler instance = new RequestHandler();
    }

    private RequestHandler() {
    }

    public static RequestHandler getInstance() {
        return RequestHandlerHolder.instance;
    }

    public HttpRequest create(BufferedReader br) throws IOException {

        RequestLine requestLine = createRequestStartLine(br);
        RequestHeader requestHeader = createRequestHeader(br);
        RequestBody requestBody = createRequestBody(br, requestHeader);

        return HttpRequest.of(requestLine, requestHeader, requestBody);
    }

    private RequestLine createRequestStartLine(BufferedReader br) throws IOException {
        String startLine = br.readLine();
        return RequestLine.of(startLine);
    }

    private RequestHeader createRequestHeader(BufferedReader br) throws IOException {
        List<String> header = new ArrayList<>();
        String line;
        while (!("".equals(line = br.readLine()))) {
            header.add(line);
            logger.debug("requestHeader: {}", line);
        }

        return RequestHeader.of(header);
    }

    private RequestBody createRequestBody(BufferedReader br, RequestHeader requestHeader) throws IOException {
        if (requestHeader.getHeader(HttpRequest.CONTENT_LENGTH_NAME) != null) {
            byte[] body = IOUtils.readData(br, Integer.parseInt(requestHeader.getHeader(HttpRequest.CONTENT_LENGTH_NAME))).getBytes();
            return RequestBody.of(body);
        }

        return RequestBody.of("".getBytes());
    }
}
