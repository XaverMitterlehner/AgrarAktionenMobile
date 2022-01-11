package htlperg.bhif17.agraraktionenmobilev2.model;

public class MyProperties {

    private static MyProperties mInstance = null;

    public String userLoginData;

    public String selectedCategory;

    public int priceFilter1;
    public int priceFilter2;

    public User user;

    protected MyProperties(){}

    public static synchronized MyProperties getInstance() {
        if(null == mInstance){
            mInstance = new MyProperties();
        }
        return mInstance;
    }

}
