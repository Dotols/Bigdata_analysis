import java.util.*;
import java.io.*;

public class Data_preprocessing {
    public static void main(final String args[]) throws NumberFormatException, IOException {
        Preprocessing pc = new Preprocessing();

        pc.data_load(3000);

        //pc.print_path();

        // pc.print_spot();
    }
}

class Preprocessing {
    String line;
    String[] line_parser;
    int[] line_arr_int = new int[4];
    double[] line_arr_double = new double[2];
    int[][] link_arr = new int[3001][3001];
    int same_spot = 0, diff_spot = 0;
    double totalNodes;
    ArrayList[][] path_detector = new ArrayList[182][4];
    int[][] path_arr = new int[182][];

    public void data_load(double total) throws NumberFormatException, IOException {
        this.totalNodes = total;
        String path = "./PreprocessedData_with_latlong/";

        for (int i = 0; i < 182; i++) {
            path_detector[i][0] = new ArrayList<Integer>();
            path_detector[i][1] = new ArrayList<Integer>();
            path_detector[i][2] = new ArrayList<Double>();
            path_detector[i][3] = new ArrayList<Double>();
        }

        for (int i = 0; i < 200; i++) {
            System.out.println(path + "part-00" + String.format("%03d", i));
            final File file = new File(path + "part-00" + String.format("%03d", i));
            final FileReader file_reader = new FileReader(file);
            final BufferedReader br = new BufferedReader(file_reader);

            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                line_parser = line.substring(1, line.length() - 1).split(",");
                for (int j = 0; j < 4; j++) {
                    line_arr_int[j] = (int) Double.parseDouble(line_parser[j]);
                }
                for (int j = 0; j < 2; j++) {
                    line_arr_double[j] = Double.parseDouble(line_parser[j + 4]);
                }
                if (line_arr_int[2] == line_arr_int[3]) {
                    same_spot++;
                } else {
                    link_arr[line_arr_int[2]][line_arr_int[3]]++;
                    diff_spot++;

                    path_detector[line_arr_int[1]][0].add(line_arr_int[0]);
                    path_detector[line_arr_int[1]][1].add(line_arr_int[2]);
                    path_detector[line_arr_int[1]][2].add(line_arr_double[0]);
                    path_detector[line_arr_int[1]][3].add(line_arr_double[1]);
                }
            }
            file_reader.close();
        }


        System.out.println("Read  file complete\n");
    }
    
    public void path_calc(){
        for (int i=0 ; i<182 ; i++) {
            path_arr[i] = new int[path_detector[i][0].size()];
            for(int j=0 ; j<path_detector[i][0].size() ; j++){
                path_arr[i][j] = (int) path_detector[i][1].get(j);
            }
        }
    }

    public void print_path() {
        for (int i = 0; i < path_detector.length; i++) {
            System.out.println("Man " + i);
            // for (int j = 0; j < path_detector[i][0].size(); j+=100) {
                // System.out.println("Day : " + path_detector[i][0].get(j) + "\tNode : " + path_detector[i][1].get(j));
                // System.out.println("Latitude : " + path_detector[i][2].get(j) + "\tLongitude: " + path_detector[i][3].get(j));
            // }
            System.out.println("Frequency of move cluster : " + path_detector[i][0].size());
        }
    }

    // public void print_spot(){
    //     System.out.println("different spot : " + diff_spot);
    //     System.out.println("Same spot : " + same_spot);
    //     System.out.println("total spot : " + (same_spot + diff_spot));
    // }
}
