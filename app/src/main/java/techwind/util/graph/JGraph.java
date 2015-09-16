package techwind.util.graph;

import java.util.ArrayList;

import techwind.util.graphics.Rectangle;
import techwind.util.graphics.Vector;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;


public class JGraph 
{
	static int[] m_CurveColors = new int[10];
	static private final float PRECISION = 0.0000001f;
	static public final int NOT_EXTENSIBLE = 0;
	static public final int EXTENSIBLE = 1;
	
	static private int m_pointRadius=6;
	
	Paint m_Paint;

	String m_GraphName;
	String m_XTitle;
	String m_YTitle[];

	Rect m_rCltRect;         //in screen coordinates
	Rectangle m_rDrawRect;	  //in viewport coordinated
	ArrayList<JCurve> m_oaCurves;
	Point[] m_ptOffset; //in screen coordinates, w.r.t. the client area

	boolean m_bAutoX;
	boolean m_bXMajGrid, m_bXMinGrid;
	boolean m_bXAutoMinGrid;

	boolean m_bYInverted;
	boolean m_bShowY2Axis;
	boolean m_bAlignYZero;  /** true if the origins of both Y axis are to be aligned horizontally*/
	boolean[] m_bAutoY, m_bYAutoMinGrid;
	boolean[] m_bYMajGrid, m_bYMinGrid;
	boolean m_bBorder;
	boolean m_bShowLegend;
	boolean m_bLandscape;
	
	Point m_legendPosition = new Point();

	int m_Type;

	int m_AxisStyle;// axis style
	int m_AxisWidth;// axis width

	int m_XMajStyle, m_XMajWidth, m_XMajClr, m_XMinStyle, m_XMinWidth, m_XMinClr;
	double xo, xunit, xmin, xmax, m_XMinorUnit, m_scalex;
	int exp_x;
	int m_X;//index of X variable
	
	int[] m_YMajStyle, m_YMajWidth, m_YMajClr, m_YMinStyle, m_YMinWidth, m_YMinClr;
	double[] ymin, ymax, yo, yunit, m_scaley, m_YMinorUnit;
	int []exp_y;
	int[] m_Y; //index of Y variables
	int[] m_yAxisColor;
	
	double Cmin, Cmax;
	double m_height, m_width; //graph width and height
	int m_iLeftMargin, m_iRightMargin, m_iTopMargin, m_iBottomMargin;
	int m_xAxisColor;
	
	
	int m_iTextSize;

	int m_TitleColor;
	int m_LabelColor;

	int m_BkColor;
	int m_BorderColor;
	int m_BorderStyle;
	int m_BorderWidth;



	public JGraph()
	{
		// Initializes the graph with standard data
		// Shall be overwritten by user defined input
		m_iLeftMargin = m_iRightMargin = m_iTopMargin = m_iBottomMargin = 17;

		m_iTextSize = 16;
		
		m_rCltRect = new Rect(0, 0, 200, 300);
		m_Paint =  new Paint();
		m_Paint.setAntiAlias(true);
//    	m_Paint.setPathEffect(new DashPathEffect(new float[]{8,10}, 0));

		m_GraphName="";
		m_XTitle="";
		
		m_Y = new int[]{0,1};
		
		m_rDrawRect = new Rectangle();
		m_oaCurves = new ArrayList<JCurve>();

		m_ptOffset = new Point[]{new Point(0,0), new Point(0,0)};

		//Type is used to determine automatic scales
		m_Type = NOT_EXTENSIBLE;

		xmin          = 0.0;
		xmax          = 0.1;
		xunit         = 0.1;
		xo            = 0.0;
		m_scalex      = 0.1;
		m_XMinorUnit  = 0.01;
		exp_x= 0;
		
		ymin     = new double[]{0,0};
		ymax     = new double[]{0,0};
		yunit    = new double[]{0,0};
		yo       = new double[]{0,0};
		m_scaley = new double[]{0,0};
		m_YMinorUnit = new double[]{0.01, 0.01};
		m_bAutoY        = new boolean[]{true, true};
		m_bYAutoMinGrid = new boolean[]{true, true};
		m_bYMajGrid = new boolean[]{true, true};
		m_bYMinGrid = new boolean[]{true, true};		
		exp_y = new int[]{0,0};
		m_yAxisColor = new int[]{1,1};
		m_YMajStyle  = new int[]{1,1};
		m_YMajWidth  = new int[]{1,1};
		m_YMajClr    = new int[]{Color.rgb(200,200,200),Color.rgb(200,200,200)};
		m_YMinStyle  = new int[]{1,1};
		m_YMinWidth  = new int[]{1,1};
		m_YMinClr    = new int[]{Color.rgb(120,120,120),Color.rgb(120,120,120)};
		m_YTitle     = new String[]{"Axis 1", "Axis 2"};
		m_bShowY2Axis = false;
		m_bAlignYZero = true;
		
		setDefaults();


		Cmin  = 0.0;
		Cmax  = 1.0;


		m_bYInverted     = false;
		m_bAutoX         = true;
		m_bXAutoMinGrid  = true;
		m_bBorder        = false;
		m_bShowLegend    = true;
		m_bLandscape     = true;

		m_CurveColors[0] = Color.rgb(255,   0,   0);
		m_CurveColors[1] = Color.rgb(  0,   0, 255);
		m_CurveColors[2] = Color.rgb(  0, 255,   0);
		m_CurveColors[3] = Color.rgb(255, 255,   0);
		m_CurveColors[4] = Color.rgb(  0, 255, 255);
		m_CurveColors[5] = Color.rgb(255,   0, 255);
		m_CurveColors[6] = Color.rgb(255, 125,  70);
		m_CurveColors[7] = Color.rgb( 70, 125, 255);
		m_CurveColors[8] = Color.rgb(125, 255,  70);
		m_CurveColors[9] = Color.rgb(255, 70,  200);


		m_height      = 0;
		m_width       = 0;
		m_rDrawRect.SetRectEmpty();

		setDefaults();
	}


	JCurve addCurve()
	{
		JCurve pCurve = new JCurve();
		if(pCurve!=null)
		{
			int nIndex = m_oaCurves.size();
			pCurve.setColor(m_CurveColors[nIndex%10]);
			pCurve.setStyle(0);
			pCurve.m_pGraph = this;
			m_oaCurves.add(pCurve);
		}
		return pCurve;
	}



	double ClientTox(double x)
	{
		return (x-(double)m_ptOffset[0].x)*m_scalex;
	}

	double ClientToy(int iScale, double y)
	{
		return (y-(double)m_ptOffset[iScale].y)*m_scaley[iScale];
	}

	double ClientTox(int x)
	{
		return ((double)x-(double)m_ptOffset[0].x)*m_scalex;
	}

	double ClientToy(int iScale, int y)
	{
		return ((double)y-(double)m_ptOffset[iScale].y)*m_scaley[iScale];
	}



	void CopySettings(JGraph pGraph, boolean bScales)
	{
		if(bScales)
		{
			xmin            = pGraph.xmin;
			xmax            = pGraph.xmax;
			xo              = pGraph.xo;
			xunit           = pGraph.xunit;

			ymin            = pGraph.ymin;
			ymax            = pGraph.ymax;
			yo              = pGraph.yo;
			yunit           = pGraph.yunit;

			m_scalex        = pGraph.m_scalex;
			m_scaley        = pGraph.m_scaley;
		}

		m_xAxisColor    = pGraph.m_xAxisColor;
		m_yAxisColor    = pGraph.m_yAxisColor;
		m_BkColor       = pGraph.m_BkColor;
		m_bBorder       = pGraph.m_bBorder;
		m_BorderColor   = pGraph.m_BorderColor;
		m_BorderStyle   = pGraph.m_BorderStyle;
		m_BorderWidth   = pGraph.m_BorderWidth;
		m_LabelColor    = pGraph.m_LabelColor;
		m_TitleColor    = pGraph.m_TitleColor;
		m_AxisStyle     = pGraph.m_AxisStyle;
		m_AxisWidth     = pGraph.m_AxisWidth;
		m_XMajClr       = pGraph.m_XMajClr;
		m_XMajStyle     = pGraph.m_XMajStyle;
		m_XMajWidth     = pGraph.m_XMajWidth;
		m_XMinClr       = pGraph.m_XMinClr;
		m_XMinStyle     = pGraph.m_XMinStyle;
		m_XMinWidth     = pGraph.m_XMinWidth;
		m_YMajClr       = pGraph.m_YMajClr;
		m_YMajStyle     = pGraph.m_YMajStyle;
		m_YMajWidth     = pGraph.m_YMajWidth;
		m_YMinClr       = pGraph.m_YMinClr;
		m_YMinStyle     = pGraph.m_YMinStyle;
		m_YMinWidth     = pGraph.m_YMinWidth;

		m_bAutoX        = pGraph.m_bAutoX;
		m_bAutoY        = pGraph.m_bAutoY;
		m_bXAutoMinGrid = pGraph.m_bXAutoMinGrid;
		m_bYAutoMinGrid = pGraph.m_bYAutoMinGrid;
		m_bYInverted    = pGraph.m_bYInverted;
		m_bXMajGrid     = pGraph.m_bXMajGrid;
		m_bXMinGrid     = pGraph.m_bXMinGrid;
		m_bYMajGrid     = pGraph.m_bYMajGrid;
		m_bYMinGrid     = pGraph.m_bYMinGrid;
		m_bBorder       = pGraph.m_bBorder;

	}


