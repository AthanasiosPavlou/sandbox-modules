package com.blipnip.app.client.mainapp.utils;

import java.util.ArrayList;

import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Geometry;
import org.gwtopenmaps.openlayers.client.geometry.LineString;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import com.blipnip.app.shared.BlipMaster;

/**
 * Given a blip (or feature, as the OpenLayers terminology puts it)
 * and its position on the map this class offer the methods for adding
 * points that will lead to the construnction of its path on the map.
 * 
 * @author Thanos
 *
 */
public class BlipPathBuilder
{
	//Blip Master of blip (and blip path)
	String username = null;
	
	//Blip Master Rank (we need this to check how long the path is allowed to be)
	String blipMasterRank = null;
	
	// The Id of the feature (blip) for which the path will be constructed 
	private String tempBlipId = null; 
	
	// The "collection" of points comprising the blip path
	private ArrayList<Point> pointList = null;
	
	// The geometry (LineString) created by the point list
	private LineString blipPath = null;
	
	// The feature created by the geometry
	private VectorFeature path = null;
	
	// if true then it refers to user clicking on map, if false default orbit. 
	private Boolean customOrbit = null;
		
	/*
	 * Getters-Setters
	 */
	public String getFeatureId()
	{
		return this.tempBlipId;
	}
	
	public ArrayList<Point> getPointList()
	{
		return this.pointList;
	}
	
	private void setPath(VectorFeature path)
	{
		this.path = path;
	}
	
	public VectorFeature getPath()
	{
		return this.path;
	}
	
	public Point getLastPoint()
	{
		Point lastPoint = null;
		if (this.pointList != null)
		{
			if (this.pointList.size() >0)
			{
				lastPoint = this.pointList.get(this.pointList.size()-1);
			}
		}
		return lastPoint;
	}
	
	public Boolean getCustomOrbit()
	{
		return customOrbit;
	}

	/**
	 * When a feature id and its coordinates on the map are provided
	 * the point list is initialised with the first point being its coordinates
	 * on the map.
	 * 
	 * @param featureId
	 * @param featurePoint
	 */
	public BlipPathBuilder(String featureId, Point featurePoint, BlipMaster blipMaster, boolean customOrbit)
	{
		this.username = blipMaster.getBlipMasterEmail();
		this.blipMasterRank = blipMaster.getLoginRank();
		
		this.tempBlipId = featureId;
		this.pointList = new ArrayList<Point>();
		
		this.customOrbit = customOrbit;
		
		// When BlipPathBuilder is constructed, the root coordinate is added.
		addPoint(featurePoint);
	}
	
	/**
	 * Default constructor
	 */
	public BlipPathBuilder()
	{
		
	}

	/**
	 * Add a point in the point list
	 * 
	 * @param point
	 */
	public void addPoint(Point point)
	{
		this.pointList.add(point);
	}
	
	/**
	 * Using all collected points (that are user clicks on the map), 
	 * constuct the Blip path as a LineString geometry, then create
	 * a VectorFeature based on that Geometry and set that feature
	 * as the path.
	 * 
	 * @return: The vector feature representing the path
	 */
	public VectorFeature contructPath()
	{
		VectorFeature constructedPath = null;
		
		// Only start constructing if there is a pointList and it contains at least 2 points (needed for a line)
		if (pointList != null && pointList.size() >=2)
		{
			// Whenever contructPath is called, set path to null. The newly created path (constructed path) will be set later (setPath) 
			this.path = null;
			
			// This is a list to array conversion cause the LineString geometry requires an array 
			Point[] points = new Point[pointList.size()];
			for (int index=0;index<pointList.size();index++)
			{
				points[index] = pointList.get(index);
				//System.out.println("@BlipPathBuilder, @construct path, construction, "+index +": [x, y], ["+points[index].getX()+", "+points[index].getY()+"]");
			}
			
			// Create geometry
			this.blipPath = new LineString(points);
			
			if (isDistancePermitted(this.blipPath))
			{
				// Create feature based on Geometry
				constructedPath = new VectorFeature(this.blipPath);
				setPath(constructedPath);
			}
			else
			{
				System.out.println("@BlipPathBuilder, @construct path, isDistancePermitted, distance not permitted, not setting path. ");
			}
		}
		return constructedPath;
	}
	
	/**
	 * Use this to limit the path length a user defines
	 *  
	 *  (Could also try blipPath.getGeodesicLength(projection))
	 *  
	 * @param line
	 * @return
	 */
	public boolean isDistancePermitted(LineString line)
	{
		System.out.println("@BlipPathBuilder, @ isDistancePermitted, length of line is: "+line.getLength());
		
		//TODO - add threshold, leave 0 to allow any length for now.
		if (line.getLength() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}	
	}
	
