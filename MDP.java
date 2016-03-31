import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.Random;

public class MDP {
	
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	
	public static int [][]obstacles = {
			{3, 1},
			{3, 2},
			{4, 3}
	};
	
	public static int [][]goal = {{2,4}};
	public static int []agent = {4,1};
	public static int [][]bound_x = {{1,0},{2,0},{3,0},{4,0},{5,0},{1,6},{2,6},{3,6},{4,6},{5,6}};
	public static int [][]bound_y = {{0,1},{0,2},{0,3},{0, 4},{0, 5},{6, 1},{6, 2},{6, 3},{6, 4},{6, 5}};
	
	public static List<Coordinates> no_action = new ArrayList<Coordinates>();
	public static List<Coordinates> bound = new ArrayList<Coordinates>();
	
	public static double gamma = 0.9;
	public static int iteration = 100;
	
	public static Map<Integer, Double> reward = new HashMap<Integer, Double>();
	public static Map<Integer, Double> utility = new HashMap<Integer, Double>();
	public static Map<Integer, String> direction = new HashMap<Integer, String>();
	
	public static List<Coordinates> updated = new ArrayList<Coordinates>();
	public static List<Coordinates> present = new ArrayList<Coordinates>();
	
		
	public static double p = 0.7;
	public static double q = (1-p)/2;
	
	
	/**
	 * Method to calculate vertical sum
	 * @param next_list
	 * @param m
	 * @param isNorth
	 * @return
	 */
	public static double goVertical(List<Coordinates> next_list, Coordinates m, boolean isNorth){
		double sum_correct = 0.0, sum_neigh_E = 0.0, sum_neigh_W = 0.0;
		Coordinates next_co = null;
		
		if(isNorth){
			next_co = new Coordinates(m.x-1, m.y);
		} else {
			next_co = new Coordinates(m.x+1, m.y);
		}
		
		if(!bound.contains(next_co)){
			int next_pos = 5 * (next_co.x-1) + next_co.y;
			sum_correct = gamma * (p * utility.get(next_pos));
			if(!next_list.contains(next_co) && !no_action.contains(next_co)){
				next_list.add(next_co);
			}
		} else {
			sum_correct = 0;
		}
		//Neighboring cell west
		next_co = new Coordinates(m.x, m.y-1);
		if(!bound.contains(next_co)){
			int next_pos = 5 * (next_co.x-1) + next_co.y;
			sum_neigh_W = gamma * (q * utility.get(next_pos));
			if(!next_list.contains(next_co) && !no_action.contains(next_co)){
				next_list.add(next_co);
			}
		} else {
			sum_neigh_W = 0;
		}
		//Neighboring cell East
		next_co = new Coordinates(m.x, m.y+1);
		if(!bound.contains(next_co)){
			int next_pos = 5 * (next_co.x-1) + next_co.y;
			sum_neigh_E = gamma * (q * utility.get(next_pos));
			if(!next_list.contains(next_co) && !no_action.contains(next_co)){
				next_list.add(next_co);
			}
		} else {
			sum_neigh_E = 0;
		}
		
		return sum_correct+sum_neigh_E+sum_neigh_W;
	}
	
