package com.rethinkdb.model;

import org.json.JSONArray;

public class Profile {

    public static Profile fromJSONArray(JSONArray profileObj) {
        if (profileObj == null || profileObj.length() == 0) return null;
        return new Profile(profileObj);
    }

    private JSONArray profileObj;

    private Profile(JSONArray profileObj) {
        this.profileObj = profileObj;
    }

    public JSONArray getProfileObj() {
        return profileObj;
    }
}
