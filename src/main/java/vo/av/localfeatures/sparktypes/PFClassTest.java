package vo.av.localfeatures.sparktypes;

public class PFClassTest {
    public static void main(String[] args){
        shoutOut(PointFeaturesType1.class);
        shoutOut(PointFeaturesType2.class);
    }

    private static void shoutOut(Class<? extends PointFeatures> clazz){
        if(clazz == PointFeaturesType1.class){
            System.out.println("type 1");
        }else if(clazz == PointFeaturesType2.class){
            System.out.println("type 2");
        }
    }
}