	/**
	 * Method to calculate horizontal sum
	 * @param next_list
	 * @param m
	 * @param isEast
	 * @return
	 */
	public static double goHorizontal(List<Coordinates> next_list, Coordinates m, boolean isEast){
		double sum_correct = 0.0, sum_neigh_N = 0.0, sum_neigh_S = 0.0;
		Coordinates next_co = null;
		
		if(isEast){
			next_co = new Coordinates(m.x, m.y+1);
		} else {
			next_co = new Coordinates(m.x, m.y-1);
		}
		if(!bound.contains(next_co)){
			int index = 5*(next_co.x-1)+next_co.y;
			sum_correct = gamma * (p * utility.get(index));
			if(!next_list.contains(next_co) && !no_action.contains(next_co)){
				next_list.add(next_co);
			}
		} else {
			sum_correct = 0;
		}
		
		next_co = new Coordinates(m.x+1, m.y);
		if(!bound.contains(next_co)){
			int index = 5*(next_co.x-1)+next_co.y;
			sum_neigh_N = gamma * (q * utility.get(index));
			if(!next_list.contains(next_co) && !no_action.contains(next_co)){
				next_list.add(next_co);
			}
		} else {
			sum_neigh_N = 0;
		}
		next_co = new Coordinates(m.x-1, m.y);
		if(!bound.contains(next_co)){
			int index = 5*(next_co.x-1)+next_co.y;
			sum_neigh_S = gamma * (q * utility.get(index));
			if(!next_list.contains(next_co) && !no_action.contains(next_co)){
				next_list.add(next_co);
			}
		} else {
			sum_neigh_S = 0;
		}
		
		return sum_correct+sum_neigh_N+sum_neigh_S;
	}
	
	static{
		for(int i=0; i<bound_x.length; ++i){
			bound.add(new Coordinates(bound_x[i][0], bound_x[i][1]));
			no_action.add(new Coordinates(bound_x[i][0], bound_x[i][1]));
		}
		for(int j = 0; j<bound_y.length; ++j){
			bound.add(new Coordinates(bound_y[j][0], bound_y[j][1]));
			no_action.add(new Coordinates(bound_y[j][0], bound_y[j][1]));
		}
		
		for(int j = 0; j < goal.length;++j ){
			no_action.add(new Coordinates(goal[j][0], goal[j][1]));
		}
		for(int j = 0; j<obstacles.length; ++j){
			no_action.add(new Coordinates(obstacles[j][0], obstacles[j][1]));
		}
		
		
		for(int i= 1; i < 26; ++i){
			reward.put(i, 0.0);
			utility.put(i, 0.0);
			direction.put(i, "0.0");
		}
		
	}
	
	public static void getUtility(){
		for(int i=1; i < 26; i += 5){
			System.out.println("\t"+df2.format(utility.get(i))+"\t"+df2.format(utility.get(i+1))+"\t"+df2.format(utility.get(i+2))+"\t"+df2.format(utility.get(i+3))+"\t"+df2.format(utility.get(i+4)));
		}
	}
	
	public static void getDir(){
		for(int i=1; i < 26; i += 5){
			System.out.println("\t"+direction.get(i)+"\t"+direction.get(i+1)+"\t"+direction.get(i+2)+"\t"+direction.get(i+3)+"\t"+direction.get(i+4));
		}
	}
	
	public static Integer [] changeToObject(int [] arr){
		Integer []result = new Integer [arr.length];
		for(int i=0; i< arr.length; ++i){
			result[i] = new Integer(arr[i]);
		}
		return null;
	}
	
