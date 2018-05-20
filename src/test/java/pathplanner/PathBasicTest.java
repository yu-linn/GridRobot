package pathplanner;

import static edu.toronto.csc301.util.TestUtil.createPathPlanner;
import static edu.toronto.csc301.util.TestUtil.createWarehouse;
import static edu.toronto.csc301.util.TestUtil.oneCellOver;
import static edu.toronto.csc301.util.TestUtil.randomInt;
import static org.junit.Assert.*;
import static edu.toronto.csc301.util.TestUtil.randomDirection;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;
import edu.toronto.csc301.util.SimpleGridImpl;
import edu.toronto.csc301.util.Counter;
import edu.toronto.csc301.warehouse.IParallelizingPath;
import edu.toronto.csc301.warehouse.IPathPlanner;
import edu.toronto.csc301.warehouse.IWarehouse;
import edu.toronto.csc301.warehouse.ParallelizingPath;

public class PathBasicTest {


	/*Basic Case 1: Return the result when two robots are one step away from their destinations*/
	@Test
	public void returnTheCorrectResultOfTwoRobotsWhenDestinationIsOneCellAway() throws Exception {	
		// Start somewhere in the room (but not near the walls)
		IPathPlanner pathPlanner = createPathPlanner();
		IWarehouse warehouse = createWarehouse(
				SimpleGridImpl.emptyRactanlge(20, 20, GridCell.at(0, 0)));
		GridCell initialLocation = GridCell.at(randomInt(3, 5), randomInt(3, 5));
		GridCell initialLocation2 = GridCell.at(randomInt(10, 15), randomInt(10, 15));


		Direction stepDirection = randomDirection();
		Direction stepDirection2 = randomDirection();



		// Add the 2 robots into warehouse;
		IGridRobot robot = warehouse.addRobot(initialLocation);
		IGridRobot robot2 = warehouse.addRobot(initialLocation2);

		// Set the robot's final destination to be one cell over  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, oneCellOver(initialLocation, stepDirection));
		robot2dest.put(robot2, oneCellOver(initialLocation2, stepDirection2));

		

		
		// The planner should return the correct next step 
		Entry<IGridRobot, Direction> result = pathPlanner.nextStep(warehouse, robot2dest);
		assertEquals(robot, result.getKey());
		assertEquals(stepDirection, result.getValue());
		robot.step(stepDirection);
		
		Entry<IGridRobot, Direction> result2 = pathPlanner.nextStep(warehouse, robot2dest);
		assertEquals(robot2, result2.getKey());
		assertEquals(stepDirection2, result2.getValue());
		robot.step(stepDirection2);

	}
	@Test
	public void returnTheCorrectResultTwoRobotsWhenDestinationIsAFewCellsAway() throws Exception {
		IWarehouse warehouse2 = createWarehouse(
				SimpleGridImpl.emptyRactanlge(20, 20, GridCell.at(0, 0)));
		IPathPlanner pathPlanner2 = createPathPlanner();

		// The starting point is more or less in the middle of the room
		GridCell initialLocation = GridCell.at(5,5);
		GridCell initialLocation2 = GridCell.at(15, 15);

		
		// The destination will be a few (not too many) cells away

		int deltaX = 3;
		int deltaY = 3;
		GridCell destination = GridCell.at(
				initialLocation.x + deltaX, initialLocation.y - deltaY);
		
		GridCell destination2 = GridCell.at(
				initialLocation2.x + deltaX, initialLocation2.y - deltaY);
		
		// Add the robot
		IGridRobot robot = warehouse2.addRobot(initialLocation);
		IGridRobot robot2 = warehouse2.addRobot(initialLocation2);

		
		// Set the robot's final destination  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, destination);
		robot2dest.put(robot2, destination2);

		// The planner should get the robot to the destination (and do it efficiently)
		int stepLimit = deltaX + deltaY;
		int stepCount = 0;
		while(stepCount < stepLimit){
			Entry<IGridRobot, Direction> nextStep = pathPlanner2.nextStep(warehouse2, robot2dest);
			IGridRobot r = nextStep.getKey();
			
			// Take the step ...
			r.step(nextStep.getValue());
			stepCount++;
			
			// Now, make sure we're one step closer ...
			int distance = Math.abs(r.getLocation().x - destination.x) + 
					Math.abs(r.getLocation().y - destination.y);    
			assertEquals(stepLimit - stepCount, distance);
		}
		//check if robot 2 is closer
		int steplimit = deltaX + deltaY;
		int stepcount = 0;
		while(stepcount < steplimit){
			Entry<IGridRobot, Direction> nextStep = pathPlanner2.nextStep(warehouse2, robot2dest);
			IGridRobot r = nextStep.getKey();
			
			// Take the step ...
			r.step(nextStep.getValue());
			stepcount++;
			
			// Now, make sure we're one step closer ...
			int distance = Math.abs(r.getLocation().x - destination2.x) + 
					Math.abs(r.getLocation().y - destination2.y);    
			assertEquals(steplimit - stepcount, distance);
		}
	}
	/*check and see if the robots move concurrently, not one at a time.*/
	@Test(timeout=500*11)
	public void returnCorrectResultThreeRobotsMovingParallelToDestinationNonConflicting() throws Exception {
		IWarehouse warehouse3 = createWarehouse(
				SimpleGridImpl.emptyRactanlge(20, 20, GridCell.at(0, 0)));

		// The starting point is more or less in the middle of the room
		GridCell initialLocation = GridCell.at(5,5);
		GridCell initialLocation2 = GridCell.at(10, 10);
		GridCell initialLocation3 = GridCell.at(18, 18);

		

		
		// The destination will be a few (not too many) cells away
		
		GridCell destination = GridCell.at(7, 7);
		
		GridCell destination2 = GridCell.at(15,15);
		GridCell destination3 = GridCell.at(18,19);

		
		// Add the robot
		IGridRobot robot = warehouse3.addRobot(initialLocation);
		IGridRobot robot2 = warehouse3.addRobot(initialLocation2);
		IGridRobot robot3 = warehouse3.addRobot(initialLocation3);


		
		// Set the robot's final destination  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();

		robot2dest.put(robot, destination);
		robot2dest.put(robot2, destination2);
		robot2dest.put(robot3, destination3);

		IParallelizingPath path = new ParallelizingPath(warehouse3,robot2dest);

		// The planner should get the robot to the destination (and do it efficiently)
		//four steps to (7,7)
		int stepLimit1 = 4;
		//10 steps to (15,15)
		int stepLimit2 = 10;
		
		Counter<IWarehouse> listener = new Counter<IWarehouse>();
		warehouse3.subscribe(listener);
		//10 steps is longest path, should timeout at most 500*11 seconds including the first step of
		//the shorter path robot.

		path.currentMotion(warehouse3, robot2dest);
		//count start stepping and end stepping
		assertEquals((stepLimit1 + stepLimit2 + 1)*2,listener.getCount());
		assertEquals(robot.getLocation(),destination);
		assertEquals(robot2.getLocation(),destination2);
		assertEquals(robot3.getLocation(),destination3);


		//check if robot 2 is closer


	}

}
