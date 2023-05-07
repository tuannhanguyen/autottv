package com.ttv.at.test.selenium;

import java.util.ArrayList;

public class guielement {

    private String key;
    public String get_key() { return key; }
    public void set_key(String key) { this.key = key; }
    public boolean check_key(String check_key)
    {
        if (check_key != null && key.length() == check_key.length() && key.toLowerCase().equals(check_key.toLowerCase()))
            return true;
        return false;
    }

    com.ttv.at.test.testobject testobject_instance = null;
    public com.ttv.at.test.testobject get_testobject_instance () { return testobject_instance; }

    ArrayList<guiinfo> guiinfos;
    public ArrayList<guiinfo> get_guiinfos() { return guiinfos; }

    public guielement(com.ttv.at.test.testobject testobject_instance)
    {
        key = testobject_instance.get_key();
        this.testobject_instance = testobject_instance;
        if (testobject_instance.get_all_properties() != null && testobject_instance.get_all_properties().size() > 0)
        {
            guiinfos = new ArrayList<guiinfo>();
            for (com.ttv.at.test.testobjectproperties scan_eprop : testobject_instance.get_all_properties())
                guiinfos.add(new guiinfo (scan_eprop));
        }
    }
    public guielement(String key, ArrayList<com.ttv.at.test.property> properties)
    {
        if (key != null && key.length() > 0)
            this.key = key;
        guiinfos = guiinfo.load_from_properties(properties);
    }
    public void reload(String key, ArrayList<com.ttv.at.test.property> properties)
    {
        if (key != null && key.length() > 0)
            this.key = key;
        guiinfos.clear();
        guiinfos = guiinfo.load_from_properties(properties);
    }

    public void update_ref_object(ArrayList guielements)
    {
        if (guiinfos != null)
            for (guiinfo scan_guiinfo : guiinfos)
                scan_guiinfo.update_ref_object(guielements);
    }
}
