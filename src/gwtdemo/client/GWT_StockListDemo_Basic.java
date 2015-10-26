/*
 * Copyright (c) Lightstreamer Srl
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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * GWT Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWT_StockListDemo_Basic implements EntryPoint {

    /**
     * List of items available on the Lightsteamer server.
     */
    private final static String[] items = {"item1", "item2", "item3",
        "item4", "item5", "item6", "item7", "item8", "item9", "item10"};

    /**
     * Lightstreamer field names available on the Lightstreamer server for given
     * items.
     */
    private final static LSGWTFieldElement[] fields = {
        new LSGWTFieldElement("stock_name", "Name"),
        new LSGWTFieldElement("last_price", "Price"),
        new LSGWTFieldElement("time", "Time"),
        new LSGWTFieldElement("pct_change", "Change"),
        new LSGWTFieldElement("bid_quantity", "Bid Size"),
        new LSGWTFieldElement("bid", "Bid"),
        new LSGWTFieldElement("ask", "Ask"),
        new LSGWTFieldElement("ask_quantity", "Ask Size"),
        new LSGWTFieldElement("min", "Min"),
        new LSGWTFieldElement("max", "Max"),
        new LSGWTFieldElement("ref_price", "Ref."),
        new LSGWTFieldElement("open_price", "Open"),
    };

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        //load the configuration
        HashMap<String,String> config = LSGWTClientProperties.readProperties(((LSGWTResources)GWT.create(LSGWTResources.class)).lsconfig().getText());
                
        // create LSGWTLightstreamerClient instance that will handle connection and subscription
        final LSGWTLightstreamerClient client = new LSGWTLightstreamerClient();
        
        // configure the LightstreamerClient
        client.setAdapterSet("DEMO");
        client.setEngineName(config.get("engine_name"));
        client.setHost(config.get("host"));
        client.setStatusImgDir(config.get("images_dir"));
        client.setPort(config.get("port"));
        client.setEngineKind(config.get("engine_kind"));
        
      	client.start();
      	
        //create a LSGWTDemoTable representing our subscription
        final LSGWTDemoTable grid = new LSGWTDemoTable(GWT_StockListDemo_Basic.fields,GWT_StockListDemo_Basic.items, LSGWTSubscription.SubscriptionMode.MERGE);
        grid.setRequestedSnapshot("yes");
        grid.setDataAdapter("QUOTE_ADAPTER");
        
        // start the subscription
        client.subscribe(grid);
    }
    
    static class LSGWTClientProperties {
        
        //receives a property files as a String and returns it as a HashMap
        static public HashMap<String,String> readProperties(String configString) {
            final HashMap<String,String> map = new HashMap<String,String>();
            int endLineIndex;
            for (int startIndex = 0; startIndex < configString.length(); startIndex = endLineIndex + 1) {

                endLineIndex = configString.indexOf('\n', startIndex);
                final String line = configString.substring(startIndex,
                        endLineIndex == -1 ? configString.length() : endLineIndex)
                        .trim();
                if (!"".equals(line) && !line.startsWith("#")) {
                    addNameValue(line, map);
                }
                if (endLineIndex == -1) {
                    break;
                }

            }

            return map;
        }

        static private void addNameValue(String line, HashMap<String,String> map) {

            final int equalsIndex = line.indexOf('=');
            final String key = line.substring(0, equalsIndex).trim();
            final int hashIndex = line.indexOf('#');
            final String value = line.substring(equalsIndex + 1,
                    hashIndex == -1 ? line.length() : hashIndex).trim();
            map.put(key, value);

        }
        
    }

}


