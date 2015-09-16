
package techwind.util.lineplot;

import android.content.SharedPreferences;
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
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;

import techwind.util.graph.AndroidGraphView;
import techwind.util.preference.ColorListPreference;

public class PrefsAxisActivity extends PreferenceActivity
{
    final static private String TAG = "PrefsAxisActivity";
    static public AndroidGraphView s_pGraphView; /** a pointer to the graph view for the settings*/
static int s_iScale = 0;  //0 = x-axis, 1=left y-axis, 2=right y-axis
    static String s_strScale="";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        s_pGraphView = MainActivity.s_graphView;
        s_iScale = getIntent().getExtras().getInt("iScale");

        getFragmentManager().beginTransaction().replace(android.R.id.content, new AxisPrefsFragment()).commit();
        s_pGraphView = MainActivity.s_graphView;

        s_iScale = getIntent().getExtras().getInt("iScale");

        if(s_iScale==0)     s_strScale = "x";
        else if(s_iScale==1) s_strScale = "y0";
        else if(s_iScale==2) s_strScale = "y1";
    }

    public int getColorfromName(String colorName)
    {
        int clr = getResources().getIdentifier(colorName,  "color", getPackageName());
        return getResources().getColor(clr);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return     AxisPrefsFragment.class.getName().equals(fragmentName);

    }

    static int getStyle(String styleName)
    {
        if(styleName.equalsIgnoreCase("dotted"))      return 1;
        else if(styleName.equalsIgnoreCase("dashed")) return 2;
        else                                          return 0;
    }


    public static class AxisPrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            addPreferences();

            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++)
            {
                initSummary(getPreferenceScreen().getPreference(i));
            }
        }


        private void addPreferences()
        {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

            PreferenceScreen prefScreen = getPreferenceManager().createPreferenceScreen(getActivity());

            PreferenceCategory rangePrefCat = new PreferenceCategory(getActivity());
            if(s_iScale==0) rangePrefCat.setTitle("X scale");
            else if(s_iScale==1) rangePrefCat.setTitle("Left Y axis scale");
            else if(s_iScale==2) rangePrefCat.setTitle("Right Y axis scale");
            prefScreen.addPreference(rangePrefCat);

            // auto range
            CheckBoxPreference axisAutoScale = new CheckBoxPreference(getActivity());
            axisAutoScale.setKey(s_strScale + "AxisManualScale");
            axisAutoScale.setChecked(sharedPrefs.getBoolean(s_strScale+"AxisManualScale", true));
            axisAutoScale.setTitle(R.string.axisManualScaleTitle);
            axisAutoScale.setSummary(R.string.axisManualScaleSummary);
            rangePrefCat.addPreference(axisAutoScale);

            //Min axis value value
            EditTextPreference axisMinPref = new EditTextPreference(getActivity());
            axisMinPref.setKey(s_strScale + "MinAxis");
//            axisMinPref.setDefaultValue("0.0");
            axisMinPref.setTitle(R.string.MinTitle);
            EditText et= axisMinPref.getEditText();
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            et.setGravity(Gravity.RIGHT);
            et.setSelectAllOnFocus(true);
            rangePrefCat.addPreference(axisMinPref);

            //Max axis value value
            EditTextPreference axisMaxPref = new EditTextPreference(getActivity());
            axisMaxPref.setKey(s_strScale + "MaxAxis");
//            axisMaxPref.setDefaultValue("1.0");
            axisMaxPref.setTitle(R.string.MaxTitle);
            et= axisMaxPref.getEditText();
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            et.setGravity(Gravity.RIGHT);
            et.setSelectAllOnFocus(true);
            rangePrefCat.addPreference(axisMaxPref);


            if(s_iScale==2)
            {
                //zero scale alignments
                CheckBoxPreference lockZeroPref = new CheckBoxPreference(getActivity());
                lockZeroPref.setKey("yLockZero");
                lockZeroPref.setDefaultValue(true);
                lockZeroPref.setChecked(sharedPrefs.getBoolean("yLockZero", false));
                lockZeroPref.setTitle(R.string.yAxisAlignZeroTitle);
                lockZeroPref.setSummary(R.string.yAxisAlignZeroSummary);
                rangePrefCat.addPreference(lockZeroPref);
            }

            PreferenceCategory prefGridCategory = new PreferenceCategory(getActivity());
            if(s_iScale==0)      prefGridCategory.setTitle("X grid style");
            else if(s_iScale==1) prefGridCategory.setTitle("Left Y grid style");
            else if(s_iScale==2) prefGridCategory.setTitle("Right Y grid style");
            prefScreen.addPreference(prefGridCategory);

            //grid visibility
            CheckBoxPreference gridShowPref = new CheckBoxPreference(getActivity());
            gridShowPref.setKey(s_strScale + "GridShow");
            gridShowPref.setChecked(MainActivity.s_graphView.s_bGrid[s_iScale]);
            gridShowPref.setTitle(R.string.varShowTitle);
            gridShowPref.setSummary(R.string.varShowSummary);
            prefGridCategory.addPreference(gridShowPref);

            //curve style
            ListPreference gridStylePref = new ListPreference(getActivity());
            gridStylePref.setKey(s_strScale + "GridStylePreference");
            gridStylePref.setEntries(R.array.lineStyleStringArray);
            gridStylePref.setEntryValues(R.array.lineStyleStringArray);
            gridStylePref.setDialogTitle(R.string.lineStyleDialogTitle);
            gridStylePref.setTitle(R.string.lineStyleTitle);
            gridStylePref.setSummary(R.string.lineStyleSummary);
            gridStylePref.setDefaultValue("Dashed");
            prefGridCategory.addPreference(gridStylePref);

            //curve width
            ListPreference gridWidthPref = new ListPreference(getActivity());
            gridWidthPref.setKey(s_strScale + "GridWidthPreference");
            gridWidthPref.setEntries(R.array.curveWidthStringArray);
            gridWidthPref.setEntryValues(R.array.curveWidthStringArray);
            gridWidthPref.setDialogTitle(R.string.varWidthDialogTitle);
            gridWidthPref.setTitle(R.string.lineWidthTitle);
            gridWidthPref.setSummary(R.string.lineWidthSummary);
            gridWidthPref.setDefaultValue("1");
            prefGridCategory.addPreference(gridWidthPref);

            //curve color
            ColorListPreference gridColorListPref = new ColorListPreference(getActivity());
            gridColorListPref.setKey(s_strScale + "GridColorPreference");
            gridColorListPref.setEntries(R.array.curveColorStringArray);
            gridColorListPref.setEntryValues(R.array.curveColorStringArray);
            gridColorListPref.setDialogTitle(R.string.varColorDialogTitle);
            gridColorListPref.setTitle(R.string.selColorTitle);
            gridColorListPref.setSummary(R.string.selColorSummary);
            prefGridCategory.addPreference(gridColorListPref);

            setPreferenceScreen(prefScreen);

            axisMinPref.setDependency(s_strScale + "AxisManualScale");
            axisMaxPref.setDependency(s_strScale + "AxisManualScale");
            gridStylePref.setDependency(s_strScale + "GridShow");
            gridWidthPref.setDependency(s_strScale + "GridShow");
            gridColorListPref.setDependency(s_strScale + "GridShow");
        }

        @Override
        public void onResume()
        {
            super.onResume();
            // Set up a listener for key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause()
        {
            super.onPause();
            // Unregister the listener for key changes
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

        }


        private void initSummary(Preference p)
        {
            if (p instanceof PreferenceCategory)
            {
                PreferenceCategory pCat = (PreferenceCategory)p;
                for(int i=0;i<pCat.getPreferenceCount();i++)
                {
                    initSummary(pCat.getPreference(i));
                }
            }
            else
            {
                updatePrefSummary(p);
            }
        }


        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            if(key.equals(s_strScale+"AxisManualScale"))
            {
                if(s_iScale==0) s_pGraphView.s_Graph.setAutoXLimits(!sharedPreferences.getBoolean(key, true));
                else if(s_iScale==1) s_pGraphView.s_Graph.setAutoYLimits(0, !sharedPreferences.getBoolean(key, true));
                else if(s_iScale==2) s_pGraphView.s_Graph.setAutoYLimits(1, !sharedPreferences.getBoolean(key, true));
                MainActivity.s_bResetScales = true;
            }
            else if(key.equals(s_strScale+"MinAxis"))
            {
                String strong = sharedPreferences.getString(key, "0.0");
                s_pGraphView.s_Graph.setMin(s_iScale, Double.parseDouble(strong.trim()));
                MainActivity.s_bResetScales = true;
            }
            else if(key.equals(s_strScale+"MaxAxis"))
            {
                String strong = sharedPreferences.getString(key, "1.0");
                s_pGraphView.s_Graph.setMax(s_iScale, Double.parseDouble(strong.trim()));
                MainActivity.s_bResetScales = true;
            }
            else if(key.equals("yLockZero"))
            {
                s_pGraphView.s_bAlignYZero  = sharedPreferences.getBoolean(key, false);
                MainActivity.s_bResetScales = true;
            }
            else if(key.equals(s_strScale+"GridShow"))
            {
                s_pGraphView.s_bGrid[s_iScale]= sharedPreferences.getBoolean(key, true);
                MainActivity.s_bResetGraphStyle = true;
            }
            else if(key.equals(s_strScale+"GridStylePreference"))
            {
                String strong = sharedPreferences.getString(key, "1");
                s_pGraphView.s_iGridStyle[s_iScale]= getStyle(strong.trim());
                MainActivity.s_bResetGraphStyle = true;
            }
            else if(key.equals(s_strScale+"GridWidthPreference"))
            {
                String strong = sharedPreferences.getString(key, "1");
                s_pGraphView.s_iGridWidth[s_iScale]= Integer.parseInt(strong.trim());
                MainActivity.s_bResetGraphStyle = true;
            }
            else if(key.equals(s_strScale+"GridColorPreference"))
            {
                String strong = sharedPreferences.getString(key, "white");
                s_pGraphView.s_iGridColor[s_iScale] = ((PrefsAxisActivity)getActivity()).getColorfromName(strong);
                MainActivity.s_bResetGraphStyle = true;
            }

            updatePrefSummary(findPreference(key));
        }


        private void updatePrefSummary(Preference p)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

            if (p instanceof ListPreference)
            {
//				ListPreference listPref = (ListPreference) p;
                if(p.getKey().equalsIgnoreCase(s_strScale+"GridStylePreference"))
                {
                    p.setSummary(getString(R.string.lineStyleSummary)+" "+prefs.getString(p.getKey(), "solid") );
                }
                else if(p.getKey().equalsIgnoreCase(s_strScale+"GridWidthPreference" ))
                {
                    p.setSummary(getString(R.string.lineWidthSummary)+" "+prefs.getString(p.getKey(), "1") +" pixels");
                }
                else if(p.getKey().equalsIgnoreCase(s_strScale+"GridColorPreference"))
                {
                    p.setSummary(getString(R.string.selColorSummary)+" "+prefs.getString(p.getKey(), "lightgrey"));
                }
            }
            else if (p instanceof EditTextPreference)
            {
//				EditTextPreference editTextPref = (EditTextPreference) p;
                if(p.getKey().equalsIgnoreCase(s_strScale+"MinAxis"))
                {
                    p.setSummary("Min= "+prefs.getString(p.getKey(), "0.0"));
                }
                else if(p.getKey().equalsIgnoreCase(s_strScale+"MaxAxis"))
                {
                    p.setSummary("Max= "+prefs.getString(p.getKey(), "1.0"));
                }
            }
            else if (p instanceof CheckBoxPreference)
            {
                CheckBoxPreference checkBoxPref = (CheckBoxPreference) p;
                if(p.getKey().equalsIgnoreCase("yLockZero"))
                {
                    if (checkBoxPref.isChecked()) p.setSummary("Zero is aligned with left scale");
                    else                          p.setSummary("Zero is not aligned with left scale");
                }
                else if(p.getKey().equalsIgnoreCase(s_strScale+"AxisManualScale"))
                {
                    if (checkBoxPref.isChecked()) p.setSummary("User-defined scale");
                    else                          p.setSummary("Automatic scale");
                }
                else if(p.getKey().equalsIgnoreCase(s_strScale+"GridShow"))
                {
                    if (checkBoxPref.isChecked()) p.setSummary("Grid is visible");
                    else                          p.setSummary("Grid is not visible");
                }
            }
        }
    }

}
