/**
 * 
 */
package com.nauj27.android.colorpicker.ral;

import com.nauj27.android.colorpicker.R;

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
	
	private static final int DEFAULT_COLOR_INDEX = 0;
	private static final int MAX_COLOR_DIFFERENCE = 512;
	
	/**
	 * Creates a new RalColor without index value
	 */
	public RalColor() {}
	
	/**
	 * Search and set the RAL index of a color integer and the difference.
	 * @param color the color integer to search into the RAL Color System
	 */
	public RalColor(int color) {
		this.setColor(color);
	}
	
	public String getName() {
		if (this.index == 0) {
			return "Unknown";
		} else {
			return RalSystem.names[this.index];
			// TODO: String[] colorNames = getStringArray(R.array.color_names);
		}
	}
	
	public int getCode() {
		if (this.index == 0) {
			return 0;
		} else {
			return RalSystem.code[this.index];
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
		return this.index;
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
		return this.difference;
	}

	/**
	 * @return the color
	 */
	public int getColor() {
		return this.color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(int color) {
		this.index = DEFAULT_COLOR_INDEX;
		this.color = color;
		this.difference = MAX_COLOR_DIFFERENCE;
		
		double differencetmp = 0;
		
		for (int i = 0; i < RalSystem.code.length; i++) {
			// Euclidian distance in 3D color space 
			differencetmp = Math.sqrt(
				Math.pow(RalSystem.red[i] - Color.red(color), 2) +
				Math.pow(RalSystem.green[i] - Color.green(color), 2) +
				Math.pow(RalSystem.blue[i] - Color.blue(color), 2));
			
			if (differencetmp < this.difference) {
				this.difference = differencetmp;
				this.index = i;
			}
		}
	}
	
}
