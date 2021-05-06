package au.edu.federation.itech3107.fedunimillionaire30360914.models;

public class Score {

    private long id;
    private String name;
    private int money;
    private String datetime;
    private boolean isChecked = false;
    private double lat;
    private double lng;

    public Score(String name, int money, String datetime, double lat, double lng) {
        this.name = name;
        this.money = money;
        this.datetime = datetime;
        this.lat = lat;
        this.lng = lng;
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

    public String getMoney() {
        return String.format("$%d", money);
    }

    public String getDatetime() {
        return datetime;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "Score {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                ", datetime='" + datetime + '\'' +
                ", isChecked=" + isChecked +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
