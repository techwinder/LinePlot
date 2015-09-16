package techwind.util.lineplot;


import java.util.List;


import techwind.util.graph.AndroidGraphView;

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

public class PrefsGraphActivity extends PreferenceActivity
{
    static public AndroidGraphView s_pGraphView; /** a pointer to the graph view for the settings*/

@Override
public void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    s_pGraphView = MainActivity.s_graphView;
}



    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.graph_preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return FontPrefsFragment.class.getName().equals(fragmentName)
                || MarginPrefsFragment.class.getName().equals(fragmentName)
                || HdwAccPrefsFragment.class.getName().equals(fragmentName);
//                || super.isValidFragment(fragmentName);
    }


    static int getStyle(String styleName)
    {
        if(styleName.equalsIgnoreCase("dotted"))      return 1;
        else if(styleName.equalsIgnoreCase("dashed")) return 2;
        else                                          return 0;
    }


    static public class FontPrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.graph_font_prefs);

            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++)
            {
                initSummary(getPreferenceScreen().getPreference(i));
            }
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


        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            if(key.equals("graphFontType"))
            {
                AndroidGraphView.s_fontType = sharedPreferences.getString(key, "sans-serif");
            }
            else if(key.equals("graphFontStyle"))
            {
                AndroidGraphView.s_fontStyle = sharedPreferences.getString(key, "normal");
            }
            else if(key.equals("graphFontSize"))
            {
                String strong = sharedPreferences.getString(key, "23"); //crashes if we try to getInt()???
                AndroidGraphView.s_iTextSize = Integer.parseInt(strong.trim());
            }
            updatePrefSummary(findPreference(key));
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

        private void updatePrefSummary(Preference p)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

            if (p instanceof ListPreference)
            {
                //				ListPreference listPref = (ListPreference)p;
                if(p.getKey().equals("graphFontType"))
                {
                    p.setSummary(getString(R.string.fontTypeSummary)+" "+prefs.getString(p.getKey(), "sans-serif"));
                }
                else if(p.getKey().equals("graphFontStyle"))
                {
                    p.setSummary(getString(R.string.fontStyleSummary)+" "+prefs.getString(p.getKey(), "normal"));
                }
            }
            else if (p instanceof EditTextPreference)
            {
                //				EditTextPreference editTextPref = (EditTextPreference) p;
                if(p.getKey().equals("graphFontSize"))
                {
                    p.setSummary(getString(R.string.graphFontSizeSummary)+" "+prefs.getString(p.getKey(), "0")+" pixels");
                }
            }
            else if (p instanceof CheckBoxPreference)
            {
            }
        }
    }


    /**
     * This fragment shows the preferences for the second header.
     */
    public static class MarginPrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.graph_margin_prefs);

            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++)
            {
                initSummary(getPreferenceScreen().getPreference(i));
            }
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
            if(key.equals("leftMargin"))
            {
                String strong = sharedPreferences.getString(key, "73");
                s_pGraphView.s_iGraphMargin[0] = Integer.parseInt(strong.trim());
            }
            else if(key.equals("rightMargin"))
            {
                String strong = sharedPreferences.getString(key, "73");
                s_pGraphView.s_iGraphMargin[1] = Integer.parseInt(strong.trim());
            }
            else if(key.equals("topMargin"))
            {
                String strong = sharedPreferences.getString(key, "73");
                s_pGraphView.s_iGraphMargin[2] = Integer.parseInt(strong.trim());
            }
            else if(key.equals("botMargin"))
            {
                String strong = sharedPreferences.getString(key, "73");
                s_pGraphView.s_iGraphMargin[3] = Integer.parseInt(strong.trim());
            }

            updatePrefSummary(findPreference(key));
        }


        private void updatePrefSummary(Preference p)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

            if (p instanceof ListPreference)
            {
                //				ListPreference listPref = (ListPreference) p;
            }
            else if (p instanceof EditTextPreference)
            {
                //				EditTextPreference editTextPref = (EditTextPreference) p;
                if(p.getKey().equals("leftMargin"))
                {
                    p.setSummary(getString(R.string.graphMarginSummary)+" "+prefs.getString(p.getKey(), "0")+" pixels");
                }
                else if(p.getKey().equals("rightMargin"))
                {
                    p.setSummary(getString(R.string.graphMarginSummary)+" "+prefs.getString(p.getKey(), "0")+" pixels");
                }
                else if(p.getKey().equals("topMargin"))
                {
                    p.setSummary(getString(R.string.graphMarginSummary)+" "+prefs.getString(p.getKey(), "0")+" pixels");
                }
                else if(p.getKey().equals("botMargin"))
                {
                    p.setSummary(getString(R.string.graphMarginSummary)+" "+prefs.getString(p.getKey(), "0")+" pixels");
                }
            }
            else if (p instanceof CheckBoxPreference)
            {
            }
        }
    }


    static public class HdwAccPrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
    {
        //		private static final String TAG = "FTS/HdwAccPrefsFragment";
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
//			Log.i(TAG, "OnCreate fragment");
            addPreferencesFromResource(R.xml.graph_hdw_accel_prefs);

            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++)
            {
                initSummary(getPreferenceScreen().getPreference(i));
            }
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


        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            if(key.equalsIgnoreCase("hardwareAcceleration"))
            {
                AndroidGraphView.s_bHdwAcceleration = sharedPreferences.getBoolean(key, false);
            }
            updatePrefSummary(findPreference(key));
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

        private void updatePrefSummary(Preference p)
        {
            // 	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            if (p instanceof ListPreference)
            {
            }
            else if (p instanceof EditTextPreference)
            {
            }
            else if (p instanceof CheckBoxPreference)
            {
                CheckBoxPreference checkBoxPref = (CheckBoxPreference)p;
                if(p.getKey().equalsIgnoreCase("hardwareAcceleration") )
                {
                    if (checkBoxPref.isChecked()) p.setSummary("Hardware acceleration is enabled");
                    else                          p.setSummary("Hardware acceleration is disabled");
                }
            }
        }
    }
}

