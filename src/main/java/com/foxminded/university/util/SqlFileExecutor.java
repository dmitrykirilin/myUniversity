package com.foxminded.university.util;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Component
@Scope("prototype")
@Transactional
public class SqlFileExecutor {
    private final DataSource dataSource;
    private ResourceDatabasePopulator populator;
    private Logger logger = (Logger) LoggerFactory.getLogger(SqlFileExecutor.class);

    public SqlFileExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
        populator = new ResourceDatabasePopulator();
    }

    public void execute(String path){
        logger.info("Name of file with SQL -- {}", path);
        populator.addScript(new ClassPathResource(path));
        DatabasePopulatorUtils.execute(populator, dataSource);
        logger.info("All expressions are completed");
    }
}
