import java.util.Random;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

//import Data_preprocessing;


class Person {
    int index;
    String name;
    int finish_stage;

    ArrayList<Integer> path;
    int position;
    boolean initial_infection;
    boolean infection;

    int stage = 0;
    boolean finish_flag = false;
    float infect_rate = 0.4f;

    public Person(int index, ArrayList<Integer> path_arraylist, boolean infection, String name) {
        this.index = index;
        this.path = path_arraylist;
        this.position = this.initial_position(path);
        this.finish_stage = path.size();
        this.infection = infection;
        this.name = name;
        this.initial_infection = infection;
    }

    public void Deepcopy(Person person) {
        this.index = person.index;
        this.position = person.position;
        this.path = person.path;
        this.finish_stage = person.finish_stage;
        this.infection = person.infection;
        this.name = person.name;
        this.initial_infection = person.initial_infection;
    }

    private int initial_position(ArrayList<Integer> path){
        if (path.size() == 0){
            return -1;
        }else{
            return this.path.get(this.stage);
        }
    }

    public int get_position() {
        return this.position;
    }

    public boolean get_infection() {
        return this.infection;
    }

    public void change_initial_infection(boolean tf) {
        this.initial_infection = tf;
        this.infection = tf;
    }

    public void update_next_position() {
        this.update_stage();
        this.update_position();

    }

    public void update_state(boolean infected_place) {
        this.update_infected(infected_place);
    }

    private void update_stage() {
        if (this.finish_stage - 1 <= this.stage) {
            this.finish_flag = true;
        }
        this.stage++;

    }

    private void update_position() {
        if (this.finish_flag == false) {
            this.position = path.get(this.stage);
        }
    }

    private void update_infected(boolean infected_place) {
        if (infected_place == true && this.infection == false && this.finish_flag == false) {
            this.infection = this.infected();
        }

    }

    private boolean infected() {
        float infect_rate = this.infect_rate;
        float non_infect_rate = 1 - infect_rate;
        boolean infected_result;
        Random rand = new Random();
        infected_result = rand.nextInt(100) <= infect_rate * 100;
        // System.out.println("infected result : " + infected_result);
        return infected_result;
    }

    public void show_info() {
        System.out.println("person_index : " + this.index);
        System.out.println("position : " + this.position);
        System.out.println("stage : " + this.stage);
        System.out.println("path : " + this.path);
        System.out.println("finish_stage : " + this.finish_stage);
        System.out.println("finish_flag : " + this.finish_flag);
        System.out.println("initial_infection : " + this.initial_infection);
        System.out.println("infection : " + this.infection);
        // System.out.println("#############################################");
    }

    public int compareTo(Person s) {
        if (this.index < s.index) {
            return -1;
        } else if (this.index > s.index) {
            return 1;
        }
        return 0;
    }

    public int[] convertIntegers(ArrayList<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }
}

class Node {
    int covide_service_time = 3;

    int index;
    String name;
    int[] coordinate;
    ArrayList<Person> now_people_list = new ArrayList<Person>();
    ArrayList<Integer> now_people_list_index = new ArrayList<>();
    ArrayList<Person> infect_member_in_node = new ArrayList<>();
    Boolean contaminated = false;

    ArrayList<Person> added_person_list = new ArrayList<Person>();
    int contaminated_remain_stage = 0;

    ArrayList<Person> infect_member_come = new ArrayList<Person>();
    ArrayList<Person> infect_member_new = new ArrayList<Person>();

