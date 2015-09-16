package techwind.util.lineplot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import techwind.util.file.Globals;
import techwind.util.graph.AndroidGraphView;
import techwind.util.graph.JCurve;

public class MainActivity extends Activity
{
    public static final String TAG = "linePlot";

    static private final String eol = System.getProperty("line.separator");
    static public final String slash = "/";

    static private final int REQUEST_LOAD= 1002;
    static private final int RESULT_GRAPH_VARIABLES = 2;
    static private final int RESULT_GRAPH_SETTINGS  = 3;

    static private String s_defaultDirPath = "";
    static public String s_fileName = "";
    static public String s_filePrefsName = "";
    public static String s_listSeparator =" ";
    public static String s_decimalSeparator =" ";
    public static int s_nLinesIgnored = 0;

    private boolean m_bLabels1stRow=true, m_bColumns=true;
    static public boolean s_bx1stColumn =true;
    public static boolean s_bLandscape=false;

    static public AndroidGraphView s_graphView;

    public static boolean s_bResetCurveStyles = true;
    public static boolean s_bResetGraphStyle = true;
    public static boolean s_bRefillCurves = true;
    public static boolean s_bResetScales = true;
    public static boolean s_bResetMargins = true;
    public static boolean s_bResetLayerType = true;

    static public HashMap<String, Integer> s_colorMap = new HashMap<String, Integer>();

    ArrayList[] m_doubleData = new ArrayList[s_channelName.size()];
    static public ArrayList<String> s_channelName = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupLayout();

        String[] colors = getResources().getStringArray(R.array.curveColorStringArray);
        for (String color : colors) {
            s_colorMap.put(color, colorFromName(color));
        }

        loadSharedPreferences();

        Globals.setupDataDir(s_defaultDirPath);

        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
//		int orientation = display.getRotation();
        Point size = new Point();
        display.getSize(size);
        s_bLandscape = size.x>size.y;
        s_graphView.setLandscapeMode(s_bLandscape);