	void DeleteCurve(int index)
	{
		m_oaCurves.remove(index);
	}


	void DeleteCurve(JCurve pCurve)
	{
		JCurve pOldCurve = null;
		for(int i=0; i<m_oaCurves.size(); i++)
		{
			pOldCurve = m_oaCurves.get(i);
			if(pOldCurve==pCurve)
			{
				m_oaCurves.remove(i);
				return;
			}
		}
	}


	void DeleteCurve(String CurveTitle)
	{
		JCurve pOldCurve = null;
		for(int i=0; i<m_oaCurves.size(); i++)
		{
			pOldCurve = m_oaCurves.get(i);
			if(pOldCurve.title().equals(CurveTitle))
			{
				m_oaCurves.remove(i);
				return;
			}
		}
	}


	void DeleteCurves()
	{
		m_oaCurves.clear();//removes the pointers

		if (m_bAutoX && m_Type==NOT_EXTENSIBLE)
		{
			xmin =  0.0;
			xmax =  0.1;
		}

		if (m_bAutoY[0] && m_Type==NOT_EXTENSIBLE)
		{
			ymin[0] =  0.0;
			ymax[0] =  1.0;
		}
		
		if (m_bAutoY[1] && m_Type==NOT_EXTENSIBLE)
		{
			ymin[1] =  0.0;
			ymax[1] =  1.0;
		}
	}


	//___________________Start Gets______________________________________________________________

	int curveCount()
	{
		return (int)m_oaCurves.size();
	}


	int labelColor()
	{
		return m_LabelColor;
	}

	int xAxisColor()
	{
		return m_xAxisColor;
	}

	int yAxisColor(int iScale)
	{
		return m_yAxisColor[iScale];
	}

	int borderColor()
	{
		return m_BorderColor;
	}

	int backColor()
	{
		return m_BkColor;
	}

	int axisStyle()
	{
		return m_AxisStyle;
	}

	int axisWidth()
	{
		return m_AxisWidth;
	}


	boolean isAutoX()
	{
		return m_bAutoX;
	}
	
	
	boolean isAutoY(int iScale)
	{
		return m_bAutoY[iScale];
	}

	boolean isAutoXMin()
	{
		return m_bXAutoMinGrid;
	}
	
	public boolean isAutoYMin(int iScale)
	{
		return m_bYAutoMinGrid[iScale];
	}

	public boolean isBorderVisible()
	{
		return m_bBorder;
	}

	public int borderStyle()
	{
		return m_BorderStyle;
	}

	public int borderWidth()
	{
		return m_BorderWidth;
	}
	
	public Rect clientRect()
	{
		return m_rCltRect;
	}

	public JCurve curve(int nIndex)
	{
		if(m_oaCurves.size()>nIndex)
			return m_oaCurves.get(nIndex);
		else return null;
	}


	public JCurve curve(String CurveTitle)
	{
		JCurve pCurve;
		for(int i=0; i<m_oaCurves.size(); i++)
		{
			pCurve = m_oaCurves.get(i);
			if(pCurve!=null)
			{
				if(pCurve.title().equals(CurveTitle)) return pCurve;
			}
		}
		return null;
	}


	public boolean isInverted()
	{
		if(m_bYInverted) return true;
		else return false;
	}

	public int leftMargin()
	{
		return m_iLeftMargin;
	}

	public int rightMargin()
	{
		return m_iRightMargin;
	}

	public int topMargin()
	{
		return m_iTopMargin;
	}

	public int bottomMargin()
	{
		return m_iBottomMargin;
	}

	
	public String graphName()
	{
		return m_GraphName;
	}

	public int titleColor()
	{
		return m_TitleColor;
	}
	
	public double x0()
	{
		return xo;
	}


	public boolean isXMajGridVisible()
	{
		return m_bXMajGrid;
	}


	public void getXMajGrid(boolean bstate, int clr, int style, int width)
	{
		bstate = m_bXMajGrid;
		clr   = m_XMajClr;
		style = m_XMajStyle;
		width = m_XMajWidth;
	}


	public boolean isXMinGridVisible()
	{
		return m_bXMinGrid;
	}

	public void getXMinGrid(boolean state, boolean bAuto, int clr, int style, int width, double unit)
	{
		state = m_bXMinGrid;
		bAuto = m_bXAutoMinGrid;
		clr   = m_XMinClr;
		style = m_XMinStyle;
		width = m_XMinWidth;
		unit  = m_XMinorUnit;
	}

	public double xMin()
	{
		return xmin;
	}

	public double xMax()
	{
		return xmax;
	}


	public double xScale()
	{
		return m_scalex;
	}
	
	
	public String xTitle()
	{
		return m_XTitle;
	}

	public double xUnit()
	{
		return xunit;
	}


	public int xVariable()
	{
		return m_X;
	}



	public boolean isYMajGridVisible(int iScale)
	{
		return m_bYMajGrid[iScale];
	}

	
	public void getYMajGrid(int iScale, boolean state, int clr, int style, int width)
	{
		state = m_bYMajGrid[iScale];
		clr   = m_YMajClr[iScale];
		style = m_YMajStyle[iScale];
		width = m_YMajWidth[iScale];
	}

	public String yTitle(int iScale)
	{
		return m_YTitle[iScale];
	}


	public boolean isYMinGridVisible(int iScale)
	{
		return m_bYMinGrid[iScale];
	}


	void getYMinGrid(int iScale, boolean state, boolean bAuto, int clr, int style, int width, double unit)
	{
		state = m_bYMinGrid[iScale];
		bAuto = m_bYAutoMinGrid[iScale];
		clr   = m_YMinClr[iScale];
		style = m_YMinStyle[iScale];
		width = m_YMinWidth[iScale];
		unit  = m_YMinorUnit[iScale];
	}


	double y0(int iScale)
	{
		return yo[iScale];
	}
	
	double yMin(int iScale)
	{
		return ymin[iScale];
	}
	
	double yMax(int iScale)
	{
		return ymax[iScale];
	}
	
	double yUnit(int iScale)
	{
		return yunit[iScale];
	}


	double yScale(int iScale)
	{
		return m_scaley[iScale];
	}


	int yVariable(int iScale)
	{
		return m_Y[iScale];
	}

	int textSize() {return m_iTextSize;}

	boolean Init()
	{
		//graph width and height
		m_width  =  m_rCltRect.width()  - m_iLeftMargin - m_iRightMargin;
		m_height =  m_rCltRect.height() - m_iTopMargin  - m_iBottomMargin;

		setXScale();
		
		setYScale(0);
		setYScale(1);

		if(m_bXAutoMinGrid) m_XMinorUnit = xunit/5.0;
		if(m_bYAutoMinGrid[0]) m_YMinorUnit[0] = yunit[0]/5.0;
		if(m_bYAutoMinGrid[1]) m_YMinorUnit[1] = yunit[1]/5.0;

		return true;
	}


