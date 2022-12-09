package fit.wenchao.simplechatclient;

import fit.wenchao.simplechatparent.constants.BeanNameConstants;
import fit.wenchao.simplechatparent.utils.SpringIOC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(value={"fit.wenchao.simplechatparent", "fit.wenchao.simplechatclient"})
@Slf4j
@SpringBootApplication
public class SimpleChatClientApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext appCtx = SpringApplication.run(SimpleChatClientApplication.class, args);

        SpringIOC.SINGLETON.init(appCtx);

        NettyClient client = SpringIOC.SINGLETON.getBean(BeanNameConstants.CLIENT_BEAN_NAME, NettyClient.class);

        client.start();

    }
}
