package org.jobrunr.storage.sql.postgres;

import org.apache.tomcat.jdbc.pool.DataSource;

class TomcatJdbcPoolPostgresStorageProviderTest extends AbstractPostgresStorageProviderTest {

    private static DataSource dataSource;

    @Override
    protected DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new DataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl(sqlContainer.getJdbcUrl());
            dataSource.setUsername(sqlContainer.getUsername());
            dataSource.setPassword(sqlContainer.getPassword());
        }
        return dataSource;
    }
}