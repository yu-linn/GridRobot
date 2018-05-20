package edu.toronto.csc301.warehouse;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public class PathPlanner implements IPathPlanner{
	/** return direction from one gridcell (initial) to another gridcell (next)
	 * @param initial, the initial origin GridCell
	 * @param next, the next GridCell you want to check for the direction from
	 * the initial GridCell.**/
	public Direction checkDirection(GridCell initial, GridCell next) {
		int adj[][] = {{-1,0},{0,-1},{1,0},{0,1}};
		if ((initial.x + adj[0][0]) == next.x && (initial.y + adj[0][1]) == next.y ) {
			return Direction.WEST;
		}
		else if ((initial.x + adj[1][0]) == next.x && (initial.y + adj[1][1]) == next.y ) {
			return Direction.SOUTH;
		}
		else if ((initial.x + adj[2][0]) == next.x && (initial.y + adj[2][1]) == next.y ) {
			return Direction.EAST;
		}
		else if ((initial.x + adj[3][0]) == next.x && (initial.y + adj[3][1]) == next.y ) {
			return Direction.NORTH;
		}
		else {
			return null;
		}
	}
	/** return next step in the warehouse  for the robot direction to get closer to destination.
	 * @param warehouse, full current state of the warehouse
	 * @param robot2dest, maps every robot to the destination they plan to go to.**/

	@Override
	public Entry<IGridRobot, Direction> nextStep(IWarehouse warehouse, Map<IGridRobot, GridCell> robot2dest) throws IllegalArgumentException{
		// TODO Auto-generated method stub

		IGrid<Rack> g = warehouse.getFloorPlan();
		Iterator<IGridRobot> robotI = warehouse.getRobots();
		//check grids with robots
		ArrayList<GridCell> robotsCell = new ArrayList<GridCell>();
		while (robotI.hasNext()) {
			IGridRobot n = robotI.next();
			robotsCell.add(n.getLocation());		
		}
		//check if robots destination is the same
		HashSet<GridCell> unique_dest = new HashSet<GridCell>();
		for (GridCell c: robot2dest.values()) {
			if (!unique_dest.contains(c)) {
				unique_dest.add(c);			}
			else {
				throw new IllegalArgumentException();

			}
		}
		for (IGridRobot r: robot2dest.keySet()) {

			GridCell start = GridCell.at(r.getLocation().x, r.getLocation().y);
			GridCell end = GridCell.at(robot2dest.get(r).x, robot2dest.get(r).y);
			//use BFS to find path
			ArrayList<GridCell> path = this.BreadthSearch(g, robotsCell, start, end);
			//check if robot not at destination or if robots not in motion
			if (path != null && !warehouse.getRobotsInMotion().containsKey(r)) {
				System.out.println("robot at:" + path.get(1));
				GridCell next = path.get(1);
				return new AbstractMap.SimpleEntry<IGridRobot,Direction>(r, checkDirection(start,next));
			}
			
			
		}
		
		return null;
	}
	/** Get shortest path using BFS
	 * @param grid
	 * @param robotCell, occupied GridCells
	 * @param start, origin of BFS
	 * @param end, destination of BFS
	 * @return ArrayList of GridCell of the shortest path from start to end**/

	public ArrayList<GridCell> BreadthSearch(IGrid<Rack> grid,ArrayList<GridCell> robotCell, GridCell start, GridCell end) {
		ArrayList<GridCell> shortestPath = new ArrayList<GridCell>();
		HashMap<GridCell, Boolean> visited = new HashMap<GridCell, Boolean>();
		HashMap<GridCell, GridCell> prev = new HashMap<GridCell, GridCell>();
		if (start.equals(end)) {
			return null;
		}
		Queue<GridCell> q = new LinkedList<GridCell>();
		GridCell current = start;
		q.add(current);
		visited.put(current, true);
		//do BFS
		while (!q.isEmpty()) {
			current = q.remove();
			if (current.equals(end)) {
				break;
			}
			else {
				ArrayList<GridCell> adjList = getAdjacentUnvisited(current, grid, robotCell);
					for (GridCell c: adjList) {
						if (!visited.containsKey(c)) {
							q.add(c);
							visited.put(c, true);
							prev.put(c,current);
						}
					}

			}
		}

		for(GridCell node = end; node != null; node = prev.get(node)) {
			shortestPath.add(node);
		}
		Collections.reverse(shortestPath);
		return shortestPath;

		

	}

	/**finds valid neighbours of cell
	 * @param cell
	 * @param grid
	 * @param robotCell
	 * @return ArrayList of adjacent available, unvisited GridCells of cell**/
	public ArrayList<GridCell> getAdjacentUnvisited(GridCell cell,IGrid<Rack> grid, ArrayList<GridCell> robotCell) {
		ArrayList<GridCell> adj= new ArrayList<GridCell>();
		int a[][] = {{-1,0},{0,-1},{1,0},{0,1}};
		for (int i=0;i<a.length;i++) {
			int x_value = cell.x + a[i][0];
			int y_value = cell.y + a[i][1];
			GridCell temp = GridCell.at(x_value, y_value);
			if (grid.hasCell(temp)&& !robotCell.contains(temp) && !(grid.getItem(temp) instanceof Rack)) {
				adj.add(temp);
			}
		}
		return adj;

	}


}
