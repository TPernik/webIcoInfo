package cz.madeta.icoJasonParse.beanFinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component
public class PropertyPrinter {

    @Autowired
    private Environment environment;

    public void printProperties() {
        String[] propertyNames = environment.getActiveProfiles();
        for (String propertyName : propertyNames) {
            System.out.println(propertyName + " = " + environment.getProperty(propertyName));
        }
    }
}
