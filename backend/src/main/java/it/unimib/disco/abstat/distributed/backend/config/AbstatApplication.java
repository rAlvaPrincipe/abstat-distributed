package it.unimib.disco.abstat.distributed.backend.config;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AbstatApplication {
	
	
	public static void main(String[] args) {
		SpringApplication.run(AbstatApplication.class, args);
	}
	
	@Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbedded() {

        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();

        tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
                //-1 means unlimited
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setKeepAliveTimeout(-1);
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setSoTimeout(60000 * 60);
            }
        });

        return tomcat;

    }
}
