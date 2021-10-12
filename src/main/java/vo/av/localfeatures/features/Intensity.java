package vo.av.localfeatures.features;

public class Intensity extends IntFeature{
    @Override
    public String getName() {
        return Constants.INTENSITY;
    }

    private Intensity(int value){data = value;}

    /**
     * Static constructor
     * @param value
     * @return
     */
    public static Intensity create(int value){
        return new Intensity(value);
    }

    public String toString(){
        return String.format("%d", data);
    }
}
