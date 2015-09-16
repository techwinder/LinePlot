package techwind.util.graph;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;

import techwind.util.lineplot.PrefsAxisActivity;

public class AndroidGraphView extends View 
{
	Context s_Context;
	//	private static final String TAG="FTS/AndroidGraphView";
	GestureDetector m_GestureDetector;
	ScaleGestureDetector m_ScaleDetector;

    private final Long NAPTIME = (long)500; /** the time after a scale event before a gesture detector is considered*/
    private Long m_lastScaleEvent;

	//common to all graphs
	static public int s_iTextSize;
	static public String s_fontType="sans-serif", s_fontStyle="normal"; 
	static public boolean s_bHdwAcceleration = false;
	static public boolean s_bLandscape = false;

    float m_leftBand, m_rightBand;

	//graph specific
	public  JGraph s_Graph = new JGraph();
	private Rect s_drawRect;
    public boolean s_bAlignYZero=true;
	public boolean[] s_bGrid= {true, true, true};
    public int[] s_iGridStyle = {0, 0, 0}; //0 is x-axis, 1 is y-left-axis, 2 is y-right-axis
	public int[] s_iGridWidth = {1,1,1};
    public int[] s_iGridColor = {7, 3, 9};
	public int[] s_iGraphMargin = {23, 23, 23};

	public ArrayList<Boolean> s_bShowPoints  = new ArrayList<Boolean>();
	public ArrayList<Boolean> s_bShowCurve   = new ArrayList<Boolean>();
	public ArrayList<Integer> s_iyAxis       = new ArrayList<Integer>();
	public ArrayList<Integer> s_ixVariable   = new ArrayList<Integer>();
	public ArrayList<Integer> s_iCurveStyle  = new ArrayList<Integer>();
	public ArrayList<Integer> s_iCurveWidth  = new ArrayList<Integer>();
	public ArrayList<String>  s_curveColor   = new ArrayList<String>();


	public AndroidGraphView(Context context) 
	{
		super(context);
	}

	public AndroidGraphView(Context context, DisplayMetrics dm) 
	{
		super(context);

		s_Context = context;

		m_GestureDetector = new GestureDetector(context, new GraphGestureListener(this));
		m_ScaleDetector = new ScaleGestureDetector(context, new GraphScaleListener(this));

		//TODO : use display metrics to adjust font size depending on screen capability
		if(dm!=null)
		{
//			double density =  dm.density;
			int screenWidth = dm.widthPixels;
//			int screenHeight = dm.heightPixels;

            m_leftBand = screenWidth /5;
            m_rightBand = screenWidth * 4/5;
		}

		s_iGraphMargin = new int[] {73,73,73,73};

		s_drawRect = new Rect();

		s_Graph.setAutoXLimits(true);
		s_Graph.setXUnit(2.0);
		s_Graph.setXMin(-1.0);
		s_Graph.setXMax( 1.0);
		s_Graph.setYMin(0, 0.000);
		s_Graph.setYMax(0, 0.001);
		s_Graph.setYMin(1, 0.000);
		s_Graph.setYMax(1, 0.001);
		s_Graph.setXMajGrid(true, Color.rgb(120,120,120),2,1);
		s_Graph.setXMinGrid(false, true, Color.rgb(80,80,80),2, 1, 100.0);
		s_Graph.setYMajGrid(0, true, Color.rgb(120,120,120),2,1);
		s_Graph.setYMinGrid(0, false, true, Color.rgb(80,80,80),2, 1, 0.1);
		s_Graph.setYMajGrid(1, true, Color.rgb(120,120,120),2,1);
		s_Graph.setYMinGrid(1, false, true, Color.rgb(80,80,80),2, 1, 0.1);
		s_Graph.setGraphType(JGraph.NOT_EXTENSIBLE);
		s_Graph.setTextSize(23); //TODO : set text size as a function of screen metrics;
		s_Graph.setMargins(s_iGraphMargin);
		s_Graph.DeleteCurves();

        m_lastScaleEvent = System.currentTimeMillis();

/*		setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{				
				// Let the ScaleGestureDetector inspect all events.
				m_ScaleDetector.onTouchEvent(event);
				return m_GestureDetector.onTouchEvent(event);
			}
		});*/

		//disable hardware graphic acceleration to enable dashed lines
		//Cf. android issue 29944 
		//		if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB)
	}

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean result = m_ScaleDetector.onTouchEvent(event);
        // result is always true - android bug

