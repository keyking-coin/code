﻿using UnityEngine;
using System.Collections;

public class OpenCalendar : MonoBehaviour {
    UIPanel panel;
    UIInput year;
    UIInput month;
    UIInput day;
    UIInput hour;
    UIInput minute;
    GameObject base_obj;

	void Start () {
        panel = GameObject.Find("main").GetComponent<UIPanel>();
        year = transform.parent.FindChild("year").GetComponent<UIInput>();
        month = transform.parent.FindChild("month").GetComponent<UIInput>();
        day = transform.parent.FindChild("day").GetComponent<UIInput>();
        hour = transform.parent.FindChild("hour").GetComponent<UIInput>();
        minute = transform.parent.FindChild("minute").GetComponent<UIInput>();
        System.DateTime now = System.DateTime.Now;
        year.value = MyUtilTools.numToString(now.Year);
        month.value = MyUtilTools.numToString(now.Month);
        day.value = MyUtilTools.numToString(now.Day);
        hour.value = MyUtilTools.numToString(23);
        minute.value = MyUtilTools.numToString(59);
        base_obj = GameObject.Find("base");
	}
	
	//Update is called once per frame
	void Update () {
	
	}

    public void open()
    {
        panel.alpha = 0.1f;
        //UILabel target = gameObject.GetComponent<UILabel>();
        string str = year.value + "-" + month.value + "-" + day.value + " " + hour.value + ":" + minute.value + ":00";
        Calendar calender = Calendar.create(base_obj,str);
        CameraUtil.push(5,3);
        calender.CallBack = back;
    }

    void back(string str)
    {
        if (!MyUtilTools.stringIsNull(str))
        {
            string[] ss  = str.Split(" "[0]);
            string[] ssy = ss[0].Split("-"[0]);
            year.value   = ssy[0];
            month.value  = ssy[1];
            day.value    = ssy[2];
            string[] sst = ss[1].Split(":"[0]);
            hour.value   = sst[0];
            minute.value = sst[1];
        }
        panel.alpha = 1f;
        CameraUtil.pop(5);
    }
}
