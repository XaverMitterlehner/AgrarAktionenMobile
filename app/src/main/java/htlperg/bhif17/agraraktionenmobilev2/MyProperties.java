package htlperg.bhif17.agraraktionenmobilev2;

public class MyProperties {

    private static MyProperties mInstance = null;

    public String userLoginData;

    public String selectedCategory;

    public String priceFilter1;
    public String priceFilter2;

    protected MyProperties(){}

    public static synchronized MyProperties getInstance() {
        if(null == mInstance){
            mInstance = new MyProperties();
        }
        return mInstance;
    }

}
