package htlperg.bhif17.agraraktionenmobilev2.model;


import java.util.Arrays;

public class CheckSum {
    private int id;
    private String checkSum;
    private String timestamp;
    private byte[] csvFile;
    private boolean changed;

    @Override
    public String toString() {
        return "CheckSum{" +
                "id=" + id +
                ", checkSum='" + checkSum + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", csvFile=" + Arrays.toString(csvFile) +
                ", changed=" + changed +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(byte[] csvFile) {
        this.csvFile = csvFile;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
