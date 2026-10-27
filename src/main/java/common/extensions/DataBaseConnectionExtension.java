package common.extensions;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import common.annotations.DataBase;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class DataBaseConnectionExtension implements ParameterResolver {
    private static HikariDataSource dataSource;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(DataBase.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return DSL.using(getDataSource(extensionContext), SQLDialect.POSTGRES);


    }

    private static synchronized HikariDataSource getDataSource(ExtensionContext extensionContext) {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/nbank");
            config.setUsername("postgres");
            config.setPassword("postgres");
            config.setMaximumPoolSize(10);
            config.setPoolName("junit-dsl");
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    public void afterAll(ExtensionContext extensionContext) {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