	boolean isInDrawRect(Vector pt)
	{
		if(m_rDrawRect.PtInRect(pt))
		{
			return true;
		}
		else return false;
	}


	boolean isInDrawRect(Point pt)
	{
		if(m_rCltRect.contains(pt.x, pt.y)) return true;
		else return false;
	}

	boolean isInDrawRect(int x, int y)
	{
		if(m_rCltRect.contains(x,y)) return true;
		else return false;
	}


	public void resetCurves()
	{
        for (JCurve pCurve : m_oaCurves)
        {
            pCurve.resetCurve();
        }
	}


	public void resetCurve(int iCurve)
	{
		if(iCurve<m_oaCurves.size())
		{
			m_oaCurves.get(iCurve).resetCurve();
		}
	}

	
	public void resetLimits()
	{
		resetXLimits();
		resetYLimits(0);
		resetYLimits(1);
	}


	void resetXLimits()
	{
		if(m_bAutoX)
		{
			xmin =  0.0;
			xmax =  0.1;
			xo   =  0.0;
		}
	}


	void resetYLimits(int iScale)
	{
		if(m_bAutoY[iScale])
		{
			ymin[iScale] =   0.000;
			ymax[iScale] =   0.001;
			yo[iScale]   =   0.000;
		}
	}


	void scale(double zoom)
	{
		if (zoom<0.01) zoom =0.01;
		m_bAutoX = false;
		m_bAutoY[0] = false;
		m_bAutoY[1] = false;

		double xm = (xmin + xmax)/2.0;
		xmin = xm+(xmin-xm)*zoom;
		xmax = xm+(xmax-xm)*zoom;

		double ym;
		ym = (ymin[0] + ymax[0])/2.0;
		ymin[0] = ym+(ymin[0]-ym)*zoom;
		ymax[0] = ym+(ymax[0]-ym)*zoom;
		ym = (ymin[1] + ymax[1])/2.0;
		ymin[1] = ym+(ymin[1]-ym)*zoom;
		ymax[1] = ym+(ymax[1]-ym)*zoom;
	}


	void scaleX(double zoom)
	{
		if (zoom<0.01) zoom =0.01;
		m_bAutoX = false;

		double xm = (xmin + xmax)/2.0;
		xmin = xm+(xmin-xm)*zoom;
		xmax = xm+(xmax-xm)*zoom;

	}

	void scaleY(int iScale, double zoom)
	{
		if (zoom<0.01) zoom =0.01;
		m_bAutoY[iScale] = false;

		double ym = (ymin[iScale] + ymax[iScale])/2.0;
		ymin[iScale] = ym+(ymin[iScale]-ym)*zoom;
		ymax[iScale] = ym+(ymax[iScale]-ym)*zoom;
	}

	//___________________Start Sets______________________________________________________________

	void setLegendPosition(int x, int y)
	{
		m_legendPosition.x = x;
		m_legendPosition.y = y;
	}

	
	public void setAutoLimits(boolean bAuto)
	{
		m_bAutoX = bAuto;
		m_bAutoY[0] = bAuto;
		m_bAutoY[1] = bAuto;
		resetXLimits();
		resetYLimits(0);
		resetYLimits(1);
	}

	public void setAutoXLimits(boolean bAuto)
	{
		m_bAutoX = bAuto;
		resetXLimits();
	}
	
	public void setAutoYLimits(int iScale, boolean bAuto)
	{
		m_bAutoY[iScale] = bAuto;
		resetYLimits(iScale);
	}
	
	void SetAutoXMinUnit(boolean bAuto)
	{
		m_bXAutoMinGrid = bAuto;
		if(bAuto) m_XMinorUnit = xunit/5.0;
	}


	void setAutoXUnit()
	{
		//	xunit = 100.0*m_scalex;
		xunit = (xmax-xmin)/3.0;

		if (xunit<1.0)
		{
			exp_x = (int)Math.log10(xunit*1.00001)-1;
			exp_x = Math.max(-4, exp_x);
		}
		else exp_x = (int)Math.log10(xunit*1.00001);
		int main_x = (int)(xunit/Math.pow(10.0, exp_x)*1.000001);


		if(main_x<2)
			xunit = Math.pow(10.0,exp_x);
		else if (main_x<5)
			xunit = 2.0*Math.pow(10.0,exp_x);
		else
			xunit = 5.0*Math.pow(10.0,exp_x);

	}


	void setAutoYMinUnit(int iScale, boolean bAuto)
	{
		m_bYAutoMinGrid[iScale] = bAuto;
		if(bAuto) m_YMinorUnit[iScale] = yunit[iScale]/5.0;
	}


	void setAutoYUnit(int iScale)
	{
		//	yunit = 100.0 * m_scaley;
		yunit[iScale] = (ymax[iScale]-ymin[iScale])/5.0;
		if (yunit[iScale]<1.0)
		{
			exp_y[iScale] = (int)Math.log10(yunit[iScale]*1.00001)-1;
			//		exp_y = Math.max(-4, exp_y);
		}
		else  exp_y[iScale] = (int)Math.log10(yunit[iScale]*1.00001);

		int main_y = (int)(yunit[iScale]/Math.pow(10.0, exp_y[iScale]));

		if(main_y<2)
			yunit[iScale] = Math.pow(10.0,exp_y[iScale]);
		else if (main_y<5)
			yunit[iScale] = 2.0*Math.pow(10.0,exp_y[iScale]);
		else
			yunit[iScale] = 5.0*Math.pow(10.0,exp_y[iScale]);	
	}

	void setXAxisData(int s, int w, int clr)
	{
		m_AxisStyle = s;
		m_AxisWidth = w;
		m_xAxisColor = clr;
	}

	void setYAxisData(int iScale, int s, int w, int clr)
	{
		m_AxisStyle = s;
		m_AxisWidth = w;
		m_yAxisColor[iScale] = clr;
	}

	void setXAxisColor(int crColor)
	{
		m_xAxisColor = crColor;
	}
	
	void setYAxisColor(int iScale, int crColor)
	{
		m_yAxisColor[iScale] = crColor;
	}
	

	void setAxisStyle(int nStyle)
	{
		m_AxisStyle = nStyle;
	}

	void setAxisWidth(int Width)
	{
		m_AxisWidth = Width;
	}


	void setBkColor(int cr)
	{
		m_BkColor = cr;
	}

	void setBorderColor(int crBorder)
	{
		m_BorderColor = crBorder;
	}

	void setBorder(boolean bBorder)
	{
		m_bBorder = bBorder;
	}

	void setBorderWidth(int w)
	{
		m_BorderWidth = w;
	}

	void setBorderStyle(int s)
	{
		m_BorderStyle = s;
	}

	void setDrawRect(Rectangle Rect)
	{
		m_rDrawRect = Rect;
	}


	void setDrawRect(Rect Rect)
	{
		m_rCltRect = Rect;
	}

	
	void setGraphName(String GraphName)
	{
		m_GraphName = GraphName;
	}

	
	void setDefaults()
	{
		m_BkColor = Color.TRANSPARENT;
		m_BorderColor = Color.rgb(200,200,200);
		m_BorderStyle = 0;
		m_BorderWidth = 3;

		m_AxisStyle = 0;
		m_AxisWidth = 3;

		m_bYInverted = false;

		setXAxisColor(Color.rgb(190,190,255));
		setYAxisColor(0, Color.rgb(190,190,255));
		setYAxisColor(1, Color.rgb(190,190,255));
		setTitleColor(Color.rgb(255,255,255));
		setLabelColor(Color.rgb(255,255,255));

		m_bXMajGrid    = true;
		m_bYMajGrid[0] = true;
		m_bYMajGrid[1] = true;
		m_bXMinGrid    = false;
		m_bYMinGrid[0] = false;
		m_bYMinGrid[1] = false;

		m_XMajStyle    = 1;
		m_YMajStyle[0] = 1;
		m_YMajStyle[1] = 1;
		m_XMajWidth    = 1;
		m_YMajWidth[1] = 1;
		m_YMajWidth[0] = 1;
		m_XMajClr      = Color.rgb(90,90,90);
		m_YMajClr[0]   = Color.rgb(90,90,90);
		m_YMajClr[1]   = Color.rgb(90,90,90);

		m_XMinStyle    = 1;
		m_YMinStyle[0] = 1;
		m_YMinStyle[1] = 1;
		m_XMinWidth    = 1;
		m_YMinWidth[0] = 1;
		m_YMinWidth[1] = 1;
		m_XMinClr    = Color.rgb(50,50,50);
		m_YMinClr[0] = Color.rgb(50,50,50);
		m_YMinClr[1] = Color.rgb(50,50,50);

		m_XMinorUnit = 0.1;
		m_YMinorUnit[0] = 0.1;
		m_YMinorUnit[1] = 0.1;
	}

	
	public void setFontType(Typeface type)
	{
		m_Paint.setTypeface(type);
	}


