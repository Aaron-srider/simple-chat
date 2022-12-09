package fit.wenchao.simplechatparent.utils;

import org.springframework.context.ApplicationContext;


public class SpringIOC {

    public static final SpringIOC SINGLETON;

    private ApplicationContext applicationContext;

    private boolean init;

    static {
        SINGLETON = new SpringIOC();
    }

    public void init(ApplicationContext applicationContext) {
        if (!init) {
            this.applicationContext = applicationContext;
            init = true;
        }
    }

    private ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }


    public <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}
