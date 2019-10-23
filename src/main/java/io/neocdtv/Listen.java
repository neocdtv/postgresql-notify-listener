package io.neocdtv;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;

import java.sql.SQLException;
import java.sql.Statement;

public class Listen implements App {
  public void exec(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {

    final String channelName = "db_notifications";

    final String host = CliUtil.findCommandArgumentByName(DataSourceFactory.HOST, args);
    final int port = Integer.valueOf(CliUtil.findCommandArgumentByName(DataSourceFactory.PORT, args));
    final String database = CliUtil.findCommandArgumentByName(DataSourceFactory.DATABASE, args);
    final String user = CliUtil.findCommandArgumentByName(DataSourceFactory.USER, args);
    final String password = CliUtil.findCommandArgumentByName(DataSourceFactory.PASSWORD, args);
    final PGDataSource dataSource = DataSourceFactory.create(host, port, database, user, password);

    PGNotificationListener listener = new PGNotificationListener() {
      @Override
      public void notification(int processId, String channelName, String payload) {
        System.out.println(String.format("======== Database event received over channel %s and processId %d ======== ", channelName, processId));
        System.out.println(String.format(payload));
      }
    };

    try (PGConnection connection = (PGConnection) dataSource.getConnection()) {
      System.out.printf("Starting to listen on database events...");
      Statement statement = connection.createStatement();
      statement.execute(String.format("LISTEN %s", channelName));
      statement.close();
      connection.addNotificationListener(listener);
      System.out.println("OK");
      while(true) {
        Thread.sleep(10);
      }
    } catch (Throwable e) {
      System.out.println("FAIL");
      throw e;
    }
  }
}