	void setLabelColor(int crColor)
	{
		m_LabelColor = crColor;
	}


	void setYInverted(boolean bInverted)
	{
		m_bYInverted = bInverted;
	}
	
	
	void setLandscape(boolean bLandScape)
	{
		m_bLandscape = bLandScape;
	}

	void setMargins(int m)
	{
		m_iLeftMargin = m_iRightMargin = m_iTopMargin = m_iBottomMargin = m;
	}
	
	void setMargins(int[] m)
	{
		m_iLeftMargin   = m[0];
		m_iRightMargin  = m[1];
		m_iTopMargin    = m[2];
		m_iBottomMargin = m[3];
	}
	
	
	void setLeftMargin(int m)
	{
		m_iLeftMargin = m;
	}

	
	void setRightMargin(int m)
	{
		m_iRightMargin = m;
	}

	
	void setTopMargin(int m)
	{
		m_iTopMargin = m;
	}

	
	void setBottomMargin(int m)
	{
		m_iBottomMargin = m;
	}

	
	void setTextSize(int size)
	{
		m_iTextSize= size;
	}


	void setTitleColor(int crColor)
	{
		m_TitleColor = crColor;
	}



	void setGraphType(int type)
	{
		m_Type = type;
	}



	void setVariables(int  X, int  Y, int Y1)
	{
		m_X = X;
		m_Y[0] = Y;
		m_Y[1] = Y1;
	}


	void setViewPort(int iScale, double x1, double x2, double y1, double y2)
	{
		m_bAutoX = false;
		m_bAutoY[iScale] = false;
		xmin = x1;
		xmax = x2;
		ymin[iScale] = y1;
		ymax[iScale] = y2;
	}


    public void setMin(int iScale, double f)
    {
        if(iScale==0) xmin = f;
        else if (iScale==1) ymin[0] = f;
        else if (iScale==2) ymin[1] = f;
    }

    public void setMax(int iScale, double f)
    {
        if(iScale==0)       xmax = f;
        else if (iScale==1) ymax[0] = f;
        else if (iScale==2) ymax[1] = f;
    }


	void setX0(double f)
	{
		xo = f;
	}



	void setXMajGrid(boolean state, int clr, int style, int width)
	{
		m_bXMajGrid = state;
		m_XMajClr   = clr;
		m_XMajStyle = style;
		m_XMajWidth = width;
	}


	void setXMajGrid(boolean bGrid)
	{
		m_bXMajGrid = bGrid;
	}

	void setXMinGrid(boolean bGrid)
	{
		m_bXMinGrid = bGrid;
	}



	void setXMax(double f){
		xmax = f;
	}


	public void setXMin(double f)
    {
		xmin = f;
	}

	void setXMinGrid(boolean state, boolean bAuto, int clr, int style, int width, double unit)
	{
		m_bXMinGrid = state;
		m_bXAutoMinGrid = bAuto;
		m_XMinClr   = clr;
		m_XMinStyle = style;
		m_XMinWidth = width;
		if(unit>0.0) m_XMinorUnit  = unit;
	}



	void setXMinorUnit(double f)
	{
		m_XMinorUnit = f;
	}


	boolean setXScale()
	{
		JCurve pCurve;
		int nc;

		if(m_bAutoX)
		{
			boolean bCurve = false;

			if (m_oaCurves.size()>0)
			{
				//init only if we have a curve
				for (nc=0; nc < m_oaCurves.size(); nc++)
				{
					pCurve = m_oaCurves.get(nc);
					if ((pCurve.isVisible() ||pCurve.pointsVisible()) && pCurve.pointCount()>1)
					{
						bCurve = true;
						break;//there is something to draw
					}
				}
			}
			if (bCurve)
			{
				Cmin =  9999999.0;
				Cmax = -9999999.0;
				for (nc=0; nc < m_oaCurves.size(); nc++)
				{
					pCurve = m_oaCurves.get(nc);
					if ((pCurve.isVisible() ||pCurve.pointsVisible()) &&  pCurve.pointCount()>0)
					{
						Cmin = Math.min(Cmin, pCurve.xMin());
						Cmax = Math.max(Cmax, pCurve.xMax());
					}
				}

				if(Cmax<=Cmin)
					Cmax = (Cmin+1.0)*2.0;

				if(m_Type==EXTENSIBLE)
				{
					xmin = Math.min(xmin, Cmin);
					xmax = Math.max(xmax, Cmax);
				}
				else
				{
					xmin = Cmin;
					xmax = Cmax;
				}
				if(Cmin>=0.0) xmin = 0.0;
				if(Cmax<=0.0) xmax = 0.0;

			}
			else
			{
				// until things are made clear
				for (nc=0; nc < m_oaCurves.size(); nc++)
				{
					pCurve = m_oaCurves.get(nc);
					if ((pCurve.isVisible() ||pCurve.pointsVisible()) && pCurve.pointCount()>0)
					{
						xmin = Math.min(xmin, pCurve.x(0));
						xmax = Math.max(xmax, pCurve.x(0));
					}
				}
			}
			xo=0.0;

			if(Math.abs((xmin-xmax)/xmin)<0.001)
			{
				if(Math.abs(xmin)<0.00001) xmax = 1.0;
				else
				{
					xmax = 2.0 * xmin;
					if(xmax < xmin)
					{
						double tmp = xmax;
						xmax = xmin;
						xmin = tmp;
					}
				}
			}

			if(m_width<=0.0) return false;

			m_scalex   = (xmax-xmin)/m_width;


			//try to set an automatic scale for X Axis

			setAutoXUnit();
		}
		else
		{
			//scales are set manually
			if(m_width<=0.0) return false;

			//		m_scalex   =  (xmax-xmin)/m_w;
			if (xunit<1.0)
			{
				exp_x = (int)Math.log10(xunit*1.00001)-1;
				exp_x = Math.max(-4, exp_x);
			}
			else exp_x = (int)Math.log10(xunit*1.00001);

		}
		m_scalex   =  (xmax-xmin)/m_width;

		//graph center position
//		int Xg = (m_rCltRect.right + m_rCltRect.left)/2;
		int Xg = m_iLeftMargin + (m_rCltRect.right-m_iRightMargin - m_rCltRect.left - m_iLeftMargin)/2;
		
		// curves center position
		int Xc = (int)((xmin+xmax)/2.0/m_scalex);
		
		// center graph in drawing rectangle
		m_ptOffset[0].x = (Xg-Xc);
		m_ptOffset[1].x = (Xg-Xc);
		return true;
	}


	void setXUnit(double f)
	{
		xunit = f;
	}

	
	void setXTitle(String str)
	{
		m_XTitle = str;
	}


	void setXVariable(int  X)
	{
		m_X = X;
	}

	
	void setYMin(int iScale, double f){
		ymin[iScale] = f;
	}

	
	void setYMinorUnit(int iScale, double f)
	{
		m_YMinorUnit[iScale] = f;
	}


	void setYMax(int iScale, double f)
	{
		ymax[iScale] = f;
	}

	
	void setY0(int iScale, double f)
	{
		yo[iScale] = f;
	}

	
	void setYTitle(int iScale, String str)
	{
		m_YTitle[iScale] = str;
	}


	
	void setYUnit(int iScale, double f)
	{
		yunit[iScale] = f;
	}

	void setAlignYZero(boolean bAlign)
	{
		m_bAlignYZero = bAlign;
        setYScale(1);
	}


