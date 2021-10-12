package vo.av.localfeatures.sparktypes;

/**
 * Wrapper for LPD to avoid excessive decimal points when writing to files.
 */
public class LPD {
    double lpd;

    public LPD(double lpd){
        this.lpd = lpd;
    }

    @Override
    public String toString(){
        return String.format("%.1f", lpd);
    }
}
