package fit.wenchao.simplechatclient.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

@Configuration
public class CountDownLatchFactory
{
    @Bean
    public CountDownLatch loginCountDownLatch() {
        return new CountDownLatch(1);
    }

    //
    //@Bean
    //public CountDownLatch tCountDownLatch() {
    //    return new CountDownLatch(1);
    //}

}
