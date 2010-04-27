/**
 * 
 */
package com.nauj27.android.colorpicker.ral;

import android.graphics.Color;


/**
 * @author nauj27
 *
 */
public class RalColor {
	/** The RAL index. */
	private int index = 0;
	/** The color. */
	private int color = 0;
	/** The difference with the RAL more closed. */
	private double difference = 512;
	
	/**
	 * Creates a new RalColor without index value
	 */
	public RalColor() {}
	
	/**
	 * Search and set the RAL index of a color int and the difference.
	 * @param color the color int to search
	 */
	public RalColor(int color) {
		this.index = 0;
		this.color = color;
		
		int i = 0;
		double differencetmp = 0;
		double difference = 512;
		final int size = RalSystem.code.length;
		
		while (i < size) {
			// Euclidian distance in 3D color space 
			differencetmp = Math.sqrt(
				Math.pow(RalSystem.red[i] - Color.red(color), 2) +
				Math.pow(RalSystem.green[i] - Color.green(color), 2) +
				Math.pow(RalSystem.blue[i] - Color.blue(color), 2)
			);
			
			if (differencetmp < difference) {
				difference = differencetmp;
				this.index = i;
			}
			i++;
		}
		
		setIndex(this.index);
		setDifference(difference);
	}
	
	public String getName() {
		if (this.index == 0) {
			return "Unknown";
		} else {
			return RalSystem.names[index];
		}
	}
	
	public int getCode() {
		if (index == 0) {
			return 0;
		} else {
			return RalSystem.code[index];
		}
	}
	
	// Getters and setters
	
	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param difference the difference to set
	 */
	public void setDifference(double difference) {
		this.difference = difference;
	}

	/**
	 * @return the difference
	 */
	public double getDifference() {
		return difference;
	}
	
}