	boolean setYScale(int iScale)
	{
		int nc;
		JCurve pCurve;

		if(m_bAutoY[iScale])
		{
			boolean bCurve = false;
			if (m_oaCurves.size()>0)
			{
				//init only if we have a visible curve on this scle
				for (nc=0; nc<m_oaCurves.size(); nc++)
				{
					pCurve = m_oaCurves.get(nc);
					if ((pCurve.isVisible() ||pCurve.pointsVisible()) && pCurve.pointCount()>0 && pCurve.axis()==iScale)
					{
						bCurve = true;
						break;
					}
				}
			}
			if(bCurve)
			{
				Cmin =  9999999.0;
				Cmax = -9999999.0;
				for (nc=0; nc < m_oaCurves.size(); nc++)
				{
					pCurve = m_oaCurves.get(nc);
					if ((pCurve.isVisible() ||pCurve.pointsVisible()) && pCurve.pointCount()>0 && pCurve.axis()==iScale)
					{
						Cmin = Math.min(Cmin, pCurve.yMin());
						Cmax = Math.max(Cmax, pCurve.yMax());
					}
				}
				if(Cmax<=Cmin)
				{
					Cmax = (Cmin+1.0)*2.0;
				}

				if(m_Type==EXTENSIBLE)
				{
					ymin[iScale] = Math.min(ymin[iScale], Cmin);
					ymax[iScale] = Math.max(ymax[iScale], Cmax);
				}
				else
				{
					ymin[iScale] = Cmin;
					ymax[iScale] = Cmax;
				}
				if(Cmin>=0.0) ymin[iScale] = 0.0;
				if(Cmax<=0.0) ymax[iScale] = 0.0;
			}
			else
			{
				// until things are made clear
				for (nc=0; nc < m_oaCurves.size(); nc++)
				{
					pCurve = m_oaCurves.get(nc);
					if ((pCurve.isVisible() ||pCurve.pointsVisible()) &&  pCurve.pointCount()>0 && pCurve.axis()==iScale)
					{
						ymin[iScale] = Math.min(ymin[iScale], pCurve.y(0));
						ymax[iScale] = Math.max(ymax[iScale], pCurve.y(0));
					}
				}
			}
			yo[iScale] = 0.0;

			if (Math.abs((ymin[iScale]-ymax[iScale])/ymin[iScale])<0.001)
			{
				if(Math.abs(ymin[iScale])<0.00001) ymax[iScale] = 1.0;
				else
				{
					ymax[iScale] = 2.0 * ymin[iScale];
					if(ymax[iScale] < ymin[iScale])
					{
						double tmp = ymax[iScale];
						ymax[iScale] = ymin[iScale];
						ymin[iScale] = tmp;
					}
				}
			}

			if(m_height<=0.0) return false;

			if (!m_bYInverted)
			{
				m_scaley[iScale]   = -(ymax[iScale]-ymin[iScale])/m_height;
			}
			else
			{
				m_scaley[iScale]   =  (ymax[iScale]-ymin[iScale])/m_height;
			}

			//try to set an automatic scale for Y Axis
			setAutoYUnit(iScale);
		}
		else
		{
			//scales are set manually
			if(m_height<=0) return false;

			if (!m_bYInverted)
			{
				m_scaley[iScale]   = -(ymax[iScale]-ymin[iScale])/m_height;
			}
			else
			{
				m_scaley[iScale]   =  (ymax[iScale]-ymin[iScale])/m_height;
			}

			if (yunit[iScale]<1.0)
			{
				exp_y[iScale] = (int)Math.log10(yunit[iScale]*1.00001)-1;
				exp_y[iScale] = Math.max(-4, exp_y[iScale]);
			}
			else  exp_y[iScale] = (int)Math.log10(yunit[iScale]*1.00001);

		}

		//graph center position
		int Yg = (m_rCltRect.top + m_rCltRect.bottom-m_iTopMargin-m_iBottomMargin)/2 + m_iTopMargin;


        // curves center position
        if(m_bAlignYZero && iScale==1)
        {
            int Yc = (int)((ymin[0]+ymax[0])/2.0/m_scaley[0]);
            // center graph in drawing rectangle
            m_ptOffset[1].y = (Yg-Yc);

            //
            //reset upper and lower limits
            ymin[1] = ymin[0] / m_scaley[0] * m_scaley[1];
            ymax[1] = ymax[0] / m_scaley[0] * m_scaley[1];
        }
        else
        {
            int Yc = (int)((ymin[iScale]+ymax[iScale])/2.0/m_scaley[iScale]);
            // center graph in drawing rectangle
            m_ptOffset[iScale].y = (Yg-Yc);
        }


		return true;
	}

	
	void setYMajGrid(int iScale, boolean state, int clr, int style, int width)
	{
		m_bYMajGrid[iScale] = state;
		m_YMajClr[iScale]   = clr;
		m_YMajStyle[iScale] = style;
		m_YMajWidth[iScale] = width;
	}


	void setYMajGrid(int iScale, boolean bGrid)
	{
		m_bYMajGrid[iScale] = bGrid;
	}

	void setYMinGrid(int iScale, boolean state, boolean bAuto, int clr, int style, int width, double unit)
	{
		m_bYMinGrid[iScale]     = state;
		m_bYAutoMinGrid[iScale] = bAuto;
		m_YMinClr[iScale]   = clr;
		m_YMinStyle[iScale] = style;
		m_YMinWidth[iScale] = width;
		if(unit>0.0) m_YMinorUnit[iScale]  = unit;
	}


	void setYMinGrid(int iScale, boolean bGrid)
	{
		m_bYMinGrid[iScale] = bGrid;
	}



	void setYVariable(int iScale, int  Y)
	{
		m_Y[iScale] = Y;
	}


	int xToClient(double x)
	{
		return (int)(x/m_scalex + m_ptOffset[0].x);
	}



	int yToClient(int iScale, double y)
	{
		return (int)(y/m_scaley[iScale] + m_ptOffset[iScale].y);
	}



	JCurve getClosestPoint(double x, double y, double xSel, double ySel, int nSel)
	{
		int i;
		double dist=0, dmax, x1=0, y1=0;
		dmax = 1.e40;
		JCurve pOldCurve, pCurveSel;
		pCurveSel = null;

		for(i=0; i<m_oaCurves.size(); i++)
		{
			pOldCurve = m_oaCurves.get(i);
			if(dist<dmax)
			{
				dmax = dist;
				xSel = x1;
				ySel = y1;
				pCurveSel = pOldCurve;
				nSel = i;
			}
		}
		return pCurveSel;
	}


	JCurve getCurvePoint(int iScale, int xClt, int yClt,int nSel)
	{
		int i, n=0, xc, yc;
		double dist=0, x1=0, y1=0, x,y;
		JCurve pOldCurve;

		x= ClientTox(xClt);
		y= ClientToy(iScale, yClt);
		for(i=0; i<m_oaCurves.size(); i++)
		{
			pOldCurve = m_oaCurves.get(i);
			n = pOldCurve.closestPoint(x, y, x1, y1, dist);

			xc = xToClient(x1);
			yc = yToClient(iScale, y1);

			if((xClt-xc)*(xClt-xc) + (yClt-yc)*(yClt-yc) <16)//sqrt(16) pixels distance
			{
				nSel = n;
				return pOldCurve;
			}
		}
		nSel = -1;
		return  null;
	}


	boolean selectPoint(String CurveName, int sel)
	{
		JCurve pCurve = null;

		if(sel<0) 
		{
			//		pCurve.SetSelected(-1);
			return false;				
		}		

		for(int i=0; i<m_oaCurves.size(); i++)
		{
			pCurve = m_oaCurves.get(i);

			if(pCurve.title().equals(CurveName))
			{
				if(sel>pCurve.pointCount())
				{
					pCurve.selectPoint(-1);
					return false;				
				}
				else
				{
					pCurve.selectPoint(sel);
					return true;
				}
			}
		}
		//	pCurve.SetSelected(-1);
		return false;
	}



	void deselectPoint()
	{
		JCurve pCurve;
		for(int i=0; i<m_oaCurves.size(); i++)
		{
			pCurve = m_oaCurves.get(i);
			pCurve.selectPoint(-1);
		}
	}

