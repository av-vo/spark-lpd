package vo.av.localfeatures.utils.io;

import java.io.*;

public class ASCIIGrid {
    /**
     *  The {@code WriteArcASCII} class provides a static in-core method for converting
     *  Rasterize2D output to Esri's ASCII raster file.
     *  <p>
     *  For file specification, see <a href="http://help.arcgis.com/en/arcgisdesktop/10.0/help/index.html#/ESRI_ASCII_raster_format/009t0000000z000000/"></a> of
     *  <i>Esri's ArcGIS Resource Center</i>.
     *
     *  @author av1966
     */
    //public class WriteArcASCII {
    public static void main(String[] args) throws IOException {
        String fileName = "4";
        String inputDir = String.format("/Users/av1966/tmp/shadow/output/%s",fileName),
                outputFile = String.format("/Users/av1966/tmp/shadow/output/%s.grd",fileName);
        double[] vMin = new double[]{316000,234000};
        double[] vMax = new double[]{316500,234500};
        double cellSize = .25;

        int noCols = (int) Math.ceil((vMax[0]-vMin[0])/cellSize);
        int noRows = (int) Math.ceil((vMax[1]-vMin[1])/cellSize);

        double xllCorner = (int) (vMin[0]);
        double yllCorner = (int) (vMin[1]);

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));

        writer.write("ncols " + noCols + "\n" );
        writer.write("nrows " + noRows + "\n" );
        writer.write("xllcorner " + xllCorner + "\n" );
        writer.write("yllcorner " + yllCorner + "\n" );
        writer.write("cellsize " + cellSize + "\n" );
        writer.write("NODATA_value -9999\n" );

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File directory, String fileName) {
                return fileName.startsWith("part");
            }
        };

        File iDir = new File(inputDir);
        File[] files = iDir.listFiles(filter);

        String line;

        int[][] grid = new int[noRows][noCols];

        for(File file : files){
            System.out.println(file.getAbsoluteFile());
            BufferedReader reader = new BufferedReader(new FileReader(file));

            while ((line = reader.readLine()) != null) {
                line = line.replace("(", "");
                line = line.replace(")", "");
                String[] tokens = line.split(",");

                int xG = (int) Double.parseDouble(tokens[0]);
                int yG = (int) Double.parseDouble(tokens[1]);

                int illuminance = Integer.parseInt(tokens[3]);

                if(xG<0 || yG<0 || xG >= noCols || yG >= noRows) continue;

                if(grid[yG][xG]<illuminance)
                    grid[yG][xG] = illuminance;
            }

            reader.close();
        }

        for(int yG = noRows-1; yG > -1; yG--){
            for(int xG = 0; xG < noCols; xG++){
                writer.write(Math.round(grid[yG][xG]) + " ");
            }
            writer.write("\n");
        }

        writer.close();
    }
    //}
}
