package techwind.util.graphics;


public class Rectangle 
{
	public Rectangle()
	{
		left   = 0;
		right  = 0;
		top    = 0;
		bottom = 0;
	}

	Rectangle(double l, double t, double r, double b)
	{
		left   = l;
		right  = r;
		top    = t;
		bottom = b;
	}

	Rectangle(Vector TopLeft, Vector BottomRight)
	{
		left   = TopLeft.x;
		right  = BottomRight.x;
		top    = TopLeft.y;
		bottom = BottomRight.y;
	}

	void CopyRect(Rectangle pRect)
	{
		left   = pRect.left;
		right  = pRect.right;
		top    = pRect.top;
		bottom = pRect.bottom;
	}


	Rectangle(Rectangle Rect)
	{
		left   = Rect.left;
		right  = Rect.right;
		top    = Rect.top;
		bottom = Rect.bottom;
	}
	//		~CRectangle(void);


	boolean IsRectEmpty()
	{
		if(bottom==top && right==left) return true;
		else                           return false;
	}
	
	public boolean PtInRect(Vector pt)
	{
		if(left<pt.x && pt.x<right && bottom<pt.y && pt.y<top ) return true;
		return false;
	}
	double width(){return (right-left);}
	
	double height(){return(top-bottom);}
	
	public void SetRectEmpty(){left = right = top = bottom = 0;}
	
	void DeflateRect(double x, double y)
	{
		//DeflateRect adds units to the left and top and subtracts units from the right and bottom
		left   +=x;
		right  -=x;
		top    +=y;
		bottom -=y;
	};

	void DeflateRect(double l, double t, double r, double b)
	{
		//DeflateRect adds units to the left and top and subtracts units from the right and bottom
		left   +=l;
		right  -=r;
		top    +=t;
		bottom -=b;
	};

	void InflateRect(double x, double y)
	{
		//InflateRect subtracts units from the left and top and adds units to the right and bottom
		left   -=x;
		right  +=x;
		top    -=y;
		bottom +=y;
	}

	void InflateRect(double l, double t, double r, double b)
	{
		//InflateRect subtracts units from the left and top and adds units to the right and bottom
		left   -=l;
		right  +=r;
		top    -=t;
		bottom +=b;
	}

	void SetRect(double l, double t, double r, double b)
	{
		left   =l;
		right  =r;
		top    =t;
		bottom =b;
	}

	void SetRect(Rectangle Rect)
	{
		left   = Rect.left;
		right  = Rect.right;
		top    = Rect.top;
		bottom = Rect.bottom;
	}

	void NormalizeRect()
	{
		double tmp;
		if (left > right)
		{
			tmp = left;
			left = right;
			right = tmp;
		}
		if(bottom>top)
		{
			tmp = bottom;
			bottom = top;
			top = tmp;
		}
	};

	double left;
	double top;
	double right;
	double bottom;


}
