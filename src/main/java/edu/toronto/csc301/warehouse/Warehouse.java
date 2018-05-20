package edu.toronto.csc301.warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.robot.GridRobot;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public class Warehouse implements IWarehouse, IGridRobot.StepListener {
	private IGrid<Rack> grid;
	private HashMap<IGridRobot, GridCell> robots;
	private HashMap<IGridRobot, Direction> robotsMotion = new HashMap<IGridRobot, Direction>();
	private ArrayList<Consumer<IWarehouse>> observers = new ArrayList<Consumer<IWarehouse>>();

	/**
	 * TODO: Complete the implementation of this class.
	 * (you can probably use your implementation from A4) 
	 */

	
	
	public Warehouse(IGrid<Rack> floorPlan) throws NullPointerException{
		// TODO Auto-generated constructor stub
		if (floorPlan == null) {
			throw new NullPointerException();
		}
		this.grid = floorPlan;
		this.robots = new HashMap<IGridRobot,GridCell>();

	}
	
	

	@Override
	public void onStepStart(IGridRobot robot, Direction direction) {
		// TODO Auto-generated method stub
		for (Consumer<IWarehouse> observer: observers) {
			observer.accept(this);
		}
		robotsMotion.put(robot, direction);
	}

	@Override
	public void onStepEnd(IGridRobot robot, Direction direction) {
		// TODO Auto-generated method stub
//		robotsMotion.put(robot, direction);
		for (Consumer<IWarehouse> observer: observers) {
			observer.accept(this);
		}
		robotsMotion.remove(robot);
	}

	@Override
	public IGrid<Rack> getFloorPlan() {
		// TODO Auto-generated method stub
		return this.grid;
	}

	@Override
	public IGridRobot addRobot(GridCell initialLocation) throws IllegalArgumentException{
		// TODO Auto-generated method stub
		if (this.robots.containsValue(initialLocation) || !this.grid.hasCell(initialLocation)) {
			throw new IllegalArgumentException();
		}
		GridRobot new_robot = new GridRobot(initialLocation);
		new_robot.addWarehouse(this);

		this.robots.put(new_robot,initialLocation);
		// TODO Auto-generated method stub
		for (int i=0; i<this.observers.size();i++) {
			this.observers.get(i).accept(this);
		}
		return new_robot;
	}

	@Override
	public Iterator<IGridRobot> getRobots() {
		// TODO Auto-generated method stub
		return this.robots.keySet().iterator();
	}

	@Override
	public Map<IGridRobot, Direction> getRobotsInMotion() {
		// TODO Auto-generated method stub
		HashMap<IGridRobot,Direction> robotMotion = new HashMap<IGridRobot,Direction>();
		for (IGridRobot r: this.robots.keySet()) {
			GridRobot rob = (GridRobot) r;
			if (rob.getIs_changed() == 0) {

			}
			else if (rob.getIs_changed() == 1) {
				robotMotion.put(rob, rob.getD());
			}
			else if (rob.getIs_changed() == 2) {
				robotMotion.remove(rob, rob.getD());

			}
		}		
		return robotMotion;
	}

	@Override
	public void subscribe(Consumer<IWarehouse> observer) {
		// TODO Auto-generated method stub
		this.observers.add(observer);
		
	}

	@Override
	public void unsubscribe(Consumer<IWarehouse> observer) {
		// TODO Auto-generated method stub
		this.observers.remove(observer);

	}

	
}
