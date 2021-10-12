package vo.av.localfeatures.functions;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.decomposition.eig.SwitchingEigenDecomposition_DDRM;

import java.util.List;

public class LocalFeatures {

    private double[] normal;
    private double[] center;
    private double[] eigenValue;
    private double residual;
    private double rho;
    private List<Integer> inliers;
    private List<Integer> outliers;

    public double[] normal() {
        return this.normal;
    }

    public double[] center() {
        return this.center;
    }

    public double[] eigenValue() {
        return this.eigenValue;
    }

    public double residual() {
        return this.residual;
    }

    public double rho() {
        return this.rho;
    }

    public List<Integer> inliers() {
        return this.inliers;
    }

    public List<Integer> outliers() {
        return this.outliers;
    }

    public void normal(double[] normal) {
        this.normal = normal;
    }

    public void center(double[] center) {
        this.center = center;
    }

    public void eigenValue(double[] eigenValue) {
        this.eigenValue = eigenValue;
    }

    public void residual(double residual) {
        this.residual = residual;
    }

    public void rho(double rho) {
        this.rho = rho;
    }

    public void inliers(List<Integer> inliers) {
        this.inliers = inliers;
    }

    public void outliers(List<Integer> outliers) {
        this.outliers = outliers;
    }

    public double inlierRatio() {
        return (double)this.inliers.size() / (double)(this.inliers.size() + this.outliers.size());
    }

    public static LocalFeatures compute(double[][] data) {
        LocalFeatures localFeatures = new LocalFeatures();
        int numPoints = data.length;
        RealMatrix matrix = new Array2DRowRealMatrix(data);
        RealMatrix covarianceMatrix = (new Covariance(matrix)).getCovarianceMatrix();

        EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);
        double[] eigenValue = ed.getRealEigenvalues();

        //todo this is clumsy
        int idx = 0;
        if (eigenValue[1] <= eigenValue[0] && eigenValue[1] <= eigenValue[2]) {
            idx = 1;
        } else if (eigenValue[2] <= eigenValue[0] && eigenValue[2] <= eigenValue[1]) {
            idx = 2;
        }

        double[] normal = ed.getEigenvector(idx).toArray();
        double[] center = matrix.preMultiply(
                new ArrayRealVector(numPoints, 1.0D)
        ).mapMultiply(1.0D / (double)numPoints).toArray();

        double d = -(normal[0] * center[0] + normal[1] * center[1] + normal[2] * center[2]);
        double SE = 0.0D;
        double denominator = normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2];

        for(int i = 0; i < data.length; ++i) {
            double[] pt = data[i];
            SE += (normal[0] * pt[0] + normal[1] * pt[1] + normal[2] * pt[2] + d) * (normal[0] * pt[0] + normal[1] * pt[1] + normal[2] * pt[2] + d) / denominator;
        }

        localFeatures.normal(normal);
        //localFeatures.center(center);
        //localFeatures.eigenValue(eigenValue);
        //localFeatures.residual(Math.sqrt(SE / (double)data.length));
        return localFeatures;
    }

    public static LocalFeatures compute2(double[][] data) {
        LocalFeatures localFeatures = new LocalFeatures();
        int numPoints = data.length;

        RealMatrix matrix = new Array2DRowRealMatrix(data);
        RealMatrix covarianceMatrix = (new Covariance(matrix)).getCovarianceMatrix();

        SwitchingEigenDecomposition_DDRM ed = new SwitchingEigenDecomposition_DDRM(numPoints);

        ed.decompose(new DMatrixRMaj(covarianceMatrix.getData()));

        int numEigenValues = ed.getNumberOfEigenvalues();

        //for(int i=0; i< numEigenValues; i++){
        //    System.out.printf("%d %.3f %s\n", i, ed.getEigenvalue(i).real, ed.getEigenVector(i));
        //}

        int idx = 0;
        if (ed.getEigenvalue(1).real <= ed.getEigenvalue(0).real
                && ed.getEigenvalue(1).real <= ed.getEigenvalue(2).real) {
            idx = 1;
        } else if (ed.getEigenvalue(2).real <= ed.getEigenvalue(0).real
                && ed.getEigenvalue(2).real <= ed.getEigenvalue(1).real) {
            idx = 2;
        }

        double[] normal = ed.getEigenVector(idx).data;
        double[] center = matrix.preMultiply(
                new ArrayRealVector(numPoints, 1.0D)
        ).mapMultiply(1.0D / (double)numPoints).toArray();

        double d = -(normal[0] * center[0] + normal[1] * center[1] + normal[2] * center[2]);
        double SE = 0.0D;
        double denominator = normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2];

        for(int i = 0; i < data.length; ++i) {
            double[] pt = data[i];
            SE += (normal[0] * pt[0] + normal[1] * pt[1] + normal[2] * pt[2] + d) * (normal[0] * pt[0] + normal[1] * pt[1] + normal[2] * pt[2] + d) / denominator;
        }

        localFeatures.normal(normal);
        //localFeatures.center(center);
        //localFeatures.eigenValue();
        //localFeatures.residual(Math.sqrt(SE / (double)data.length));
        return localFeatures;
    }

    /*
    public static LocalFeatures compute(double[][] data, double distanceThreshold) {
        LocalFeatures result = new LocalFeatures();
        int k = data.length;
        RealMatrix matrix = new Array2DRowRealMatrix(data);
        RealMatrix covarianceMatrix = (new Covariance(matrix)).getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);
        double[] eigenValue = ed.getRealEigenvalues();
        int idx = 0;
        if (eigenValue[1] <= eigenValue[0] && eigenValue[1] <= eigenValue[2]) {
            idx = 1;
        } else if (eigenValue[2] <= eigenValue[0] && eigenValue[2] <= eigenValue[1]) {
            idx = 2;
        }

        double[] normal = ed.getEigenvector(idx).toArray();
        double[] center = matrix.preMultiply(new ArrayRealVector(k, 1.0D)).mapMultiply(1.0D / (double)k).toArray();
        double d = normal[0] * center[0] + normal[1] * center[1] + normal[2] * center[2];
        if (d < 0.0D) {
            d = -d;

            for(int i = 0; i < 3; ++i) {
                normal[i] = -normal[i];
            }
        }

        double SE = 0.0D;
        double denominator = normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2];
        List<Integer> inliers = new ArrayList();
        List<Integer> outliers = new ArrayList();
        int pIdx = 0;

        for(int i = 0; i < data.length; ++i) {
            double[] pt = data[i];
            double distance = (normal[0] * pt[0] + normal[1] * pt[1] + normal[2] * pt[2] - d) * (normal[0] * pt[0] + normal[1] * pt[1] + normal[2] * pt[2] - d) / denominator;
            if (Math.sqrt(distance) <= distanceThreshold) {
                inliers.add(pIdx++);
            } else {
                outliers.add(pIdx++);
            }

            SE += distance;
        }

        result.normal(normal);
        result.center(center);
        result.eigenValue(eigenValue);
        result.residual(Math.sqrt(SE / (double)data.length));
        result.rho(Math.sqrt(d * d / denominator));
        result.inliers(inliers);
        result.outliers(outliers);
        return result;
    }
     */

}
