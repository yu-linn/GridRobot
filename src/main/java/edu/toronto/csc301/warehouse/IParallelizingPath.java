package edu.toronto.csc301.warehouse;

import java.util.Map;
import java.util.Map.Entry;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public interface IParallelizingPath {
	/** Function used to generate threads for concurrent execution of robots moving
	 * @param warehouse, full state of warehouse
	 * @param robot2dest Specifies our goals - Which robots should get to which locations in the warehouse. 
	 **/
	void currentMotion(IWarehouse warehouse, Map<IGridRobot, GridCell> robot2dest);
}
