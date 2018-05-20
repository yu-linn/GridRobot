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
import edu.toronto.csc301.warehouse.IPathPlanner;
import edu.toronto.csc301.warehouse.IWarehouse;
import edu.toronto.csc301.warehouse.ParallelizingPath;

import org.junit.Test;

public class PathEdgeTest {
	private IPathPlanner pathPlanner;
	private IWarehouse warehouse;
	
	@Before
	public void setup() throws Exception{
		pathPlanner = createPathPlanner();
		warehouse = createWarehouse(
				SimpleGridImpl.emptyRactanlge(20, 20, GridCell.at(0, 0)));
	}
	
	@After
	public void tearDown(){
		pathPlanner = null;
		warehouse = null;
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotHaveRobotsWithSameDestination() throws Exception{
		GridCell initialLocation = GridCell.at(randomInt(0, 5), randomInt(0, 5));
		GridCell initialLocation2 = GridCell.at(randomInt(10, 15), randomInt(10, 15));

		// Add the 2 robots into warehouse;
		IGridRobot robot = warehouse.addRobot(initialLocation);
		IGridRobot robot2 = warehouse.addRobot(initialLocation2);

		// Set the robot's final destination to be one cell over  
		Map<IGridRobot, GridCell> robot2dest = new HashMap<IGridRobot,GridCell>();
		robot2dest.put(robot, GridCell.at(10, 10));
		robot2dest.put(robot2, GridCell.at(10, 10));
		pathPlanner.nextStep(warehouse, robot2dest);
	}
	@Test(expected=NullPointerException.class)
	public void cannotInstantiateParallelizingPathWithNullArguments() throws Exception{

		ParallelizingPath p = new ParallelizingPath(null,null);
	}

}