        // In case of scaling, we need to avoid subsequent scrolling if the user does not
        // remove both fingers at the same time.
        // Android does not provide the information about how many fingers are still down
        // so we just introduce a latence time at the end of scaling

        if (!m_ScaleDetector.isInProgress() && System.currentTimeMillis()>m_lastScaleEvent+NAPTIME)
        {
            // if no scaling is performed, check for other gestures (fling, long tab, etc.)
            result = m_GestureDetector.onTouchEvent(event);
        }

        return result ? result : super.onTouchEvent(event);
    }

	@Override
	protected void onDraw(Canvas canvas) 
	{
		canvas.getClipBounds(s_drawRect);
		if(s_bLandscape) s_Graph.setLegendPosition(s_drawRect.right-s_Graph.rightMargin()*2/3,
		                                           s_Graph.topMargin());
		else             s_Graph.setLegendPosition(s_drawRect.left +s_Graph.leftMargin(),
		                                           s_drawRect.height()-s_Graph.bottomMargin()/4);
		s_Graph.drawGraph(s_drawRect, canvas);
	}

	
	public void setLayerType()
	{
		if(s_bHdwAcceleration)
		{
			setLayerType(View.LAYER_TYPE_HARDWARE, null);			
		}
		else
		{
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	/** Creates an empty curve
	 * 
	 * @return a pointer to the created curve 
	 */
	public JCurve addCurve()
	{
		return s_Graph.addCurve();
	}


	/** Creates a curve from the data in array x and y
	 * @param x : the array of x-values
	 * @param y : the array of y-values
	 * 
	 * @return a pointer to the created curve 
	 */
	public JCurve addCurve(double[] x, double[]y)
	{
		JCurve aCurve = s_Graph.addCurve();
		for(int i=0; i<Math.min(x.length, y.length); i++)
		{
			aCurve.addPoint(x[i], y[i]);
		}
		return aCurve;
	}


	/** Creates a curve from the data in array x and y
	 * @param x : the array of x-values
	 * @param y : the array of y-values
	 * 
	 * @return a pointer to the created curve 
	 */
	public JCurve addCurve(ArrayList<Double> x, ArrayList<Double>y)
	{
		JCurve aCurve = s_Graph.addCurve();
		for(int i=0; i<Math.min(x.size(), y.size()); i++)
		{
			aCurve.addPoint(x.get(i), y.get(i));		
		}
		return aCurve;
	}


	/** Creates a curve from the data in array x and y
	 * @param y : the array of y-values
	 * 
	 * @return a pointer to the created curve 
	 */
	public JCurve addCurve( ArrayList<Double>y)
	{
		JCurve aCurve = s_Graph.addCurve();
		for(int i=0; i< y.size(); i++)
		{
			aCurve.addPoint((double)i, y.get(i));
		}
		return aCurve;
	}


	public void setCurvePoints(int iCurve, double[]x, double[]y )
	{
		JCurve pCurve = s_Graph.curve(iCurve);
		if(pCurve==null) return;

		for(int i=0; i<Math.min(x.length, y.length); i++)
		{
			pCurve.addPoint(x[i], y[i]);
		}
	}


	public void setCurvePoints(int iCurve, ArrayList<Double> x, ArrayList<Double>y)
	{
		JCurve pCurve = s_Graph.curve(iCurve);
		if(pCurve==null) return;

		for(int i=0; i<Math.min(x.size(), y.size()); i++)
		{
			pCurve.addPoint(x.get(i), y.get(i));
		}
	}

	public void setScales()
	{
//		s_Graph.setXMin(s_Min[0]);
//		s_Graph.setXMax(s_Max[0]);
//		s_Graph.setAutoXLimits(s_bAutoScale[0]);
		s_Graph.setAutoXUnit();

//        s_Graph.setYMin(0, s_Min[1]);
//        s_Graph.setYMax(0, s_Max[1]);
//        s_Graph.setAutoYLimits(0, s_bAutoScale[1]);
        s_Graph.setAutoYUnit(0);

        s_Graph.setAlignYZero(s_bAlignYZero);
		if(s_bAlignYZero)
		{
//			s_Graph.setAutoLimits(true);
//            s_bAutoScale[2] = true;
            s_Graph.m_bAutoY[1] = true;
            s_Graph.resetYLimits(1);
        }
		else
		{
//			s_Graph.setYMin(1, s_Min[2]);
//			s_Graph.setYMax(1, s_Max[2]);
//			s_Graph.setAutoYLimits(1, s_bAutoScale[2]);
			s_Graph.setAutoYUnit(1);
		}
	}

	public JCurve getCurve(int iCurve)
	{
		return s_Graph.curve(iCurve);
	}

	public void setCurveTitle(int iCurve, String curveTitle)
	{
		JCurve pCurve = s_Graph.curve(iCurve);
		if(pCurve==null) return;
		pCurve.setTitle(curveTitle);
	}

	public void setYAxisTitle(String yTitle, String y1Title)
	{
		s_Graph.setYTitle(0, yTitle );
		s_Graph.setYTitle(1, y1Title);
	}

	public void resetCurves()
	{
		s_Graph.resetCurves();
	}

	public void resetCurve(int iCurve)
	{
		s_Graph.resetCurve(iCurve);
	}


	public void deleteCurves()
	{
		s_Graph.DeleteCurves();
	}


	public void resetAutoScales()
	{
		s_Graph.setAutoLimits(true);
	}


	public void setCurveStyle()
	{
		//		String[] clr = getResources().getStringArray(R.array.curveColorStringArray);

		s_Graph.m_bShowY2Axis = false;
		for(int is=0; is<s_Graph.curveCount(); is++)
		{
			JCurve pCurve = s_Graph.curve(is);

			if(pCurve!=null)
			{
				if(s_bShowCurve.get(is)&& s_iyAxis.get(is)==1)
				{
					s_Graph.m_bShowY2Axis = true;
				}

				pCurve.setAxis(s_iyAxis.get(is));
				pCurve.setVisible(s_bShowCurve.get(is));
				pCurve.showPoints(s_bShowPoints.get(is));
				pCurve.setColor(colorFromName(s_curveColor.get(is)));
				pCurve.setStyle(s_iCurveStyle.get(is));
				pCurve.setWidth(s_iCurveWidth.get(is));
			}
		}
	}

	/** Returns a color as an int from the color name
	 * 
	 * @param colorName : the color's name using the www standard  
	 * @return          : the color's int value
	 */
	public int colorFromName(String colorName)
	{
		int clr  = getResources().getIdentifier(colorName, "color", s_Context.getPackageName());
        return getResources().getColor(clr);
	}


	public void setLandscapeMode(boolean bLandscape)
	{
		s_bLandscape = bLandscape;
		s_Graph.setMargins(s_iGraphMargin);
		s_Graph.setLandscape(bLandscape);		
		if(bLandscape) 
		{
			s_Graph.setRightMargin(2*s_iGraphMargin[1]);
		}
		else 
		{
			s_Graph.setBottomMargin(Math.max(s_Graph.textSize()*3, s_iGraphMargin[3]));
		}
	}


	public void setGraphStyle()
	{
		Typeface type;
		if(s_fontStyle.equalsIgnoreCase("bold"))
		{
			type = Typeface.create(s_fontType, Typeface.BOLD);
		} 
		else if(s_fontStyle.equalsIgnoreCase("italic"))
		{
			type = Typeface.create(s_fontType, Typeface.ITALIC);
		} 
		else if(s_fontStyle.equalsIgnoreCase("bold-italic"))
		{
			type = Typeface.create(s_fontType, Typeface.BOLD_ITALIC);
		}
		else
		{
			type = Typeface.create(s_fontType, Typeface.NORMAL);
		}

		s_Graph.setFontType(type);
		//leave space for legend

		s_Graph.setTextSize(s_iTextSize);		

		s_Graph.setXAxisColor(s_iGridColor[0]);
		s_Graph.setYAxisColor(0, s_iGridColor[1]);
		s_Graph.setYAxisColor(1, s_iGridColor[2]);

		s_Graph.setXMajGrid(s_bGrid[0], s_iGridColor[0], s_iGridStyle[0], s_iGridWidth[0]);
		s_Graph.setYMajGrid(0, s_bGrid[1], s_iGridColor[1], s_iGridStyle[1], s_iGridWidth[1]);
		s_Graph.setYMajGrid(1, s_bGrid[2], s_iGridColor[2], s_iGridStyle[2], s_iGridWidth[2]);
	}




    public void onAxisSettings(int iScale)
    {
        Intent settingsActivity = new Intent(s_Context, PrefsAxisActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("iScale", iScale);
        settingsActivity.putExtras(bundle);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(s_Context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("xAxisManualScale", !s_Graph.isAutoX());
        editor.putBoolean("y0AxisManualScale",!s_Graph.isAutoY(0));
        editor.putBoolean("y1AxisManualScale", !s_Graph.isAutoY(1));
        editor.putString("xMinAxis",  String.format("%f", s_Graph.xMin()));
        editor.putString("y0MinAxis", String.format("%f", s_Graph.yMin(0)));
        editor.putString("y1MinAxis", String.format("%f", s_Graph.yMin(1)));
        editor.putString("xMaxAxis",  String.format("%f", s_Graph.xMax()));
        editor.putString("y0MaxAxis", String.format("%f", s_Graph.yMax(0)));
        editor.putString("y1MaxAxis", String.format("%f", s_Graph.yMax(1)));
        editor.commit();

        s_Context.startActivity(settingsActivity, bundle);
    }



    private DecelerateInterpolator animateInterpolator;
    private long startTime, endTime;
    private float totalAnimDx, totalAnimDy0, totalAnimDy1;
    float percentDistance;

    public void onAnimateMove(float dx, float dy0, float dy1, long duration)
    {
        animateInterpolator = new DecelerateInterpolator();
        startTime = System.currentTimeMillis();
        endTime = startTime + duration;
        totalAnimDx = dx;
        totalAnimDy0 = dy0;
        totalAnimDy1 = dy1;

        percentDistance = 0.0f;

        post(new Runnable() {
            @Override
            public void run()
            {
                onAnimateStep();
            }
        });
    }


    private void onAnimateStep()
    {
        long curTime = System.currentTimeMillis();
        float percentTime = (float) (curTime - startTime) / (float) (endTime-startTime);
        float newPercentDistance = animateInterpolator.getInterpolation(percentTime);
        float curDx  = -(newPercentDistance-percentDistance) * totalAnimDx;
        float curDy0 = -(newPercentDistance-percentDistance) * totalAnimDy0;
        float curDy1 = -(newPercentDistance-percentDistance) * totalAnimDy1;
        percentDistance = newPercentDistance;
        onMove(curDx, curDy0, curDy1);
//		String strange = String.format("percent=%11.7f   dx=%11.7f    dy=%11.7f", percentDistance, curDx, curDy);
//		Log.i(TAG, strange);

        if (percentTime < 1.0f)
        {
            post(new Runnable()
            {
                @Override
                public void run()
                {
                    onAnimateStep();
                }
            });
        }
    }


    public void onMove(float dx, float dy0, float dy1)
    {
        double xmin, xmax, ymin, ymax;

        s_Graph.setAutoLimits(false);

        double deltaX = dx * s_Graph.m_scalex;
        double deltaY0 = dy0 * s_Graph.m_scaley[0];
        double deltaY1 = dy1 * s_Graph.m_scaley[1];

        xmin = s_Graph.xMin()+deltaX;
        xmax = s_Graph.xMax()+deltaX;

        ymin = s_Graph.yMin(0) + deltaY0;
        ymax = s_Graph.yMax(0) + deltaY0;
        s_Graph.setViewPort(0, xmin, xmax, ymin, ymax);

        ymin = s_Graph.yMin(1) + deltaY1;
        ymax = s_Graph.yMax(1) + deltaY1;
        s_Graph.setViewPort(1, xmin, xmax, ymin, ymax);

        invalidate();
    }



	public double xMin()               { return s_Graph.xMin();}
	public double xMax()               { return s_Graph.xMax();}
	public double yMin(int iScale)     { return s_Graph.yMin(iScale);}
	public double yMax(int iScale)     { return s_Graph.yMax(iScale);}
	public boolean isXAuto()           { return s_Graph.isAutoX();}
	public boolean isYAuto(int iScale) { return s_Graph.isAutoY(iScale);}
	public int curveCount()            { return s_Graph.curveCount();}

	private class GraphGestureListener implements OnGestureListener
	{
		int m_tapCount=0;
		long m_firstTapTime;
		static final int DOUBLE_TAP_TIME = 500; //ms

        private static final String TAG="GraphGestureListener";

		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;
		float lastX, lastY;

        private AndroidGraphView m_gView;

        public GraphGestureListener(View v)
        {
            m_gView= (AndroidGraphView)v;

        }

		@Override
		public boolean onDown(MotionEvent event) 
		{
			lastX = event.getX();
			lastY = event.getY();

			if(System.currentTimeMillis() - m_firstTapTime>DOUBLE_TAP_TIME)
			{
				//too late for a double tap
				m_tapCount = 0;
			}

			m_tapCount++;
			if(m_tapCount==1)
			{
				m_firstTapTime=System.currentTimeMillis();
			}
			else if(m_tapCount>2) m_tapCount=0;

			return true;// do not return false to capture scroll and fling...
		}

		@Override
		public boolean onSingleTapUp(MotionEvent evUp) 
		{
			// we count the double tap time once the user has lifted his finger the second time
			if(m_tapCount>=2 && (System.currentTimeMillis() - m_firstTapTime<=DOUBLE_TAP_TIME))
			{
//				Toast.makeText(m_Context, "Double tap "+evUp.getX()+" "+evUp.getY(), Toast.LENGTH_SHORT).show();
				//we translate the graph to this point
				double xmin, xmax, ymin0, ymax0, ymin1, ymax1;

				//define the translations
				double tx =  s_Graph.ClientTox(evUp.getX())   - s_Graph.ClientTox(s_drawRect.centerX());
				double ty0 = s_Graph.ClientToy(0, evUp.getY())- s_Graph.ClientToy(0, s_drawRect.centerY());
				double ty1 = s_Graph.ClientToy(1, evUp.getY())- s_Graph.ClientToy(1, s_drawRect.centerY());

				s_Graph.setAutoLimits(false);

				//View shift
				xmin = s_Graph.xMin() + tx;
				xmax = s_Graph.xMax() + tx;				
				ymin0 = s_Graph.yMin(0) + ty0;
				ymax0 = s_Graph.yMax(0) + ty0;
				ymin1 = s_Graph.yMin(1) + ty1;
				ymax1 = s_Graph.yMax(1) + ty1;
				s_Graph.setViewPort(0, xmin, xmax, ymin0, ymax0);
				s_Graph.setViewPort(1, xmin, xmax, ymin1, ymax1);
				m_gView.invalidate();

				m_tapCount=0;
			}
            else
            {
                if(evUp.getX()<m_leftBand )
                {
                    m_gView.onAxisSettings(1);
                }
                else if(evUp.getX()>m_rightBand)
                {
                    m_gView.onAxisSettings(2);
                }
                else
                {
                    //detect if the user has tapped on the x-axis
                    double y = s_Graph.ClientToy(0, evUp.getY());
                    if(Math.abs(y)<(s_Graph.xMax()-s_Graph.xMin()/10.0)) m_gView.onAxisSettings(0);

                }
            }

			return false;
		}


		@Override
		public void onShowPress(MotionEvent arg0) 
		{
		}

		@Override
		public boolean onScroll(MotionEvent ev1, MotionEvent ev2, float distanceX,	float distanceY)
		{
            // we translate the curves inside the graph
            double xu, yu, x1, y1, xmin, xmax, ymin, ymax;

            if(lastX<m_leftBand && ev2.getX()<m_leftBand)
            {
                s_Graph.setAutoYLimits(0, false);

                xmin = s_Graph.xMin();
                xmax = s_Graph.xMax();

                y1 = s_Graph.ClientToy(0, lastY) ;
                yu = s_Graph.ClientToy(0, ev2.getY());
                ymin = s_Graph.yMin(0) + y1-yu;
                ymax = s_Graph.yMax(0) + y1-yu;
                s_Graph.setViewPort(0, xmin, xmax, ymin, ymax);

            }
            else if(lastX>m_rightBand && ev2.getX()>m_rightBand)
            {
                s_Graph.setAutoYLimits(1, false);

                xmin = s_Graph.xMin();
                xmax = s_Graph.xMax();

                if(s_bAlignYZero)
                {
                    y1 = s_Graph.ClientToy(0, lastY) ;
                    yu = s_Graph.ClientToy(0, ev2.getY());
                    ymin = s_Graph.yMin(0) + y1-yu;
                    ymax = s_Graph.yMax(0) + y1-yu;
                    s_Graph.setViewPort(0, xmin, xmax, ymin, ymax);
                }
                else
                {
                    y1 = s_Graph.ClientToy(1, lastY) ;
                    yu = s_Graph.ClientToy(1, ev2.getY());
                    ymin = s_Graph.yMin(1) + y1-yu;
                    ymax = s_Graph.yMax(1) + y1-yu;
                    s_Graph.setViewPort(1, xmin, xmax, ymin, ymax);
                }
            }
            else
            {
                s_Graph.setAutoLimits(false);

                x1 = s_Graph.ClientTox(lastX) ;
                xu = s_Graph.ClientTox(ev2.getX());
                xmin = s_Graph.xMin() + x1-xu;
                xmax = s_Graph.xMax() + x1-xu;

                y1 = s_Graph.ClientToy(0, lastY) ;
                yu = s_Graph.ClientToy(0, ev2.getY());
                ymin = s_Graph.yMin(0) + y1-yu;
                ymax = s_Graph.yMax(0) + y1-yu;
                s_Graph.setViewPort(0, xmin, xmax, ymin, ymax);

                y1 = s_Graph.ClientToy(1, lastY) ;
                yu = s_Graph.ClientToy(1, ev2.getY());
                ymin = s_Graph.yMin(1) + y1-yu;
                ymax = s_Graph.yMax(1) + y1-yu;
                s_Graph.setViewPort(1, xmin, xmax, ymin, ymax);
            }

            m_gView.invalidate();

            lastX= ev2.getX();
            lastY= ev2.getY();

            return true;
		}

		@Override
		public void onLongPress(MotionEvent arg0) 
		{
			//set auto
			s_Graph.setAutoXLimits(true);
			s_Graph.setAutoXUnit();
			s_Graph.setAutoYLimits(0, true);
			s_Graph.setAutoYUnit(0);
			s_Graph.setAutoYLimits(1, true);
			s_Graph.setAutoYUnit(1);
            m_gView.invalidate();
		}


        @Override
        public boolean onFling(MotionEvent ev1, MotionEvent ev2, float velocityX, float velocityY)
        {
            if(Math.abs(velocityX)<SWIPE_THRESHOLD_VELOCITY) return false;

            if(Math.abs(ev1.getX()-ev2.getX())>SWIPE_MIN_DISTANCE || Math.abs(ev1.getY()-ev2.getY())>SWIPE_MIN_DISTANCE )
            {
                final float distanceTimeFactor = 0.4f;
                final float totalDx = (distanceTimeFactor * velocityX/2);
                final float totalDy = (distanceTimeFactor * velocityY/2);

                if(ev1.getX()<m_leftBand && ev2.getX()<m_leftBand)
                {
                    s_Graph.setAutoYLimits(0, false);
                    m_gView.onAnimateMove(totalDx, totalDy, 0.0f, (long) (1000 * distanceTimeFactor));
                }
                else if(ev1.getX()>m_rightBand && ev2.getX()>m_rightBand)
                {
                    s_Graph.setAutoYLimits(1,false);
                    m_gView.onAnimateMove(totalDx, 0.0f, totalDy, (long) (1000 * distanceTimeFactor));
                }
                else
                {
                    s_Graph.setAutoLimits(false);

                    m_gView.onAnimateMove(totalDx, totalDy, totalDy, (long) (1000 * distanceTimeFactor));
                }

                return false;
            }
            return true;
        }
	}
	

	private class GraphScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener 
	{
		private View m_View;
		public GraphScaleListener(View v)
		{
			m_View = v;
		}
		@Override
		public boolean onScale(ScaleGestureDetector detector) 
		{
			float mScaleFactor = detector.getScaleFactor();

			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

			if (Math.abs((detector.getCurrentSpanX() - detector.getCurrentSpanY())/detector.getCurrentSpan())<0.25)
			{
				//zoom both
				s_Graph.setAutoLimits(false);
				s_Graph.setAutoXUnit();
				s_Graph.setAutoYUnit(0);
				s_Graph.setAutoYUnit(1);
				s_Graph.scale(1./mScaleFactor);
			}
			else if (detector.getCurrentSpanX()>detector.getCurrentSpanY())
			{
				//zoom x scale
				s_Graph.setAutoXLimits(false);
				s_Graph.setAutoXUnit();
				s_Graph.scaleX(1./mScaleFactor);
			}
			else 
			{
				//zoom y scale
				s_Graph.setAutoYLimits(0, false);
				s_Graph.setAutoYLimits(1, false);
				s_Graph.setAutoYUnit(0);
				s_Graph.setAutoYUnit(1);
				s_Graph.scaleY(0, 1./mScaleFactor);
				s_Graph.scaleY(1, 1./mScaleFactor);
			}

            m_lastScaleEvent = System.currentTimeMillis();

			m_View.invalidate();
			return true;
		}
	}
}
