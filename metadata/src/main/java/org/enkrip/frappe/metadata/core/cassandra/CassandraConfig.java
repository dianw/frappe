package org.enkrip.frappe.metadata.core.cassandra;

import org.enkrip.frappe.metadata.Application;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;
import org.springframework.data.cassandra.core.cql.session.init.SessionFactoryInitializer;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

@Configuration
@EnableReactiveCassandraRepositories(basePackageClasses = Application.class)
@ConditionalOnBean(SessionFactory.class)
public class CassandraConfig {
    @Bean
    public SessionFactoryInitializer sessionFactoryInitializer(SessionFactory sessionFactory) {
        SessionFactoryInitializer initializer = new SessionFactoryInitializer();
        initializer.setSessionFactory(sessionFactory);
        {
            ResourceKeyspacePopulator populator = new ResourceKeyspacePopulator();
            populator.addScript(new ClassPathResource("scripts/cql/db-schema.cql"));
            initializer.setKeyspacePopulator(populator);
        }
        return initializer;
    }
}
