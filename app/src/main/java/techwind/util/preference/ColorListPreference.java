package techwind.util.preference;

import java.util.HashMap;
import java.util.Map;

import techwind.util.lineplot.R;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class ColorListPreference extends ListPreference
{
	Context s_Context;
	CharSequence[] m_entries;
	CharSequence[] m_entryValues;
	static public HashMap<String, Integer> s_colorMap = new HashMap<String, Integer>();

	public ColorListPreference(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		s_Context = context;
	}


	public ColorListPreference(Context context) 
	{
		super(context);
		s_Context = context;
	}


	/** Returns a color as an int from the color name
	 * 
	 * @param colorName : the color's name using the www standard  
	 * @return          : the color's int value
	 */
	public int colorFromName(String colorName)
	{
		int clr = s_Context.getResources().getIdentifier(colorName,  "color", s_Context.getPackageName());
		return s_Context.getResources().getColor(clr);
	}

	
	@Override
	protected void onPrepareDialogBuilder(Builder builder) 
	{
		String[] colors = s_Context.getResources().getStringArray(R.array.curveColorStringArray);
		for (int ic=0; ic<colors.length; ic++)
		{
			s_colorMap.put(colors[ic], colorFromName(colors[ic]));
		}
		
		int index = findIndexOfValue(getSharedPreferences().getString(getKey(), "1"));
		builder.setNegativeButton(null, null);

		m_entries = getEntries();
		m_entryValues = getEntryValues();

		ListAdapter listAdapter = (ListAdapter) new ColorArrayAdapter(getContext(),
		                                                              R.layout.colorlistrow,
		                                                              this.getEntryValues(),
		                                                              index,
		                                                              this);

		builder.setAdapter(listAdapter, this);
		super.onPrepareDialogBuilder(builder);
	}

	
	public void setResult(String colorName)
	{
		Editor edit = getSharedPreferences().edit();
		edit.putString(this.getKey(), colorName);
		edit.commit();
		this.getDialog().dismiss();
	}
	
	
	public class ColorArrayAdapter extends ArrayAdapter<CharSequence> 
	{
		int index;
		ColorListPreference clp;

		public ColorArrayAdapter(Context context, int textViewResourceId, 
		                         CharSequence[] objects, int selectedColor, ColorListPreference clp) 
		{
			super(context, textViewResourceId, objects);
			index = selectedColor;
			this.clp = clp;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			final int pos = position;

			LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
			
			View row = inflater.inflate(R.layout.colorlistrow, parent, false);
			row.setOnClickListener(new Button.OnClickListener()
			{  
		        public void onClick(View v)
	            {
					clp.setResult((String)m_entries[pos]);
	            }
			});
			
			
			TextView tv = (TextView) row.findViewById(R.id.colorName);
			Map<String, Integer> map = s_colorMap;
			for (Map.Entry<String, Integer> entry : map.entrySet()) 
			{
				if(entry.getKey().equals(m_entries[position]))
				{
					tv.setText(entry.getKey());
					tv.setTextColor(entry.getValue());
					break;
				}
			}

			//set checkbox
			RadioButton tb = (RadioButton) row.findViewById(R.id.ckbox);
			if (position == index)
			{
				tb.setChecked(true);
			}
			tb.setClickable(false);

			return row;
		}
	}
}
