package fit.wenchao.simplechatclient.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.SynchronousQueue;

@Configuration
public class SynchronousQueueFactory
{

    @Bean
    public SynchronousQueue<Object> loginSyncQueue() {
        return new SynchronousQueue<>();
    }
}
