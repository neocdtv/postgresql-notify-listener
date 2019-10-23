package io.neocdtv;

import com.impossibl.postgres.jdbc.PGDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Generate implements App {
  public static final String TABLE_NAME_PATTERN = "pattern";
  private static final String DROP_TRIGGER_TEMPLATE = "drop trigger %s_trigger on %s;";

  public void exec(String[] args) throws Throwable {
    final String host = CliUtil.findCommandArgumentByName(DataSourceFactory.HOST, args);
    final int port = Integer.valueOf(CliUtil.findCommandArgumentByName(DataSourceFactory.PORT, args));
    final String database = CliUtil.findCommandArgumentByName(DataSourceFactory.DATABASE, args);
    final String user = CliUtil.findCommandArgumentByName(DataSourceFactory.USER, args);
    final String password = CliUtil.findCommandArgumentByName(DataSourceFactory.PASSWORD, args);
    final String tableNamePattern = CliUtil.findCommandArgumentByName(TABLE_NAME_PATTERN, args);


    final PGDataSource dataSource = DataSourceFactory.create(host, port, database, user, password);
    final Connection connection = dataSource.getConnection();
    DatabaseMetaData metaData = connection.getMetaData();

    createOrReplaceNotifyTrigger(connection);
    try (ResultSet tables = metaData.getTables(null, null, tableNamePattern, new String[]{"TABLE"})) {
      while (tables.next()) {
        final String table_name = tables.getString("TABLE_NAME");
        final String columnsForTable = getColumnsForTable(connection, table_name);
        dropTrigger(connection, table_name);
        createTriggerComplex(connection, table_name, columnsForTable);
      }
    }
  }

  private static void createOrReplaceNotifyTrigger(final Connection connection) throws IOException {
    System.out.printf("Creating notify trigger function...");
    try {
      final String sql = loadFile("notify_trigger_complex.sql");
      executeUpdate(connection, sql.toString());
      System.out.println("OK");
    } catch (Throwable e) {
      System.out.println("FAIL");
    }
  }

  public static void dropTrigger(final Connection connection, final String tableName) {
    System.out.printf("Dropping trigger for table: " + tableName + "...");
    try {
      final String sql = String.format(DROP_TRIGGER_TEMPLATE, tableName, tableName);
      executeUpdate(connection, sql.toString());
      System.out.println("OK");
    } catch (Throwable e) {
      System.out.println("FAIL");
    }
  }

  public static void createTriggerComplex(final Connection connection, final String tableName, String columnsForTable) {
    System.out.printf("Creating trigger for table: " + tableName + "...");
    try {
      final String sqlTemplate = loadFile("create_trigger_complex_for_table.sql");
      final String sql = String.format(sqlTemplate, tableName, tableName, columnsForTable);
      executeUpdate(connection, sql);
      System.out.println("OK");
    } catch (Throwable e) {
      System.out.println("FAIL");
    }
  }

  private static String loadFile(final String notifyTriggerComplexFileName) throws IOException {
    final InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(notifyTriggerComplexFileName);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
    final StringBuffer lines = new StringBuffer();
    reader.lines().forEach(line -> {
      lines.append(line);
      lines.append("\n");
    });
    return lines.toString();
  }

  public static String getColumnsForTable(final Connection connection, final String tableName) throws SQLException {
    String sql = "select * from " + tableName;
    final Statement statement = connection.createStatement();
    final ResultSet resultSet = statement.executeQuery(sql);
    ResultSetMetaData metaData = resultSet.getMetaData();

    int rowCount = metaData.getColumnCount();

    final Set<String> columnNames = new TreeSet<>();
    for (int i = 0; i < rowCount; i++) {
      final String columnName = metaData.getColumnName(i + 1);
      columnNames.add(columnName);
    }
    return convertColumnNames(columnNames);
  }

  public static String convertColumnNames(final Set<String> columnNames) {
    final StringBuffer stringBuffer = new StringBuffer();
    Iterator<String> iterator = columnNames.iterator();
    while (iterator.hasNext()) {
      String column = iterator.next();
      stringBuffer.append("'");
      stringBuffer.append(column);
      stringBuffer.append("'");
      if (iterator.hasNext()) {
        stringBuffer.append(",");
      }
    }
    return stringBuffer.toString();
  }

  private static void executeUpdate(final Connection connection, final String sql) throws SQLException {
      Statement statement = connection.createStatement();
      statement.executeUpdate(sql);
  }
}
