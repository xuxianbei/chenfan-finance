package com.chenfan.finance.config;

/** @author lizj */
public class DatabaseContextHolder {

  private static final ThreadLocal<DatabaseType> CONTEXT_HOLDER = ThreadLocal.withInitial(() -> DatabaseType.finance);

  private DatabaseContextHolder() {}

  public static DatabaseType getDatabaseType() {
    return CONTEXT_HOLDER.get();
  }

  public static void setDatabaseType(DatabaseType type) {
    CONTEXT_HOLDER.set(type);
  }

  public static void remove() {
    CONTEXT_HOLDER.remove();
  }
}
