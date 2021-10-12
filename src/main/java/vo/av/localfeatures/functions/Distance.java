package vo.av.localfeatures.functions;

public class Distance {
    public static double point2pointD2(double[] p1, double[] p2) {
        double d = 0;

        for (int i = 0; i < p1.length; i++) {
            double diff = (p1[i] - p2[i]);
            if (!Double.isNaN(diff)) {
                d += diff * diff;
            }
        }

        return d;
    }

    public static double point2RegionD2(double[] point, double[] min, double[] max) {
        double d = 0;

        for (int i = 0; i < point.length; i++) {
            double diff = 0;
            if (point[i] > max[i]) {
                diff = (point[i] - max[i]);
            }
            else if (point[i] < min[i]) {
                diff = (point[i] - min[i]);
            }

            if (!Double.isNaN(diff)) {
                d += diff * diff;
            }
        }

        return d;
    }
}
