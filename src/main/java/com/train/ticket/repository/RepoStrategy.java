package com.train.ticket.repository;

import com.train.ticket.model.DBStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RepoStrategy {
    @Autowired
    public ApplicationContext appContext;

    public TicketRepo getTicketRepoStrategyInstance(DBStrategy dBStrategy) {
        if (DBStrategy.LOCAL.equals(dBStrategy)) {
            return appContext.getBean("ticketLocalMemoryRepoImpl", TicketRepo.class);
        }
        return null;
    }

    public TrainRepo getTrainRepoStrategyInstance(DBStrategy dBStrategy) {
        if (DBStrategy.LOCAL.equals(dBStrategy)) {
            return appContext.getBean("trainLocalMemoryRepoImpl", TrainRepo.class);
        }
        return null;
    }
}
