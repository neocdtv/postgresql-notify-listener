package io.neocdtv;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;

import java.sql.SQLException;
import java.sql.Statement;

public class ListenOnTriggers {
  public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {

    CliUtil.setPropertiesFromArgs(DataSourceFactory.ARG_NAMES, args);
    final String channelName = "db_notifications";
    final PGDataSource dataSource = DataSourceFactory.create();

    PGNotificationListener listener = new PGNotificationListener() {
      @Override
      public void notification(int processId, String channelName, String payload) {
        System.out.println(String.format("processId: %d, channelName: %s, notification: %s", processId, channelName, payload));
      }
    };

    try (PGConnection connection = (PGConnection) dataSource.getConnection()) {
      Statement statement = connection.createStatement();
      statement.execute(String.format("LISTEN %s", channelName));
      statement.close();
      connection.addNotificationListener(listener);
      while(true) {
        Thread.sleep(10);
      }
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