    public Node(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public void Deepcopy(Node node) {
        this.index = node.index;
        this.name = node.name;
        this.coordinate = node.coordinate;
    }

    public int get_infect_member_size() {
        return this.infect_member_in_node.size();
    }

    public ArrayList<Integer> get_infected_people_index() {
        ArrayList<Integer> people_list = new ArrayList<>();
        for (Person person : this.infect_member_in_node) {
            people_list.add(person.index);
        }
        Collections.sort(people_list);
        return people_list;
    }

    public void add_person(Person person) {
        this.added_person_list.add(person);
        // System.out.println(person.index + " Add");
    }

    public void update_state() {
        ArrayList<Person> people = new ArrayList<Person>();
        ArrayList<Person> infected_people = new ArrayList<Person>();
        boolean infected_people_in_node = false;

        people.addAll(this.added_person_list);

        this.init_poeple_list();
        this.control_contaminate_state();

        this.now_people_list.addAll(people);
        for (Person person : people) {
            now_people_list_index.add(person.index);
        }

        infected_people.addAll(this.take_infected_people_info(people));
        this.infect_member_come.addAll(infected_people);

        if (infected_people.size() > 0) {
            infected_people_in_node = true;
        }

        this.change_contaminated_state(infected_people_in_node);
    }

    public void update_node_info() {
        ArrayList<Person> _infect_member_in_node = new ArrayList<Person>();
        this.infect_member_new.addAll(this.take_infected_people_info(this.now_people_list));
        this.infect_member_in_node
                .addAll(this.find_infected_new_people(this.infect_member_come, this.infect_member_new));

        // System.out.println("Node : "+this.index);
        // System.out.println("Come people : " +
        // this.get_people_index(this.infect_member_come));
        // System.out.println("NEW people : " +
        // this.get_people_index(this.infect_member_new));
        // System.out.println("Update people : " +
        // this.get_people_index(this.find_infected_new_people(this.infect_member_come,
        // this.infect_member_new)));
        // System.out.println("infected people : " +
        // this.get_people_index(this.infect_member_in_node));

        _infect_member_in_node.addAll(this.infect_member_in_node);
        // Set<Person> set = new HashSet<Person>(_infect_member_in_node);
        this.infect_member_in_node.clear();
        this.infect_member_in_node.addAll(_infect_member_in_node);
    }

    private void control_contaminate_state() {
        if (this.contaminated_remain_stage <= 1) {
            this.contaminated = false;
        } else {
            this.contaminated_remain_stage--;
        }

    }

    private void change_contaminated_state(boolean infection_info) {
        if (infection_info == true) {
            this.contaminated = true;
            this.contaminated_remain_stage = this.covide_service_time;
        }

    }

    private void init_poeple_list() {
        this.now_people_list.clear();
        this.now_people_list_index.clear();
        this.infect_member_come.clear();
        this.infect_member_new.clear();
        this.added_person_list.clear();

    }

    private ArrayList<Person> take_infected_people_info(ArrayList<Person> people) {
        ArrayList<Person> now_people_infected = new ArrayList<Person>();
        if (people.size() == 0) {
            return new ArrayList<Person>();
        }

        for (Person person : people) {
            if (person.infection) {
                now_people_infected.add(person);
            }
        }
        return now_people_infected;

    }

    private ArrayList<Person> find_infected_new_people(ArrayList<Person> old_people, ArrayList<Person> new_people) {
        ArrayList<Person> infected_people_in_node = new ArrayList<>();
        ArrayList<Integer> old_people_index = new ArrayList<>();
        ArrayList<Integer> new_people_index = new ArrayList<>();

        if (old_people.size() == 0) {
            return new_people;
        }

        for (Person person : old_people) {
            old_people_index.add(person.index);
        }
        for (Person person : new_people) {
            new_people_index.add(person.index);
        }
        for (int i = 0; i < new_people_index.size(); i++) {
            if (!old_people_index.contains(new_people_index.get(i))) {
                infected_people_in_node.add(new_people.get(i));
            }
        }
        return infected_people_in_node;
    }

    public void show_info() {
        System.out.println("Node_index : " + this.index);
        System.out.println("now_people_list_index : " + this.now_people_list_index);
        System.out.println("infect_member_node : " + this.get_infected_people_index());
        System.out.println("contaminated_state : " + this.contaminated);
        // System.out.println("#############################################");
    }

    public ArrayList<Integer> get_people_index(ArrayList<Person> people) {
        ArrayList<Integer> people_index = new ArrayList<>();
        for (Person person : people) {
            people_index.add(person.index);
        }
        return people_index;
    }

    public int compareTo(Node s) {
        if (this.index < s.index) {
            return -1;
        } else if (this.index > s.index) {
            return 1;
        }
        return 0;
    }
}

class Two_result_people {
    ArrayList<Person> infected = new ArrayList<Person>();
    ArrayList<Person> non_infected = new ArrayList<Person>();
}

class Two_result_node {
    ArrayList<Node> contamiated = new ArrayList<Node>();
    ArrayList<Node> non_contamiated = new ArrayList<Node>();
}

class Simulation {
    ArrayList<Person> people_list = new ArrayList<Person>();
    ArrayList<Node> node_list = new ArrayList<Node>();

