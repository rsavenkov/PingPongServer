package ru.glumobile.server.domain;

import ru.glumobile.server.Protocol;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Administrator on 17.05.15.
 */
@Entity
public class User {

    @Id
    private String name;
    private Long pingCount;

    public User() {
    }

    public User(String name, Long pingCount) {
        this.name = name;
        this.pingCount = pingCount;
    }

    public User(Protocol protocol) {
        this(protocol.getArgs().get("user"), Long.valueOf(protocol.getArgs().get("count")));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPingCount() {
        return pingCount;
    }

    public void setPingCount(Long pingCount) {
        this.pingCount = pingCount;
    }
}
