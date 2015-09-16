package techwind.util.graph;

import java.util.ArrayList;

import techwind.util.graphics.Vector;


import android.graphics.Color;

public class JCurve 
{
	private String m_CurveName;
//	private int n;   /** the number of data points*/
//	private double[] x = new double[MAXPOINTS];
//	private double[] y = new double[MAXPOINTS];
	private ArrayList<Double> x;
	private ArrayList<Double> y;
	private boolean m_bShowPoints;
	private boolean m_bIsVisible;
	private int m_CurveColor;
	private int m_CurveStyle;
	private int m_CurveWidth;
	private int m_iSelected;
	private int m_iAxis; /**0 if left side axis, 1 if right-side axis*/
	

	JGraph m_pGraph;
	

	public JCurve()
	{
		x = new ArrayList<Double>();
		y = new ArrayList<Double>();
	    m_CurveColor = Color.rgb(255,0,127);

	    m_iAxis = 0;
		m_CurveName = "";
		m_bIsVisible = true;
		m_bShowPoints = false;
		m_CurveWidth = 1;
		m_CurveStyle = 0;
		m_iSelected = -1;
	}
	
	public double x(int index) {return x.get(index);}
	public double y(int index) {return y.get(index);}
	
	public int addPoint(double xn, double yn)
	{
		x.add(xn);
		y.add(yn);
		return x.size();
	}
	
	
	void copy(JCurve pCurve)
	{
		int i;
		x.clear();
		y.clear();
	
		for (i=0; i<pCurve.pointCount() ;i++)
		{
			x.add(pCurve.x.get(i));
			y.add(pCurve.y.get(i));
		}
		m_CurveColor  = pCurve.m_CurveColor;
		m_CurveStyle  = pCurve.m_CurveStyle;
		m_CurveWidth  = pCurve.m_CurveWidth;
		m_bIsVisible  = pCurve.m_bIsVisible;
		m_bShowPoints = pCurve.m_bShowPoints;
		m_CurveName   = pCurve.m_CurveName;
	}
	
	
	public int color()
	{
		return m_CurveColor;
	}
	
	
	
	Vector point(int ref)
	{
		Vector r = new Vector();
		if(ref<0 || ref>=pointCount())
		{
			r.x = 0.0;
			r.y = 0.0;
		}
		else{
			r.x = x.get(ref);
			r.y = y.get(ref);
		}
		return r;
	}
	
	
	/**TODO : java error, cannot pass variables by reference (JAVA...)*/	
	int closestPoint(int iScale, double xs, double ys, double dist )
	{
		int ref;
		double d2;
		ref = -1;
		dist = 1.e10;
		if (pointCount()<1) return -1;
		for(int i=0; i<pointCount(); i++)
		{
			d2 =   (xs-x.get(i))*(xs-x.get(i))/m_pGraph.xScale()/m_pGraph.xScale() 
				 + (ys-y.get(i))*(ys-y.get(i))/m_pGraph.yScale(iScale)/m_pGraph.yScale(iScale);
			if (d2<dist)
			{
				dist = d2;
				ref = i;
			}
		}
		return ref;
	}
	
	
	/**TODO : java error, cannot pass variables by reference (JAVA...)*/
	int closestPoint(double xs, double ys, double xSel, double ySel, double dist)
	{
		int nSel=-1;
		double d2;
		dist = 1.e40;
	
		for(int i=0; i<pointCount(); i++)
		{
			d2 =   (xs-x.get(i))*(xs-x.get(i)) + (ys-y.get(i))*(ys-y.get(i));
			if (d2<dist)
			{
				dist = d2;
				xSel = x.get(i);
				ySel = y.get(i);
				nSel = i;
			}
		}
		return nSel;
	}
	
	
	
	int pointCount()
	{
		return x.size();
	}
	
	
	int style()
	{
		return m_CurveStyle;
	}
	
	int axis()
	{
		return m_iAxis;
	}
	
	String title()
	{
		return m_CurveName;
	}
	

	int width()
	{
		return m_CurveWidth;
	}
	
	
	double xMin()
	{
		double xMin = 99999999.0;
	//	if(n==0) xmin = .0; 
	//	else
		for(int i=0; i<pointCount();i++)
			xMin = Math.min(xMin, x.get(i));
		return xMin;
	}
	
	
	double xMax()
	{
		double xMax = -99999999.0;
	//	if(n==0) xmax = 1.0; 
	//	else
	for(int i=0; i<pointCount();i++)
		xMax = Math.max(xMax, x.get(i));
		return xMax;
	}
	
	
	double yMin()
	{
		double yMin = 99999999.0;
		for(int i=0; i<pointCount();i++)
			yMin = Math.min(yMin, y.get(i));
		return yMin;
	}
	
	
	double yMax()
	{
		double yMax = -99999999.0;
		for(int i=0; i<pointCount();i++)
			yMax = Math.max(yMax, y.get(i));
		return yMax;
	}
	
	
	boolean isVisible()
	{
		return m_bIsVisible;
	}
	
	
	boolean pointsVisible()
	{
		return m_bShowPoints;
	}
	
	
	public void resetCurve()
	{
		x.clear();
		y.clear();
	}
	
	
	public void setTitle(String Title)
	{
		m_CurveName = Title;
	}
	
	
	public void setColor(int clr)
	{
		m_CurveColor = clr;
	}
	
	
	public void selectPoint(int iPoint)
	{
		m_iSelected = iPoint;
	}
	
	public void setStyle(int nStyle)
	{
		m_CurveStyle = nStyle;
	}
	
	
	public void setWidth(int nWidth)
	{
		m_CurveWidth = nWidth;
	}
	
	
	public void setVisible(boolean bVisible)
	{
		m_bIsVisible = bVisible;
	}
	
	public void showPoints(boolean bShow)
	{
		m_bShowPoints = bShow;
	}
	
	public void setAxis(int iAxis)
	{
		if(iAxis<0 || iAxis>1) iAxis=0;
		else                   m_iAxis = iAxis;
	}
	
	
	int selectedPoint()
	{
		return m_iSelected;
	}

}
