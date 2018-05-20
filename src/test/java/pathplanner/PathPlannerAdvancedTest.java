package pathplanner;

import static edu.toronto.csc301.util.TestUtil.createPathPlanner;
import static edu.toronto.csc301.util.TestUtil.createWarehouse;
import static edu.toronto.csc301.util.TestUtil.oneCellOver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;
import edu.toronto.csc301.util.SimpleGridImpl;
import edu.toronto.csc301.warehouse.IPathPlanner;
import edu.toronto.csc301.warehouse.IWarehouse;

public class PathPlannerAdvancedTest {
	
	
	@Rule
    public Timeout globalTimeout = Timeout.seconds(5);
	
	
	
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
	
	
	// ========================================================================
	
	
	

	@Test
	public void dontStepOffTheGrid() throws Exception {
		IPathPlanner pathPlanner = createPathPlanner();
		IWarehouse warehouse = createWarehouse(createHShapedFloorPlan());
		
		// Add the robot
		IGridRobot robot = warehouse.addRobot(GridCell.at(0, 0));
		GridCell destination = GridCell.at(4,0);
		
		// Set the robot's final destination  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, destination);
		
		// The planner should get the robot to the destination (and do it efficiently)
		int stepLimit = 6;
		int stepCount = 0;
		while(stepCount < stepLimit){
			Entry<IGridRobot, Direction> nextStep = pathPlanner.nextStep(warehouse, robot2dest);
			IGridRobot r = nextStep.getKey();
			Direction  d = nextStep.getValue();
			
			// Before we take the step, let's make sure we're staying on the grid
			GridCell endCell = oneCellOver(r.getLocation(), d);
			assertTrue(warehouse.getFloorPlan().hasCell(endCell));
			
			// Take the step ...
			r.step(nextStep.getValue());
			stepCount++;
		}
		
		assertEquals(destination, robot.getLocation());
	}
	
	
	
	
	@Test
	public void dontStepWhereThereIsAlreadyARobot() throws Exception {
		IPathPlanner pathPlanner = createPathPlanner();
		IWarehouse warehouse = createWarehouse(
				SimpleGridImpl.emptyRactanlge(3, 2, GridCell.at(0, 0)));
		
		// Add our robot at 0,0
		IGridRobot robot = warehouse.addRobot(GridCell.at(0, 0));
		
		// Set its destination as 2,0
		GridCell destination = GridCell.at(2,0);
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, destination);
		
		// Add an additional robot at 1,0
		warehouse.addRobot(GridCell.at(1, 0));
		
		// The planner should get the robot to the destination without crashing
		// into the other robot (i.e. by going around it)
		int stepLimit = 4;
		int stepCount = 0;
		while(stepCount < stepLimit){
			Entry<IGridRobot, Direction> nextStep = pathPlanner.nextStep(warehouse, robot2dest);
			IGridRobot r = nextStep.getKey();
			Direction  d = nextStep.getValue();
			
			// Before we take the step
			GridCell endCell = oneCellOver(r.getLocation(), d);
			// Let's make sure we're staying on the grid
			assertTrue(warehouse.getFloorPlan().hasCell(endCell));
			// And that we're not crashing into other robots
			if(endCell.equals(GridCell.at(1, 0))){
				fail("Cannot step " + d + " to (1,0), there is a robot there.");
			}
			
			// Take the step ...
			r.step(nextStep.getValue());
			stepCount++;
		}
	
		// Verify that the robot has reached its destination
		assertEquals(destination, robot.getLocation());
	}
	
	
	
}
