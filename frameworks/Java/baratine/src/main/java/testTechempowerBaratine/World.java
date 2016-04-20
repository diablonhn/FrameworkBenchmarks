package testTechempowerBaratine;

public class World
{
  private int id;
  private int randomNumber;

  protected World()
  {
  }

  public World(int id, int randomNumber)
  {
    this.id = id;
    this.randomNumber = randomNumber;
  }

  public int getId()
  {
    return id;
  }

  public int getRandomNumber()
  {
    return randomNumber;
  }
}