    int stage = 0;

    public Simulation(ArrayList<Person> people, ArrayList<Node> node_list) {
        this.people_list.addAll(people);
        this.node_list.addAll(node_list);
    }

    public void initial_stage() {
        this.go_node(this.people_list);
        this.update_node_state(this.node_list);
        this.update_people_state(this.people_list);
        this.update_node_info(this.node_list);
    }

    public void next_stage() {
        this.update_stage(this.people_list);

        this.go_node(this.people_list);
        this.update_node_state(this.node_list);
        this.update_people_state(this.people_list);
        this.update_node_info(this.node_list);
    }

    public void show_info() {
        // ArrayList<ArrayList> people_array = new
        // ArrayList<ArrayList>(this.find_infected_member(this.people_list));
        // ArrayList<ArrayList> node_array = new
        // ArrayList<ArrayList>(this.find_contaminated_node(this.node_list));
        Two_result_people two_people_list = new Two_result_people();
        Two_result_node two_node_list = new Two_result_node();
        two_people_list = this.find_infected_member(this.people_list);
        two_node_list = this.find_contaminated_node(this.node_list);

        ArrayList<Integer> infected_people_index = new ArrayList<Integer>(
                this.return_people_index(two_people_list.infected));
        ArrayList<Integer> non_infected_people_index = new ArrayList<Integer>(
                this.return_people_index(two_people_list.non_infected));
        ArrayList<Integer> contaminated_node_index = new ArrayList<Integer>(
                this.return_node_index(two_node_list.contamiated));
        ArrayList<Integer> non_contaminated_node_index = new ArrayList<Integer>(
                this.return_node_index(two_node_list.non_contamiated));

        System.out.println("******************************************");
        System.out.println("******************************************");
        System.out.println("******************************************");
        System.out.println("Show situation");
        System.out.println("Stage : " + this.stage);
        System.out.println("infected_poeple : " + infected_people_index);
        System.out.println("non-infected_people : " + non_infected_people_index);
        System.out.println("contaminated_node_index : " + contaminated_node_index);
        System.out.println("non-contaminated_node_index : " + non_contaminated_node_index);

        // System.out.println("========================================");
        // System.out.println("========================================");
        // System.out.println("show people info");
        // this.show_people(this.people_list);

        // System.out.println("========================================");
        // System.out.println("========================================");
        // System.out.println("show node info");
        // this.show_nodes(this.node_list);

    }

    public void go_node(ArrayList<Person> people) {
        for (Person person : people) {
            this.find_node(person.position).add_person(person);
        }
    }

    public void update_node_state(ArrayList<Node> node_list) {
        for (Node node : node_list) {
            node.update_state();
        }
    }

    public void update_people_state(ArrayList<Person> people) {
        for (Person person : people) {
            person.update_state(this.find_node(person.position).contaminated);
        }

    }

    public void update_node_info(ArrayList<Node> node_list) {
        for (Node node : node_list) {
            node.update_node_info();
        }
    }

    public void update_stage(ArrayList<Person> people) {
        this.stage++;
        for (Person person : people) {
            person.update_next_position();
        }
    }

    public void show_people(ArrayList<Person> people) {
        for (Person person : people) {
            person.show_info();
            System.out.println("========================================");
        }

    }

    public void show_nodes(ArrayList<Node> node_list) {
        for (Node node : node_list) {
            node.show_info();
            System.out.println("========================================");
        }
    }

    public ArrayList<Node> indexing_node(ArrayList<Node> node_list) {
        Collections.sort(node_list, new Comparator<Node>() {
            @Override
            public int compare(Node s1, Node s2) {
                return s1.compareTo(s2);
            }
        });
        return node_list;
    }

