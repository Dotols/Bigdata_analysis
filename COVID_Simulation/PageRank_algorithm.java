import java.util.*;
import java.io.*;

public class PageRank_algorithm{
  public static void main(final String args[]) throws NumberFormatException, IOException {

      PageRank pr = new PageRank();

      pr.data_load(2000);

      pr.init();

      pr.calc();

      pr.damp(1);

      pr.noderank();

      // pr.WriteCSV();

      // pr.print_path();

      // pr.print_spot();
  }
}

class PageRank {
  int k; // For Traversing
  double totalNodes;
  double pagerank[] = new double[2000];
  double sortrank[] = new double[2000];
  String line;
  String[] line_parser;
  int[] line_arr_int = new int[4];
  double[] line_arr_double = new double[2];
  int[][] link_arr = new int[2000][2000];
  int same_spot = 0, diff_spot = 0;
  ArrayList[][] path_detector = new ArrayList[182][4];
  List<Integer> keySetList;

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
          line_arr_double[j] = Double.parseDouble(line_parser[j+4]);
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

  public void init() {
    System.out.println("\n\nThe Number of WebPages : 2000");
    double InitialPageRank = 1 / totalNodes;
    System.out.println("Total Number of Nodes : " + totalNodes);
    System.out.println("Initial PageRank of All Nodes : " + InitialPageRank);
    // 0th ITERATION _ OR _ INITIALIZATION PHASE //
    for (k = 0; k < totalNodes; k++) {
      this.pagerank[k] = InitialPageRank;
    }

    // System.out.printf("\n Initial PageRank Values , 0th Step \n");
    // for (k = 1; k <= totalNodes; k++) {
    // System.out.printf(" Page Rank of " + k + " is :\t" + this.pagerank[k] +
    // "\n");
    // }
  }

  public void calc() {
    int ITERATION_STEP = 1;
    double OutgoingLinks = 0;
    final double TempPageRank[] = new double[3001];

    while (ITERATION_STEP <= 2000) // Iterations
    {
      // Store the PageRank for All Nodes in Temporary Array
      for (k = 1; k < totalNodes; k++) {
        TempPageRank[k] = this.pagerank[k];
        this.pagerank[k] = 0;
      }

      for (int InternalNodeNumber = 1; InternalNodeNumber < totalNodes; InternalNodeNumber++) {
        for (int ExternalNodeNumber = 1; ExternalNodeNumber < totalNodes; ExternalNodeNumber++) {
          if (this.link_arr[ExternalNodeNumber][InternalNodeNumber] != 0) {
            k = 1;
            OutgoingLinks = 0; // Count the Number of Outgoing Links for each ExternalNodeNumber
            while (k < totalNodes) {
              if (this.link_arr[ExternalNodeNumber][k] != 0) {
                OutgoingLinks = OutgoingLinks + this.link_arr[ExternalNodeNumber][k]; // Counter for Outgoing Links
              }
              k = k + 1;
            }
            // Calculate PageRank
            this.pagerank[InternalNodeNumber] += TempPageRank[ExternalNodeNumber]
                * (this.link_arr[ExternalNodeNumber][InternalNodeNumber] / OutgoingLinks);
          }
        }
      }
      System.out.printf("\nAfter " + ITERATION_STEP + "th Step \n");
      ITERATION_STEP = ITERATION_STEP + 1;
    }
  }

  public void damp(double dampingfactor) {
    // Add the Damping Factor to PageRank
    final double DampingFactor = dampingfactor;
    for (k = 0; k < totalNodes; k++) {
      this.pagerank[k] = (1 - DampingFactor) + DampingFactor * this.pagerank[k];
    }

    // Display PageRank
    // System.out.printf("\n Final Page Rank : \n");
    // for (k = 1; k <= totalNodes; k++) {
      // System.out.printf(" Page Rank of " + k + " is :\t" + this.pagerank[k] + "\n");
    // }
  }
  public void noderank() {
    Map<Integer, Double> map = new HashMap<Integer, Double>();

    for (k = 0; k < totalNodes; k++) {
      map.put(k, pagerank[k]);
    }

    keySetList = new ArrayList<>(map.keySet());
    Collections.sort(keySetList, (o1, o2) -> (map.get(o2).compareTo(map.get(o1))));

    System.out.println("\n----Most hottest Node----");
    k = 0;
    for (Integer key : keySetList) {
      System.out.println(String.format(k + " Rank Key : %s, Value : %5f", key, map.get(key)));
      k++;
      if (k > 100)
        break;
    }

  }

  public void print_path() {
    for (int i = 0; i < path_detector.length; i++) {
      System.out.println("Man " + i);
      // for (int j = 0; j < path_detector[i][0].size(); j+=100) {
      //   System.out.println("Day : " + path_detector[i][0].get(j) + "\tNode : " + path_detector[i][1].get(j));
      //   System.out.println("Latitude : " + path_detector[i][2].get(j) + "\tLongitude : " + path_detector[i][3].get(j));
      // }
      System.out.println("Frequency of move cluster : " + path_detector[i][0].size());
    }
  }

  public void print_spot() {
    System.out.println("different spot : " + diff_spot);
    System.out.println("Same spot : " + same_spot);
    System.out.println("total spot : " + (same_spot + diff_spot));
  }

  // public void WriteCSV() throws IOException {
  //   String createfile="./Pagerank.csv";
  //   FileWriter fw = new FileWriter(createfile);
  //   int chk=0;
  //   fw.append("Index, Score, Rank\n");

  //   for (k = 0; k < totalNodes; k++) {
  //     fw.append(Integer.toString(k) + ", ");
  //     fw.append(Integer.toString((int)(this.pagerank[k]*1000000)) + ", ");
  //     fw.append(keySetList.indexOf(k)+1 + ", ");
      
  //     for (int i = 0; i < 182; i++) {
  //       for(int j = 0 ; j<path_detector[i][1].size() ; j++) {
  //         // System.out.println("find :  " + (int)path_detector[i][1].get(j) + " cluster : " + k);

  //         if((int)path_detector[i][1].get(j) == k){
  //           chk = 1;
  //           fw.append(path_detector[i][2].get(j) + ", ");
  //           fw.append(path_detector[i][3].get(j) + "\n");
  //           System.out.println("cluster" + k + "lat : " + path_detector[i][2].get(j) + " lang : " + path_detector[i][3].get(j));
  //           break;
  //         }
  //       }


  //       if(chk == 1) {
  //         chk = 0;
  //         break;
  //       }
  //     }
  //   }
  //   fw.flush();
  //   fw.close();

  // }

}
