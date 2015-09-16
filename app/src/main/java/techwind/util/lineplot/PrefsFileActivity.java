package techwind.util.lineplot;


import techwind.util.file.FileDialogActivity;
import techwind.util.file.Globals;
import android.app.Activity;
import android.content.Intent;
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
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.EditText;

public class PrefsFileActivity extends PreferenceActivity
{
    //	private static final String TAG = "fts/PrefsFileActivity";
    public static final String START_PATH = "START_PATH";
    public static final String RESULT_PATH = "RESULT_PATH";
    public static final String CAN_SELECT_DIR = "CAN_SELECT_DIR";
    public static final int REQUEST_FILENAME = 19;
    public static final String DATA_IN_COLUMNS = "DATA_IN_COLUMNS";
    public static final String LABELS_IN_FIRST_ROW = "LABELS_IN_FIRST_ROW";
    public static final String X_IN_FIRST_COLUMN = "X_IN_FIRST_COLUMN";
    public static final String LIST_SEPARATOR = "LISTSEPARATOR";
    public static final String DECIMAL_SEPARATOR = "DECIMALSEPARATOR";
    public static final String IGNORE_LINES = "IGNORELINES";

    public static String s_listSeparator =" ";
    public static String s_decimalSeparator =".";
    public static String s_fileName="";
    public static boolean s_bLabels1stRow =true, s_bColumns =true, s_bx1stColumn =true;
    public static int s_nIgnoreLines =0;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        s_fileName      = extras.getString(RESULT_PATH);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFileFragment()).commit();
    }


    private void onOK()
    {
        if (s_fileName != null)
        {
            getIntent().putExtra(RESULT_PATH, s_fileName);
            getIntent().putExtra(LIST_SEPARATOR, s_listSeparator);
            getIntent().putExtra(DECIMAL_SEPARATOR, s_decimalSeparator);
            getIntent().putExtra(LABELS_IN_FIRST_ROW, s_bLabels1stRow);
            getIntent().putExtra(X_IN_FIRST_COLUMN, s_bx1stColumn);
            getIntent().putExtra(DATA_IN_COLUMNS, s_bColumns);
            getIntent().putExtra(IGNORE_LINES, s_nIgnoreLines);
            setResult(RESULT_OK, getIntent());
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            onOK();
            return true;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
    }


    static public class PrefsFileFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
    {
        //		private static final String TAG = "fts/PrefsFileFragment";
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            setSharedPreferencesFile(MainActivity.s_filePrefsName);

            addPreferences();

            Preference button = findPreference("fileSelection");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference arg0)
                {
                    Intent intent = new Intent(getActivity(), FileDialogActivity.class);
                    if(Globals.s_ExportDir !=null)
                        intent.putExtra(FileDialogActivity.START_PATH, Globals.s_ExportDir.getPath());

                    intent.putExtra(FileDialogActivity.CAN_SELECT_DIR, false);
                    startActivityForResult(intent, REQUEST_FILENAME);
                    return true;
                }
            });

            button = findPreference("dataInColumns");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference pref)
                {
                    s_bColumns = !s_bColumns;
                    updatePrefSummary(pref);
                    return true;
                }
            });

            button = findPreference("labels1stRow");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference pref)
                {
                    s_bLabels1stRow = !s_bLabels1stRow;
                    updatePrefSummary(pref);

                    return true;
                }
            });

            button = findPreference("x1stColumn");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference pref)
                {
                    s_bx1stColumn = !s_bx1stColumn;
                    updatePrefSummary(pref);

                    return true;
                }
            });


            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++)
            {
                initSummary(getPreferenceScreen().getPreference(i));
            }
        }

        private void addPreferences()
        {
            PreferenceScreen prefScreen = getPreferenceManager().createPreferenceScreen(getActivity());

            PreferenceCategory filePrefCat = new PreferenceCategory(getActivity());
            prefScreen.addPreference(filePrefCat);

            Preference fileSelPref = new Preference(getActivity());
            fileSelPref.setKey("fileSelection");
            fileSelPref.setSummary("Selected file:");
            fileSelPref.setTitle("File selection");
            filePrefCat.addPreference(fileSelPref);

            EditTextPreference nIgnorePref = new EditTextPreference(getActivity());
            nIgnorePref.setKey("ignoreLines");
            nIgnorePref.setTitle(R.string.ignorefirstlinesTitle);
            nIgnorePref.setSummary(R.string.ignorefirstlinesSummary);
            nIgnorePref.setDefaultValue(String.format("%d", s_nIgnoreLines));
            EditText et= nIgnorePref.getEditText();
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            et.setGravity(Gravity.RIGHT);
            et.setSelectAllOnFocus(true);
            filePrefCat.addPreference(nIgnorePref);

            ListPreference listSepPref = new ListPreference(getActivity());
            listSepPref.setKey("listSep");
            CharSequence[] entryValues = new CharSequence[] {" ", "\t", ",", ";"};
            CharSequence[] entries     = new CharSequence[] {"Space", "tab", "comma", "semi-colon"};
            listSepPref.setEntryValues(entryValues);
            listSepPref.setEntries(entries);
            listSepPref.setDialogTitle("List separator");
            listSepPref.setTitle(R.string.listSeparatorTitle);
            listSepPref.setSummary(R.string.listSeparatorSummary);
            listSepPref.setDefaultValue(s_listSeparator);
            filePrefCat.addPreference(listSepPref);

            ListPreference decSepPref = new ListPreference(getActivity());
            decSepPref.setKey("decimalSep");
            entryValues = new CharSequence[] {".", ","};
            entries     = new CharSequence[] {"dot", "comma"};
            decSepPref.setEntryValues(entryValues);
            decSepPref.setEntries(entries);
            decSepPref.setDialogTitle("Decimal separator");
            decSepPref.setTitle(R.string.decimalSeparatorTitle);
            decSepPref.setSummary(R.string.decimalSeparatorSummary);
            decSepPref.setDefaultValue(s_decimalSeparator);
            filePrefCat.addPreference(decSepPref);

            Preference colPref = new Preference(getActivity());
            colPref.setKey("dataInColumns");
            colPref.setSummary(R.string.dataColumnsSummary);
            colPref.setTitle(R.string.dataColumnsTitle);
            filePrefCat.addPreference(colPref);

            Preference labelsPref = new Preference(getActivity());
            labelsPref.setKey("labels1stRow");
            labelsPref.setSummary(R.string.labels1stRowSummary);
            labelsPref.setTitle(R.string.labels1stRowTitle);
            filePrefCat.addPreference(labelsPref);

            Preference x1stPref = new Preference(getActivity());
            x1stPref.setKey("x1stColumn");
            x1stPref.setSummary(R.string.x1stColumnSummary);
            x1stPref.setTitle(R.string.x1stColumnTitle);
            filePrefCat.addPreference(x1stPref);

            setPreferenceScreen(prefScreen);
        }

        public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data)
        {
            if (requestCode == REQUEST_FILENAME)
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    s_fileName = data.getStringExtra(FileDialogActivity.RESULT_PATH);
                    if(s_fileName==null) return;

                    int pos = s_fileName.lastIndexOf(MainActivity.slash);
                    if(pos>=0)
                    {
                        String filePrefsName = s_fileName.substring(pos+1);
                        pos = filePrefsName.lastIndexOf(".");
                        if(pos>=0)
                        {
                            filePrefsName = filePrefsName.substring(0,pos);
                        }
                        setSharedPreferencesFile(filePrefsName);
                    }

                    for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++)
                    {
                        initSummary(getPreferenceScreen().getPreference(i));
                    }
                }
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

        private void setSharedPreferencesFile(String filePrefsName)
        {
            PreferenceManager prefsMgr = getPreferenceManager();
            prefsMgr.setSharedPreferencesName(filePrefsName);
            prefsMgr.setSharedPreferencesMode(MODE_PRIVATE);
//            prefsMgr.setDefaultValues(getActivity(), filePrefsName, MODE_PRIVATE, R.xml.file_prefs, false);

            SharedPreferences sp = prefsMgr.getSharedPreferences();
            s_bColumns         = sp.getBoolean("dataInColumns", s_bColumns);
            s_bLabels1stRow    = sp.getBoolean("labels1stRow", s_bLabels1stRow);
            s_bx1stColumn      = sp.getBoolean("x1stColumn", s_bx1stColumn);
            String strong      = sp.getString("ignoreLines",String.format("%d", s_nIgnoreLines));
            s_nIgnoreLines     = Integer.parseInt(strong);
            s_listSeparator    = sp.getString("listSep", s_listSeparator);
            s_decimalSeparator = sp.getString("decimalSep", s_decimalSeparator);
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
            if(key.equalsIgnoreCase("listSep"))
            {
                s_listSeparator = sharedPreferences.getString("listSep", s_listSeparator);
            }
            else if(key.equalsIgnoreCase("decimalSep"))
            {
                s_decimalSeparator = sharedPreferences.getString("decimalSep", s_decimalSeparator);
            }
            else if(key.equalsIgnoreCase("ignoreLines"))
            {
                String strong = sharedPreferences.getString("ignoreLines","0");
                s_nIgnoreLines = Integer.parseInt(strong);
            }
            updatePrefSummary(findPreference(key));
        }


        private void updatePrefSummary(Preference p)
        {
            SharedPreferences prefs = getPreferenceManager().getSharedPreferences();

            boolean bChangedColRow = false;
            if (p instanceof EditTextPreference)
            {
                if(p.getKey().equalsIgnoreCase("ignoreLines"))
                {
                    p.setSummary("The "+ s_nIgnoreLines +" first line(s) will be ignored");

                }
            }
            else if (p instanceof CheckBoxPreference)
            {
            }
            else if (p instanceof ListPreference)
            {
                if(p.getKey().equalsIgnoreCase("listSep"))
                {
                    String sep = prefs.getString("listSep", s_listSeparator);
                    if(sep.equalsIgnoreCase(" "))
                    {
                        p.setSummary("List separator: space");
                    }
                    else if(sep.equalsIgnoreCase("\t"))
                    {
                        p.setSummary("List separator: tab");
                    }
                    else
                    {
                        p.setSummary("List separator: "+ s_listSeparator);
                    }
                }
                else if(p.getKey().equalsIgnoreCase("decimalSep"))
                {
                    p.setSummary("Decimal separator: "+ s_decimalSeparator);
                }
            }
            else if (p instanceof Preference)
            {
                if(p.getKey().equalsIgnoreCase("fileSelection"))
                {
                    p.setSummary("Selected file: "+ s_fileName);
                }
                else if(p.getKey().equalsIgnoreCase("dataInColumns"))
                {
                    bChangedColRow = true;
                    if(s_bColumns) p.setSummary("Data is in columns");
                    else           p.setSummary("Data is in rows");
                }

                else if(p.getKey().equalsIgnoreCase("labels1stRow") || bChangedColRow)
                {
                    p = findPreference("labels1stRow");
                    if(s_bLabels1stRow)
                    {
                        if(s_bColumns) p.setSummary("Labels in the first row");
                        else           p.setSummary("Labels in the first column");
                    }
                    else
                    {
                        p.setSummary("No labels");
                    }
                }

                else if(p.getKey().equalsIgnoreCase("x1stColumn") || bChangedColRow)
                {
                    p = findPreference("x1stColumn");
                    if(s_bx1stColumn)
                    {
                        if(s_bColumns) p.setSummary("x-values are in the first column");
                        else           p.setSummary("x-values are in the first row");
                    }
                    else p.setSummary("Index will be used for x-values");
                }
            }
        }
    }

}