    public ArrayList<Person> indexing_people(ArrayList<Person> people) {
        Collections.sort(people, new Comparator<Person>() {
            @Override
            public int compare(Person s1, Person s2) {
                return s1.compareTo(s2);
            }
        });
        return people;
    }

    private Node find_node(int index) {
        for (Node node : this.node_list) {
            if (node.index == index) {
                return node;
            }
        }
        return null;
    }

    private Two_result_people find_infected_member(ArrayList<Person> people) {
        Two_result_people result = new Two_result_people();
        ArrayList<Person> infected = new ArrayList<>();
        ArrayList<Person> non_infected = new ArrayList<>();

        for (Person person : people) {
            if (person.infection == true) {
                infected.add(person);
            } else {
                non_infected.add(person);
            }
        }

        result.infected.addAll(infected);
        result.non_infected.addAll(non_infected);
        return result;
    }

    private Two_result_node find_contaminated_node(ArrayList<Node> node_list) {
        Two_result_node result = new Two_result_node();
        ArrayList<Node> contaminated = new ArrayList<>();
        ArrayList<Node> non_contaminated = new ArrayList<>();

        for (Node node : node_list) {
            if (node.contaminated == true) {
                contaminated.add(node);
            } else {
                non_contaminated.add(node);
            }
        }
        result.contamiated.addAll(contaminated);
        result.non_contamiated.addAll(non_contaminated);
        return result;
    }

    private ArrayList<Integer> return_people_index(ArrayList<Person> list) {
        ArrayList<Integer> result = new ArrayList<>();
        for (Person person : list) {
            result.add(person.index);
        }
        return result;
    }

    private ArrayList<Integer> return_node_index(ArrayList<Node> list) {
        ArrayList<Integer> result = new ArrayList<>();
        for (Node node : list) {
            result.add(node.index);
        }
        return result;
    }
}

class Node_rank {
    Node node;
    int index;
    String name;
    int[] coordinate;
    int infected_number;

    int rank;

    public Node_rank(Node node) {
        this.node = node;
        this.index = node.index;
        this.name = node.name;
        this.coordinate = node.coordinate;
        this.infected_number = 0;
    }

    public void Infected_number_add(int infected_num) {
        this.infected_number = this.infected_number + infected_num;
    }

    public int compareTo(Node_rank s) {
        if (this.infected_number < s.infected_number) {
            return 1;
        } else if (this.infected_number > s.infected_number) {
            return -1;
        }
        return 0;
    }
}

class Node_rank_sort_index {
    Node node;
    int index;
    String name;
    int[] coordinate;
    int infected_number;

    int rank;

    public Node_rank_sort_index(Node_rank node) {
        this.node = node.node;
        this.index = node.index;
        this.name = node.name;
        this.coordinate = node.coordinate;
        this.infected_number = node.infected_number;
        this.rank = node.rank;
    }

    public void Infected_number_add(int infected_num) {
        this.infected_number = this.infected_number + infected_num;
    }

    public int compareTo(Node_rank_sort_index s) {
        if (this.index < s.index) {
            return -1;
        } else if (this.index > s.index) {
            return 1;
        }
        return 0;
    }
}

class Simulation_multiple_time {

    int sim_times;
    int last_stage;
    ArrayList<Person> people = new ArrayList<>();
    ArrayList<Node> node_list = new ArrayList<>();
    ArrayList<Node_rank> node_rank_list = new ArrayList<>();
    ArrayList<Node_rank_sort_index> node_rank_sort_index_list = new ArrayList<>();

    public Simulation_multiple_time(int sim_times, ArrayList<Person> people, ArrayList<Node> node_list) {
        this.sim_times = sim_times;
        this.people = people;
        this.node_list = node_list;
        this.mapping_node_node_rank();
        this.last_stage = this.return_last_stage(people);
    }

    public void investigate_results() {
        this.Ranking_node_rank_list(this.node_rank_list);
        this.node_rank_copy(this.node_rank_list, this.node_rank_sort_index_list);
        this.Indexing_node_rank_list(this.node_rank_sort_index_list);
    }

    public void simulation_infected_multiple() {
        for (int i = 0; i < this.sim_times; i++) {
            this.simulation_infected(this.people, this.node_list);
        }
    }

