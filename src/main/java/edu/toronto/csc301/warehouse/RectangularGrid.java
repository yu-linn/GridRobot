package edu.toronto.csc301.warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.warehouse.Rack;

public class RectangularGrid<T> implements IGrid<T>{
	private int w,h;
	private GridCell c;
	private HashMap<GridCell,T> racks;
	private ArrayList<GridCell> g;
	public int getW() {
		return w;
	}
	public int getH() {
		return h;
	}
	public GridCell getC() {
		return c;
	}
	public HashMap<GridCell,T> getRacks() {
		return racks;
	}
	public ArrayList<GridCell> getG() {
		return g;
	}

	public RectangularGrid(int w, int h, GridCell cell) throws IllegalArgumentException, NullPointerException{
		if (w<1 || h<1) {
			throw new IllegalArgumentException();
		}
		if (cell == null) {
			throw new NullPointerException();
		}
		
		this.w = w;
		this.h = h;
		this.c = cell;
		this.racks = new HashMap<GridCell,T>();
		this.g = new ArrayList<GridCell>();
	}
	@Override
	public T getItem(GridCell cell) throws IllegalArgumentException{
		// TODO Auto-generated method stub
		if (cell.x > (this.w + this.c.x) || cell.y > (this.h + this.c.y) || cell.y < this.c.y|| cell.x < this.c.x) {
			throw new IllegalArgumentException();
		}
		if (this.racks.containsKey(cell)) {
			return this.racks.get(cell);
		}
		else {
			return null;
		}
		
	}

	@Override
	public Iterator<GridCell> getGridCells() {
		for (int x=this.c.x; x<this.w + this.c.x; x++) {
			for (int y=this.c.y; y<this.h + this.c.y; y++) {
				g.add(GridCell.at(x, y));
			}
		}

		// TODO Auto-generated method stub
		return g.iterator();
	}

	@Override
	public boolean hasCell(GridCell cell) {
		//ArrayList<GridCell> g = new ArrayList<GridCell>();
		for (int x=this.c.x; x<this.w + this.c.x; x++) {
			for (int y=this.c.y; y<this.h + this.c.y; y++) {
				if (x == cell.x && y == cell.y) {
					return true;
				}
			}
		}
		// TODO Auto-generated method stub
		return false;
	}
	public void addRack(GridCell cell,T item) {
		this.racks.put(cell, item);
	}
}
