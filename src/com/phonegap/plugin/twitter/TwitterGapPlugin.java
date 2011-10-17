/**
 * 
 */
package com.phonegap.plugin.twitter;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;
import com.twitter.android.*;
import com.twitter.android.Twitter.*;


/**
 * @author mmihok
 *
 */
public class TwitterGapPlugin extends Plugin {

	//public static String FACEBOOK_APP_ID = "";
	public Twitter twitter;
	
	/* (non-Javadoc)
	 * @see com.phonegap.api.Plugin#execute(java.lang.String, org.json.JSONArray, java.lang.String)
	 */
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		PluginResult result = new PluginResult(Status.NO_RESULT);
		result.setKeepCallback(true);
		//JSONObject response = new JSONObject();
		Log.d("TwitterGapPlugin","Plugin called");
		
        // Create twitter object
    	try {
			twitter = new Twitter(data.getString(0), data.getString(1));
			Log.d("TwitterGapPlugin", "Initializing Twitter(" + twitter.getAppKey() + ") object");
		} catch(JSONException ex) {
			Log.d("TwitterGapPlugin", "JSONError: " + ex.getMessage());
		}
		
        if(action.equals("oauth")) {
        	Log.d("TwitterGapPlugin", "Attempting to authorize");
        	if(twitter != null) {
        		if(twitter.isSessionValid()) {
        			Log.d("TwitterGapPlugin", "Already authorized, skipping...");
        			result = new PluginResult(Status.OK);
        		} else {
        			
        			ctx.setActivityResultCallback(this);
        			final TwitterGapPlugin me = this;
        			
        			new Thread(new Runnable() {
        			    public void run() {
        			    	me.webView.post(new Runnable(){
    			    			public void run() {
    			    				me.twitter.authorize(me.ctx, 1234567890, new DialogListener() {
    			    					@Override
    			    					public void onComplete(Bundle values) {
										   Log.d("TwitterGapPlugin", "Twitter authorized [" + twitter.getAccessToken() + "]");
    			    					}
									
    			    					@Override
    			    					public void onTwitterError(TwitterError error) {
										   Log.d("TwitterGapPlugin", "Twitter Error: " + error.getMessage());
    			    					}
									
    			    					@Override
    			    					public void onError(DialogError e) {
										   Log.d("TwitterGapPlugin", "Twitter Error: " + e.getMessage());
    			    					}
									
    			    					@Override
    			    					public void onCancel() {}
    			    				});
    			    			}
        			    	});
        			    }
        			}).start();
        		}
        	} else {
        		
        	}
        }
		
		return result;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
        twitter.authorizeCallback(requestCode, resultCode, intent);
	}
}
