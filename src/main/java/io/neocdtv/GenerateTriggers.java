package io.neocdtv;

import com.impossibl.postgres.jdbc.PGDataSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class GenerateTriggers {
  private static final String DROP_TRIGGER_TEMPLATE = "drop trigger %s_trigger on %s;";

  public static void main(String[] args) throws SQLException, IOException {

    CliUtil.setPropertiesFromArgs(DataSourceFactory.ARG_NAMES, args);

    final PGDataSource dataSource = DataSourceFactory.create();
    final Connection connection = dataSource.getConnection();
    DatabaseMetaData metaData = connection.getMetaData();

    createOrReplaceNotifyTrigger(connection);
    try (ResultSet tables = metaData.getTables(null, null, "t_%", new String[]{"TABLE"})) {
      while (tables.next()) {
        final String table_name = tables.getString("TABLE_NAME");
        System.out.println("working on table: " + table_name);
        final String columnsForTable = getColumnsForTable(connection, table_name);
        dropTrigger(connection, table_name);
        createTriggerComplex(connection, table_name, columnsForTable);
      }
    }
  }

  private static void createOrReplaceNotifyTrigger(final Connection connection) throws IOException {
    System.out.println("creating notify trigger complex");
    final String sql = loadFile("notify_trigger_complex.sql");
    executeUpdate(connection, sql.toString());
  }

  public static void dropTrigger(final Connection connection, final String tableName) {
    System.out.println("dropping trigger for table: " + tableName);
    final String dropTriggerSql = String.format(DROP_TRIGGER_TEMPLATE, tableName, tableName);
    executeUpdate(connection, dropTriggerSql);
  }

  public static void createTriggerComplex(final Connection connection, final String tableName, String columnsForTable) throws IOException {
    System.out.println("creating trigger complex for table: " + tableName);
    final String sql = loadFile("create_trigger_complex_for_table.sql");
    final String createTriggerSql = String.format(sql, tableName, tableName, columnsForTable);
    executeUpdate(connection, createTriggerSql);
  }

  private static String loadFile(final String notifyTriggerComplexFileName) throws IOException {
    final URL resource = Thread.currentThread().getContextClassLoader().getResource(notifyTriggerComplexFileName);
    final StringBuffer lines = new StringBuffer();
    for (String line : Files.readAllLines(new File(resource.getPath()).toPath())) {
      lines.append(line);
      lines.append("\n");
    }
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

  private static void executeUpdate(final Connection connection, final String sql) {
    try {
      Statement statement = connection.createStatement();
      statement.executeUpdate(sql);
    } catch (Throwable e) {
      System.err.println(e.getMessage());
    }
  }
}
