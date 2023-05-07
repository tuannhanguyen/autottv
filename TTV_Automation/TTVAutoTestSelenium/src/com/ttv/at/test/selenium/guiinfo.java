package com.ttv.at.test.selenium;

import java.util.ArrayList;

import com.ttv.at.test.action.util;
import com.ttv.at.test.testobjectproperties.ref_object_type;

public class guiinfo {

    String htmlTagName = null;
    public String get_htmlTagName () {return htmlTagName;}

    String htmlID = null;
    public String get_htmlID () {return htmlID;}

    String htmlName = null;
    public String get_htmlName () {return htmlName;}

    String htmlClassName = null;
    public String get_htmlClassName () {return htmlClassName;}

    String htmlText = null;
    public String get_htmlText () {return htmlText;}

    int htmlMinWidth = -1;
    public int get_htmlMinWidth() { return htmlMinWidth; }
    
    int htmlMaxWidth = -1;
    public int get_htmlMaxWidth() { return htmlMaxWidth; }

    int htmlMinHeight = -1;
    public int get_htmlMinHeight() { return htmlMinHeight; }
    
    int htmlMaxHeight = -1;
    public int get_htmlMaxHeight() { return htmlMaxHeight; }
    
    String runtime = null;
    public String get_runtime() {return runtime;}

    guielement related_object = null;
    public guielement get_related_object() { return related_object; }

    String related_object_id = null;
    public String get_related_object_id() { return related_object_id;  }

    ref_object_type related_type = ref_object_type.NONE;
    public ref_object_type get_related_type() { return related_type; }

    String htmlPagePosition = null;
    public String get_htmlPagePosition() { return htmlPagePosition; }
    
    String htmlXpath =	null;
    public String get_htmlXpath() { return htmlXpath; }
    		
    String htmlLinkText = null;
    public String get_htmlLinkText() { return htmlLinkText; }

    String htmlPartialLinkText = null;
    public String get_htmlPartialLinkText() { return htmlPartialLinkText; }
    
    String htmlCssSelector = null;
    public String get_htmlCssSelector() { return htmlCssSelector; }



    int htmlIndex = 0;
    public int get_htmlIndex() { return htmlIndex; }

    com.ttv.at.test.testobjectproperties testobjectproperties_instance = null;

    private ArrayList<com.ttv.at.test.property> properties;
    public ArrayList<com.ttv.at.test.property> get_properties() { return properties; }

    public guiinfo(com.ttv.at.test.testobjectproperties testobjectproperties_instance)
    {
        this.testobjectproperties_instance = testobjectproperties_instance;
        if (testobjectproperties_instance != null && testobjectproperties_instance.get_properties() != null)
            init_from_properties(testobjectproperties_instance.get_properties());

    }
    public guiinfo(ArrayList<com.ttv.at.test.property> properties)
    {
        init_from_properties(properties);
    }

