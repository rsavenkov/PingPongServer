package ru.glumobile.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.glumobile.server.core.ServerImpl;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        ServerImpl serverImpl = context.getBean(ServerImpl.class);
        Thread thread = new Thread(serverImpl);
        thread.setDaemon(false);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }
}