	//DRAWING platform specific


	void drawGraph(Rect rect, Canvas canvas)
	{
		m_rCltRect = rect;
		drawGraph(canvas);
	}


	void drawGraph(Canvas canvas)
	{		
		Paint painter = m_Paint;
//    	Paint background
		canvas.drawColor(m_BkColor);

//    	Draw Border
		if(m_bBorder)
		{
			painter.setColor(m_BorderColor);
			painter.setStrokeWidth(m_BorderWidth);
			canvas.drawRect(m_rCltRect, painter);
		}
			
		Init();

		if(m_bXMinGrid)    drawXMinGrid(canvas);
		if(m_bYMinGrid[0]) drawYMinGrid(canvas, 0);
		if(m_bShowY2Axis && m_bYMinGrid[1]) drawYMinGrid(canvas, 1);
		if(m_bXMajGrid)    drawXMajGrid(canvas);
		if(m_bYMajGrid[0]) drawYMajGrid(canvas, 0);
		if(m_bShowY2Axis && m_bYMajGrid[1]) drawYMajGrid(canvas, 1);

		drawAxes(canvas);

		drawXTicks(canvas);
		drawYTicks(canvas,0);
		if(m_bShowY2Axis) drawYTicks(canvas,1);

		drawCurves(canvas);
		
		drawTitles(canvas);
		
		if(m_bShowLegend) drawLegend(canvas, m_legendPosition, Color.WHITE);
	}


	void drawCurves(Canvas canvas)
	{
		for(int ic=0; ic<curveCount(); ic++)
		{
			JCurve pCurve = m_oaCurves.get(ic);
			if(pCurve!=null && (pCurve.isVisible() || pCurve.pointsVisible())) drawCurve(pCurve, canvas);
		}
	}

	
	void drawCurve(JCurve pCurve, Canvas canvas)
	{
		Paint painter = m_Paint;
		double scaley;
		int i;
		Point From = new Point();
		Point To   = new Point();

		int iScale = pCurve.axis();
		scaley = m_scaley[iScale];

		painter.setColor(pCurve.color());
		painter.setStrokeWidth(pCurve.width());
		
		if(pCurve.style()==1)      painter.setPathEffect(new DashPathEffect(new float[]{3,3}, 0)); //dot
		else if(pCurve.style()==2) painter.setPathEffect(new DashPathEffect(new float[]{13,7}, 0)); //dash
		else                       painter.setPathEffect(null); //solid
		painter.setStyle(Paint.Style.STROKE);
    	
		if(pCurve.pointCount()>=1)
		{
			From.set((int)(pCurve.x(0)/m_scalex+m_ptOffset[0].x), (int)(pCurve.y(0)/scaley  +m_ptOffset[iScale].y));

			if(pCurve.isVisible())
			{
				for (i=1; i<pCurve.pointCount();i++)
				{
					To.set((int)(pCurve.x(i)/m_scalex+m_ptOffset[0].x), (int)(pCurve.y(i)/scaley  +m_ptOffset[iScale].y));
					canvas.drawLine(From.x, From.y, To.x, To.y, painter);

					From.x = To.x;
					From.y = To.y;
				}
			}

			if(pCurve.pointsVisible())
			{
				for (i=0; i<pCurve.pointCount();i++)
				{
					if(pCurve.selectedPoint()!=i)
/*						canvas.drawRect( (int)(pCurve.x(i)/m_scalex+m_ptOffset[0].x)     -ptside,
				                 (int)(pCurve.y(i)/  scaley+m_ptOffset[iScale].y)-ptside,
				                 (int)(pCurve.x(i)/m_scalex+m_ptOffset[0].x)     -ptside+2*ptside,
				                 (int)(pCurve.y(i)/  scaley+m_ptOffset[iScale].y)-ptside+2*ptside,
				                 painter);*/
						canvas.drawCircle( (int)(pCurve.x(i)/m_scalex+m_ptOffset[0].x),
						                   (int)(pCurve.y(i)/  scaley+m_ptOffset[iScale].y),m_pointRadius,
						                   painter);
				}
			}
		}
	}


	void drawAxes(Canvas canvas)
	{	
		double yp;

		Paint painter = m_Paint;

//   	AxesPen.setStyle(GetStyle(m_AxisStyle));
		painter.setStrokeWidth(m_AxisWidth);
		painter.setStyle(Style.STROKE);
    	painter.setPathEffect(null);

		//Draw left axis first, for curve 0
		painter.setColor(m_yAxisColor[0]);
		canvas.drawLine((int)(xmin/m_scalex) + m_ptOffset[0].x, (int)(ymin[0]/m_scaley[0]) + m_ptOffset[0].y,
		                (int)(xmin/m_scalex) + m_ptOffset[0].x, (int)(ymax[0]/m_scaley[0]) + m_ptOffset[0].y,
		                painter);

		if(m_bShowY2Axis)
		{
			//Draw right axis next, for curve 1
			painter.setColor(m_yAxisColor[1]);
			canvas.drawLine((int)(xmax/m_scalex) + m_ptOffset[1].x, (int)(ymin[1]/m_scaley[1]) + m_ptOffset[1].y,
			                (int)(xmax/m_scalex) + m_ptOffset[1].x, (int)(ymax[1]/m_scaley[1]) + m_ptOffset[1].y,
			                painter);
		}

		//horizontal axis
		painter.setColor(m_xAxisColor);
		if(yo[0]>=ymin[0] && yo[0]<=ymax[0]) yp = yo[0];
		else if(yo[0]>ymax[0])		         yp = ymax[0];
		else			                     yp = ymin[0];
		canvas.drawLine((int)(xmin/m_scalex) +m_ptOffset[0].x, (int)(yp/m_scaley[0]) + m_ptOffset[0].y,
		                (int)(xmax/m_scalex) +m_ptOffset[0].x, (int)(yp/m_scaley[0]) + m_ptOffset[0].y,
		                painter);
	}


	void drawTitles(Canvas canvas)
	{
		//draws the x & y axis name
		float XPosXTitle, YPosXTitle, XPosYTitle, YPosYTitle;
		double yp;

		XPosXTitle =   5;
		YPosXTitle = -10;

		YPosYTitle = m_iTopMargin/3;

		Paint painter = m_Paint;
		painter.setTextSize(m_iTextSize);
		painter.setStrokeWidth(1);



		painter.setColor(m_TitleColor);

		if(yo[0]>=ymin[0] && yo[0]<=ymax[0]) yp = yo[0];
		else if(yo[0]>ymax[0])               yp = ymax[0];
		else                                 yp = ymin[0];
		canvas.drawText(m_XTitle,  
		                (int)(xmax/m_scalex)      + m_ptOffset[0].x + XPosXTitle,
		                (int)(yp  /m_scaley[0])   + m_ptOffset[0].y + YPosXTitle,
		                painter);

		XPosYTitle = -painter.measureText(m_YTitle[0], 0, m_YTitle[0].length())/2.0f;
		canvas.drawText(m_YTitle[0], 
		                m_ptOffset[0].x + (int)(xmin/m_scalex)   + XPosYTitle,
		                m_rCltRect.top +                         + YPosYTitle,
		                painter);

		if(m_bShowY2Axis)
		{
			XPosYTitle = -painter.measureText(m_YTitle[1], 0, m_YTitle[1].length())/2.0f;
			canvas.drawText(m_YTitle[1], 
	                        m_ptOffset[0].x + (int)(xmax/m_scalex)   + XPosYTitle,
	                        m_rCltRect.top +                         + YPosYTitle,
	                        painter);
		}
	}



