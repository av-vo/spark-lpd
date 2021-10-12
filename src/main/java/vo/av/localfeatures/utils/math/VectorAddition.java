package vo.av.localfeatures.utils.math;

import org.ejml.data.DMatrix3;
import org.ejml.dense.fixed.CommonOps_DDF3;

public class VectorAddition {
    public static void main(String[] args){
        DMatrix3 vect1 = new DMatrix3(1, 2, 3);
        DMatrix3 vect2 = new DMatrix3(3, 9, 3);

        DMatrix3 vectSum = new DMatrix3();

        CommonOps_DDF3.add(vect1, vect2, vectSum);

        vectSum.print();

        DMatrix3 vectProduct = new DMatrix3();
        CommonOps_DDF3.elementMult(vect1, vect2, vectProduct);



        vectProduct.print();
    }
}
