using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;
using DotNet.Utilities;

public class Calendar : JustChangeLayer
{
    public static Texture selectTexture = null;

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
        if (selectTexture == null)
        {
            selectTexture = Resources.Load<Texture>("pic/time-sb");
        }
        GameObject calendar_obj = NGUITools.AddChild(father,calendarPref);
        Calendar calendar_script = calendar_obj.GetComponent<Calendar>();
        calendar_script.DateDeserialize(str);
        //初始化
        calendar_script.tryToUpdate(true);
        calendar_obj.name = "calendar";
        calendar_script.change(11);
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
            if (ss.Length > 1)
            {
                string[] sst = ss[1].Split(":"[0]);
                _hour = int.Parse(sst[0]);
                _minute = int.Parse(sst[1]);
                _second = int.Parse(sst[2]);
            }
            else
            {
                _hour   = 0;
                _minute = 0;
                _second = 0;
            }
        }
    }

    void update_days(bool flag)
    {
        GameObject days_obj = transform.FindChild("days").gameObject;
        GameObject select = null;
        int maxDays = DateTime.DaysInMonth(_year,_month);
        DateTime fisrt = new DateTime(_year,_month,1);
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
        int temp = start == 0 ? 7 : start;
        int preLastDay = DateTime.DaysInMonth(preYear, preMonth);
        int dayIndex = preLastDay - temp + 1;
        int dayCount = 0;
        int nextMonth = _month;
        int nextYear  = _year;
        bool nextMonthStart = false;
        bool thisMonthStart = false;
        for (int i = 0 ; i < 6 ; i++ )
        {
            GameObject row_obj = days_obj.transform.FindChild("row" + i).gameObject;
            if (i <= stand)
            {
                for (int j = 0 ; j < 7 ; j++)
                {
                    GameObject day = row_obj.transform.FindChild("col" + j).gameObject;
                    if (flag)
                    {
                        UIButton button = day.GetComponent<UIButton>();
                        EventDelegate event_select = new EventDelegate(this, "doSelect");
                        event_select.parameters[0] = new EventDelegate.Parameter();
                        event_select.parameters[0].obj = day;
                        button.onClick.Add(event_select);
                    }
                    if (dayIndex < preLastDay - 1 && !thisMonthStart)
                    {
                        day.SetActive(false);
                    }
                    else
                    {
                        if (!thisMonthStart && dayIndex > preLastDay)
                        {
                            thisMonthStart = true;
                            dayIndex = 1;
                            preMonth++;
                            if (preMonth > 12)
                            {
                                preMonth = 1;
                                preYear++;
                            }
                        }
                        day.SetActive(true);
                        UILabel label = day.transform.FindChild("gl").GetComponent<UILabel>();
                        label.text = dayIndex + "";
                        label = day.transform.FindChild("nl").GetComponent<UILabel>();
                        if (dayIndex == DateTime.Now.Day && preMonth == DateTime.Now.Month && preYear == DateTime.Now.Year)
                        {
                            label.text = "今天";
                        }
                        else
                        {
                            DateTime dateTime = DateTime.Parse(preYear + "-" + MyUtilTools.numToString(preMonth) + "-" + MyUtilTools.numToString(dayIndex) + " 00:00:00");
                            CNDate cnDate = ChinaDate.getChinaDate(dateTime);
                            if (!cnDate.cnFtvl.Equals(""))
                            {
                                label.text = cnDate.cnFtvl;
                            }
                            else if (!cnDate.cnFtvs.Equals(""))
                            {
                                label.text = cnDate.cnFtvs;
                            }
                            else if (!cnDate.cnSolarTerm.Equals(""))
                            {
                                label.text = cnDate.cnSolarTerm;
                            }
                            else
                            {
                                label.text = cnDate.cnStrDay;
                            }
                        }
                        if (selectIndex == -1 && _day == dayIndex && nextMonth == _month && nextYear == _year)
                        {
                            selectIndex = dayCount;
                        }
                    }
                    CalendarData cd = day.GetComponent<CalendarData>();
                    cd.Value = MyUtilTools.numToString(preYear) + "-" + MyUtilTools.numToString(preMonth) + "-" + MyUtilTools.numToString(dayIndex);
                    cd.Index = dayCount;
                    dayIndex++;
                    dayCount ++;
                }
            }
            else
            {
                for (int j = 0; j < 7; j++)
                {
                    GameObject day = row_obj.transform.FindChild("col" + j).gameObject;
                    if (flag)
                    {
                        UIButton button = day.GetComponent<UIButton>();
                        EventDelegate event_select = new EventDelegate(this, "doSelect");
                        event_select.parameters[0] = new EventDelegate.Parameter();
                        event_select.parameters[0].obj = day;
                        button.onClick.Add(event_select);
                    }
                    if (nextMonthStart)
                    {
                        day.SetActive(false);
                    }
                    else
                    {
                        day.SetActive(true);
                        UILabel label = day.transform.FindChild("gl").GetComponent<UILabel>();
                        label.text = dayIndex + "";
                        label = day.transform.FindChild("nl").GetComponent<UILabel>();
                        if (dayIndex == DateTime.Now.Day && nextMonth == DateTime.Now.Month && nextYear == DateTime.Now.Year)
                        {
                            label.text = "今天";
                        }
                        else
                        {
                            DateTime dateTime = DateTime.Parse(nextYear + "-" + MyUtilTools.numToString(nextMonth) + "-" + MyUtilTools.numToString(dayIndex) + " 00:00:00");
                            CNDate cnDate = ChinaDate.getChinaDate(dateTime);
                            if (!cnDate.cnFtvl.Equals(""))
                            {
                                label.text = cnDate.cnFtvl;
                            }
                            else if (!cnDate.cnFtvs.Equals(""))
                            {
                                label.text = cnDate.cnFtvs;
                            }
                            else if (!cnDate.cnSolarTerm.Equals(""))
                            {
                                label.text = cnDate.cnSolarTerm;
                            }
                            else
                            {
                                label.text = cnDate.cnStrDay;
                            }
                        }
                        if (selectIndex == -1 && _day == dayIndex && nextMonth == _month && nextYear == _year)
                        {
                            selectIndex = dayCount;
                        }
                        CalendarData cd = day.GetComponent<CalendarData>();
                        cd.Value = MyUtilTools.numToString(nextYear) + "-" + MyUtilTools.numToString(nextMonth) + "-" + MyUtilTools.numToString(dayIndex);
                        cd.Index = dayCount;
                        dayIndex++;
                        if (dayIndex > maxDays)
                        {//下一个月的天数
                            nextMonthStart = true;
                            dayIndex = 1;
                            nextMonth++;
                            if (nextMonth > 12)
                            {
                                nextMonth = 1;
                                nextYear++;
                            }
                        }
                    } 
                    dayCount++;
                }
            }
        }
        int row = selectIndex / 7;
        int col = selectIndex % 7;
        select = days_obj.transform.FindChild("row" + row).FindChild("col" + col).gameObject;
        if (flag)
        {
            selectIndex = -1;
        }
        doSelect(select);
    }



    void update_year_month()
    {
        UILabel label_year = transform.FindChild("year").FindChild("year-moth").GetComponent<UILabel>();
        label_year.text = _year + "年" + MyUtilTools.numToString(_month) + "日";
    }

    void update_time()
    {
        Transform time_trans = transform.FindChild("time");
        UIInput hour_input = time_trans.FindChild("hour").GetComponent<UIInput>();
        hour_input.value = MyUtilTools.numToString(_hour);
        UIInput minute_input = time_trans.FindChild("minute").GetComponent<UIInput>();
        minute_input.value = MyUtilTools.numToString(_minute);
        UIInput second_input = time_trans.FindChild("second").GetComponent<UIInput>();
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
        if (selectIndex != -1)
        {
            int row = selectIndex / 7;
            int col = selectIndex % 7;
            GameObject select = transform.FindChild("days").FindChild("row" + row).FindChild("col" + col).gameObject;
            select.transform.FindChild("gl").GetComponent<UILabel>().color = Color.black;
            select.transform.FindChild("nl").GetComponent<UILabel>().color = Color.gray;
            Transform  sTrans = select.transform.FindChild("select");
            if (sTrans != null)
            {
                GameObject.Destroy(sTrans.gameObject);
            }
        }
        obj.transform.FindChild("gl").GetComponent<UILabel>().color = Color.white;
        obj.transform.FindChild("nl").GetComponent<UILabel>().color = Color.white;
        UITexture texture = NGUITools.AddChild<UITexture>(obj);
        texture.gameObject.name = "select";
        texture.transform.localPosition = new Vector3(0,-20,0);
        texture.width = 100;
        texture.width = 100;
        texture.depth = 0;
        texture.mainTexture = selectTexture;
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
            GameObject select = gameObject.transform.FindChild("days").FindChild("row" + row).FindChild("col" + col).gameObject;
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