	void drawXTicks(Canvas canvas)
	{
		double main, scaley, xt, yp, f1;
		int exp, TickSize, nx;
		float fmwidth, fmheight;

		if(Math.abs(xunit)<0.00000001) return;
		if(Math.abs(xmax-xmin)/xunit>30.0) return;

		Paint painter = m_Paint;

		scaley = m_scaley[0];
//		exp=0;
		
		String strLabel="10.4", strLabelExp;
		Rect bounds = new Rect();
		painter.getTextBounds(strLabel, 0, strLabel.length(), bounds);
//        fmwidth = painter.measureText(strLabel, 0, strLabel.length());
        fmheight = bounds.height();

		TickSize = 10;

		nx = (int)((xo-xmin)/xunit);
		xt = xo - nx*xunit;


		if(yo[0]>=ymin[0] && yo[0]<=ymax[0]) yp = yo[0];
		else if(yo[0]>ymax[0])               yp = ymax[0];
		else                                 yp = ymin[0];


		while(xt<=xmax*1.0001)
		{
			//Draw ticks
			if(xt>=xmin)
			{
				painter.setStrokeWidth(m_AxisWidth);
				painter.setColor(m_xAxisColor);
				canvas.drawLine( (int)(xt/m_scalex) + m_ptOffset[0].x,(int)(yp/scaley) +TickSize + m_ptOffset[0].y,
				                 (int)(xt/m_scalex) + m_ptOffset[0].x,(int)(yp/scaley)           + m_ptOffset[0].y,
				                 painter);


				painter.setStrokeWidth(1);
				painter.setColor(m_TitleColor);
				if(exp_x>=4 || exp_x<=-4)
				{
					main = xt;
					f1 = Math.abs(main);
					if(Math.abs(main)>PRECISION)
					{
						if(f1<1) exp = (int)Math.log10(f1)-1;
						else     exp = (int)Math.log10(f1);
						main = main/Math.pow(10.0,exp);
						strLabel = String.format("%5.1f 10",main);
						strLabelExp = String.format("%d", exp);
					}
					else
					{
//						exp = 0;
						strLabel = "0.0";
						strLabelExp = "";						
					}

					painter.setTextSize(m_iTextSize);
					fmwidth = painter.measureText(strLabel, 0, strLabel.length());
					canvas.drawText(strLabel, 
					               (int)(xt/m_scalex) - fmwidth/2               +m_ptOffset[0].x,
					               (int)(yp/scaley)   + TickSize*2 +fmheight    +m_ptOffset[0].y,
					               painter);
					
					painter.setTextSize((float)m_iTextSize/1.5f);
					canvas.drawText(strLabelExp, 
					                (int)(xt/m_scalex) + fmwidth/2                    +m_ptOffset[0].x,
					                (int)(yp/scaley)   + TickSize*2 +fmheight/4.0f    +m_ptOffset[0].y,
					                painter);
				}
				else
				{
					if (exp_x>=1)       strLabel = String.format("%5.0f",xt);
					else if (exp_x>=-1) strLabel = String.format("%6.1f",xt);
					else if (exp_x>=-2) strLabel = String.format("%6.2f",xt);
					else if (exp_x>=-3) strLabel = String.format("%6.3f",xt);
					else                strLabel = String.format("%g",xt);
					painter.setTextSize(m_iTextSize);
					fmwidth = painter.measureText(strLabel, 0, strLabel.length());
					canvas.drawText(strLabel, 
					                (int)(xt/m_scalex) - fmwidth/2            + m_ptOffset[0].x,
					                (int)(yp/scaley)   + TickSize*2 +fmheight + m_ptOffset[0].y,
					                painter);
				}
			}
			xt += xunit ;
		}
	}



	void drawYTicks(Canvas canvas, int iScale)
	{
		double scaley, xp, main, yt, f1;
		int exp;
		float TickSize, fmwidth, fmheight, fmheight4, fmheight34;
		
		if(Math.abs(xunit)<0.00000001) return;
		if(Math.abs(yunit[iScale])<0.00000001) return;
		if(Math.abs(ymax[iScale]-ymin[iScale])/yunit[iScale]>30.0) return;
		scaley = m_scaley[iScale];

		Paint painter = m_Paint;

//		exp = 0;

		String strLabel="10.4", strLabelExp;
		Rect bounds = new Rect();
		painter.getTextBounds(strLabel, 0, strLabel.length(), bounds);
        fmheight  = bounds.height();
		fmheight34 = (int)(fmheight*3.0f/4.0f);
		fmheight4 = (int)(fmheight/4);
	
		if(iScale==0)
		{
			TickSize =  -10;
		}
		else
		{
			TickSize =  10;
		}
		
		if(iScale==0) xp = xmin;
		else          xp = xmax;

		yt = yo[iScale]-(int)((yo[iScale]-ymin[iScale])*1.0001/yunit[iScale])*yunit[iScale];//one tick at the origin

		while(yt<=ymax[iScale]*1.0001)
		{
			//Draw ticks
			if(yt>=ymin[iScale])
			{
				painter.setStrokeWidth(m_AxisWidth);
				painter.setColor(m_yAxisColor[iScale]);
				canvas.drawLine((int)(xp/m_scalex)           +m_ptOffset[0].x, (int)(yt/scaley) + m_ptOffset[iScale].y,
				                (int)(xp/m_scalex)+TickSize  +m_ptOffset[0].x, (int)(yt/scaley) + m_ptOffset[iScale].y,
				                painter);


				painter.setStrokeWidth(1);
				painter.setColor(m_TitleColor);
				if(exp_y[iScale]>=3 || exp_y[iScale]<=-3)
				{
					main = yt;
					f1 = Math.abs(main);
					if(Math.abs(main)>PRECISION)
					{
						if(f1<1) exp = (int)Math.log10(f1)-1;
						else     exp = (int)Math.log10(f1);
						main = main/Math.pow(10.0,exp);
						strLabel = String.format("%5.1f 10",main);
						strLabelExp = String.format("%d", exp);
					}
					else
					{
//						exp = 0;
						strLabel = "0.0";
						strLabelExp = "";						
					}

					
					if(iScale==0) 
					{
						painter.setTextSize(m_iTextSize);
						fmwidth = painter.measureText(strLabel+strLabelExp);
						canvas.drawText(strLabel, 
								(int)(xp/m_scalex) - fmwidth + TickSize    + m_ptOffset[0].x,
								(int)(yt/scaley)                           + m_ptOffset[iScale].y,
								painter);

						fmwidth = painter.measureText(strLabelExp);
						painter.setTextSize((float)m_iTextSize/1.5f);
						if(exp_y[iScale]>=3)
						{
							canvas.drawText(strLabelExp,
									(int)(xp/m_scalex) - fmwidth + TickSize  + m_ptOffset[0].x,
									(int)(yt/scaley)   - fmheight34          + m_ptOffset[iScale].y,
									painter);
						}
						else
						{
							canvas.drawText(strLabelExp,
									(int)(xp/m_scalex) - fmwidth + TickSize      + m_ptOffset[0].x,
									(int)(yt/scaley)   - fmheight34              + m_ptOffset[iScale].y,
									painter);
						}
					}
					else
					{
						painter.setTextSize(m_iTextSize);
//						fmwidth = 0;
						canvas.drawText(strLabel, 
								(int)(xp/m_scalex)                         + m_ptOffset[0].x,
								(int)(yt/scaley)                           + m_ptOffset[iScale].y,
								painter);

						fmwidth = painter.measureText(strLabel);
						painter.setTextSize((float)m_iTextSize/1.5f);
						if(exp_y[iScale]>=3)
						{
							canvas.drawText(strLabelExp,
									(int)(xp/m_scalex) + fmwidth                   + m_ptOffset[0].x,
									(int)(yt/scaley)   - fmheight34                + m_ptOffset[iScale].y,
									painter);
						}
						else
						{
							canvas.drawText(strLabelExp,
									(int)(xp/m_scalex) + fmwidth      + m_ptOffset[0].x,
									(int)(yt/scaley)   - fmheight34   + m_ptOffset[iScale].y,
									painter);
						}
					}
				}
				else
				{
					if (exp_y[iScale]>=1)       strLabel = String.format("%3.0f", yt);
					else if (exp_y[iScale]>=-1) strLabel = String.format("%4.1f", yt);
					else if (exp_y[iScale]>=-2) strLabel = String.format("%6.2f", yt);
					else if (exp_y[iScale]>=-3) strLabel = String.format("%7.3f", yt);
					else                        strLabel = String.format(   "%g", yt);
					painter.setTextSize(m_iTextSize);
					if(iScale==0) fmwidth = painter.measureText(strLabel, 0, strLabel.length()) - TickSize;
					else          fmwidth = 0;
					canvas.drawText(strLabel, 
					                (int)(xp/m_scalex) - fmwidth + TickSize  + m_ptOffset[0].x,
					                (int)(yt/scaley)   + fmheight4           + m_ptOffset[iScale].y,
					                painter);
				}
			}
			yt += yunit[iScale];
		}
	}

	
	void drawXMajGrid(Canvas canvas)
	{
		double scaley = m_scaley[0];
		if(Math.abs(xunit)<0.00000001)     return;
		if(Math.abs(xmax-xmin)/xunit>30.0) return;

		float YMin, YMax;

		Paint painter = m_Paint;

		painter.setColor(m_XMajClr);
		painter.setStrokeWidth(m_XMajWidth);

		painter.setStyle(Paint.Style.STROKE);
		if(m_XMajStyle==1)      painter.setPathEffect(new DashPathEffect(new float[]{3,3}, 0)); //dot
		else if(m_XMajStyle==2) painter.setPathEffect(new DashPathEffect(new float[]{13,7}, 0)); //dash
		else                    painter.setPathEffect(null); //solid

		
		YMin = (int)(ymin[0]/scaley) + m_ptOffset[0].y;
		YMax = (int)(ymax[0]/scaley) + m_ptOffset[0].y;

		double xt = xo-(int)((xo-xmin)*1.0001/xunit)*xunit;//one tick at the origin
		while(xt<=xmax*1.001)
		{
			if(xt>=xmin)
			{
				canvas.drawLine((float)(xt/m_scalex) + m_ptOffset[0].x, YMin, 
				                (float)(xt/m_scalex) + m_ptOffset[0].x, YMax, 
				                painter);
			}
			xt += xunit;
		}
		painter.setPathEffect(null);
		
	}


