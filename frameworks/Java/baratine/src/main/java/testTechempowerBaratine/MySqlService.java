package testTechempowerBaratine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.caucho.v5.jdbc.JdbcResultSet;
import com.caucho.v5.jdbc.JdbcService;

import io.baratine.service.OnInit;
import io.baratine.service.Result;
import io.baratine.service.Service;
import io.baratine.web.Get;
import io.baratine.web.Query;
import io.baratine.web.View;

@Service
public class MySqlService
{
  private Logger _logger = Logger.getLogger(MySqlService.class.toString());

  public static final int DB_ROWS = 10000;
  public static final int FORTUNE_ROWS = 12;

  private static final String SINGLE_QUERY = "SELECT * FROM World WHERE id = ?";
  private static final String UPDATE_QUERY = "UPDATE World SET randomNumber = ? WHERE id = ?";

  private static final String FORTUNE_QUERY = "SELECT * FROM Fortune";

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

        Number idVal = (Number) map.get("id");
        Number randomNumberVal = (Number) map.get("randomNumber");

        World world = new World(idVal.intValue(), randomNumberVal.intValue());

        result.ok(world);
      }
    }, SINGLE_QUERY, id);
  }

  @Get("/query")
  public void doQueries(@Query("count") String countStr, Result<World[]> result)
  {
    int count = 0;

    try {
      count = Integer.parseInt(countStr);
    }
    catch (Exception e) {
    }

    if (count < 1) {
      count = 1;
    }
    else if (count > 500) {
      count = 500;
    }

    doQueries(count, result);
  }

  private void doQueries(int count, Result<World[]> result)
  {
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
          JdbcResultSet rs = rsList[i];

          Map<String,Object> map = rs.getFirstRow();

          Number idVal = (Number) map.get("id");
          Number randomNumberVal = (Number) map.get("randomNumber");

          World world = new World(idVal.intValue(), randomNumberVal.intValue());

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
        World[] updateList = new World[list.length];

        for (int i = 0; i < list.length; i++) {
          World world = list[i];

          int id = world.getId();
          int randomNumber = _rand.nextInt(DB_ROWS);

          updateList[i] = new World(id, randomNumber);
        }

        doUpdate(updateList, result);
      }
    });
  }

  private void doUpdate(World[] updateList, Result<World[]> result)
  {
    Object[][] updateParamsList = new Object[updateList.length][2];

    for (int i = 0; i < updateList.length; i++) {
      World world = updateList[i];

      int id = world.getId();
      int randomNumber = _rand.nextInt(DB_ROWS);

      updateParamsList[i] = new Integer[] {randomNumber, id};
    }

    _jdbcService.queryBatch((rsList, e) -> {
      if (e != null) {
        result.fail(e);
      }
      else {
        result.ok(updateList);
      }
    }, UPDATE_QUERY, updateParamsList);
  }

  @Get("/fortune")
  public void getFortunes(Result<View> result)
  {
    _jdbcService.query((rs, e) -> {
      ArrayList<Fortune> list = new ArrayList<>();

      for (Map<String,Object> row : rs.getRows()) {
        Number idVal = (Number) row.get("id");
        String message = (String) row.get("message");

        Fortune fortune = new Fortune(idVal.intValue(), message);
        list.add(fortune);
      }

      list.add(new Fortune(0, "Additional fortune added at request time."));

      Collections.sort(list);

      View view = View.newView("fortunes.mustache").add("fortunes", list);

      result.ok(view);

    }, FORTUNE_QUERY);
  }
}
