package testTechempowerBaratine;

import static io.baratine.web.Web.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.caucho.v5.jdbc.JdbcService;
import com.caucho.v5.jdbc.JdbcServiceImpl;

import io.baratine.web.Web;

public class Main
{
  public static void main(String[] args)
    throws Exception
  {
    String jdbcHost;

    if (args.length > 0) {
      jdbcHost = args[0];
    }
    else {
      jdbcHost = "jdbc:hsqldb:mem:testdb";
    }

    if (jdbcHost.startsWith("jdbc:hsqldb:mem")) {
      initHsqldb(jdbcHost);
    }

    if (args.length > 1 && "finer".equals(args[1])) {
      Logger.getLogger("").setLevel(Level.FINER);
    }

    System.out.println("Main.main: jdbcHost=" + jdbcHost);

    Web.property(JdbcService.CONFIG_URL, jdbcHost);

    include(JdbcServiceImpl.class);

    include(PlaintextService.class);
    include(JsonService.class);
    include(MySqlService.class);

    start();
  }

  private static void initHsqldb(String url)
    throws SQLException
  {
    Connection conn = null;

    try {
      conn = DriverManager.getConnection(url);

      try {
        conn.prepareCall("DROP TABLE testTable").execute();
      }
      catch (SQLException e) {
      }

      conn.createStatement().execute("CREATE TABLE World (\"id\" INT PRIMARY KEY, \"randomNumber\" INT)");

      for (int i = 0; i < MySqlService.DB_ROWS; i++) {
        conn.createStatement().execute("INSERT INTO World VALUES (" + i + ", " + 9000 + i + ")");
      }
    }
    finally {
      if (conn != null) {
        conn.close();
      }
    }
  }
}
