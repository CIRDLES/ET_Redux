package org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author James F. Bowring
 */
public class Preferences {

    /**
     *
     * @throws Exception
     */
    public Preferences() throws Exception {
        this(true);
    }

    /**
     *
     * @param densityplotter
     * @throws Exception
     */
    public Preferences(boolean densityplotter) throws Exception {
        this.densityplotter = densityplotter;
        this.fname = (System.getProperty("user.dir") + "/.");
        fname += densityplotter ? "densityplotter" : "radialplotter";
        this.file = new File(this.fname);
        this.preferences = new HashMap<>();
        read();
    }

    /**
     *
     * @throws Exception
     */
    public void delete() throws Exception {
        this.file.delete();
    }

    /**
     *
     * @throws Exception
     */
    public void read() throws Exception {
        String aLine;
        try {
            FileInputStream fin = new FileInputStream(fname);
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            int numtokens = 0;
            for (int i = 0; (aLine = br.readLine()) != null; i++) {
                StringTokenizer st = new StringTokenizer(aLine, ": ");
                numtokens = st.countTokens();
                if (numtokens == 2) {
                    this.put(st.nextToken(), st.nextToken());
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            this.defaults();
        } catch (Exception e) {
            System.err.println("Problem in Preferences:read()");
        }
    }

    /**
     *
     * @throws Exception
     */
    public void write() throws Exception {
        String nl = System.getProperties().getProperty("line.separator");
        BufferedWriter out = new BufferedWriter(new FileWriter(fname));
        Iterator<Map.Entry<String, String>> it = preferences.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            out.write(entry.getKey() + ": " + entry.getValue() + nl);
        }
        out.close();
    }

    /**
     *
     * @throws Exception
     */
    public void defaults() throws Exception {
        if (densityplotter) {
            this.input("other"); // options: fissiontracks or other
            this.output("densityplot"); // options: radialplot or densityplot
            this.transformation("linear"); // options: linear or logarithmic
        } else {
            this.input("fissiontracks");
            this.output("radialplot");
            this.transformation("logarithmic"); // options: linear, logarithmic or arcsin
        }
        this.setLabels();
        this.sigmalines(false);
        this.datalabels(false);
        this.doKDEfill(true);
        this.doKDEstroke(true);
        this.doPDPfill(false);
        this.doPDPstroke(false);
        this.dohistfill(false);
        this.dohiststroke(true);
        this.dopointsfill(false);
        this.dopointstroke(true);
        this.saveprefs(false);
        this.KDEfillcolour(hexstring(Preferences.KDEAREACOLOUR));
        this.KDEstrokecolour(hexstring(Preferences.KDELINECOLOUR));
        this.PDPfillcolour(hexstring(Preferences.PDPAREACOLOUR));
        this.PDPstrokecolour(hexstring(Preferences.PDPLINECOLOUR));
        this.histfillcolour(hexstring(Preferences.HISTAREACOLOUR));
        this.histstrokecolour(hexstring(Preferences.HISTLINECOLOUR));
        this.pointsfillcolour(hexstring(Preferences.POINTSAREACOLOUR));
        this.pointstrokecolour(hexstring(Preferences.POINTSLINECOLOUR));
        this.minbarcolour(hexstring(Preferences.MINBARCOLOUR));
        this.maxbarcolour(hexstring(Preferences.MAXBARCOLOUR));
        this.write();
    }

    /**
     *
     * @return @throws Exception
     */
    public String input() throws Exception {
        return (String) this.preferences.get("input");
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean fissiontracks() throws Exception {
        return ((String) this.preferences.get("input")).equals("fissiontracks");
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean other() throws Exception {
        return ((String) this.preferences.get("input")).equals("other");
    }

    private void put(String key, String value) throws Exception {
        this.preferences.put(key, value);
        write();
    }

    /**
     *
     * @throws Exception
     */
    public void setLabels() throws Exception {
        if (fissiontracks()) {
            this.xlabel("Ns");
            this.ylabel("Ni");
            this.zlabel("[Dpar]");
        } else if (other()) {
            this.xlabel("x");
            this.ylabel("se(x)");
            this.zlabel("[c]");
        }
    }

    private String hexstring(Color colour) {
        return "0x" + Integer.toHexString(colour.getRGB()).substring(2, 8);
    }

    /**
     *
     * @param input
     * @throws Exception
     */
    public void input(String input) throws Exception {
        put("input", input);
    }

    /**
     *
     * @return @throws Exception
     */
    public String output() throws Exception {
        return (String) this.preferences.get("output");
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean densityplot() throws Exception {
        return ((String) this.preferences.get("output")).equals("densityplot");
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean radialplot() throws Exception {
        return ((String) this.preferences.get("output")).equals("radialplot");
    }

    /**
     *
     * @param output
     * @throws Exception
     */
    public void output(String output) throws Exception {
        put("output", output);
    }

    /**
     *
     * @return @throws Exception
     */
    public String transformation() throws Exception {
        return (String) this.preferences.get("transformation");
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean linear() throws Exception {
        return ((String) this.preferences.get("transformation")).equals("linear");
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean logarithmic() throws Exception {
        return ((String) this.preferences.get("transformation")).equals("logarithmic");
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean arcsin() throws Exception {
        return ((String) this.preferences.get("transformation")).equals("arcsin");
    }

    /**
     *
     * @param transformation
     * @throws Exception
     */
    public void transformation(String transformation) throws Exception {
        put("transformation", transformation);
    }

    /**
     *
     * @return @throws Exception
     */
    public String xlabel() throws Exception {
        return (String) this.preferences.get("xlabel");
    }

    /**
     *
     * @param xlabel
     * @throws Exception
     */
    public void xlabel(String xlabel) throws Exception {
        put("xlabel", xlabel);
    }

    /**
     *
     * @return @throws Exception
     */
    public String ylabel() throws Exception {
        return (String) this.preferences.get("ylabel");
    }

    /**
     *
     * @param ylabel
     * @throws Exception
     */
    public void ylabel(String ylabel) throws Exception {
        put("ylabel", ylabel);
    }

    /**
     *
     * @return @throws Exception
     */
    public String zlabel() throws Exception {
        return (String) this.preferences.get("zlabel");
    }

    /**
     *
     * @param zlabel
     * @throws Exception
     */
    public void zlabel(String zlabel) throws Exception {
        put("zlabel", zlabel);
    }

    private void boolput(String key, boolean value) throws Exception {
        String text = value ? "true" : "false";
        put(key, text);
    }

    private boolean boolget(String key) throws Exception {
        return ((String) this.preferences.get(key)).equals("true");
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean sigmalines() throws Exception {
        return boolget("plot_2-sigma_lines");
    }

    /**
     *
     * @param sigmalines
     * @throws Exception
     */
    public void sigmalines(boolean sigmalines) throws Exception {
        boolput("plot_2-sigma_lines", sigmalines);
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean datalabels() throws Exception {
        return boolget("plot_data_labels");
    }

    /**
     *
     * @param datalabels
     * @throws Exception
     */
    public void datalabels(boolean datalabels) throws Exception {
        boolput("plot_data_labels", datalabels);
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean doKDEfill() throws Exception {
        return boolget("fill_KDE");
    }

    /**
     *
     * @param doKDEfill
     * @throws Exception
     */
    public void doKDEfill(boolean doKDEfill) throws Exception {
        boolput("fill_KDE", doKDEfill);
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean doKDEstroke() throws Exception {
        return boolget("stroke_KDE");
    }

    /**
     *
     * @param doKDEstroke
     * @throws Exception
     */
    public void doKDEstroke(boolean doKDEstroke) throws Exception {
        boolput("stroke_KDE", doKDEstroke);
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean doPDPfill() throws Exception {
        return boolget("fill_PDP");
    }

    /**
     *
     * @param doPDPfill
     * @throws Exception
     */
    public void doPDPfill(boolean doPDPfill) throws Exception {
        boolput("fill_PDP", doPDPfill);
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean doPDPstroke() throws Exception {
        return boolget("stroke_PDP");
    }

    /**
     *
     * @param doPDPstroke
     * @throws Exception
     */
    public void doPDPstroke(boolean doPDPstroke) throws Exception {
        boolput("stroke_PDP", doPDPstroke);
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean dohistfill() throws Exception {
        return boolget("fill_histogram");
    }

    /**
     *
     * @param dohistfill
     * @throws Exception
     */
    public void dohistfill(boolean dohistfill) throws Exception {
        boolput("fill_histogram", dohistfill);
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean dohiststroke() throws Exception {
        return boolget("stroke_histogram");
    }

    /**
     *
     * @param dohiststroke
     * @throws Exception
     */
    public void dohiststroke(boolean dohiststroke) throws Exception {
        boolput("stroke_histogram", dohiststroke);
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean dopointsfill() throws Exception {
        return boolget("fill_data_points");
    }

    /**
     *
     * @param dopointsfill
     * @throws Exception
     */
    public void dopointsfill(boolean dopointsfill) throws Exception {
        boolput("fill_data_points", dopointsfill);
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean dopointstroke() throws Exception {
        return boolget("stroke_data_points");
    }

    /**
     *
     * @param dopointstroke
     * @throws Exception
     */
    public void dopointstroke(boolean dopointstroke) throws Exception {
        boolput("stroke_data_points", dopointstroke);
    }

    /**
     *
     * @return @throws Exception
     */
    public boolean saveprefs() throws Exception {
        return boolget("save_preferences");
    }

    /**
     *
     * @param saveprefs
     * @throws Exception
     */
    public void saveprefs(boolean saveprefs) throws Exception {
        boolput("save_preferences", saveprefs);
    }

    /**
     *
     * @return @throws Exception
     */
    public Color KDEfillcolour() throws Exception {
        return Color.decode((String) this.preferences.get("KDE_fill_colour"));
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void KDEfillcolour(String colour) throws Exception {
        put("KDE_fill_colour", colour);
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void KDEfillcolour(Color colour) throws Exception {
        KDEfillcolour(hexstring(colour));
    }

    /**
     *
     * @return @throws Exception
     */
    public Color KDEstrokecolour() throws Exception {
        return Color.decode((String) this.preferences.get("KDE_stroke_colour"));
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void KDEstrokecolour(String colour) throws Exception {
        put("KDE_stroke_colour", colour);
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void KDEstrokecolour(Color colour) throws Exception {
        KDEstrokecolour(hexstring(colour));
    }

    /**
     *
     * @return @throws Exception
     */
    public Color PDPfillcolour() throws Exception {
        return Color.decode((String) this.preferences.get("PDP_fill_colour"));
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void PDPfillcolour(String colour) throws Exception {
        put("PDP_fill_colour", colour);
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void PDPfillcolour(Color colour) throws Exception {
        PDPfillcolour(hexstring(colour));
    }

    /**
     *
     * @return @throws Exception
     */
    public Color PDPstrokecolour() throws Exception {
        return Color.decode((String) this.preferences.get("PDP_stroke_colour"));
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void PDPstrokecolour(String colour) throws Exception {
        put("PDP_stroke_colour", colour);
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void PDPstrokecolour(Color colour) throws Exception {
        PDPstrokecolour(hexstring(colour));
    }

    /**
     *
     * @return @throws Exception
     */
    public Color histfillcolour() throws Exception {
        return Color.decode((String) this.preferences.get("hist_fill_colour"));
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void histfillcolour(String colour) throws Exception {
        put("hist_fill_colour", colour);
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void histfillcolour(Color colour) throws Exception {
        histfillcolour(hexstring(colour));
    }

    /**
     *
     * @return @throws Exception
     */
    public Color histstrokecolour() throws Exception {
        return Color.decode((String) this.preferences.get("hist_stroke_colour"));
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void histstrokecolour(String colour) throws Exception {
        put("hist_stroke_colour", colour);
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void histstrokecolour(Color colour) throws Exception {
        histstrokecolour(hexstring(colour));
    }

    /**
     *
     * @return @throws Exception
     */
    public Color pointsfillcolour() throws Exception {
        return Color.decode((String) this.preferences.get("points_fill_colour"));
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void pointsfillcolour(String colour) throws Exception {
        put("points_fill_colour", colour);
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void pointsfillcolour(Color colour) throws Exception {
        pointsfillcolour(hexstring(colour));
    }

    /**
     *
     * @return @throws Exception
     */
    public Color pointstrokecolour() throws Exception {
        return Color.decode((String) this.preferences.get("points_stroke_colour"));
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void pointstrokecolour(String colour) throws Exception {
        put("points_stroke_colour", colour);
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void pointstrokecolour(Color colour) throws Exception {
        pointstrokecolour(hexstring(colour));
    }

    /**
     *
     * @return @throws Exception
     */
    public Color minbarcolour() throws Exception {
        return Color.decode((String) this.preferences.get("min_bar_colour"));
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void minbarcolour(String colour) throws Exception {
        put("min_bar_colour", colour);
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void minbarcolour(Color colour) throws Exception {
        minbarcolour(hexstring(colour));
    }

    /**
     *
     * @return @throws Exception
     */
    public Color maxbarcolour() throws Exception {
        return Color.decode((String) this.preferences.get("max_bar_colour"));
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void maxbarcolour(String colour) throws Exception {
        put("max_bar_colour", colour);
    }

    /**
     *
     * @param colour
     * @throws Exception
     */
    public void maxbarcolour(Color colour) throws Exception {
        maxbarcolour(hexstring(colour));
    }

    private String fname;
    private HashMap<String, String> preferences;
    private boolean densityplotter;
    private File file;
    static final Color KDEAREACOLOUR = Color.cyan, KDELINECOLOUR = Color.blue,
            PDPAREACOLOUR = Color.magenta, PDPLINECOLOUR = Color.black,
            HISTAREACOLOUR = Color.white, HISTLINECOLOUR = Color.gray,
            POINTSAREACOLOUR = Color.yellow, POINTSLINECOLOUR = Color.black,
            MINBARCOLOUR = Color.yellow, MAXBARCOLOUR = Color.red;
}