	void drawYMajGrid(Canvas canvas, int iScale)
	{
		double scaley = m_scaley[iScale];
		if(Math.abs(yunit[iScale])<0.00000001) return;
		if(Math.abs(ymax[iScale]-ymin[iScale])/yunit[iScale]>30.0) return;

		int width = m_YMajWidth[iScale];
		if(m_YMajWidth[iScale]<=1) width = 1;

		Paint painter = m_Paint;

		painter.setColor(m_YMajClr[iScale]);
		
		painter.setStyle(Paint.Style.STROKE);
		if(m_YMajStyle[iScale]==1)      painter.setPathEffect(new DashPathEffect(new float[]{3,3}, 0)); //dot
		else if(m_YMajStyle[iScale]==2) painter.setPathEffect(new DashPathEffect(new float[]{13,7}, 0)); //dash
		else                            painter.setPathEffect(null); //solid

		painter.setStrokeWidth(width);

		double yt = yo[iScale]-(int)((yo[iScale]-ymin[iScale])*1.0001/yunit[iScale])*yunit[iScale];//one tick at the origin

		int XMin = Math.max((int)(xmin/m_scalex + m_ptOffset[0].x), m_rCltRect.left);
		int XMax = Math.min((int)(xmax/m_scalex + m_ptOffset[0].x), m_rCltRect.right);

		while(yt<=ymax[iScale]*1.0001)
		{
			if(yt>=ymin[iScale])
			{
				canvas.drawLine(XMin, (int)(yt/scaley)   + m_ptOffset[iScale].y, 
				                XMax, (int)(yt/scaley)   + m_ptOffset[iScale].y, painter);
			}
			yt += yunit[iScale] ;
		}
    	painter.setPathEffect(null);
	}
	

	void drawXMinGrid(Canvas canvas)
	{
		double scaley = m_scaley[0];
		if(Math.abs(xunit)<0.00000001) return;
		if(Math.abs(m_XMinorUnit)<0.00000001) return;
		if(Math.abs(xmax-xmin)/xunit>30.0) return;
		if(Math.abs(xmax-xmin)/m_XMinorUnit>100.0) return;
		int YMin, YMax;

		Paint painter = m_Paint;

		painter.setColor(m_XMinClr);
		//    	GridPen.setStyle(GetStyle(m_XMinStyle));
		painter.setStrokeWidth(m_XMinWidth);

		YMin = (int)(ymin[0]/scaley)+ m_ptOffset[0].y;
		YMax = (int)(ymax[0]/scaley)+ m_ptOffset[0].y;

		double xDelta = m_XMinorUnit;
		double xt = xo-(int)((xo-xmin)*1.0001/xDelta)*xDelta;//one tick at the origin


		while(xt<=xmax*1.001)
		{
			if(xt>=xmin)
			{
				canvas.drawLine((int)(xt/m_scalex) + m_ptOffset[0].x, YMin, 
				                (int)(xt/m_scalex) + m_ptOffset[0].x, YMax, 
				                painter);
			}
			xt += xDelta;
		}
	}


	void drawYMinGrid(Canvas canvas, int iScale)
	{
		double scaley = m_scaley[iScale];
		if(Math.abs(yunit[iScale])<0.00000001) return;
		if(Math.abs(m_YMinorUnit[iScale])<0.00000001) return;
		if(Math.abs(ymax[iScale]-ymin[iScale])/yunit[iScale]>30.0) return;
		if(Math.abs(ymax[iScale]-ymin[iScale])/m_YMinorUnit[iScale]>100.0) return;

		Paint painter = m_Paint;

		painter.setColor(m_YMinClr[iScale]);
		//    	GridPen.setStyle(GetStyle(m_YMinStyle));
		painter.setStrokeWidth(m_YMinWidth[iScale]);

		double yDelta = m_YMinorUnit[iScale];
		double yt = yo[iScale] - (int)((yo[iScale]-ymin[iScale])*1.0001/yDelta)*yDelta;//one tick at the origin
		int XMin = Math.max((int)(xmin/m_scalex + m_ptOffset[0].x), m_rCltRect.left);
		int XMax = Math.min((int)(xmax/m_scalex + m_ptOffset[0].x), m_rCltRect.right);

		while(yt<=ymax[iScale]*1.0001)
		{
			if(yt>=ymin[iScale])
			{
				canvas.drawLine(XMin, (int)(yt/scaley)   + m_ptOffset[iScale].y, XMax, 
						              (int)(yt/scaley)   + m_ptOffset[iScale].y, painter);
			}
			yt += yDelta ;
		}
	}


	void drawLegend(Canvas canvas, Point Place, int LegendColor)
	{
		Paint painter = m_Paint;

		String strong;

		float xl = (float)Place.x;
		float yl = (float)Place.y;
		float LegendSize;
		if(m_bLandscape) LegendSize = 47;
		else             LegendSize = 35;


		for (JCurve pCurve : m_oaCurves)
		{
			if(pCurve.isVisible())
			{
				strong = pCurve.title();
				if(pCurve.pointCount()>0 && strong.length()>0)//is there anything to draw ?
				{
					if(m_bLandscape)
					{
						//draw line
						xl = (float)Place.x;
						painter.setColor(pCurve.color());
						painter.setStrokeWidth(pCurve.width());
						canvas.drawLine(xl,              yl,
						                xl + LegendSize, yl,
						                painter);
						//draw point
						if(pCurve.pointsVisible())
						{
							canvas.drawCircle(xl+LegendSize/2, yl, m_pointRadius, painter);
						}
						
						xl += 1.2 * LegendSize;
	
						painter.setColor(LegendColor);
						painter.setStrokeWidth(1);
						canvas.drawText(strong,
						                xl, yl,
						                painter);

						//move to next line
						yl += painter.getTextSize()*1.5;
					}
					else
					{
						painter.setColor(pCurve.color());
						painter.setStrokeWidth(pCurve.width());
						canvas.drawLine(xl,              Place.y,
						                xl + LegendSize, Place.y,
						                painter);
						//draw point
						if(pCurve.pointsVisible())
						{
							canvas.drawCircle(xl+LegendSize/2, Place.y, m_pointRadius, painter);
						}

						xl += 1.2 * LegendSize;
	
						painter.setColor(LegendColor);
						painter.setStrokeWidth(1);
						canvas.drawText(strong,
						                xl, Place.y,
						                painter);
						xl += painter.measureText(strong)+21;
					}
				}
			}
		}
	}


	Point offset(int iScale)
	{
		return m_ptOffset[iScale];
	}
}











