package cz.madeta;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import cz.madeta.icoJasonParse.ControllerIco;

public class ServletInitiazer extends SpringBootServletInitializer{
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(ControllerIco.class);
    }
}
