package ru.glumobile.server.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.glumobile.server.Protocol;
import ru.glumobile.server.domain.User;
import ru.glumobile.server.domain.UserDaoImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 14.05.15.
 */
@Service
@Scope("prototype")
public class ConnectionListener implements Callable<Protocol> {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionListener.class);
    private static final String PATH = "/handler";

    private Socket socket;
    private int bufferSize;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private UserDaoImpl dao;

    enum Request {

        POST,
        OPTIONS,
        UNKNOWN;

        /**
         * Contains raw headers
         */
        Map<String, String> headers = new HashMap<>();
        String body;

        static Request process(String request) {
            if (request == "" || request == null) return UNKNOWN;
            Request r = UNKNOWN;
            String[] headeAndBody = request.split("\r\n\r\n");
            if (headeAndBody.length != 2) return r;
            List<String> headerLine = Arrays.asList(headeAndBody[0].split("\r\n"));
            if (headerLine.isEmpty()) return r;
            String[] method = headerLine.get(0).split(" ");
            if (method.length != 3) return r;
            if (!PATH.equals(method[1])) return r;
            for (Request _request : values()) {
                if (headerLine.get(0).contains(_request.name())) {
                    r = _request;
                    break;
                }
            }
            if (r == UNKNOWN) return r;
            for (String line : headerLine) {
                String[] hs = line.split(": ");
                r.headers.put(hs[0], hs.length == 2 ? hs[1] : null);
            }
            if (r.headers.get("Content-Length") != null) {
                r.body = headeAndBody[1].substring(0, Integer.valueOf(r.headers.get("Content-Length")));
            }
            return r;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    public ConnectionListener(Socket socket, int bufferSize) {
        this.socket = socket;
        this.bufferSize = bufferSize;
    }

    @Override
    public Protocol call() throws Exception {
        boolean finished = false;
        int timeoutAttempts = 1;
        while (!finished) {
            try {
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                byte[] buffer = new byte[bufferSize];
                is.read(buffer);
                String request = new String(buffer);
                logger.info("==========> Request:\n {}", request);

                Request _request = Request.process(request);

                switch (_request) {
                    case OPTIONS:
                        String response = "HTTP/1.1 200 OK\n"
                                + "Access-Control-Allow-Origin: *\n"
                                + "Access-Control-Allow-Methods: POST, OPTIONS\n"
                                + "Access-Control-Max-Age: 1000\n"
                                + "Access-Control-Allow-Headers: origin, x-csrftoken, content-type, accept\n\n";
                        logger.info("==========> Response:\n {}", response.toString());
                        os.write(response.toString().getBytes());
                        break;
                    case POST:
                        Protocol protocol = mapper.readValue(_request.getBody(), Protocol.class);
                        if (protocol.getCommand().equals(Protocol.Command.ping)) {
                            String pong = dao.upsert(new User(protocol));
                            response = "HTTP/1.1 200 OK\n"
                                    + "Content-Type:application/json; charset=UTF-8\n"
                                    + "Access-Control-Allow-Origin: *\n\n"
                                    + "{\"pong\" : " + pong + "}";
                            logger.info("==========> Response:\n {}", response.toString());
                            os.write(response.toString().getBytes());
                        } else {
                            response = "HTTP/1.1 400 Bad Request\n";
                            DateFormat df = DateFormat.getTimeInstance();
                            df.setTimeZone(TimeZone.getTimeZone("GMT"));
                            response = response + "Date: " + df.format(new Date()) + "\n"
                                    + "Connection: close\n"
                                    + "Server: SimpleWEBServer\n"
                                    + "Pragma: no-cache\n\n";
                            os.write(response.getBytes());
                        }
                        break;
                    default:
                        response = "HTTP/1.1 400 Bad Request\n";
                        DateFormat df = DateFormat.getTimeInstance();
                        df.setTimeZone(TimeZone.getTimeZone("GMT"));
                        response = response + "Date: " + df.format(new Date()) + "\n"
                                + "Connection: close\n"
                                + "Server: SimpleWEBServer\n"
                                + "Pragma: no-cache\n\n";
                        os.write(response.getBytes());
                }
                finished = true;
            } catch (SocketTimeoutException ex) {
                logger.info("==========> Timeout waiting next sequence of bytes from socket. Attempt number: {}", timeoutAttempts);
                if (timeoutAttempts++ >= 5) {
                    logger.info("==========> Timeouts limit reached. Stopping sequence execution.");
                    finished = true;
                }
            } catch (Exception e) {
                logger.error("=====> Error reading bytes from socket.", e);
                finished = true;
            } finally {
                if (finished) {
                    socket.close();
                }
            }
        }


        return null;
    }

}