    public void simulation_infected(ArrayList<Person> people, ArrayList<Node> node_list) {
        ArrayList<Person> people_tmp = new ArrayList<Person>();
        ArrayList<Node> node_list_tmp = new ArrayList<Node>();
        for (int i = 0; i < people.size(); i++) {
            System.out.println("Infected_person : " + i);
            people_tmp.clear();
            node_list_tmp.clear();
            people_tmp.addAll(this.deepcopy_people(people));
            node_list_tmp.addAll(this.deepcopy_node(node_list));

            people_tmp.get(i).change_initial_infection(true);
            this.simulation_basic(people_tmp, node_list_tmp);
        }
    }

    public void simulation_basic(ArrayList<Person> people, ArrayList<Node> node_list) {
        Simulation simulation = new Simulation(people, node_list);

        simulation.initial_stage();
        // simulation.show_info();
        for (int i = 0; i < this.last_stage; i++) {
            simulation.next_stage();
            // simulation.show_info();
        }
        // simulation.show_info();
        this.add_node_rank_infected_number(node_list);
    }

    private void add_node_rank_infected_number(ArrayList<Node> node_list) {
        for (int i = 0; i < node_list.size(); i++) {
            if (this.node_rank_list.get(i).index == node_list.get(i).index) {
                this.node_rank_list.get(i).Infected_number_add(node_list.get(i).infect_member_in_node.size());
            } else {
                try {
                    throw new Exception("Different node!!!");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    System.out.println("Different node!!!!");
                    e.printStackTrace();
                }
            }

        }
    }

    private void mapping_node_node_rank() {
        for (Node node : this.node_list) {
            this.node_rank_list.add(new Node_rank(node));
        }
    }

    private void node_rank_copy(ArrayList<Node_rank> node_rank_list, ArrayList<Node_rank_sort_index> node_rank_index_list){
        for (Node_rank node : node_rank_list){
            node_rank_index_list.add(new Node_rank_sort_index(node));
        }
    }

    public void Ranking_node_rank_list(ArrayList<Node_rank> node_rank_list) {
        Collections.sort(node_rank_list, new Comparator<Node_rank>() {
            @Override
            public int compare(Node_rank s1, Node_rank s2) {
                return s1.compareTo(s2);
            }
        });
        for (int i = 0 ; i < node_rank_list.size() ; i++){
            node_rank_list.get(i).rank = i;
        }

    }

    public void Indexing_node_rank_list(ArrayList<Node_rank_sort_index> node_rank_sort_index_list) {
        Collections.sort(node_rank_sort_index_list, new Comparator<Node_rank_sort_index>() {
            @Override
            public int compare(Node_rank_sort_index s1, Node_rank_sort_index s2) {
                return s1.compareTo(s2);
            }
        });

    }

    public ArrayList<Integer> return_ranking_index() {
        ArrayList<Integer> node_ranking_index = new ArrayList<Integer>();
        for (Node_rank node_rank : this.node_rank_list) {
            node_ranking_index.add(node_rank.index);
        }
        return node_ranking_index;
    }

    public ArrayList<Integer> return_ranking_infect_number() {
        ArrayList<Integer> node_ranking_infect_number = new ArrayList<Integer>();
        for (Node_rank node_rank : this.node_rank_list) {
            node_ranking_infect_number.add(node_rank.infected_number);
        }
        return node_ranking_infect_number;
    }

    public int return_last_stage(ArrayList<Person> people) {
        ArrayList<Integer> stage_list = new ArrayList<Integer>();
        for (Person person : people) {
            stage_list.add(person.finish_stage);
        }
        return Collections.max(stage_list);
    }

    private ArrayList<Person> deepcopy_people(ArrayList<Person> people) {
        ArrayList<Person> people_new = new ArrayList<>();
        for (Person person : people) {
            Person tmp_person = new Person(person.index, person.path, person.infection, person.name);
            people_new.add(tmp_person);
        }
        return people_new;
    }