        if(s_fileName.length()>0)
        {
            SharedPreferences sp = getSharedPreferences(s_filePrefsName, MODE_PRIVATE);
            s_listSeparator  = sp.getString("listSeparator", s_listSeparator);
            m_bColumns       = sp.getBoolean("dataInColumns", true);
            m_bLabels1stRow  = sp.getBoolean("labels1stRow", true);
            s_bx1stColumn    = sp.getBoolean("x1stColumn", true);
            String strange   = sp.getString("ignoreLines", "0");
            s_nLinesIgnored  = Integer.parseInt(strange);

            readData();
            loadFilePreferences();
            addCurves();
            resetGraph();

            String fileName;
            int ind = s_fileName.lastIndexOf("/");
            fileName = s_fileName.substring(ind+1);
            setTitle(fileName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.openfile:
            {
                Intent intent = new Intent(getBaseContext(), PrefsFileActivity.class);
                if(Globals.s_ExportDir != null)
                    intent.putExtra(PrefsFileActivity.START_PATH, Globals.s_ExportDir.getPath());

                intent.putExtra(PrefsFileActivity.RESULT_PATH, s_fileName);

                //can user select directories or not
                intent.putExtra(PrefsFileActivity.CAN_SELECT_DIR, false);

                startActivityForResult(intent, REQUEST_LOAD);
                return true;
            }
            case R.id.graphvariables:
            {
                Intent settingsActivity = new Intent(getBaseContext(), PrefsVariableSelActivity.class);

                startActivityForResult(settingsActivity, RESULT_GRAPH_VARIABLES);
                return true;
            }
            case R.id.graphsettings:
            {
                Intent settingsActivity = new Intent(getBaseContext(), PrefsGraphActivity.class);
                startActivityForResult(settingsActivity, RESULT_GRAPH_SETTINGS);
                return true;
            }
            case R.id.contact:
            {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "techwinder@gmail.com", null));
                //				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
            }
            case R.id.about:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.app_name);
                alert.setIcon(getDrawable(R.mipmap.ic_launcher));

                final TextView output = new TextView(this);
                output.setPadding(50, 20, 50, 20);
                String strong;
                strong="(C) 2015, techwinder (A. Deperrois)"+eol;
                strong += "(C) 2012, A. Ponomarev, android-file-dialog class";
                output.setText(strong);
                alert.setView(output);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        //just exit
                    }
                });

                alert.show();
                return true;
            }
            case R.id.quit:
            {
                saveSharedPreferences();
                saveFileSharedPreferences();
                System.exit(0);
                return true;
            }
        }
        return false;
    }

    protected void onResume()
    {
        super.onResume();

        if(s_bResetLayerType)
        {
            if(s_graphView!=null)
            {
                s_graphView.setLayerType();
                s_bResetLayerType =false;
            }
        }

        if(s_bResetMargins)
        {
            if(s_graphView!=null)
            {
                s_graphView.setLandscapeMode(s_bLandscape);
                s_bResetMargins =false;
            }
        }

        if(s_bResetGraphStyle)
        {
            if(s_graphView!=null)
            {
                s_graphView.setGraphStyle();
                s_bResetGraphStyle =false;
            }
        }

        if(s_bResetCurveStyles)
        {
            if(s_graphView!=null)
            {
                s_graphView.setCurveStyle();
                s_bResetCurveStyles =false;
            }
        }


        if(s_bResetScales)
        {
            if(s_graphView!=null)
            {
                s_graphView.setScales();
                s_bResetScales = false;
            }
        }

        if(s_graphView!=null) s_graphView.invalidate();
    }




    private void resetGraph()
    {
        if(s_graphView!=null)
        {
            s_graphView.resetAutoScales();
            s_graphView.invalidate();
        }
    }


    private void readData()
    {
        if(m_bColumns) readDataColumns(s_fileName, m_bLabels1stRow);
        else           readDataRows(   s_fileName, m_bLabels1stRow);
    }


    private void readDataColumns(String filePathName, boolean bLabels1stRow)
    {
        int iLine = 0;
        try
        {
            if(filePathName.length()==0) return;

            // read it and map it!
            BufferedReader br;
            br = new BufferedReader(new FileReader(filePathName));

            String[] strange;
            String line;
            s_channelName.clear();

            while(iLine<s_nLinesIgnored)
            {
                iLine++;
                line = br.readLine();
            }

            if(bLabels1stRow)
            {
                iLine++;
                line = br.readLine();

                //merge white spaces
                while(line.indexOf("  ")>0)
                {
                    line.replace("  "," ");
                }

                strange = line.split(s_listSeparator);
                int nSeries = strange.length;

                for(int is=0; is<nSeries; is++)
                {
                    if(strange[is].trim().length()>0)
                    {
                        s_channelName.add(strange[is].trim());
                    }
                    else
                    {
                        s_channelName.add("Serie_"+is);
                    }
                }
            }
            else
            {
                while(iLine<s_nLinesIgnored)
                {
                    iLine++;
                    line = br.readLine();
                }
                //read one line to get the number of values
                iLine++;
                line = br.readLine();
                strange = line.split(s_listSeparator);
                int nSeries = strange.length;

                for(int is=0; is<nSeries; is++)
                {
                    s_channelName.add("Serie_"+is);
                }

                //close and restart from the beginning;
                br.close();
                br = new BufferedReader(new FileReader(filePathName));
                while(iLine<s_nLinesIgnored)
                {
                    iLine++;
                    line = br.readLine();
                }
            }

            //now's the time to allocate the memory for the channel data
            m_doubleData = new ArrayList[s_channelName.size()];
            for(int iSerie=0; iSerie<s_channelName.size(); iSerie++)
            {
                m_doubleData[iSerie] = new ArrayList<Double>();
            }

            //read the data
            int iv;
            double data;
            while ((line = br.readLine()) != null)
            {
                iLine++;
                iv=0;
                strange = line.split(s_listSeparator);
                for(int is=0; is<strange.length; is++)
                {
                    if(strange[is].trim().length()>0)
                    {
                        strange[is].replace(s_decimalSeparator, ".");
                        try
                        {
                            data = Double.parseDouble(strange[is].trim());
                            m_doubleData[iv].add(data);
                        }
                        catch (NumberFormatException e1)
                        {
                            String strong = "Error reading value at line "+iLine +eol;
                            strong += line;
                            Toast.makeText(this, strong, Toast.LENGTH_LONG).show();
                            Log.i(TAG, strong);
                            return;
                        }
                        iv++;
                    }
                }
            }

            br.close();
        }
        catch (FileNotFoundException e0)
        {
            Toast.makeText(this, "File not found",Toast.LENGTH_LONG).show();
            //				Log.i(TAG,"File not found exception "+e0.toString());
        } catch (IOException e2)
        {
            Toast.makeText(this, "I/O error at line "+iLine,Toast.LENGTH_LONG).show();
        }
    }


    private void readDataRows(String filePathName, boolean bLabels1stColumn)
    {
        int iLine = 0;
        try
        {
            if(filePathName.length()==0) return;

            // read it and map it!
            BufferedReader br;
            br = new BufferedReader(new FileReader(filePathName));

            String[] strange;
            String line;
            s_channelName.clear();

            //read the data
            int iv;
            double data;

            while(iLine<s_nLinesIgnored)
            {
                iLine++;
                line = br.readLine();
            }


            //count the number of lines to size the array
            while ((line = br.readLine()) != null)
            {
                if(line.length()>0) iLine++;
            }

            if(iLine==0)
            {
                br.close();
                return;
            }
            else
            {
                m_doubleData = new ArrayList[iLine];
                for(int il=0; il<iLine; il++)
                {
                    m_doubleData[il] = new ArrayList<Double>();
                }
            }
            br.close();

            //start reading the data
            br = new BufferedReader(new FileReader(filePathName));
            iLine=0;
            while(iLine<s_nLinesIgnored)
            {
                iLine++;
                line = br.readLine();
            }

            while ((line = br.readLine()) != null)
            {
                if(line.length()>0)
                {
                    iv=0;

                    //merge white spaces
                    while(line.indexOf("  ")>0)
                    {
                        line.replace("  "," ");
                    }


                    strange = line.split(s_listSeparator);

                    for(int id=0; id<strange.length; id++)
                    {
                        if(iv==0)
                        {
                            if(bLabels1stColumn) s_channelName.add(strange[iv].trim());
                            else                 s_channelName.add("Serie_"+iLine);
                            iv++;
                        }
                        else if(strange[iLine].trim().length()>0)
                        {
                            try
                            {
                                strange[id].replace(s_decimalSeparator, ".");

                                data = Double.parseDouble(strange[id].trim());
                                m_doubleData[iLine].add(data);
                            }
                            catch (NumberFormatException e1)
                            {
                                String strong = "Error reading value at line "+iLine +eol;
                                strong += line;
                                Toast.makeText(this, strong ,Toast.LENGTH_LONG).show();
                                Log.i(TAG, strong);
                                br.close();
                                return;
                            }
                            iv++;
                        }
                    }
                    iLine++;
                }
            }

            br.close();
        }
        catch (FileNotFoundException e0) {
            Toast.makeText(this, "File not found",Toast.LENGTH_LONG).show();
        } catch (IOException e2) {
            Toast.makeText(this, "I/O error at line "+iLine,Toast.LENGTH_LONG).show();
        }
    }


    private void loadFilePreferences()
    {
//        Random r = new Random();

        SharedPreferences sp = getApplicationContext().getSharedPreferences(s_filePrefsName, MODE_PRIVATE);

        String strong;
        String[] clr = getResources().getStringArray(R.array.curveColorStringArray);

        s_graphView.s_bShowCurve.clear();
        s_graphView.s_bShowPoints.clear();
        s_graphView.s_ixVariable.clear();
        s_graphView.s_iyAxis.clear();
        s_graphView.s_iCurveStyle.clear();
        s_graphView.s_iCurveWidth.clear();
        s_graphView.s_curveColor.clear();

        for(int is=0; is<s_channelName.size(); is++)
        {
            s_graphView.s_bShowCurve.add(sp.getBoolean("varVisibility"+is, true));
            s_graphView.s_bShowPoints.add(sp.getBoolean("varShowPoints"+is, false));

            strong = sp.getString("xSelPreference"+is, "-1").trim();
            try {
                s_graphView.s_ixVariable.add(Integer.parseInt(strong));
            }
            catch(NumberFormatException nfe)
            {
                s_graphView.s_ixVariable.add(-1);
            }

            strong = sp.getString("varAxis"+is, "Left axis").trim();
            if(strong.equals("Left axis")) s_graphView.s_iyAxis.add(0);
            else                           s_graphView.s_iyAxis.add(1);

            strong =  sp.getString("varStyle"+is, "solid");
            if(strong.equalsIgnoreCase("Solid"))        s_graphView.s_iCurveStyle.add(0);
            else if(strong.equalsIgnoreCase("Dotted"))  s_graphView.s_iCurveStyle.add(1);
            else if(strong.equalsIgnoreCase("Dashed"))  s_graphView.s_iCurveStyle.add(2);

            strong = sp.getString("varWidth"+is, String.format("%d", 2)).trim();
            s_graphView.s_iCurveWidth.add(Integer.parseInt(strong));

            int n = clr.length-2;
            strong = sp.getString("varColor"+is, clr[2 + (is*11+7)%n]);
            s_graphView.s_curveColor.add(strong);
        }

        SharedPreferences.Editor editor = sp.edit();
        for(int is=0; is<s_channelName.size(); is++)
        {
            editor.putString("varColor"+is, s_graphView.s_curveColor.get(is));
            editor.putString("varWidth"+is, String.format("%d", s_graphView.s_iCurveWidth.get(is)));
            if(s_graphView.s_iCurveStyle.get(is)==0)      editor.putString("varStyle"+is, "Solid");
            else if(s_graphView.s_iCurveStyle.get(is)==1) editor.putString("varStyle"+is, "Dotted");
            else if(s_graphView.s_iCurveStyle.get(is)==2) editor.putString("varStyle"+is, "Dashed");
        }
        editor.commit();
    }


    /**After loading a session, stores its settings in the shared preferences */
    void saveSharedPreferences()
    {
        //put those values which are note set by the settings manager
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        //do not clear the editor, or lose the other settings
        // editor.clear();
        //		String[] colors = getResources().getStringArray(R.array.curveColorStringArray);

        editor.putString("fileName", s_fileName.trim());
        editor.putString("filePrefsName", s_filePrefsName.trim());
        editor.putString("defaultDir", s_defaultDirPath);


        editor.commit();
    }


    /**After loading a session, stores its settings in the shared preferences */
    void saveFileSharedPreferences()
    {
        //put those values which are not set by the settings manager
        SharedPreferences sp = getApplicationContext().getSharedPreferences(s_filePrefsName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("listSeparator", s_listSeparator);
        editor.putBoolean("dataInColumns", m_bColumns);
        editor.putBoolean("labels1stRow", m_bLabels1stRow);
        editor.putBoolean("x1stColumn", s_bx1stColumn);
        editor.putString("ignoreLines", String.format("%d",s_nLinesIgnored));

        for(int is=0; is<s_channelName.size(); is++)
        {
            editor.putBoolean("varVisibility"+is, s_graphView.s_bShowCurve.get(is));
            editor.putBoolean("varShowPoints"+is, s_graphView.s_bShowPoints.get(is));

            editor.putString("xSelPreference"+is, String.format("%d", s_graphView.s_ixVariable.get(is)));

            if(s_graphView.s_iyAxis.get(is)==0) editor.putString("varAxis"+is, "Left axis");
            else                                editor.putString("varAxis"+is, "Right axis");


            if(s_graphView.s_iCurveStyle.get(is)==0)       editor.putString("varStyle"+is, "solid");
            else if(s_graphView.s_iCurveStyle.get(is)==1)  editor.putString("varStyle"+is, "dotted");
            else if(s_graphView.s_iCurveStyle.get(is)==2)  editor.putString("varStyle"+is, "dashed");

            editor.putString("varWidth"+is, String.format("%d", s_graphView.s_iCurveWidth.get(is)));
            editor.putString("varColor"+is, s_graphView.s_curveColor.get(is));
        }

        editor.commit();
    }



    /**Loads the last saved settings from the preferences file*/
    private void loadSharedPreferences()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String strong;

        s_defaultDirPath = sp.getString("defaultDir", s_defaultDirPath);
        s_fileName       = sp.getString("fileName", s_fileName);
        s_filePrefsName  = sp.getString("filePrefsName", "");

        s_graphView.s_bGrid[0]    = sp.getBoolean("xGridShow", true);
        s_graphView.s_bGrid[1]    = sp.getBoolean("y0GridShow", true);
        s_graphView.s_bGrid[2]    = sp.getBoolean("y1GridShow", true);
        s_graphView.s_bAlignYZero = sp.getBoolean("yLockZero", false);

        strong = sp.getString("xGridStylePreference", "Dotted");
        s_graphView.s_iGridStyle[0] = getStyle(strong.trim());
        strong = sp.getString("y0GridStylePreference", "Dashed");
        s_graphView.s_iGridStyle[1] = getStyle(strong.trim());
        strong = sp.getString("y1GridStylePreference", "Dashed");
        s_graphView.s_iGridStyle[2] = getStyle(strong.trim());

        strong = sp.getString("xGridColorPreference", "lightgrey");
        s_graphView.s_iGridColor[0] = colorFromName(strong);
        strong = sp.getString("y0GridColorPreference", "lightgrey");
        s_graphView.s_iGridColor[1] = colorFromName(strong);
        strong = sp.getString("y1GridColorPreference", "lightgrey");
        s_graphView.s_iGridColor[2] = colorFromName(strong);

        AndroidGraphView.s_fontType = sp.getString("graphFontType", "sans-serif-light");
        AndroidGraphView.s_fontStyle = sp.getString("graphFontStyle", "normal");

        try
        {
            s_graphView.s_Graph.setAutoXLimits(!sp.getBoolean("xAxisManualScale", false));
            s_graphView.s_Graph.setAutoYLimits(0, !sp.getBoolean("y0AxisManualScale", false));
            s_graphView.s_Graph.setAutoYLimits(1, !sp.getBoolean("y1AxisManualScale", false));

            strong = sp.getString("xMinAxis","0.0");
            s_graphView.s_Graph.setMin(0, Double.parseDouble(strong));
            strong = sp.getString("xMaxAxis","1.0");
            s_graphView.s_Graph.setMax(0, Double.parseDouble(strong));

            strong = sp.getString("y0MinAxis","0.0");
            s_graphView.s_Graph.setMin(1, Double.parseDouble(strong));
            strong = sp.getString("y0MaxAxis","1.0");
            s_graphView.s_Graph.setMax(1, Double.parseDouble(strong));

            strong = sp.getString("y1MinAxis","0.0");
            s_graphView.s_Graph.setMin(2, Double.parseDouble(strong));
            strong = sp.getString("y1MaxAxis","1.0");
            s_graphView.s_Graph.setMax(2, Double.parseDouble(strong));

            strong = sp.getString("graphFontSize", "23");
            AndroidGraphView.s_iTextSize = Integer.parseInt(strong);

            strong = sp.getString("leftMargin","83");
            s_graphView.s_iGraphMargin[0] = Integer.parseInt(strong);
            strong = sp.getString("rightMargin","79");
            s_graphView.s_iGraphMargin[1] = Integer.parseInt(strong);
            strong = sp.getString("toptMargin","51");
            s_graphView.s_iGraphMargin[2] = Integer.parseInt(strong);
            strong = sp.getString("botMargin","37");
            s_graphView.s_iGraphMargin[3] = Integer.parseInt(strong);

            strong = sp.getString("xGridWidthPreference", "1");
            s_graphView.s_iGridWidth[0] = Integer.parseInt(strong.trim());
            strong = sp.getString("y0GridWidthPreference", "1");
            s_graphView.s_iGridWidth[1] = Integer.parseInt(strong.trim());
            strong = sp.getString("y1GridWidthPreference", "1");
            s_graphView.s_iGridWidth[2] = Integer.parseInt(strong.trim());
        }
        catch (NumberFormatException Ignore)
        {
        }

        AndroidGraphView.s_bHdwAcceleration  = sp.getBoolean("hardwareAcceleration", false);

        s_bResetGraphStyle = true;
        s_bResetCurveStyles = true;
        s_bRefillCurves = true;
        s_bResetScales = true;
    }


    public  String colorNameFromIndex(int index)
    {
        String[] clr = getResources().getStringArray(R.array.curveColorStringArray);
        return clr[index];
    }


    public int colorFromIndex(int index)
    {
        String[] clr = getResources().getStringArray(R.array.curveColorStringArray);
        int colour = getResources().getIdentifier(clr[index],  "color", getPackageName());
        return getResources().getColor(colour);
    }

    /** Returns a color as an int from the color name
     *
     * @param colorName : the color's name using the www standard
     * @return          : the color's int value
     */
    public int colorFromName(String colorName)
    {
        int clr = getResources().getIdentifier(colorName,  "color", getPackageName());
        return getResources().getColor(clr);
    }


    /** Returns a style as an int from the style name
     *
     * @param styleName : the style's name
     * @return          : the style's int value
     */
    int getStyle(String styleName)
    {
        if(styleName.equalsIgnoreCase("dotted"))      return 1;
        else if(styleName.equalsIgnoreCase("dashed")) return 2;
        else                                          return 0;
    }

    private void setupLayout()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        s_graphView = new AndroidGraphView(this, dm);
        LinearLayout layout = (LinearLayout)findViewById(R.id.lineGraph);
        layout.addView(s_graphView);
    }


    private void addCurves()
    {
        if(s_graphView!=null)
        {
            s_graphView.deleteCurves();
            s_bResetCurveStyles =true;
        }

        JCurve pCurve;
        for(int is=0; is<s_channelName.size(); is++)
        {
            if(s_graphView.s_ixVariable.get(is)==-1)
            {
                pCurve = s_graphView.addCurve(m_doubleData[is]);
            }
            else
            {
                pCurve = s_graphView.addCurve(m_doubleData[s_graphView.s_ixVariable.get(is)], m_doubleData[is]);
            }
            if(pCurve!=null)
            {
                pCurve.setAxis(s_graphView.s_iyAxis.get(is));
            }
            pCurve.setTitle(s_channelName.get(is));
        }
    }



    public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data)
    {
        if (requestCode == REQUEST_LOAD)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                //save the settings for the current graph
//                saveFileSharedPreferences();

                //then load the new file
                s_fileName          = data.getStringExtra(PrefsFileActivity.RESULT_PATH);
                s_listSeparator     = data.getStringExtra(PrefsFileActivity.LIST_SEPARATOR);
                s_decimalSeparator  = data.getStringExtra(PrefsFileActivity.DECIMAL_SEPARATOR);
                m_bColumns          = data.getBooleanExtra(PrefsFileActivity.DATA_IN_COLUMNS, true);
                m_bLabels1stRow     = data.getBooleanExtra(PrefsFileActivity.LABELS_IN_FIRST_ROW, true);
                s_bx1stColumn       = data.getBooleanExtra(PrefsFileActivity.X_IN_FIRST_COLUMN, true);
                s_nLinesIgnored     = data.getIntExtra(PrefsFileActivity.IGNORE_LINES, 0);

                int pos = s_fileName.lastIndexOf(slash);
                if(pos>=0)
                {
                    s_defaultDirPath = s_fileName.substring(0, pos);
                    Globals.s_ExportDir = new File(s_defaultDirPath);

                    s_filePrefsName = s_fileName.substring(pos+1);
                }
                else
                {
                    s_filePrefsName = s_fileName;
                }

                pos = s_filePrefsName.lastIndexOf(".");
                if(pos>=0)
                {
                    s_filePrefsName = s_filePrefsName.substring(0,pos);
                }


                readData();
                loadFilePreferences();
                if(s_bx1stColumn && s_channelName.size()>0)
                {
                    for(int is=1; is<s_channelName.size(); is++)
                    {
                        s_graphView.s_ixVariable.set(is,0);
                    }
                    s_graphView.s_bShowCurve.set(0, false);
                    s_graphView.s_bShowPoints.set(0, false);
                }
                addCurves();
                resetGraph();

                String fileName;
                int ind = s_fileName.lastIndexOf("/")+1;
                fileName = s_fileName.substring(ind);
                setTitle(fileName);
            }
        }
        else if(requestCode==RESULT_GRAPH_VARIABLES)
        {
            s_bRefillCurves = true;
            s_bResetGraphStyle = true;
            addCurves();
            resetGraph();
        }
        else if(requestCode==RESULT_GRAPH_SETTINGS)
        {
            s_bRefillCurves = true;
            s_bResetGraphStyle = true;
            s_bResetScales = true;
            s_bResetMargins = true;
            s_bResetLayerType = true;
        }
    }


    /** Sets an application exit action for the Android return button*/
    public final boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Exit session");
            alert.setMessage("Exit" +getString(R.string.app_name)+  "?");


            alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    saveSharedPreferences();
                    saveFileSharedPreferences();
                    System.exit(0);
                }
            });

            alert.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    // Canceled.
                }
            });

            alert.show();
        }
        return false;
    }


    public void onConfigurationChanged(Configuration config)
    {
        super.onConfigurationChanged(config);
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //		int orientation = display.getRotation();
        Point size = new Point();
        display.getSize(size);
        s_bLandscape = size.x>size.y;
        s_graphView.setLandscapeMode(s_bLandscape);
        s_graphView.setGraphStyle();
        s_bResetGraphStyle =false;
        s_graphView.invalidate();
    }

}