	/**
	 * Break down (translate) a geometry to a set of points.
	 * If for example we have a path defined, of the form *----*----*
	 * where three points are defined. This method will calculate
	 * all points between point 1 and 2 and all between point 2 and 3.
	 * 
	 * //http://stackoverflow.com/questions/5784129/coordinates-not-accurate-when-converting-to-int
	 * 
	 * @param vf: The vector feature that will be translated. This has to be a LineString.
	 */
	public ArrayList<Point> translatePathToPoints(VectorFeature vf)
	{
		ArrayList<Point> pathPointCollection = null;
		if (vf != null)
		{
			Geometry geometry = vf.getGeometry(); 
		
			if (geometry != null)
			{
				if (geometry.getClassName().equals(Geometry.LINESTRING_CLASS_NAME))
				{ 	
					LineString lineString = LineString.narrowToLineString(geometry.getJSObject());
					
					Point[] linePointStartEnd       = lineString.getVertices(true);
					Point[] linePointIntermediate = lineString.getVertices(false);
					
//					System.out.println("Start-End");
//					for (int index=0;index<linePointStartEnd.length;index++)
//					{
//						System.out.println("@BlipPathBuilder, @ translatePathToPoints "+ index+". :"+linePointStartEnd[index].getX()+", "+ linePointStartEnd[index].getY());
//					}
//					
//					System.out.println("Intermediate");
//					for (int index=0;index<linePointIntermediate.length;index++)
//					{
//						System.out.println("@BlipPathBuilder, @ translatePathToPoints "+ index+". :"+linePointIntermediate[index].getX()+", "+ linePointIntermediate[index].getY());
//					}
					
					//if there is a start and finish
					if (linePointStartEnd !=null && linePointStartEnd.length==2)
					{
						//if there are no intermediate points
						if (linePointIntermediate.length == 0)
						{
							 pathPointCollection = getlinePoints((int)linePointStartEnd[0].getX(), (int)linePointStartEnd[0].getY(), (int)linePointStartEnd[1].getX(), (int)linePointStartEnd[1].getY());
						}
						else
						{
							pathPointCollection = new ArrayList<Point>();
								
							// Get all points within segments. A segment is a line between two points.
							for (int index=0; index<linePointIntermediate.length;index++)
							{
								ArrayList<Point> tempPathPointCollection = null;
								
								if (index==0)
								{
									//Add first segment
									tempPathPointCollection = getlinePoints((int)linePointStartEnd[0].getX(), (int)linePointStartEnd[0].getY(), (int)linePointIntermediate[0].getX(), (int)linePointIntermediate[0].getY());		
								}
								else
								{
									//Add intermediate segments
									tempPathPointCollection = getlinePoints((int)linePointIntermediate[index-1].getX(), (int)linePointIntermediate[index-1].getY(), (int)linePointIntermediate[index].getX(), (int)linePointIntermediate[index].getY());
								}						
								pathPointCollection.addAll(tempPathPointCollection);
							}
							// Add last segment
							pathPointCollection.addAll(getlinePoints((int)linePointIntermediate[linePointIntermediate.length-1].getX(), (int)linePointIntermediate[linePointIntermediate.length-1].getY(), (int)linePointStartEnd[1].getX(), (int)linePointStartEnd[1].getY()));	
						}
					}
					/**
					 *Condition and loop for testing
					 *
					if (pathPointCollection != null && !pathPointCollection.isEmpty())
					{
						int count = 0;
						for(Point point:pathPointCollection)
						{
							//System.out.println("Translated path, [x, y], "+count+"  ["+point.getX()+", "+point.getY()+"]"); count++;
						}
					}
					else
					{
						System.out.println("@BlipPathBuilder, @ translatePathToPoints, point collection not created.");
					}
					*/
				}
				else
				{
					System.out.println("@BlipPathBuilder, @ translatePathToPoints, feature to process not a linestring...");
				}	
			}
		}
		return pathPointCollection;
	}
	
	/**
	 * 
	 * This algorithm can draw a line from point [x, y] to point [x2, y2].
	 * It accepts integers, so when using with data that have decimals
	 * they need to be converted first (i.e. 5.4 --> 5.4*10 -> use 54).
	 * 
	 *  More details at:
	 * http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/
	 * 
	 * @param xStartPoint
	 * @param yStartPoint
	 * @param xEndPoint
	 * @param yEndPoint
	 * @return
	 */
	public ArrayList<Point> getlinePoints(int xStartPoint, int yStartPoint, int xEndPoint, int yEndPoint)
	{
		ArrayList<Point> linePointList = new ArrayList<Point>();
		
		int w = xEndPoint - xStartPoint;
		int h = yEndPoint - yStartPoint;
		
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
		
		if (w < 0)
		{
			dx1 = -1;
		}
		else if (w > 0)
		{
			dx1 = 1;
		}
		
		if (h < 0)
		{
			dy1 = -1;
		}
		else if (h > 0)
		{
			dy1 = 1;
		}
		
		if (w < 0)
		{
			dx2 = -1;
		}
		else if (w > 0)
		{
			dx2 = 1;
		}
		
		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		
		if (!(longest > shortest))
		{
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0)
				dy2 = -1;
			else if (h > 0)
				dy2 = 1;
			dx2 = 0;
		}
		
		int numerator = longest >> 1;
		
		for (int i = 0; i <= longest; i = i + 1)
		{
			//putpixel(x, y, color);
			//System.out.println("index: " + i + " | x: " + xStartPoint + " | y: " + yStartPoint);
			linePointList.add(new Point(xStartPoint,yStartPoint));
			
			
			numerator += shortest;
			
			if (!(numerator < longest))
			{
				numerator -= longest;
				xStartPoint += dx1;
				yStartPoint += dy1;
			}
			else
			{
				xStartPoint += dx2;
				yStartPoint += dy2;
			}
		}
		return linePointList;
	}
}