    private ArrayList<Node> deepcopy_node(ArrayList<Node> node_list) {
        ArrayList<Node> node_list_new = new ArrayList<>();
        for (Node node : node_list) {
            Node tmp_node = new Node(node.index, node.name);
            node_list_new.add(tmp_node);
        }
        return node_list_new;
    }

    public void show_rank(ArrayList<Node_rank> node_rank_list) {
        for (int i = 0; i < node_rank_list.size(); i++) {
            System.out.println("Rank : " + node_rank_list.get(i).rank);
            System.out.println("node : " + node_rank_list.get(i).index);
            System.out.println("infect_num : " + node_rank_list.get(i).infected_number);
            System.out.println("----------------------------------------------");
        }
    }

    public void show_node_indexed(ArrayList<Node_rank_sort_index> node_rank_sort_index_list) {
        for (int i = 0; i < node_rank_sort_index_list.size(); i++) {
            System.out.println("node : " + node_rank_sort_index_list.get(i).index);
            System.out.println("Rank : " + node_rank_sort_index_list.get(i).rank);
            System.out.println("infect_num : " + node_rank_sort_index_list.get(i).infected_number);
            System.out.println("----------------------------------------------");
        }
    }

    public void save_csv_file() {
        String creatfile_index = "./result_node.csv";
        String creatfile_rank = "./result_node_rank.csv";

        FileWriter fw_index;
        try {
            fw_index = new FileWriter(creatfile_index);
            fw_index.append("index");
            fw_index.append(",");
            fw_index.append("infect_num");
            fw_index.append(",");
            fw_index.append("rank");
            fw_index.append("\n");

            for (Node_rank_sort_index node : this.node_rank_sort_index_list){
                fw_index.append(Integer.toString(node.index));
                fw_index.append(",");
                fw_index.append(Integer.toString(node.infected_number));
                fw_index.append(",");
                fw_index.append(Integer.toString(node.rank));
                fw_index.append("\n");
            }
            fw_index.flush();
            fw_index.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        FileWriter fw_rank;
        try {
            fw_rank = new FileWriter(creatfile_rank);
            fw_rank.append("index");
            fw_rank.append(",");
            fw_rank.append("infect_num");
            fw_rank.append(",");
            fw_rank.append("rank");
            fw_rank.append("\n");

            for (Node_rank node : this.node_rank_list){
                fw_rank.append(Integer.toString(node.index));
                fw_rank.append(",");
                fw_rank.append(Integer.toString(node.infected_number));
                fw_rank.append(",");
                fw_rank.append(Integer.toString(node.rank));
                fw_rank.append("\n");
            }
            fw_rank.flush();
            fw_rank.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

class Covid_simulation{
    ArrayList<Person> people = new ArrayList<Person>();
    ArrayList<Node> node_list = new ArrayList<Node>();
    int sim_times;

    int people_number = 182;
    int cluster_num = 3000;

    public Covid_simulation(int sim_times){
        this.sim_times = sim_times;
    }

    public void read_data() throws NumberFormatException, IOException {
        Preprocessing pc = new Preprocessing();
        pc.data_load((double)this.cluster_num);

        this.people.clear();
        this.node_list.clear();

        for (int i = 0; i < this.people_number; i++){
            //System.out.println(i);
            //System.out.println(pc.path_detector[i][1]);
            this.people.add(new Person(i, pc.path_detector[i][1], false,"null"));
        }
        for (int i  = -1; i < this.cluster_num; i++){
            this.node_list.add(new Node(i, "null"));
        }
    }

    public void covid_simulation(){
        Simulation_multiple_time sim_multi = new Simulation_multiple_time(this.sim_times, this.people,this.node_list);
        sim_multi.simulation_infected_multiple();
        sim_multi.investigate_results();
        sim_multi.show_rank(sim_multi.node_rank_list);
        System.out.println("========================================================");
        System.out.println("========================================================");
        sim_multi.show_node_indexed(sim_multi.node_rank_sort_index_list);
        sim_multi.save_csv_file();
    }
}


public class COVID_Simulation {
    public static void main(String args[]) throws NumberFormatException, IOException {
        
        Covid_simulation covid_sim = new Covid_simulation(3);
        covid_sim.read_data();
        covid_sim.covid_simulation();
        
    }
}

