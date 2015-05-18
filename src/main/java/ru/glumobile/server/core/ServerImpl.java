package ru.glumobile.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerImpl implements Runnable, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);

    private ServerSocket socket;
    private int port;
    private byte threadCount;
    private int bufferSize;
    private boolean interrupted;

    private ApplicationContext context;
    private ExecutorService executor;

    @PostConstruct
    public void init() throws IOException {
        socket = new ServerSocket(port, threadCount, InetAddress.getByName("0.0.0.0"));
        socket.setSoTimeout(60000);
        executor = Executors.newFixedThreadPool(threadCount);
        logger.info("==========> Server ready for incoming connection on {} port", port);
    }

    @Override
    public void run() {
        logger.info("==========> Start server thread execution");
        while (!interrupted) {
            try {
                Socket client = socket.accept();
                logger.info("==========> Accept client connection from: {}", client.getInetAddress().getHostAddress());
                client.setSoTimeout(60000);
                ConnectionListener listener = context.getBean(ConnectionListener.class, client, bufferSize);
                executor.submit(listener);
            } catch (SocketTimeoutException | ExceptionInInitializerError e) {
                //do nothing here
            } catch (IOException e) {
                logger.info("==========> Error accepting connection from server socket. Exiting.", e);
                interrupted = true;
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(byte threadCount) {
        this.threadCount = threadCount;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}
