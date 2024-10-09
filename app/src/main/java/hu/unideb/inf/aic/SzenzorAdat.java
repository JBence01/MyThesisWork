package hu.unideb.inf.aic;

public class SzenzorAdat {

    private double temperature;
    private double humidity;
    private double soilMoisture;
    private String valveState;

    public SzenzorAdat(double temperature, double humidity, double soilMoisture, String valveState) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.soilMoisture = soilMoisture;
        this.valveState = valveState;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getSoilMoisture() {
        return soilMoisture;
    }

    public void setSoilMoisture(double soilMoisture) {
        this.soilMoisture = soilMoisture;
    }

    public String getValveState() {
        return valveState;
    }

    public void setValveState(String valveState) {
        this.valveState = valveState;
    }
}
