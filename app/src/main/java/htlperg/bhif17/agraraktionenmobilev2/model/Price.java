package htlperg.bhif17.agraraktionenmobilev2.model;

import lombok.Data;

public class Price {

    private String bruttopreis;
    private String stattpreis;
    private boolean validFlag;

    public Price(String bruttopreis, String stattpreis, boolean validFlag) {
        this.bruttopreis = bruttopreis;
        this.stattpreis = stattpreis;
        this.validFlag = validFlag;
    }

    public Price(){

    }

    public String getBruttopreis() {
        return bruttopreis;
    }

    public void setBruttopreis(String bruttopreis) {
        this.bruttopreis = bruttopreis;
    }

    public String getStattpreis() {
        return stattpreis;
    }

    public void setStattpreis(String stattpreis) {
        this.stattpreis = stattpreis;
    }

    public boolean isValidFlag() {
        return validFlag;
    }

    public void setValidFlag(boolean validFlag) {
        this.validFlag = validFlag;
    }
}
