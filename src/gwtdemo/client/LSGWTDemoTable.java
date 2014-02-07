/*
 * Copyright 2014 Weswit Srl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gwtdemo.client;

import java.util.HashMap;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This is the NonVisualGWTClient subclass connected to GWT_StockListDemo_Basic
 * UI. <code>onItemUpdate()</code> is overridden in order to feed the HTML page
 * with new data.
 */
class LSGWTDemoTable extends LSGWTSubscription {

    /**
     * This is our list of fields array, provided by <code>GWT_StockListDemo_Basic</code> class.
     */
    private LSGWTFieldElement[] fields;

    /**
     * This is our list of items array, provided by <code>GWT_StockListDemo_Basic</code> class.
     */
    private String[] items;

    /**
     * Internal Hash Table map required to speed up UI updates.
     * Group item id is used as key, while row number is used as value.
     */
    private HashMap<String, Integer> groupCache = new HashMap<String, Integer>();

    /**
     * Internal Hash Table map required to speed up UI updates.
     * Schema item id is used as key, while column number is used as value.
     */
    private HashMap<String, Integer> schemaCache = new HashMap<String, Integer>();

    /** GWT dynamic table that contains all the stock quotes */
    FlexTable htmlTable = new FlexTable();
    
    
    public LSGWTDemoTable(LSGWTFieldElement[] fields, String[] items, SubscriptionMode subscriptionMode) {
        super(fields, items, subscriptionMode);
        this.fields = fields;
        this.items = items;
        this.generateLSHtml();
    }

    /**
     * Generate HTML code required for handling Lightstreamer
     * data updates.
     */
    protected void generateLSHtml() {
        //RootPanel rootDiv =;
    	DockPanel mainDiv = new DockPanel();

        this.htmlTable.clear();
        this.htmlTable.setWidth("766px");
        this.htmlTable.setCellPadding(2);
        this.htmlTable.setCellSpacing(0);
        this.htmlTable.setBorderWidth(0);
        
        /* generate header of table */
        for (int i = 0; i < this.fields.length; i++) {
            LSGWTFieldElement elem = this.fields[i];
            this.schemaCache.put(elem.toString(), new Integer(i));
            this.htmlTable.setText(0, i, elem.getName());
            if (i == 0) {
                this.htmlTable.getCellFormatter().setStylePrimaryName(0, i, "tableTitleStockName");
            }
        }
        /* set CSS class */
        this.htmlTable.getRowFormatter().setStylePrimaryName(0, "tableTitle");

        /* generate element fields */
        this.groupCache.clear();
        for (int i = 0; i < this.items.length; i++) {
            String itemId = this.items[i];
            // first row is table headers
            int realRowNum = i + 1;
            this.groupCache.put(itemId, new Integer(realRowNum));
            String styleName = (realRowNum % 2) == 0 ? "coldEven" : "coldOdd";
            
            for (int n = 0; n < this.fields.length; n++) {
                if (n == 0) {
                    this.htmlTable.setText(realRowNum, n, itemId);
                    this.htmlTable.getCellFormatter().setStylePrimaryName(realRowNum, n, styleName+"StockName");
                } else {
                    this.htmlTable.setText(realRowNum, n, "-");
                    this.htmlTable.getCellFormatter().setStylePrimaryName(realRowNum, n, styleName);
                }
                
            }

            /* set CSS class */
            this.htmlTable.getRowFormatter().setStylePrimaryName(realRowNum, styleName+"Row");
        
        }

        mainDiv.setStyleName("centrDiv");
        mainDiv.add(this.htmlTable, DockPanel.CENTER);
        RootPanel.get("lsGwtDynamic").add(mainDiv);
        
    }

    /**
     * Return the variation between two string encoded double values.
     * @param oldValue old string double value
     * @param value current string double value
     * @return the actual variation, or 0.0 if provided strings can't be converted to double.
     */
    private double getVariation(String oldValue, String value) {
        try {
            double valueInt = Double.parseDouble(value);
            double oldValueInt = Double.parseDouble(oldValue);
            return valueInt - oldValueInt;
        } catch (NumberFormatException nfe) {
            return 0.0;
        }
    }

    class CellSwitchTimer extends Timer {

        Integer row, column;
        String styleName;

        CellSwitchTimer(Integer row, Integer column, String styleName) {
            super();
            this.row = row;
            this.column = column;
            this.styleName = styleName;
        }
        
        @Override
        public void run() {
            LSGWTDemoTable.this.htmlTable.getCellFormatter()
                .removeStyleName(this.row, this.column, styleName);
        }
        
    }

    /**
     * Highlight <code>htmlTable</code> FlexTable cell basing on given variation.
     * @param row table row number
     * @param column table column number
     * @param variation actual variation
     */
    private void highlightCell(Integer row, Integer column, double variation) {
        String styleName;
        if (variation > 0) {
            styleName = "cellUpColor";
        } else {
            styleName = "cellDownColor";
        }
        this.htmlTable.getCellFormatter().addStyleName(row, column, styleName);

        // Start highlight switch-off timer.
        new CellSwitchTimer(row, column, styleName).schedule(600);
    }

    @Override
    public void onItemUpdate(String itemId, String fieldName, String value) {

        Integer row = this.groupCache.get(itemId);
        if (row == null) {
            throw new RuntimeException("Cannot find itemId in cache: " + itemId);
        }
        Integer column = this.schemaCache.get(fieldName);
        if (column == null) {
            throw new RuntimeException("Cannot find field name in cache: " + fieldName);
        }
        String oldValue = this.htmlTable.getText(row, column);
        this.htmlTable.setText(row, column, value);
        // highlight cell and start an un-highlight timer
        double variation = this.getVariation(oldValue, value);
        this.highlightCell(row, column, variation);
        
    }
    
}
