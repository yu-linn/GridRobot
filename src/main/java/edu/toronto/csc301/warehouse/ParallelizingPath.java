package edu.toronto.csc301.warehouse;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.GridRobot;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public class ParallelizingPath implements IParallelizingPath{
	
	private IWarehouse warehouse;
	private Map<IGridRobot, GridCell> robot2dest;
	private PathPlanner p = new PathPlanner();
	private Map<IGridRobot, ArrayList<GridCell>> robotPaths = new HashMap<IGridRobot, ArrayList<GridCell>>();
	/** Initialize ParallelizingPath**/
	public ParallelizingPath(IWarehouse warehouse, Map<IGridRobot, GridCell> robot2dest) throws NullPointerException{
		if (warehouse == null || robot2dest == null) {
			throw new NullPointerException();
		}
		this.warehouse = warehouse;
		this.robot2dest = robot2dest;
		//get all robots current paths
		for (IGridRobot r:this.robot2dest.keySet()) {
			ArrayList<GridCell> shortestPath = p.BreadthSearch(warehouse.getFloorPlan(),
					this.getRobotsLocations(), r.getLocation(), robot2dest.get(r));
			robotPaths.put(r, shortestPath);
		}
	}
	/** Get all the robots location within the warehouse.
	 * @return ArrayList of occupied GridCells with robots.**/
	public ArrayList<GridCell> getRobotsLocations() {
		Iterator<IGridRobot> robotI = warehouse.getRobots();
		//check grids with robots
		ArrayList<GridCell> robotsCell = new ArrayList<GridCell>();
		while (robotI.hasNext()) {
			IGridRobot n = robotI.next();
			robotsCell.add(n.getLocation());		
		}
		return robotsCell;
	}
	/** Return true if robot's destination or current location is within other robot's paths
	 * @param robot, the robot to be checked
	 * @param cell, check if that robot's destination or current location is within other robot's
	 * paths.**/
	public boolean checkRobotCellInOtherPaths(IGridRobot robot,GridCell cell) {
		for (IGridRobot r:this.robot2dest.keySet()) {
			ArrayList<GridCell> shortestPath = p.BreadthSearch(warehouse.getFloorPlan(),
					this.getRobotsLocations(), r.getLocation(), robot2dest.get(r));
			this.robotPaths.put(r, shortestPath);
		}
		for (IGridRobot r:this.robotPaths.keySet()) {
			if (!robot.equals(r)) {
				//robot dest not conflicting with other paths
				if (!this.robotPaths.get(r).contains(cell)) {
					return true;
				}
			}
		}
		//robot dest conflicting with other paths
		return false;
	}
	/** Initialize the threads and execute the callable function
	 * @param warehouse, current state of warehouse
	 * @param robot2dest, maps robots to desired destination
	 * **/
	public void currentMotion(IWarehouse warehouse, Map<IGridRobot, GridCell> robot2dest) {
		//create new pool
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(robot2dest.size());
        
        List<Future<Entry<IGridRobot, Direction>>> list = new ArrayList<Future<Entry<IGridRobot, Direction>>>();

        //create thread for every robot
		for (IGridRobot r: robot2dest.keySet()) {

			Map<IGridRobot, GridCell> entry = new HashMap<IGridRobot, GridCell>();
			entry.put(r, robot2dest.get(r));
				CallablePath call  = new CallablePath(warehouse,entry);
				Future<Entry<IGridRobot, Direction>> future = executor.submit(call);
				list.add(future);
			
		}
		//check if robots going the correct direction
        for(Future<Entry<IGridRobot, Direction>>  future : list)
        {
              try
              {
                  System.out.println("Future result is - " + Thread.currentThread().getName() + " - " + future.get().getValue() + "; And Task done is " + future.isDone());
                  
              } 
              catch (InterruptedException | ExecutionException e) 
              {
                  e.printStackTrace();
              }
          }
          //shut down the executor service now
          executor.shutdown();
	}
	/** Helper class to create multiple threads. **/
	public class CallablePath implements Callable<Entry<IGridRobot, Direction>> {
		
		private IWarehouse warehouse;
		private Map<IGridRobot,GridCell> oneRobot;
		
		public CallablePath(IWarehouse w, Map<IGridRobot, GridCell> gridrobot) {
			this.warehouse = w;
			this.oneRobot = gridrobot;
		}
		/** Callable function that finds the next step until the robot reaches destination. 
		 * @return last step of the robot (should be final destination).**/
		@Override
	    public Entry<IGridRobot, Direction> call() throws Exception {
			Entry<IGridRobot, Direction> nextstep = p.nextStep(this.warehouse, this.oneRobot);
			for (IGridRobot r: this.oneRobot.keySet()) {
				//check if destination is in a path of another robot, then wait
				while (!ParallelizingPath.this.checkRobotCellInOtherPaths(r, this.oneRobot.get(r))) {
					
				}
				//loop until robot gets to destination
				while (!r.getLocation().equals(this.oneRobot.get(r))) {
					nextstep = p.nextStep(this.warehouse, this.oneRobot);

					nextstep.getKey().step(nextstep.getValue());	
				}
			}
	        //return the thread name executing this callable task
			System.out.println("Robot at thread " + Thread.currentThread().getName() + " going " + nextstep.getValue());
			//when return next step means it's done
	        return nextstep;
	    }
	}
	
}
