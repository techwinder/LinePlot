package techwind.util.graphics;

public class Vector 
{
	public double x;
	public double y;
	public double z;

	//inline constructors
	public Vector()
	{
		x  = 0.0;
		y  = 0.0;
		z  = 0.0;
	}

	Vector(double xi, double yi, double zi)
	{
		x  = xi;
		y  = yi;
		z  = zi;
	}

	
	//inline methods
	void Copy(Vector V)
	{	
		x = V.x;
		y = V.y;
		z = V.z;
	}
	
	void Set(double x0, double y0, double z0)
	{	
		x = x0;
		y = y0;
		z = z0;
	}
	
	void Set(Vector V)
	{	
		x = V.x;
		y = V.y;
		z = V.z;
	}

	void Normalize()
	{
		double abs = VAbs();
		if(abs< 1.e-10) return;
		x/=abs;
		y/=abs;
		z/=abs;
	}
		
	double VAbs()
	{
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	double dot(Vector V)
	{	
		return x*V.x + y*V.y + z*V.z;
	}
	
	boolean IsSame(Vector V)
	{
		//used only to compare point positions
		return (V.x-x)*(V.x-x) + (V.y-y)*(V.y-y) + (V.z-z)*(V.z-z)<0.000000001;
	}

	void Translate(Vector T)
	{
		x += T.x;
		y += T.y;
		z += T.z;
	}

	void Translate(double tx, double ty, double tz)
	{
		x += tx;
		y += ty;
		z += tz;
	}

	int size() 
	{
		return 3;//dimension
	}


}
