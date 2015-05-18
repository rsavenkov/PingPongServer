package ru.glumobile.server.domain;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Administrator on 17.05.15.
 */
@Transactional
public class UserDaoImpl {

    @PersistenceContext
    private EntityManager manager;

    /**
     *
     * @param user
     * @return Updated ping count
     */
    public String upsert(User user) {
        User finded = manager.find(User.class, user.getName());
        if (finded == null) {
            manager.persist(user);
            return String.valueOf(user.getPingCount());
        }
        finded.setPingCount(finded.getPingCount() + 1);
        manager.persist(finded);
        return String.valueOf(finded.getPingCount());
    }

}
