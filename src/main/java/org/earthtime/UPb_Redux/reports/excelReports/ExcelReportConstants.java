/*
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.earthtime.UPb_Redux.reports.excelReports;

import jxl.format.Alignment;
import jxl.format.BoldStyle;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;

/**
 *  ERConst _ ExcelReport Constants
 * @author Jonathan S. Tashakori
 */
public class ExcelReportConstants {
  /**
     * General File and Dialog Options
     */
    protected static final String DIALOG_TITLE = "Select the file name to save the Excel report to.",
                                DEFAULT_SAVE_DIRECTORY = "user.home",
                                FILE_EXTENSION = ".xls",
                                SHEET_NAME = "Results";
    /**
     * 
     */
    protected static final int SHEET_INDEX = 0;

    /** Excel Format Options **/
    protected static final int EXCEL_FONT_SIZE = 10,
                             STANDARD_COLUMN_WIDTH = 13, //sets a standard width only when CONFORM_COLUMN_WIDTH_TO_LONGEST_VISIBLE_TABLE_VALUE_PLUS_CCW_OFFSET is false
                             CATEGORY_SEPARATING_COLUMN_WIDTH = 2, //character width
                             CCW_OFFSET = 3; //additional offset when conforming col width/ to longest table value

    /**
     * 
     */
    protected static final String EXCEL_FONT = "Lucida Sans Typewriter";

    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    protected static final boolean CATEGORY_SEPARATING_COLUMNS = true,
                                 WRITE_CATEGORY_NAMES_ONCE = true, //false => write Category name for each column
                                 WRITE_SAMPLE_NAME = true,
                                 CONTIGUOUS_SAMPLE_NAME_ROW_BORDER = true, //span all excelColumns and Separating Columns
                                 CONFORM_COLUMN_WIDTHS_TO_LONGEST_VISIBLE_TABLE_VALUE_PLUS_CCW_OFFSET = true, //(excluding category names) .. false => use STANDARD_COLUMN_WIDTH ...set additional offset (CCW_OFFSET)
                                 WRAP_CELL_DATA = false,
                                 SUPPRESS_WARNINGS = true,
                                 WRITE_INVISIBLE_DATA = false;

    /** Sample's reportTable Format.  **/
    protected static final int CATEGORY_ROW = 0,
                               FIRST_CATEGORY_COLUMN = 3,
                               HEADER1_ROW = 1,
                               FIRST_HEADER1_COLUMN = 3,
                               HEADER2_ROW = 2,
                               FIRST_HEADER2_COLUMN = 3,
                               FIRST_DATA_ROW = 7,
                               FIRST_DATA_COLUMN = 3,
                               VISIBILITY_COLUMN = 0,
                               FRACTION_COLUMN = 2,
                               ALIQUOT_IDENTIFIER_COLUMN = 1,
                               ALIQUOT_NAME_COLUMN = 2,
                               FORMAT_STRING_ROW = 5;

    //By which row an uncertainty column can be identified
    /**
     * 
     */
    protected static final int IDENTIFYING_UNCERTAINTY_ROW = HEADER2_ROW;
    /**
     * 
     */
    protected static final String VISIBLITY_INDICATOR_STRING = "true";

    /**Excel's table Offsets. **/
    protected static final int EXCEL_SAMPLE_NAME_ROW = 0,
                               EXCEL_SAMPLE_NAME_COLUMN = 0,
                               EXCEL_CATEGORY_ROW = 1,
                               EXCEL_FRACTION_ROW = 3,
                               EXCEL_FRACTION_COLUMN = 0,
                               EXCEL_HEADER1_ROW = 2,
                               EXCEL_HEADER1_FIRST_COLUMN = 1,
                               EXCEL_HEADER2_ROW = 3,
                               EXCEL_HEADER2_FIRST_COLUMN = 1,
                               EXCEL_ALIQUOT_COLUMN = 0,
                               EXCEL_ALIQUOT_IDENTIFIER_FIRST_ROW = EXCEL_FRACTION_ROW + 1,
                               EXCEL_FIRST_DATA_ROW = 5,
                               EXCEL_FRACTION_FIRST_DATA_ROW = 4,
                               EXCEL_REPORTTABLE_COLUMN_DIFFERENCE = -3; //write reportTableIndex - 3

