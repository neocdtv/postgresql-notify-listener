package io.neocdtv;

import java.util.Set;

public class CliUtil {

  static String findCommandArgumentByName(final String argNameToFind, final String[] args) {
    for (String argToCheck : args) {
      final String[] split = argToCheck.split("=");
      if (split[0].equals(argNameToFind)) {
        return split[1];
      }
    }
    return null;
  }

  static void setPropertiesFromArgs(final Set<String> argNames, final String[] args) {
    argNames.forEach(argName -> {
      final String argValue = findCommandArgumentByName(argName, args);
      if (argValue != null) {
        System.setProperty(argName, argValue);
      }
    });
  }
}
