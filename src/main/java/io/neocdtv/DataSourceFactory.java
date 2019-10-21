package io.neocdtv;

import com.impossibl.postgres.jdbc.PGDataSource;

import java.util.Set;
import java.util.TreeSet;

public class DataSourceFactory {

  static final String HOST = "host";
  static final String PORT = "port";
  static final String DATABASE = "database";
  static final String USER = "user";
  static final String PASSWORD = "password";
  static Set<String> ARG_NAMES = new TreeSet<>();

  static {
    ARG_NAMES.add(HOST);
    ARG_NAMES.add(PORT);
    ARG_NAMES.add(DATABASE);
    ARG_NAMES.add(USER);
    ARG_NAMES.add(PASSWORD);
  }

  public static PGDataSource create(final String host, final int port, final String database, final String user, final String password) {
    PGDataSource dataSource = new PGDataSource();
    dataSource.setHost(host);
    dataSource.setPort(port);
    dataSource.setDatabaseName(database);
    dataSource.setUser(user);
    dataSource.setPassword(password);
    dataSource.setSqlTrace(true);
    return dataSource;
  }

  @Deprecated
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
    return System.getProperty(DataSourceFactory.HOST);
  }

  private static int getPort() {
    return Integer.valueOf(getVariableOrProperty(PORT));
  }

  private static String getDatabase() {
    return getVariableOrProperty(DATABASE);
  }

  private static String getUser() {
    return getVariableOrProperty(USER);
  }

  private static String getPassword() {
    return getVariableOrProperty(PASSWORD);
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
