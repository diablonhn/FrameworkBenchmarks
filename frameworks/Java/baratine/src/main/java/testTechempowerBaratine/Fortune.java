package testTechempowerBaratine;

public class Fortune implements Comparable<Fortune>
{
  private int id;
  private String message;

  protected Fortune()
  {
  }

  public Fortune(int id, String message)
  {
    this.id = id;
    this.message = message;
  }

  public int getId()
  {
    return id;
  }

  public String getMessage()
  {
    return message;
  }

  @Override
  public int compareTo(Fortune fortune)
  {
    return message.compareTo(fortune.message);
  }
}
