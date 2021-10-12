package vo.av.localfeatures.functions;

public class CoordSystem {
    //https://en.wikipedia.org/wiki/Spherical_coordinate_system
    //https://in.mathworks.com/help/matlab/ref/cart2sph.html

    /**
     * Convert from Cartesian to spherical coordinates.
     * @param xyz Cartesian coordinates
     * @return spherical coordinates [azimuth, inclination, radius]
     */
    public static double[] cart2sph(double[] xyz){
        double radius = Math.sqrt(
                xyz[0]*xyz[0]
                        +xyz[1]*xyz[1]
                        +xyz[2]*xyz[2]);
        double azimuth = Math.atan2(xyz[1], xyz[0]);
        double inclination = Math.acos(xyz[2]/radius);
        return new double[]{
                azimuth, inclination, radius
        };
    }
}
