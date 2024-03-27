package cz.madeta.icoJasonParse.beanFinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanPrinter {

    @Autowired
    private ApplicationContext context;

    public void printBeanDetails() {
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            System.out.println("Bean Name: " + beanName);
            System.out.println("Bean Class: " + bean.getClass().getName());
            // Print other bean details as needed
        }
    }
}
