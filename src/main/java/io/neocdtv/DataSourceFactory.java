package io.neocdtv;

import com.impossibl.postgres.jdbc.PGDataSource;

public class DataSourceFactory {
  public static PGDataSource create() {
    PGDataSource dataSource = new PGDataSource();
    dataSource.setHost(getHost());
    dataSource.setPort(getPort());
    dataSource.setDatabaseName(getDatabase());
    dataSource.setUser(getUser());
    dataSource.setPassword(getPassword());
    dataSource.setSqlTrace(true);
    return dataSource;
  }

  private static String getHost() {
    return System.getProperty("host");
  }

  private static int getPort() {
    return Integer.valueOf(getVariableOrProperty("port"));
  }

  private static String getDatabase() {
    return getVariableOrProperty("database");
  }

  private static String getUser() {
    return getVariableOrProperty("user");
  }

  private static String getPassword() {
    return getVariableOrProperty("password");
  }

  private static String getVariableOrProperty(final String name) {
    final String variable = System.getenv(name);
    if (variable != null) {
      return variable;
    } else {
      return System.getProperty(name);
    }
  }
}
