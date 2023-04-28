package fr.snapgames.game.tests.unit.configuration;

public class TestObject {
    String title;
    Float factorF;
    Double factorD;
    Boolean active;

    public TestObject(String title, Float factorF, Double factorD, Boolean active) {
        this.title = title;
        this.factorF = factorF;
        this.factorD = factorD;
        this.active = active;
    }

    public Float getFactorF() {
        return factorF;
    }

    public void setFactorF(Float factorF) {
        this.factorF = factorF;
    }

    public Double getFactorD() {
        return factorD;
    }

    public void setFactorD(Double factorD) {
        this.factorD = factorD;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
