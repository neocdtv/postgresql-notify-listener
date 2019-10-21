package io.neocdtv;

import java.util.Arrays;
import java.util.List;

import static io.neocdtv.DataSourceFactory.DATABASE;
import static io.neocdtv.DataSourceFactory.HOST;
import static io.neocdtv.DataSourceFactory.PASSWORD;
import static io.neocdtv.DataSourceFactory.PORT;
import static io.neocdtv.DataSourceFactory.USER;

public enum Apps {
  generate(new Generate(), Arrays.asList(HOST, PORT, DATABASE, USER, PASSWORD, Generate.TABLE_NAME_PATTERN)),
  listen(new Listen(), Arrays.asList(HOST, PORT, DATABASE, USER, PASSWORD));

  Apps(final App tool, final List<String> argNames) {
    this.tool = tool;
    this.argNames = argNames;
  }

  private final App tool;
  private final List<String> argNames;

  public App getAppExec() {
    return tool;
  }

  public void validate(String args[]) {
    argNames.forEach(argName -> {
      final String argValue = CliUtil.findCommandArgumentByName(argName, args);
      if (argValue == null) {
        throw new RuntimeException(String.format("Required argument %s not found", argName));
      }
    });
  }
}
