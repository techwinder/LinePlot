
/****************************************************************************

 GraphPrefsActivity Class
 Copyright (C) 2013 Andre Deperrois adeperrois@xflr5.com

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 *****************************************************************************/

package techwind.util.lineplot;

import techwind.util.graph.AndroidGraphView;
import techwind.util.preference.ColorListPreference;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;


public class PrefsVarActivity extends PreferenceActivity
{
    //	private static final String TAG = "fts/PrefsVarActivity";
    public static final String VAR_INDEX = "VAR_INDEX";
    static int m_Index = -1;
    static public AndroidGraphView s_pGraphView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        s_pGraphView = MainActivity.s_graphView;

        m_Index = getIntent().getIntExtra(VAR_INDEX, -1);
        if(m_Index<0)
        {
            setResult(RESULT_CANCELED);
            finish();
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new CurvePrefsFragment()).commit();
    }


    static public class CurvePrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
    {
        PreferenceCategory m_varPrefCat;
        CheckBoxPreference m_showLinePref, m_showPointsPref ;
        ListPreference m_styleListPref, m_widthListPref,m_axisListPref, m_xListPref;
        ColorListPreference m_colorListPref;
        String[] m_varNamesArray;
        String[] m_xAxisNamesArray = new String[]{"index"};

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            PreferenceManager prefMgr = getPreferenceManager();
            prefMgr.setSharedPreferencesName(MainActivity.s_filePrefsName);
            prefMgr.setSharedPreferencesMode(MODE_PRIVATE);


            m_varNamesArray = MainActivity.s_channelName.toArray(new String[MainActivity.s_channelName.size()]);
            int length = m_varNamesArray.length+1;
            m_xAxisNamesArray = new String[length];
            m_xAxisNamesArray[0]="index";
            for(int pos=0; pos<m_varNamesArray.length; pos++)
            {
                m_xAxisNamesArray[pos+1] = m_varNamesArray[pos];
            }

            addPreferences(m_Index);
            setValues(m_Index);

            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++)
            {
                initSummary(getPreferenceScreen().getPreference(i), m_Index);
            }
        }


        private void addPreferences(int index)
        {
            SharedPreferences sharedPrefs = getActivity().getBaseContext().getSharedPreferences(MainActivity.s_filePrefsName, MODE_PRIVATE);
            PreferenceScreen prefScreen = getPreferenceManager().createPreferenceScreen(getActivity());

            // var preferences header
            m_varPrefCat = new PreferenceCategory(getActivity());
            m_varPrefCat.setTitle(getString(R.string.Variable)+" "+m_varNamesArray[index]);
            prefScreen.addPreference(m_varPrefCat);

            // visibility
            m_showLinePref = new CheckBoxPreference(getActivity());
            m_showLinePref.setKey("varVisibility"+index);
            m_showLinePref.setTitle(R.string.varShowTitle);
            m_showLinePref.setChecked(sharedPrefs.getBoolean("varVisibility"+index, true));
            m_showLinePref.setDefaultValue(true);
            m_varPrefCat.addPreference(m_showLinePref);

            // visibility
            m_showPointsPref = new CheckBoxPreference(getActivity());
            m_showPointsPref.setKey("varShowPoints"+index);
            m_showPointsPref.setTitle(R.string.varShowPointsTitle);
            m_showPointsPref.setChecked(sharedPrefs.getBoolean("varShowPoints"+index, false));
            m_showPointsPref.setDefaultValue(false);
            m_varPrefCat.addPreference(m_showPointsPref);

            //x-axis serie
            m_xListPref = new ListPreference(getActivity());
            m_xListPref.setEntries(m_xAxisNamesArray);
            m_xListPref.setEntryValues(m_xAxisNamesArray);
            m_xListPref.setDialogTitle(R.string.varDialogTitle);
            m_xListPref.setKey("xSelPreference"+index);
            m_xListPref.setTitle(R.string.xSelPrefTitle);
            m_xListPref.setDefaultValue("index");
            m_varPrefCat.addPreference(m_xListPref);


            //y-axis selection
            String[] axisArray = new String[] {"Left axis", "Right axis"};
            m_axisListPref = new ListPreference(getActivity());
            m_axisListPref.setEntries(axisArray);
            m_axisListPref.setEntryValues(axisArray);
            m_axisListPref.setDialogTitle(R.string.axisSelection);
            m_axisListPref.setKey("varAxis"+index);
            m_axisListPref.setDefaultValue("Left axis");
            m_axisListPref.setTitle("y axis selection");
            m_varPrefCat.addPreference(m_axisListPref);

            //curve style
            m_styleListPref = new ListPreference(getActivity());
            m_styleListPref.setEntries(R.array.lineStyleStringArray);
            m_styleListPref.setEntryValues(R.array.lineStyleStringArray);
            m_styleListPref.setDialogTitle(R.string.varWidthDialogTitle);
            m_styleListPref.setKey("varStyle"+index);
            m_styleListPref.setTitle(R.string.lineStyleTitle);
            m_styleListPref.setDefaultValue("Solid");
            m_varPrefCat.addPreference(m_styleListPref);

            //curve width
            m_widthListPref = new ListPreference(getActivity());
            m_widthListPref.setEntries(R.array.curveWidthStringArray);
            m_widthListPref.setEntryValues(R.array.curveWidthStringArray);
            m_widthListPref.setDialogTitle(R.string.varWidthDialogTitle);
            m_widthListPref.setKey("varWidth"+index);
            m_widthListPref.setTitle(R.string.lineWidthTitle);
            m_widthListPref.setDefaultValue("1");
            m_varPrefCat.addPreference(m_widthListPref);


            //curve color
            m_colorListPref = new ColorListPreference(getActivity());
            m_colorListPref.setEntries(R.array.curveColorStringArray);
            m_colorListPref.setEntryValues(R.array.curveColorStringArray);
            m_colorListPref.setDialogTitle(R.string.varColorDialogTitle);
            m_colorListPref.setKey("varColor"+index);
            m_colorListPref.setTitle(R.string.selColorTitle);
            m_colorListPref.setDefaultValue(sharedPrefs.getString("varColor"+index, "cornflowerblue"));
            m_varPrefCat.addPreference(m_colorListPref);

            setPreferenceScreen(prefScreen);

            m_showPointsPref.setDependency("varVisibility"+index);
            m_xListPref.setDependency("varVisibility"+index);
            m_axisListPref.setDependency("varVisibility"+index);
            m_styleListPref.setDependency("varVisibility"+index);
            m_widthListPref.setDependency("varVisibility"+index);
            m_colorListPref.setDependency("varVisibility"+index);
        }


        @Override
        public void onResume()
        {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onPause()
        {
            super.onPause();
            // Unregister the listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }


        private void initSummary(Preference p, int index)
        {
            //get the currently selected variable name
//			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            //set category title
//			String strong = prefs.getString("varSelPreference", "");

            if (p instanceof PreferenceCategory)
            {
                PreferenceCategory pCat = (PreferenceCategory)p;
                for(int i=0;i<pCat.getPreferenceCount();i++)
                {
                    initSummary(pCat.getPreference(i), index);
                }
            }
            else
            {
                updatePrefSummary(p, index);
            }
        }


        private void updatePrefSummary(Preference p, int index)
        {
            SharedPreferences prefs = getActivity().getBaseContext().getSharedPreferences(MainActivity.s_filePrefsName, MODE_PRIVATE);
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

            if (p instanceof ListPreference)
            {
//				ListPreference listPref = (ListPreference) p;
                if(p.getKey().equalsIgnoreCase("xSelPreference"+index))
                {
                    String strong;
                    if(s_pGraphView.s_ixVariable.get(index)==-1)  strong = "index";
                    else                                          strong = m_varNamesArray[s_pGraphView.s_ixVariable.get(index)];
                    p.setSummary(getString(R.string.xSelPrefSummary)+" "+ strong);
                }
                else if(p.getKey().equalsIgnoreCase("varAxis"+index))
                {
                    String strong;
                    if(s_pGraphView.s_iyAxis.get(index)==0)    strong = "Left axis";
                    else                                       strong = "Right axis";
                    p.setSummary(getString(R.string.axisSelection)+" "+ strong);
                }
                else if(p.getKey().equalsIgnoreCase("varStyle"+index))
                {
                    String strange;
                    if(s_pGraphView.s_iCurveStyle.get(index)==1)      strange = "Dotted";
                    else if(s_pGraphView.s_iCurveStyle.get(index)==2) strange = "Dashed";
                    else                                              strange = "Solid";

                    p.setSummary(getString(R.string.lineStyleSummary)+" "+strange);
                }
                else if(p.getKey().equalsIgnoreCase("varWidth"+index))
                {
                    p.setSummary(getString(R.string.lineWidthSummary)+" "+s_pGraphView.s_iCurveWidth.get(index));
                }
                else if(p.getKey().equalsIgnoreCase("varColor"+index))
                {
                    p.setSummary(getString(R.string.selColorSummary)+" "+prefs.getString("varColor"+index, ""));
                }
            }
            else if (p instanceof EditTextPreference)
            {
//				EditTextPreference editTextPref = (EditTextPreference) p;
            }
            else if (p instanceof CheckBoxPreference)
            {
                if(p.getKey().equalsIgnoreCase("varVisibility"+index))
                {
                    if (s_pGraphView.s_bShowCurve.get(index)) p.setSummary("Curve is visible");
                    else                                      p.setSummary("Curve is not visible");
                }
                else if(p.getKey().equalsIgnoreCase("varShowPoints"+index))
                {
                    if (s_pGraphView.s_bShowPoints.get(index)) p.setSummary("Points are visible");
                    else                                       p.setSummary("Points are not visible");
                }
            }
        }


        private void setValues(int index)
        {
//			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            SharedPreferences sharedPrefs = getActivity().getBaseContext().getSharedPreferences(MainActivity.s_filePrefsName, MODE_PRIVATE);

            m_showLinePref.setKey("varVisibility"+index);
            m_showLinePref.setChecked(sharedPrefs.getBoolean("varVisibility"+index, true));
            updatePrefSummary(m_showLinePref, index);

            m_showPointsPref.setKey("varShowPoints"+index);
            m_showPointsPref.setChecked(sharedPrefs.getBoolean("varShowPoints"+index, false));
            updatePrefSummary(m_showPointsPref, index);

            m_xListPref.setKey("xSelPreference"+index);
            m_xListPref.setValue(sharedPrefs.getString("xSelPreference"+index, "index"));
            updatePrefSummary(m_xListPref, index);

            m_axisListPref.setKey("varAxis"+index);
            m_axisListPref.setValue(sharedPrefs.getString("varAxis"+index, "y1"));
            updatePrefSummary(m_axisListPref, index);

            m_widthListPref.setKey("varWidth"+index);
            m_widthListPref.setValue(sharedPrefs.getString("varWidth"+index, "1"));
            updatePrefSummary(m_widthListPref, index);

            m_colorListPref.setKey("varColor"+index);
            m_colorListPref.setValue(sharedPrefs.getString("varColor"+index, "cornflowerblue"));
            updatePrefSummary(m_colorListPref, index);
        }


        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            String strong;
            if(key.equals(m_showLinePref.getKey()))
            {
                s_pGraphView.s_bShowCurve.set(m_Index, sharedPreferences.getBoolean(key, true));
            }
            else if(key.equals(m_showPointsPref.getKey()))
            {
                s_pGraphView.s_bShowPoints.set(m_Index, sharedPreferences.getBoolean(key, true));
            }
            else if(key.equals(m_xListPref.getKey()))
            {
                strong = sharedPreferences.getString(key, "index");
                int iVar = MainActivity.s_channelName.indexOf(strong);
                s_pGraphView.s_ixVariable.set(m_Index, iVar);
                MainActivity.s_bx1stColumn = false;
            }
            else if(key.equals(m_axisListPref.getKey()))
            {
                strong = sharedPreferences.getString(key, "y1");
                if(strong.equalsIgnoreCase("Left axis"))
                {
                    s_pGraphView.s_iyAxis.set(m_Index, 0);
                }
                else if(strong.equalsIgnoreCase("Right axis"))
                {
                    s_pGraphView.s_iyAxis.set(m_Index, 1);
                }
            }
            else if(key.equals(m_styleListPref.getKey()))
            {
                strong = sharedPreferences.getString(key, "Solid");
                if(strong.equalsIgnoreCase("solid"))        s_pGraphView.s_iCurveStyle.set(m_Index, 0);
                else if(strong.equalsIgnoreCase("dotted"))  s_pGraphView.s_iCurveStyle.set(m_Index, 1);
                else if(strong.equalsIgnoreCase("dashed"))  s_pGraphView.s_iCurveStyle.set(m_Index, 2);
            }
            else if(key.equals(m_widthListPref.getKey()))
            {
                strong = sharedPreferences.getString(key, "5");
                s_pGraphView.s_iCurveWidth.set(m_Index, Integer.parseInt(strong.trim()));
            }
            else if(key.equals(m_colorListPref.getKey()))
            {
//				String[] colors  = getResources().getStringArray(R.array.curveColorStringArray);

                strong = sharedPreferences.getString(key, "red");
                s_pGraphView.s_curveColor.set(m_Index, strong);
            }
            updatePrefSummary(findPreference(key), m_Index);
        }
    }
}