    /**Excel's table Format **/
    protected static final Border SAMPLE_NAME_BORDER = Border.BOTTOM,
                                  CATEGORY_BORDER = Border.BOTTOM,
                                  HEADER1_BORDER = Border.TOP,
                                  HEADER2_BORDER = Border.BOTTOM,
                                  DATA_BORDER = Border.NONE,
                                  FRACTION_BORDER = HEADER2_BORDER,
                                  ALIQUOT_IDENTIFIER_BORDER = Border.NONE,
                                  ALIQUOT_NAME_BORDER = Border.NONE,
                                  UNCERTAINTY_BORDER = DATA_BORDER;
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    protected static final BorderLineStyle SAMPLE_NAME_BORDERLINESTYLE = BorderLineStyle.THIN,
                                           CATEGORY_BORDERLINESTYLE = BorderLineStyle.THIN,
                                           HEADER1_BORDERLINESTYLE = BorderLineStyle.THIN,
                                           HEADER2_BORDERLINESTYLE = BorderLineStyle.THIN,
                                           DATA_BORDERLINESTYLE = BorderLineStyle.NONE,
                                           FRACTION_BORDERLINESTYLE = HEADER2_BORDERLINESTYLE,
                                           ALIQUOT_IDENTIFIER_BORDERLINESTYLE = BorderLineStyle.NONE,
                                           ALIQUOT_NAME_BORDERLINESTYLE = BorderLineStyle.NONE,
                                           UNCERTAINTY_BORDERLINESTYLE = DATA_BORDERLINESTYLE;
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    protected static final Alignment SAMPLE_NAME_ALIGNMENT = Alignment.GENERAL,
                                     CATEGORY_ALIGNMENT = Alignment.LEFT,
                                      HEADER1_ALIGNMENT = Alignment.CENTRE,
                                     HEADER2_ALIGNMENT = Alignment.CENTRE,
                                     DATA_ALIGNMENT = Alignment.CENTRE,
                                     NUMERIC_DATA_ALIGNMENT = Alignment.RIGHT,
                                     FRACTION_ALIGNMENT = Alignment.LEFT,
                                     ALIQUOT_IDENTIFIER_ALIGNMENT = Alignment.LEFT,
                                     ALIQUOT_NAME_ALIGNMENT = Alignment.CENTRE,
                                     UNCERTAINTY_HEADER1_ALIGNMENT = Alignment.CENTRE,
                                     UNCERTAINTY_HEADER2_ALIGNMENT = Alignment.CENTRE,
                                     UNCERTAINTY_DATA_ALIGNMENT = Alignment.CENTRE;
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    protected static final BoldStyle SAMPLE_NAME_BOLDSTYLE = BoldStyle.BOLD,
                                     CATEGORY_BOLDSTYLE = BoldStyle.NORMAL,
                                     HEADER1_BOLDSTYLE = BoldStyle.NORMAL,
                                     HEADER2_BOLDSTYLE = BoldStyle.NORMAL,
                                     DATA_BOLDSTYLE = BoldStyle.NORMAL,
                                     FRACTION_BOLDSTYLE = BoldStyle.NORMAL,
                                     ALIQUOT_IDENTIFIER_BOLDSTYLE = BoldStyle.NORMAL,
                                     ALIQUOT_NAME_BOLDSTYLE = BoldStyle.NORMAL,
                                     UNCERTAINTY_BOLDSTYLE = DATA_BOLDSTYLE;
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    protected static final boolean SAMPLE_NAME_ITALIC = false,
                                   CATEGORY_ITALIC = true,
                                   HEADER1_ITALIC = false,
                                   HEADER2_ITALIC = false,
                                   DATA_ITALIC = false,
                                   FRACTION_ITALIC = false,
                                   ALIQUOT_IDENTIFIER_ITALIC = true,
                                   ALIQUOT_NAME_ITALIC = false,
                                   UNCERTAINTY_ITALIC = DATA_ITALIC;
    /**
     * 
     */
    protected static final String FRACTION_CATEGORY_NAME = "Fraction";
    /** Page Setup Options **/
    protected static final PaperSize PAPER_SIZE = PaperSize.LETTER;
    /**
     * 
     */
    protected static final PageOrientation PAGE_ORIENTATION = PageOrientation.LANDSCAPE;
    /**
     * 
     */
    protected static final short SCALE_FACTOR = 50; // (%)
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    protected static final double LEFT_MARGIN = .70,
                                RIGHT_MARGIN = .70,
                                TOP_MARGIN = .75,
                                BOTTOM_MARGIN = .75;

    /**
     * 
     */
    protected static final String UNCERTAINTY_REGEX = ".*(\u03C3|[eE]rror|%|[Cc]oef|[Cc]orr).*";
};
