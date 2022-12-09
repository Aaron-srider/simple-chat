package fit.wenchao.simplechatserver;

import fit.wenchao.simplechatparent.constants.BeanNameConstants;
import fit.wenchao.simplechatparent.utils.SpringIOC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(value={"fit.wenchao.simplechatparent", "fit.wenchao.simplechatserver"})
@SpringBootApplication
public class SimpleChatServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext appCtx = SpringApplication.run(SimpleChatServerApplication.class, args);

        SpringIOC.SINGLETON.init(appCtx);

        NettyServer server = SpringIOC.SINGLETON.getBean(BeanNameConstants.SERVER_BEAN_NAME, NettyServer.class);

        server.start();
    }

}
