package io.neocdtv;

public class AppSelector {

  public static void main(String[] args) throws Throwable {

    final String selectedApp = CliUtil.findCommandArgumentByName("app", args);
    if (selectedApp == null) {
      throw new RuntimeException(String.format("Required argument %s not found", "app"));
    }

    for (final Apps app : Apps.values()) {
      if (app.name().equals(selectedApp)) {
        app.validate(args);
        app.getAppExec().exec(args);
      }
    }
  }

}
