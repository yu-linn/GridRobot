package pathplanner;


import static edu.toronto.csc301.util.TestUtil.createWarehouse;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import org.junit.Test;


import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.util.SimpleGridImpl;
import edu.toronto.csc301.util.Counter;
import edu.toronto.csc301.warehouse.IParallelizingPath;

import edu.toronto.csc301.warehouse.IWarehouse;
import edu.toronto.csc301.warehouse.ParallelizingPath;


public class PathAdvancedCaseTest {
	
	// ============================== Helpers =================================
	
	
	/**
	 * A floor plan that looks like this:
	 * 
	 * OOO OOO
	 * OOOOOOO
	 * OOO OOO
	 * 
	 * (each O represents an empty grid cell)
	 */
	public static <T> SimpleGridImpl<T> createHShapedFloorPlan(){
		Map<GridCell, T> cell2item = new HashMap<GridCell,T>();
		
		// 3x3 square space (sw corner at 0,0)
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				cell2item.put(GridCell.at(x, y), null);
			}
		}
		
		// A narrow hallway (1 cell wide and one cell long)
		cell2item.put(GridCell.at(3, 1), null);
		
		// Another 3x3 square space (sw corner at 4,0)
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				cell2item.put(GridCell.at(4 + x, y), null);
			}
		}
		
		return new SimpleGridImpl<T>(cell2item);
	}
	/**Test when robot destination is within a path of another robot.
	 * Timeout should wait till robot 2 passes robot 1's destination and continue**/
	@Test(timeout=500*5 + 100)
	public void robotDestinationInPathOfAnotherRobot() throws Exception {
		IWarehouse warehouse3 = createWarehouse(
				createHShapedFloorPlan());
		
		//robot with blocking destination
		GridCell initialLocation = GridCell.at(2,2);
		//other robot getting to destination
		GridCell initialLocation2 = GridCell.at(4, 1);

		
		// The destination will be a few (not too many) cells away
		
		GridCell destination = GridCell.at(2, 1);
		
		GridCell destination2 = GridCell.at(0,0);
		
		// Add the robot
		IGridRobot robot = warehouse3.addRobot(initialLocation);
		IGridRobot robot2 = warehouse3.addRobot(initialLocation2);

		
		// Set the robot's final destination  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, destination);
		robot2dest.put(robot2, destination2);
		IParallelizingPath path = new ParallelizingPath(warehouse3,robot2dest);

		// The planner should get the robot to the destination (and do it efficiently)
		//first robot should be one step away from destination
		int stepLimit1 = 1;
		//second robot should be 5 steps away from destination
		int stepLimit2 = 5;
		
		Counter<IWarehouse> listener = new Counter<IWarehouse>();
		warehouse3.subscribe(listener);
		
		path.currentMotion(warehouse3, robot2dest);
		//count start stepping and end stepping
		assertEquals((stepLimit1 + stepLimit2)*2,listener.getCount());

		assertEquals(robot.getLocation(),destination);
		assertEquals(robot2.getLocation(),destination2);
	}
	/** Test if robot is blocking another robot's path to destination, move that robot
	 * timeout around the time the robot with the most steps finishes.**/
	@Test(timeout=500*8)
	public void robotBlockingAnotherRobot() throws Exception {
		IWarehouse warehouse = createWarehouse(
				createHShapedFloorPlan());

		//robots blocking each other in narrow hallway
		GridCell initialLocation = GridCell.at(2,1);
		GridCell initialLocation2 = GridCell.at(4, 1);

		
		// The destination will be a few (not too many) cells away
		
		GridCell destination = GridCell.at(6, 0);
		
		GridCell destination2 = GridCell.at(0,0);
		
		// Add the robots
		IGridRobot robot = warehouse.addRobot(initialLocation);
		IGridRobot robot2 = warehouse.addRobot(initialLocation2);

		
		// Set the robot's final destination  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, destination);
		robot2dest.put(robot2, destination2);
		IParallelizingPath path = new ParallelizingPath(warehouse,robot2dest);

		// The planner should get the robot to the destination (and do it efficiently)
		//robot 1 moves out of the way and allows robot 2 to pass through thus 8 steps to dest
		int stepLimit1 = 7;
		//robot 2 takes 6 steps to destination at shortest path
		int stepLimit2 = 5;
		Counter<IWarehouse> listener = new Counter<IWarehouse>();
		warehouse.subscribe(listener);
		
		path.currentMotion(warehouse, robot2dest);
		//count start stepping and end stepping
		assertEquals((stepLimit1 + stepLimit2)*2,listener.getCount());
		assertEquals(robot.getLocation(),destination);
		assertEquals(robot2.getLocation(),destination2);


	}
	/** Test if robot's shortest path intersect, one robot should wait for other one to pass
	 * timeout around the time the robot with the most steps finishes.**/
	@Test(timeout=500*4)
	public void robotPathInstersectsAnotherRobotPath() throws Exception {
		IWarehouse warehouse = createWarehouse(
				createHShapedFloorPlan());

		GridCell initialLocation = GridCell.at(1,0);
		GridCell initialLocation2 = GridCell.at(0, 1);

		
		// The destination will be a few (not too many) cells away
		
		GridCell destination = GridCell.at(1, 2);
		
		GridCell destination2 = GridCell.at(3,1);
		
		// Add the robots
		IGridRobot robot = warehouse.addRobot(initialLocation);
		IGridRobot robot2 = warehouse.addRobot(initialLocation2);

		
		// Set the robot's final destination  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, destination);
		robot2dest.put(robot2, destination2);
		IParallelizingPath path = new ParallelizingPath(warehouse,robot2dest);

		// The planner should get the robot to the destination (and do it efficiently)
		int stepLimit1 = 3;
		//robot 2 takes 2 steps to destination at shortest path
		int stepLimit2 = 2;
		Counter<IWarehouse> listener = new Counter<IWarehouse>();
		warehouse.subscribe(listener);
		
		path.currentMotion(warehouse, robot2dest);
		//count start stepping and end stepping
		assertEquals((stepLimit1 + stepLimit2)*2,listener.getCount());
		assertEquals(robot.getLocation(),destination);
		assertEquals(robot2.getLocation(),destination2);


	}

}
