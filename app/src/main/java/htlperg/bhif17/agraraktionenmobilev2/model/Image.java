package htlperg.bhif17.agraraktionenmobilev2.model;

public class Image {

    private boolean alreadyUsed;
    private byte[] bytes;
    private String classification;
    private int id;
    private double probability;
    private boolean usable;
    private String username;

    public Image(){

    }

    public Image (boolean alreadyUsed, byte[] bytes, String classification, int id, double probability, boolean usable, String username) {
        this.alreadyUsed = alreadyUsed;
        this.bytes = bytes;
        this.classification = classification;
        this.id = id;
        this.probability = probability;
        this.usable = usable;
        this.username = username;
    }

    public boolean isAlreadyUsed() {
        return alreadyUsed;
    }

    public void setAlreadyUsed(boolean alreadyUsed) {
        this.alreadyUsed = alreadyUsed;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public boolean isUsable() {
        return usable;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
