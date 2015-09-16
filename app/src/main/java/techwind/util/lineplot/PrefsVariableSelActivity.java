package techwind.util.lineplot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;


import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import android.preference.PreferenceScreen;

import android.view.View;
import android.widget.Button;



public class PrefsVariableSelActivity extends PreferenceActivity
{
    private static final int REQUEST_VAR_PREFERENCES = 1;
    Button[] m_varButton;
    boolean m_bDataColumns=true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //		PreferenceManager.setDefaultValues(PrefsVariableSelActivity.this, R.xml.graphcurve_prefs, false);
        //		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

        getFragmentManager().beginTransaction().replace(android.R.id.content, new VarListPrefsFragment()).commit();
    }


    public void onClick(View v)
    {
        for(int iv=0; iv<MainActivity.s_channelName.size(); iv++)
        {
            if(m_varButton[iv] ==  v)
            {
//				Log.i(TAG, "Clicked "+iv);
                // When the button is clicked, launch an activity through this intent
                Intent varPreferencesIntent = new Intent().setClass(this, PrefsVarActivity.class);
                varPreferencesIntent.putExtra(PrefsVarActivity.VAR_INDEX, iv);
                // Make it a subactivity so that we know when it returns
                startActivityForResult(varPreferencesIntent, REQUEST_VAR_PREFERENCES);
                break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VAR_PREFERENCES)
        {
            if(resultCode == RESULT_OK)
            {
//				Log.i(TAG, "Var prefs returned OK");
            }
        }
    }


    static public class VarListPrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
    {
        PreferenceCategory m_varPrefCat;
        Preference m_varPref[];
        String[] m_varNamesArray;

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            m_varPref = new Preference[MainActivity.s_channelName.size()];
            m_varNamesArray = MainActivity.s_channelName.toArray(new String[MainActivity.s_channelName.size()]);

            addPreferences();


            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++)
            {
                initSummary(getPreferenceScreen().getPreference(i));
            }
        }


        private void addPreferences()
        {
//			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            PreferenceScreen prefScreen = getPreferenceManager().createPreferenceScreen(getActivity());

            // var preferences header
            m_varPrefCat = new PreferenceCategory(getActivity());
            m_varPrefCat.setTitle("Variable selection");
            prefScreen.addPreference(m_varPrefCat);

            // visibility
            for(int ip=0; ip<m_varPref.length; ip++)
            {
                m_varPref[ip] = new Preference(getActivity());
                m_varPref[ip].setKey("variable"+ip);
                m_varPref[ip].setTitle(m_varNamesArray[ip]);
                m_varPref[ip].setSummary(null);
                m_varPref[ip].setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
                {
                    @Override
                    public boolean onPreferenceClick(Preference pref)
                    {
                        for(int iv=0; iv<MainActivity.s_channelName.size(); iv++)
                        {
                            if(m_varPref[iv] ==  pref)
                            {
                                // When the button is clicked, launch an activity through this intent
                                Intent varPreferencesIntent = new Intent().setClass(getActivity(), PrefsVarActivity.class);
                                varPreferencesIntent.putExtra(PrefsVarActivity.VAR_INDEX, iv);
                                // Make it a subactivity so we know when it returns
                                startActivityForResult(varPreferencesIntent, REQUEST_VAR_PREFERENCES);
                                return true;
                            }
                        }
                        return true;
                    }
                });

                m_varPrefCat.addPreference(m_varPref[ip]);
            }

            setPreferenceScreen(prefScreen);
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


        private void initSummary(Preference p)
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
//			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

            if (p instanceof Preference)
            {
            }
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {

        }
    }
}



