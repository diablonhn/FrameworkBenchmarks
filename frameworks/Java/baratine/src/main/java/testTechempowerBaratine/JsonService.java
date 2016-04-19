package testTechempowerBaratine;

import io.baratine.service.Result;
import io.baratine.service.Service;
import io.baratine.web.Get;

@Service
public class JsonService
{
  @Get("/json")
  public void hello(Result<Message> result)
  {
    result.ok(new Message("Hello, World!"));
  }
}
