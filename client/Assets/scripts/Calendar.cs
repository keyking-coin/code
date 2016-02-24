using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;

public class Calendar : JustChangeLayer
{

	int _year;

	int _month;

	int _day;

    int _hour;

    int _minute;

    int _second;

    int selectIndex = -1;//选择的那天

    static GameObject calendarPref;

    public delegate void CloseCalendar(string str);

    CloseCalendar _callBack;

	void Start()
	{
        
	}

    void Update()
    {
        run();
    }

    public static Calendar create(GameObject father,string str)
    {
        if (calendarPref == null)
        {
            calendarPref = Resources.Load<GameObject>("prefabs/Calendar");
        }
        GameObject calendar_obj = NGUITools.AddChild(father,calendarPref);
        Calendar calendar_script = calendar_obj.GetComponent<Calendar>();
        //GameObject year_obj = calendar_obj.transform.FindChild("year").gameObject;
        calendar_script.DateDeserialize(str);
        //初始化
        calendar_script.tryToUpdate(true);
        calendar_obj.name = "calendar";
        calendar_obj.transform.localScale = new Vector3(1.5f,1.2f,1f);
        calendar_script.change(1,11);
        return calendar_script;
    }

    void DateDeserialize(string str)
    {
        if (str == null || str == "" || str == "无")
        {
            DateTime now = DateTime.Now;
            _year = now.Year;
            _month = now.Month;
            _day = now.Day;
            _hour = now.Hour;
            _minute = now.Minute;
            _second = now.Second;
        }
        else
        {
            string[] ss = str.Split(" "[0]);
            string[] ssy = ss[0].Split("-"[0]);
            _year = int.Parse(ssy[0]);
            _month = int.Parse(ssy[1]);
            _day = int.Parse(ssy[2]);
            string[] sst = ss[1].Split(":"[0]);
            _hour   = int.Parse(sst[0]);
            _minute = int.Parse(sst[1]);
            _second = int.Parse(sst[2]);
        }
    }

    void update_days(bool flag)
    {
        GameObject days_obj = gameObject.transform.FindChild("days").gameObject;
        GameObject select = null;
        int row , col;
        if (selectIndex != -1)
        {
            row = selectIndex / 7;
            col = selectIndex % 7;
            select = days_obj.transform.FindChild("row" + row).FindChild("" + col).gameObject;
            //select.GetComponent<UISprite>().color = Color.white;
            select.GetComponentInChildren<UILabel>().color = Color.black;
        }
        int dayIndex = 1;
        int maxDays = DateTime.DaysInMonth(_year,_month);
        DateTime fisrt = new DateTime(_year, _month, 1);
        DayOfWeek week = fisrt.DayOfWeek;
        int start = MyUtilTools.GetWeekDays(week);//这个月的第一天
        int preMonth = _month - 1;
        int preYear = _year;
        if (preMonth == 0)
        {
            preMonth = 12;
            preYear--;
        }
        int stand = start == 0 ? 1 : 0;
        int days = DateTime.DaysInMonth(preYear,preMonth);
        int dayCount = 0;
        int nextMonth = _month;
        int nextYear  = _year;
        for (int i = 0 ; i < 6 ; i++ )
        {
            GameObject row_obj = days_obj.transform.FindChild("row" + i).gameObject;
            if (i <= stand)
            {
                int temp = (start == 0 && i < stand) ? 7 : start;
                days -= temp - 1;
                for (int j = 0 ; j < temp; j++)
                {
                    GameObject day = row_obj.transform.FindChild("" + j).gameObject;
                    UILabel label = day.GetComponentInChildren<UILabel>();
                    label.text = MyUtilTools.numToString(days);
                    if (flag)
                    {
                        UIButton button = day.GetComponent<UIButton>();
                        button.tweenTarget = null;
                        EventDelegate event_select = new EventDelegate(this,"doSelect");
                        EventDelegate.Parameter param = new EventDelegate.Parameter();
                        param.obj = day;
                        event_select.parameters[0] = param;
                        button.onClick.Add(event_select);
                    }
                    CalendarData cd = day.GetComponent<CalendarData>();
                    cd.Value = MyUtilTools.numToString(preYear) + "-" + MyUtilTools.numToString(preMonth) + "-" + MyUtilTools.numToString(days);
                    cd.Index = dayCount;
                    days++;
                    dayCount++;
                }
                if (temp == 7)
                {
                    continue;
                }
            }
            int _start = i <= stand ? start : 0;
            for (int j = _start ; j < 7 ; j++)
            {
                GameObject day = row_obj.transform.FindChild("" + j).gameObject;
                UILabel label = day.GetComponentInChildren<UILabel>();
                label.text = MyUtilTools.numToString(dayIndex);
                if (flag)
                {
                    UIButton button = day.GetComponent<UIButton>();
                    button.tweenTarget = null;
                    EventDelegate event_select = new EventDelegate(this,"doSelect");
                    EventDelegate.Parameter param = new EventDelegate.Parameter();
                    param.obj = day;
                    event_select.parameters[0] = param;
                    button.onClick.Add(event_select);
                }
                CalendarData cd = day.GetComponent<CalendarData>();
                cd.Value = MyUtilTools.numToString(nextYear) + "-" + MyUtilTools.numToString(nextMonth) + "-" + MyUtilTools.numToString(dayIndex);
                cd.Index = dayCount;
                if (selectIndex == -1 && _day == dayIndex && nextMonth == _month && nextYear == _year)
                {
                    selectIndex = dayCount;
                }
                dayIndex++;
                if (dayIndex > maxDays)
                {
                    dayIndex = 1;
                    nextMonth ++;
                    if (nextMonth > 12)
                    {
                        nextMonth = 1;
                        nextYear++;
                    }
                    maxDays = DateTime.DaysInMonth(nextYear,nextMonth);
                }
                dayCount++;
            }
        }
        row = selectIndex / 7;
        col = selectIndex % 7;
        select = days_obj.transform.FindChild("row" + row).FindChild("" + col).gameObject;
        select.GetComponent<UISprite>().color = Color.red;
        select.GetComponentInChildren<UILabel>().color = Color.red;
    }



