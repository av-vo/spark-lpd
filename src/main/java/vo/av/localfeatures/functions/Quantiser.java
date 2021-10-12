package vo.av.localfeatures.functions;

public class Quantiser {

    public static int[] realToGrid(
            double[] coords,
            double[] offset,
            double[] scale
    ) {
        int len = coords.length;
        assert (offset.length >= len && scale.length >= len);
        int[] gridCoords = new int[len];

        for (int i = 0; i < len; i++) {
            gridCoords[i] = (int) Math.round((coords[i] - offset[i]) / scale[i]);
        }

        return gridCoords;
    }

    public static double[] gridToReal(
            int[] gridCoords,
            double[] offset,
            double[] scale
    ) {
        int len = gridCoords.length;
        assert (offset.length >= len && scale.length >= len);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++) {
            coords[i] = gridCoords[i] * scale[i] + offset[i];
        }

        return coords;
    }

    public static double[][] gridIdx2BBox(int[] gridCoords, double[] offset, double[] scale){

        double[] coords = gridToReal(gridCoords, offset, scale);

        double[] min = new double[coords.length];
        double[] max = new double[coords.length];

        for(int i=0; i< coords.length; i++){
            min[i] = coords[i] - scale[i]/2;
            max[i] = coords[i] + scale[i]/2;
        }

        return new double[][]{min, max};
    }

}