    public void init_from_properties(ArrayList<com.ttv.at.test.property> properties)
    {
        if (properties != null)
        {
            if (this.properties == null)
                this.properties = new ArrayList<com.ttv.at.test.property>();
            for (com.ttv.at.test.property check_prop : properties)
            {
                if (check_prop.check_key("HTML tagName") || check_prop.check_key("tagName"))
                    htmlTagName = check_prop.get_value();
                else if (check_prop.check_key("HTML ID") || check_prop.check_key("ID"))
                    htmlID = check_prop.get_value();
                else if (check_prop.check_key("HTML Name") || check_prop.check_key("name"))
                    htmlName = check_prop.get_value();
                else if (check_prop.check_key("HTML classname") ||
                    check_prop.check_key("classname") ||
                    check_prop.check_key("class"))
                    htmlClassName = check_prop.get_value();
                else if (check_prop.check_key("text"))
                    htmlText = check_prop.get_value();
                else if (check_prop.check_key("min width"))
                    htmlMinWidth = check_prop.get_int_value();
                else if (check_prop.check_key("max width"))
                    htmlMaxWidth = check_prop.get_int_value();
                else if (check_prop.check_key("min Height"))
                    htmlMinHeight = check_prop.get_int_value();
                else if (check_prop.check_key("max Height"))
                    htmlMaxHeight = check_prop.get_int_value();
                else if (check_prop.check_key("runtime"))
                    runtime = check_prop.get_value();
                else if (check_prop.check_key("index"))
                    htmlIndex = check_prop.get_int_value();
                else if (check_prop.check_key("page position"))
                    htmlPagePosition = check_prop.get_value();
                else if (check_prop.check_key("related type"))
                {
                    String ref_type_text = check_prop.get_value().toLowerCase();
                    if (ref_type_text == "up")
                        related_type = ref_object_type.UP;
                    else if (ref_type_text == "down")
                        related_type = ref_object_type.DOWN;
                    else if (ref_type_text == "left")
                        related_type = ref_object_type.LEFT;
                    else if (ref_type_text == "right")
                        related_type = ref_object_type.RIGHT;
                    else if (ref_type_text == "parent")
                        related_type = ref_object_type.PARENT;
                    else if (ref_type_text == "child")
                        related_type = ref_object_type.CHILD;
                }
                else if (check_prop.check_key("related object"))
                    related_object_id = check_prop.get_value();
                else if (check_prop.check_key("xpath"))
                	htmlXpath = check_prop.get_value();
                else if (check_prop.check_key("Link Text"))
                	htmlLinkText = check_prop.get_value();
                else if (check_prop.check_key("Partial Link Text"))
                	htmlPartialLinkText = check_prop.get_value();
                else if (check_prop.check_key("css Selector"))
                	htmlCssSelector = check_prop.get_value();
                else
                    this.properties.add(check_prop);
            }
        }
        if (testobjectproperties_instance != null && testobjectproperties_instance.get_ref_type() != ref_object_type.NONE && testobjectproperties_instance.get_ref_object() != null) {
        	related_type = testobjectproperties_instance.get_ref_type();
            related_object = new guielement(testobjectproperties_instance.get_ref_object());
        }
        else
        	related_type = related_type.NONE;
    }

    public boolean update_ref_object(ArrayList<guielement> guielements)
    {
        if (related_type != ref_object_type.NONE && related_object_id != null)
        {
            related_object_id = related_object_id.toLowerCase();
            for (guielement cur_guielement : guielements)
            {
                String scan_key = cur_guielement.get_key().toLowerCase();
                if (scan_key.length() == related_object_id.length() && scan_key.equals(related_object_id))
                {
                    related_object = cur_guielement;
                    return true;
                }
            }
            return false;
        }
        else
            return true;
    }

    static public ArrayList load_from_properties(ArrayList<com.ttv.at.test.property> properties)
    {
        if (properties != null && properties.size() > 0)
        {
            ArrayList<guiinfo> props_return = new ArrayList<guiinfo>();

            // scan if object not using opt1-
            ArrayList non_opt = new ArrayList();
            for (com.ttv.at.test.property prop : properties)
            {
                if (!util.reg_compare(prop.get_key(), "opt(\\d+)-.*"))
                    non_opt.add(prop);
            }

            if (non_opt.size() > 0)
                props_return.add(new guiinfo(non_opt));

            // scan if object using opt
            for (int i = 1; i < 100; i++)
            {
                String filter_string = "opt" + i + "-";
                ArrayList<com.ttv.at.test.property> opt = new ArrayList<com.ttv.at.test.property>();
                for (com.ttv.at.test.property prop : properties)
                    if (prop.get_key().startsWith(filter_string))
                        opt.add(prop);
                int opt_len = filter_string.length();
                if (opt.size() > 0)
                {
                    for (com.ttv.at.test.property prop : opt)
                        prop.set_key(prop.get_key().substring(opt_len));
                    props_return.add(new guiinfo(opt));
                }
                else
                    break;
            }
            return props_return;
        }
        return null;
    }

}