    void update_year_month()
    {
        GameObject year_obj = gameObject.transform.FindChild("year").gameObject;
        UILabel label_year = year_obj.GetComponentInChildren<UILabel>();
        label_year.text = _year + "." + MyUtilTools.numToString(_month);
    }

    void update_time()
    {
        GameObject time_obj = gameObject.transform.FindChild("time").gameObject;
        GameObject hour_obj = time_obj.transform.FindChild("hour").gameObject;
        UIInput hour_input = hour_obj.GetComponent<UIInput>();
        hour_input.value = MyUtilTools.numToString(_hour);
        GameObject minute_obj = time_obj.transform.FindChild("minute").gameObject;
        UIInput minute_input = minute_obj.GetComponent<UIInput>();
        minute_input.value = MyUtilTools.numToString(_minute);
        GameObject second_obj = time_obj.transform.FindChild("second").gameObject;
        UIInput second_input = second_obj.GetComponent<UIInput>();
        second_input.value = MyUtilTools.numToString(_second);
    }

    void tryToUpdate(bool flag)
    {
        update_days(flag);
        update_year_month();
        update_time();
    }

    public void doLeft()
    {
        _month--;
        if (_month < 1)
        {
            _year--;
            _month = 12;
        }
        tryToUpdate(false);
    }

    public void doRight()
    {
        _month++;
        if (_month > 12)
        {
            _year++;
            _month = 1;
        }
        tryToUpdate(false);
    }

    void doSelect(GameObject obj)
    {
        CalendarData cd = obj.GetComponent<CalendarData>();
        if (cd.Index == selectIndex)
        {
            return;
        }
        int row = selectIndex / 7;
        int col = selectIndex % 7;
        GameObject select = gameObject.transform.FindChild("days").FindChild("row" + row).FindChild("" + col).gameObject;
        //select.GetComponent<UISprite>().color = Color.white;
        select.GetComponentInChildren<UILabel>().color = Color.black;
        //obj.GetComponent<UISprite>().color = Color.red;
        obj.GetComponentInChildren<UILabel>().color = Color.red;
        selectIndex = cd.Index;
    }

    public void cancle()
    {
        if (_callBack != null){
            _callBack("");
        }
        Destroy(gameObject);
    }

    public void sure()
    {
        if (_callBack != null)
        {
            int row = selectIndex / 7;
            int col = selectIndex % 7;
            GameObject select = gameObject.transform.FindChild("days").FindChild("row" + row).FindChild("" + col).gameObject;
            CalendarData cd = select.GetComponent<CalendarData>();
            GameObject time_obj = gameObject.transform.FindChild("time").gameObject;
            GameObject hour_obj = time_obj.transform.FindChild("hour").gameObject;
            UILabel label_hour = hour_obj.GetComponentInChildren<UILabel>();
            GameObject minute_obj = time_obj.transform.FindChild("minute").gameObject;
            UILabel label_minute = minute_obj.GetComponentInChildren<UILabel>();
            GameObject second_obj = time_obj.transform.FindChild("second").gameObject;
            UILabel label_second = second_obj.GetComponentInChildren<UILabel>();
            string str = cd.Value + " " + label_hour.text + ":" + label_minute.text + ":" + label_second.text;
            _callBack(str);
        }
        Destroy(gameObject);
    }

    public CloseCalendar CallBack
    {
        get
        {
            return _callBack;
        }
        set
        {
            _callBack = value;
        }
    }
}

