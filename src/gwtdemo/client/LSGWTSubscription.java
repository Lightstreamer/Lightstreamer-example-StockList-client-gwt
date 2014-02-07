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
import com.google.gwt.core.client.JavaScriptObject;

public abstract class LSGWTSubscription {
	 /**
     * Opaque JavaScript object containing the Subscription instance.
     */
    public JavaScriptObject subscription;
    
    /** items update mode */
    public enum SubscriptionMode {
        RAW, MERGE, DISTINCT, COMMAND;
    }
    
    /**
     * Internal item-list (item1, item2, item3, etc) elements cache Hash table.
     */
    protected HashMap<String, Integer> items = new HashMap<String, Integer>();
    
    /**
     * Internal fields-list (last_price, ask, bid, etc) elements cache Hash table.
     */
    protected HashMap<String, Integer> fields = new HashMap<String, Integer>();
   
    /**
     * Updates feeding mode (Stock-List Demo uses MERGE).
     */
    protected String subscriptionMode;
    
    /**
     * LSGWTSubscription abstract class constructor A schema, an item group and a subscription mode needs to be
     * provided.
     * @param schema
     * @param group
     */
    public LSGWTSubscription(LSGWTFieldElement[] schema, String[] group,
            SubscriptionMode mode) {

        // adding list of fields (schema)
        for (int i = 0; i < schema.length; i++) {
            String field = schema[i].toString();
            Integer count = this.fields.get(field);
            if (count == null || count == 0) {
                this.fields.put(field, 1);
            } else {
                count = count + 1;
                this.fields.put(field, count);
            }
        }
        // adding list of items to subscribe
        for (int i=0; i < group.length; i++) {
            String item = group[i];
            Integer count = this.items.get(item);
            if (count == null || count == 0) {
                this.items.put(item, 1);
            } else {
                count = count + 1;
                this.items.put(item, count);
            }
        }
        
        this.subscriptionMode = mode.name();
        
        this.setupSubscription();
    }

    /**
     * Instantiate (if needed) the JavaScript Subscription object.
     */
    private JavaScriptObject setupSubscription() {
        if (this.subscription == null) {
        	this.subscription =  this.initSubscription(this.getItemsArray(), this.getFieldsArray(), this.subscriptionMode);
        }
        return this.subscription;
    }
    
    JavaScriptObject getSubscription() {
        return this.subscription;
    }
    
    /**
     * Internal method that returns a new Subscription JavaScript object.
     */
    private native JavaScriptObject initSubscription(String[] itemList, String[] fieldList,
            String subscriptionMode) /*-{
            	
		var that = this;
		mySub = null;

        mySub = new $wnd.Lightstreamer.Subscription(subscriptionMode, itemList, fieldList);
        
        mySub.addListener({
        	onItemUpdate: function(itemUpdate) {
            	for (i = 0; i < fieldList.length; i++) {
            		
                 	// check whether the schema value is changed. If it's not, skip it
                 	// unless a full data snapshot is being sent.
                 	if (!itemUpdate.isValueChanged(fieldList[i]) && !itemUpdate.isSnapshot()) {
                    	continue;
                 	}
                 	
                 	var itemName = null;
                 	itemName = itemUpdate.getItemName();
                 	
                 	var value = null;
                 	value = itemUpdate.getValue(fieldList[i]);
            	    
                 	if (value != null) {
                    	that.@gwtdemo.client.LSGWTSubscription::onItemUpdate(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(itemName, fieldList[i], value);
                 	}
             	}
        }});
                
        return mySub;
    }-*/;
    
    /**
     * This is the callback where item updates arrive. You have to override this abstract
     * method and implement your own UI update logic.
     * @param itemId Lightstreamer item id
     * @param fieldName Lightstreamer field name (last_price, ask, bid, etc)
     * @param value updated element value
     */
    public abstract void onItemUpdate(String itemId, String fieldName, String value);
    
    public native void setDataAdapter(String dataAdapter) /*-{
    	this.@gwtdemo.client.LSGWTSubscription::subscription.setDataAdapter(dataAdapter);
	}-*/;

    public native void setRequestedSnapshot(String required) /*-{
    	this.@gwtdemo.client.LSGWTSubscription::subscription.setRequestedSnapshot(required);
	}-*/;
 
    /**
     * Return schema elements in String array form.
     */
    private String[] getFieldsArray() {
        return this.fields.keySet().toArray(new String[0]);
    }

    /**
     * Return group elements in String array form.
     */
    private String[] getItemsArray() {
        return this.items.keySet().toArray(new String[0]);
    }
}
