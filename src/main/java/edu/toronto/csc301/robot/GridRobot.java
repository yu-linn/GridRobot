package edu.toronto.csc301.robot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.warehouse.IWarehouse;
import edu.toronto.csc301.warehouse.Warehouse;

public class GridRobot implements IGridRobot {

	
	// =========================== Static Helper(s) ===========================
	
	
	public static GridCell oneCellOver(GridCell location, Direction direction){
		switch (direction) {
		case NORTH:
			return GridCell.at(location.x, location.y + 1);
		case EAST:
			return GridCell.at(location.x + 1, location.y);
		case SOUTH:
			return GridCell.at(location.x, location.y - 1);
		case WEST:
			return GridCell.at(location.x - 1, location.y);
		default:
			return null;
		}
	}
	
	
	// ========================================================================
	
	
	
	private GridCell location;
	private long delayInMilliseconds;
	private Set<StepListener> stepListeners;
	
	private ArrayList<Warehouse> warehouses = new ArrayList<Warehouse>();
	private int is_changed;
	private Direction d;
	public GridRobot(GridCell initialLocation, long delayInMilliseconds) {
		Objects.requireNonNull(initialLocation);
		this.location = initialLocation;
		this.delayInMilliseconds = delayInMilliseconds;
		this.stepListeners = new HashSet<StepListener>();
	}
	
	public GridRobot(GridCell initialLocation) {
		this(initialLocation, 500);   // Default delay is half a second
	}
	
	
	
	
	@Override
	public GridCell getLocation() {
		return location;
	}


	@Override
	public void step(Direction direction) {
		for(StepListener listener : stepListeners){
			
			listener.onStepStart(this, direction);
		}
		this.d=direction;
		for (Warehouse w: this.warehouses) {
			this.is_changed = 1;
			System.out.println("start stepping");
			w.onStepStart(this, direction);
		}
		// Simulate a the time it takes for a robot to move by sleeping 
		try {
			Thread.sleep(delayInMilliseconds);
		} catch (InterruptedException e) { }

		location = GridRobot.oneCellOver(location, direction); 
		for (Warehouse w: this.warehouses) {
			this.is_changed = 2;
			w.onStepEnd(this, direction);
			System.out.println("end stepping");

		}		
		for(StepListener listener : stepListeners){
			listener.onStepEnd(this, direction);
		}
		this.is_changed = 0;
		System.out.println("not stepping");

	}
	
	public void addWarehouse(Warehouse warehouse) {
		this.warehouses.add(warehouse);
	}

	@Override
	public void startListening(StepListener listener) {
		stepListeners.add(listener);
	}

	@Override
	public void stopListening(StepListener listener) {
		stepListeners.remove(listener);
	}

	/**
	 * @return the is_changed
	 */
	public int getIs_changed() {
		return is_changed;
	}

	/**
	 * @return the d
	 */
	public Direction getD() {
		return d;
	}
	




}
