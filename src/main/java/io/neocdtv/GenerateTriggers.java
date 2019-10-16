package io.neocdtv;

import com.impossibl.postgres.jdbc.PGDataSource;

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

  private static final String CREATE_SIMPLE_TRIGGER_TEMPLATE =
      "create trigger %s_trigger \n" +
          "    AFTER INSERT OR UPDATE OR DELETE on %s\n" +
          "    FOR EACH ROW EXECUTE PROCEDURE notify_change();";
  private static final String CREATE_COMPLEX_TRIGGER_TEMPLATE =
      "create trigger %s_trigger" +
          "    AFTER INSERT OR UPDATE OR DELETE ON %s\n" +
          "    FOR EACH ROW EXECUTE PROCEDURE notify_trigger(%s);";
  private static final String DROP_TRIGGER_TEMPLATE = "drop trigger %s_trigger on %s;";

  public static void main(String[] args) throws SQLException {
    final PGDataSource dataSource = DataSourceFactory.create();

    final Connection connection = dataSource.getConnection();
    DatabaseMetaData metaData = connection.getMetaData();
    try (ResultSet tables = metaData.getTables(null, null, "t_%", new String[]{"TABLE"})) {
      while (tables.next()) {
        final String table_name = tables.getString("TABLE_NAME");
        System.out.println("working on table: " + table_name);
        final String columnsForTable = getColumnsForTable(connection, table_name);
        dropTrigger(connection, table_name);
        createComplexTrigger(connection, table_name, columnsForTable);
      }
    }
  }

  public static void dropTrigger(final Connection connection, final String tableName) {
    System.out.println("dropping trigger for table: " + tableName);
    final String dropTriggerSql = String.format(DROP_TRIGGER_TEMPLATE, tableName, tableName);
    executeUpdate(connection, dropTriggerSql);
  }

  public static void createSimpleTrigger(final Connection connection, final String tableName) {
    System.out.println("creating simple trigger for table: " + tableName);
    final String createTriggerSql = String.format(CREATE_SIMPLE_TRIGGER_TEMPLATE, tableName, tableName);
    executeUpdate(connection, createTriggerSql);
  }

  public static void createComplexTrigger(final Connection connection, final String tableName, String columnsForTable) {
    System.out.println("creating complex trigger for table: " + tableName);
    final String createTriggerSql = String.format(CREATE_COMPLEX_TRIGGER_TEMPLATE, tableName, tableName, columnsForTable);
    executeUpdate(connection, createTriggerSql);
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
