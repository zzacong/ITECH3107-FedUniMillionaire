package au.edu.federation.itech3107.fedunimillionaire30360914.models;

public class Score {

    private long id;
    private String name;
    private int money;
    private String datetime;

    public Score(String name, int money, String datetime) {
        this.name = name;
        this.money = money;
        this.datetime = datetime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "Score {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
