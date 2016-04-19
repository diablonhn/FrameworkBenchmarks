package testTechempowerBaratine;

import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.caucho.v5.jdbc.JdbcService;
import com.caucho.v5.jdbc.ResultSetKraken;

import io.baratine.service.OnInit;
import io.baratine.service.Result;
import io.baratine.service.Service;
import io.baratine.web.Get;
import io.baratine.web.Query;

@Service
public class MySqlService
{
  private Logger _logger = Logger.getLogger(MySqlService.class.toString());

  public static final int DB_ROWS = 10000;

  private static final String SINGLE_QUERY = "SELECT * FROM World WHERE `id` = ?";
  private static final String UPDATE_QUERY = "UPDATE World SET `randomNumber` = ? WHERE `id` = ?";

  private Random _rand = new Random(0);

  @Inject
  private JdbcService _jdbcService;

  @OnInit
  public void onInit(Result<Void> result)
  {
    result.ok(null);
  }

  @Get("/db")
  public void doSingleQuery(Result<World> result)
  {
    int id = _rand.nextInt(DB_ROWS);

    if (_logger.isLoggable(Level.FINER)) {
      _logger.fine("doSingleQuery: id=" + id);
    }

    _jdbcService.query((rs, e) -> {
      if (e != null) {
        result.fail(e);
      }
      else {
        Map<String,Object> map = rs.getFirstRow();

        World world = new World((Integer) map.get("id"), (Integer) map.get("randomNumber"));

        result.ok(world);
      }
    }, SINGLE_QUERY, id);
  }

  @Get("/query")
  public void doQueries(@Query("queries") int count, Result<World[]> result)
  {
    if (count < 1) {
      count = 1;
    }
    else if (count > 500) {
      count = 500;
    }

    Object[][] idList = new Object[count][1];

    for (int i = 0; i < count; i++) {
      int randomNumber = _rand.nextInt(DB_ROWS);

      idList[i][0] = randomNumber;
    }

    _jdbcService.queryBatch((rsList, e) -> {
      if (e != null) {
        result.fail(e);
      }
      else {
        World[] list = new World[rsList.length];

        for (int i = 0; i < rsList.length; i++) {
          ResultSetKraken rs = rsList[i];

          Map<String,Object> map = rs.getFirstRow();

          World world = new World((Integer) map.get("id"), (Integer) map.get("randomNumber"));

          list[i] = world;
        }

        result.ok(list);
      }
    }, SINGLE_QUERY, idList);
  }

  @Get("/update")
  public void doUpdate(@Query("count") int count, Result<World[]> result)
  {
    doQueries(count, (list, e) -> {
      if (e != null) {
        result.fail(e);
      }
      else {
        Object[][] updateParamsList = new Object[list.length][2];

        for (int i = 0; i < list.length; i++) {
          World world = list[i];

          int id = world.getId();
          int randomNumber = _rand.nextInt(DB_ROWS);

          updateParamsList[i] = new Integer[] {randomNumber, id};
        }

        result.ok(list);
      }
    });
  }
}
