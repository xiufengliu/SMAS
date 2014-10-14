package ca.uwaterloo.iss4e.chart;

import java.util.List;
/**
 * Copyright (c) 2014 Xiufeng Liu ( xiufeng.liu@uwaterloo.ca )
 * <p/>
 * This file is free software: you may copy, redistribute and/or modify it
 * under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 * <p/>
 * This file is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */
public class Highchart {
	String type;
    String name;
    int pointInterval;
    long pointStart;
    List<Double>data;
    
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPointInterval() {
		return pointInterval;
	}
	public void setPointInterval(int pointInterval) {
		this.pointInterval = pointInterval;
	}
	public long getPointStart() {
		return pointStart;
	}
	public void setPointStart(long pointStart) {
		this.pointStart = pointStart;
	}
	public List<Double> getData() {
		return data;
	}
	public void setData(List<Double> data) {
		this.data = data;
	}
    
}
