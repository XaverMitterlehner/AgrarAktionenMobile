package htlperg.bhif17.agraraktionenmobilev2.model;

import java.util.List;

public class Item {

/*
    private String artikelbezeichnung;
    private String beschreibungsfeld;
    private String bildLink;
    private double versandkosten;

    public Item(String artikelbezeichnung, String beschreibungsfeld, String bildLink, double versandkosten) {
        this.artikelbezeichnung = artikelbezeichnung;
        this.beschreibungsfeld = beschreibungsfeld;
        this.bildLink = bildLink;
        this.versandkosten = versandkosten;
    }

    public String getArtikelbezeichnung() {
        return artikelbezeichnung;
    }

    public void setArtikelbezeichnung(String artikelbezeichnung) {
        this.artikelbezeichnung = artikelbezeichnung;
    }

    public String getBeschreibungsfeld() {
        return beschreibungsfeld;
    }

    public void setBeschreibungsfeld(String beschreibungsfeld) {
        this.beschreibungsfeld = beschreibungsfeld;
    }

    public String getBildLink() {
        return bildLink;
    }

    public void setBildLink(String bildLink) {
        this.bildLink = bildLink;
    }

    public double getVersandkosten() {
        return versandkosten;
    }

    public void setVersandkosten(double versandkosten) {
        this.versandkosten = versandkosten;
    }*/


    private ApiLink apiLink;
    private String artikelbezeichnung;
    private String artikelnummer;
    private String beschreibungsfeld;
    private String bildLink;
    private String bruttopreis;
    private String deeplink;
    private String ean;
    private String fourthCategory;
    private String hersteller;
    private int itemId;
    private String kategoriepfad;
    private double percentage;
    private String primeCategory;
    private String secondCategory;
    private String stattpreis;
    private String thirdCategory;
    private String timestamp;
    private String verfuegbarkeit;
    private String versandkosten;
    //private List<Price> prices;

    public Item() {
    }

    public Item(ApiLink apiLink, String artikelbezeichnung, String artikelnummer, String beschreibungsfeld, String bildLink, String bruttopreis, String deeplink, String ean, String fourthCategory, String hersteller, int itemId, String kategoriepfad, double percentage, String primeCategory, String secondCategory, String stattpreis, String thirdCategory, String timestamp, String verfuegbarkeit, String versandkosten) {
        this.apiLink = apiLink;
        this.artikelbezeichnung = artikelbezeichnung;
        this.artikelnummer = artikelnummer;
        this.beschreibungsfeld = beschreibungsfeld;
        this.bildLink = bildLink;
        this.bruttopreis = bruttopreis;
        this.deeplink = deeplink;
        this.ean = ean;
        this.fourthCategory = fourthCategory;
        this.hersteller = hersteller;
        this.itemId = itemId;
        this.kategoriepfad = kategoriepfad;
        this.percentage = percentage;
        this.primeCategory = primeCategory;
        this.secondCategory = secondCategory;
        this.stattpreis = stattpreis;
        this.thirdCategory = thirdCategory;
        this.timestamp = timestamp;
        this.verfuegbarkeit = verfuegbarkeit;
        this.versandkosten = versandkosten;
    }

    public ApiLink getApiLink() {
        return apiLink;
    }

    public void setApiLink(ApiLink apiLink) {
        this.apiLink = apiLink;
    }

    public String getArtikelbezeichnung() {
        return artikelbezeichnung;
    }

    public void setArtikelbezeichnung(String artikelbezeichnung) {
        this.artikelbezeichnung = artikelbezeichnung;
    }

    public String getArtikelnummer() {
        return artikelnummer;
    }

    public void setArtikelnummer(String artikelnummer) {
        this.artikelnummer = artikelnummer;
    }

    public String getBeschreibungsfeld() {
        return beschreibungsfeld;
    }

    public void setBeschreibungsfeld(String beschreibungsfeld) {
        this.beschreibungsfeld = beschreibungsfeld;
    }

    public String getBildLink() {
        return bildLink;
    }

    public void setBildLink(String bildLink) {
        this.bildLink = bildLink;
    }

    public String getBruttopreis() {
        return bruttopreis;
    }

    public void setBruttopreis(String bruttopreis) {
        this.bruttopreis = bruttopreis;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getFourthCategory() {
        return fourthCategory;
    }

    public void setFourthCategory(String fourthCategory) {
        this.fourthCategory = fourthCategory;
    }

    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getKategoriepfad() {
        return kategoriepfad;
    }

    public void setKategoriepfad(String kategoriepfad) {
        this.kategoriepfad = kategoriepfad;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getPrimeCategory() {
        return primeCategory;
    }

    public void setPrimeCategory(String primeCategory) {
        this.primeCategory = primeCategory;
    }

    public String getSecondCategory() {
        return secondCategory;
    }

    public void setSecondCategory(String secondCategory) {
        this.secondCategory = secondCategory;
    }

    public String getStattpreis() {
        return stattpreis;
    }

    public void setStattpreis(String stattpreis) {
        this.stattpreis = stattpreis;
    }

    public String getThirdCategory() {
        return thirdCategory;
    }

    public void setThirdCategory(String thirdCategory) {
        this.thirdCategory = thirdCategory;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVerfuegbarkeit() {
        return verfuegbarkeit;
    }

    public void setVerfuegbarkeit(String verfuegbarkeit) {
        this.verfuegbarkeit = verfuegbarkeit;
    }

    public String getVersandkosten() {
        return versandkosten;
    }

    public void setVersandkosten(String versandkosten) {
        this.versandkosten = versandkosten;
    }
}