	public static void main(String[] args) {
		
		System.out.println("Agent starting position ["+agent[0]+", "+agent[1]+"]");
		
		System.out.println("Goal Position ["+goal[0][0]+", "+goal[0][1]+"]");
		String obstacle_str = "";
		for(int []obstacle : obstacles){
			obstacle_str += Arrays.toString(obstacle);
		}
		
		System.out.println("Obstacles "+obstacle_str);
		for(int i=0; i < obstacles.length; ++i){
			int index = 5 * (obstacles[i][0] - 1) + obstacles[i][1];
			reward.put(index, -1.0);
			utility.put(index, -1.0);
			direction.put(index, "-1.0");
		}
		
		for(int i=0; i < goal.length; ++i){
			int index = 5 * (goal[i][0] - 1) + goal[i][1];
			reward.put(index, 1.0);
			utility.put(index, 1.0);
			direction.put(index, "1.0");
		}
		updated.add(new Coordinates(agent[0], agent[1]));
		
		for(int i=0; i<iteration; ++i){
			List<Coordinates> next_list = new ArrayList<Coordinates>();
			for(Coordinates m : updated){
				Map<Character, Coordinates> next_arrow_dict = new HashMap<Character, Coordinates>();
				int pos = 5*(m.x -1) + m.y;
				present.add(m);
				
				Map<Character, Double> find_max = new HashMap<Character, Double>();
				
				//Go North
				Coordinates next_co = new Coordinates(m.x-1, m.y);
				next_arrow_dict.put('N', next_co);
				
				//Sum of going north
				double sum_go_north = goVertical(next_list,m, true);
				find_max.put('N', sum_go_north);
				
				
				//Go south
				next_co = new Coordinates(m.x+1, m.y);
				next_arrow_dict.put('S', next_co);
				
				double sum_go_south = goVertical(next_list, m, false);
				find_max.put('S', sum_go_south);
				
				
				//Go East
				next_co = new Coordinates(m.x, m.y+1);
				next_arrow_dict.put('E', next_co);
				
				
				double sum_go_east = goHorizontal(next_list, m, true);
				find_max.put('E', sum_go_east);
				
				//Go West 
				next_co = new Coordinates(m.x, m.y-1);
				next_arrow_dict.put('W', next_co);
				
				
				double sum_go_west = goHorizontal(next_list, m, false);
				find_max.put('W', sum_go_west);
				
				char max_direction = 'N';
				double max = Double.NEGATIVE_INFINITY;
				Iterator<Character> mapItr = find_max.keySet().iterator();
				for(;mapItr.hasNext();){
					char ch = mapItr.next();
					if(max < find_max.get(ch)){
						max_direction = ch;
						max = find_max.get(ch);
					}
				}
				
				Map<Character, Double> cango = new HashMap<Character, Double>();
				Coordinates coordinate = next_arrow_dict.get('N');
				if(!bound.contains(coordinate)){
					cango.put('N', sum_go_north);
				}
				coordinate = next_arrow_dict.get('S');
				if(!bound.contains(coordinate)){
					cango.put('S', sum_go_south);
				}
				coordinate = next_arrow_dict.get('E');
				if(!bound.contains(coordinate)){
					cango.put('E', sum_go_east);
				}
				coordinate = next_arrow_dict.get('W');
				if(!bound.contains(coordinate)){
					cango.put('W', sum_go_west);
				}
				
				max = Double.NEGATIVE_INFINITY;
				mapItr = cango.keySet().iterator();
				for(;mapItr.hasNext();){
					char ch = mapItr.next();
					if(max < cango.get(ch)){
						max_direction = ch;
						max = cango.get(ch);
					}
				}
				
				double max_val  = find_max.get(max_direction);
				max_val = reward.get(pos) + max_val;
				
				utility.put(pos, max_val);
				String arrow;
				if(max_direction == 'N'){
					//arrow = '\u2191';
					arrow = "Up";
				} else if(max_direction == 'S'){
					//arrow = '\u2193';
					arrow = "Down";
				} else if(max_direction == 'E'){
					//arrow = '\u2192';
					arrow = "Right";
				} else {
					//arrow = '\u2190';
					arrow = "Left";
				}
				direction.put(pos, arrow+"");
				
				
			}
			
			for(Coordinates coordinates : next_list){
				if(!updated.contains(coordinates)){
					updated.add(coordinates);
				}
			}
			Collections.reverse(updated);
			
		}
		System.out.println("Optimal Utility Found");
		//Coordinates m = new Coordinates(agent[0], agent[0]);
		getUtility();
		System.out.println("Optimal Policy Found");
		getDir();
	}
	
	private static class Coordinates{
		int x;
		int y;
		public Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			return this.x == ((Coordinates)obj).x &&
					this.y == ((Coordinates)obj).y;
		}
		
		@Override
		public String toString() {
			return "{"+x+","+y+"}";
		}
	}
}
