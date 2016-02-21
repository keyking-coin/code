using UnityEngine;
using System.Collections;
using System;
using System.Collections.Generic;

public class TimeLineEvent : CenterEvent
{
    int selectIndex = -1;

    int _year;

    int _month;

    int _day;

    static GameObject timeEvent;

    Transform list_container;
    Transform detail_container;
    Texture texture1;
    Texture texture2;
    Texture texture3;
    Texture texture4;

    EventDelegate callback = null;

    public Font labelFont;

    class TimeContent
    {
        public byte type;//0字符串，1图片
        public string str_value;

        public void deserialize(ByteBuffer buffer)
        {
            type  = buffer.ReadByte();
            str_value = buffer.ReadString();
        }
    }

    class TimeLine : UnityEngine.Object
    {
        public long id;
        public byte type;
        public string title;//标题
        public string time;//发生时间
        public List<TimeContent> contents = new List<TimeContent>();
        public void deserialize(ByteBuffer buffer)
        {
            id = buffer.ReadLong();
            type = buffer.ReadByte();
            title = buffer.ReadString();
            time = buffer.ReadString();
            byte len = buffer.ReadByte();
            for (int i = 0 ; i < len; i++)
            {
                TimeContent content = new TimeContent();
                content.deserialize(buffer);
                contents.Add(content);
            }
        }

        public bool isMe(int year, int month, int day)
        {
            string[] ss = time.Split(" "[0]);
            string[] ssy = ss[0].Split("-"[0]);
            int _year = int.Parse(ssy[0]);
            int _month = int.Parse(ssy[1]);
            int _day = int.Parse(ssy[2]);
            return _year == year && _month == month && _day == day;
        }
    }

    class TimeLineRefresh : MonoBehaviour
    {
        public TimeLineEvent timeEvent;

        void Start () {

        }
	
	// Update is called once per frame
        void Update()
        {
            
            ByteBuffer buffer = MyUtilTools.tryToLogic("TimeLineEvent");
            if (buffer != null)
            {
                timeEvent.readInfo(buffer);
                timeEvent.refresh();
            }
        }
    }

    List<TimeLine> timeLines = new List<TimeLine>();

	// Use this for initialization
	void Start () {
        
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("TimeLineEvent");
        if (buffer != null)
        {
            readInfo(buffer);
            init();
        }
	}

    void readInfo(ByteBuffer buffer)
    {
        timeLines.Clear();
        int len = buffer.ReadInt();
        for (int i = 0; i < len; i++)
        {
            TimeLine timeLine = new TimeLine();
            timeLine.deserialize(buffer);
            timeLines.Add(timeLine);
        }
    }

    Dictionary<byte, List<TimeLine>> search(int year, int month, int day)
    {
        Dictionary<byte, List<TimeLine>> results = new Dictionary<byte,List<TimeLine>>();
        foreach(TimeLine time in timeLines){
            if (time.isMe(year,month,day))
            {
                List<TimeLine> lis = null;
                if (results.ContainsKey(time.type)){
                    lis = results[time.type];
                }else{
                    lis = new List<TimeLine>();
                    results.Add(time.type,lis);
                }
                lis.Add(time);
            }
        }
        return results;
    }

    void getInfoFromNet()
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("TimeLineEvent");
        buffer.WriteInt(_year);
        buffer.WriteInt(_month);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void open()
    {
        _year = DateTime.Now.Year;
        _month = DateTime.Now.Month;
        _day = DateTime.Now.Day;
        getInfoFromNet();
    }

    void refresh()
    {
        _update();
        int row = selectIndex / 7;
        int col = selectIndex % 7;
        CalendarData cd = list_container.FindChild("days").FindChild("detail").FindChild("row" + row).FindChild("" + col).GetComponent<CalendarData>();
        _updateEvent_CalendarData(cd);
    }

    void init()
    {
        click();//显示界面
        refresh();
    }

    void _update_flags(GameObject obj,int year,int month,int day)
    {
        Transform trans = obj.transform.FindChild("flags");
        Dictionary<byte, List<TimeLine>> maps = search(year, month, day);
        if (maps.Count > 0)
        {
            trans.gameObject.SetActive(true);
            string fn = maps.Count + "";
            Transform flags = trans.FindChild(fn);
            flags.gameObject.SetActive(true);
            int count = 1;
            for (byte a = 1 ; a < 5; a++)
            {
                if (maps.ContainsKey(a))
                {
                    Texture texture = null;
                    texture = a == 1 ? texture1 : texture;
                    texture = a == 2 ? texture2 : texture;
                    texture = a == 3 ? texture3 : texture;
                    texture = a == 4 ? texture4 : texture;
                    if (maps.Count == 1)
                    {
                        flags.GetComponent<UITexture>().mainTexture = texture;
                    }
                    else
                    {
                        flags.FindChild(count + "").GetComponent<UITexture>().mainTexture = texture;
                    }
                    count++;
                }
            }
        }
        else
        {
            trans.gameObject.SetActive(false);
        }
    }

    void _update()
    {
        if (list_container == null)
        {
            list_container = needshow[0].transform.FindChild("list").FindChild("body").FindChild("container");
            texture1 = Resources.Load<Texture>("pic/time-event1");
            texture2 = Resources.Load<Texture>("pic/time-event2");
            texture3 = Resources.Load<Texture>("pic/time-event3");
            texture4 = Resources.Load<Texture>("pic/time-event4");
            TimeLineRefresh refresh = needshow[0].AddComponent<TimeLineRefresh>();
            refresh.timeEvent = this;
        }
        list_container.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        list_container.parent.localPosition = new Vector3(0, 0, 0);
        UILabel year_label = list_container.FindChild("up").GetComponent<UILabel>();
        year_label.text = MyUtilTools.numToString(_year) + "年" + MyUtilTools.numToString(_month) + "月" + MyUtilTools.numToString(_day) + "日";
        Transform days_trans = list_container.FindChild("days").FindChild("detail");
        GameObject preSelect = null , select = null;
        if (selectIndex != -1)
        {
            int row = selectIndex / 7;
            int col = selectIndex % 7;
            preSelect = days_trans.FindChild("row" + row).FindChild("" + col).gameObject;
        }
        int dayIndex = 1;
        int maxDays = DateTime.DaysInMonth(_year,_month);
        DateTime fisrt = new DateTime(_year,_month,1);
        DayOfWeek week = fisrt.DayOfWeek;
        int start    = MyUtilTools.GetWeekDays(week);//这个月的第一天
        int preMonth = DateTime.Now.Month - 1;
        int preYear  = DateTime.Now.Year;
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
        bool isNext = false;
        for (int i = 0 ; i < 6; i++)
        {
            Transform row_trans = days_trans.FindChild("row" + i);
            if (i <= stand)
            {
                int temp = (start == 0 && i < stand) ? 7 : start;
                days -= temp - 1;
                for (int j = 0 ; j < temp ; j++)
                {
                    GameObject day = row_trans.FindChild("" + j).gameObject;
                    UILabel label = day.GetComponent<UILabel>();
                    label.text = MyUtilTools.numToString(days);
                    label.color = Color.gray;
                    UIButton button = day.GetComponent<UIButton>();
                    button.enabled = false;
                    if (button.onClick.Count == 0)
                    {
                        button.tweenTarget = null;
                        EventDelegate event_select = new EventDelegate(this,"doSelect");
                        EventDelegate.Parameter param = new EventDelegate.Parameter();
                        param.obj = day;
                        event_select.parameters[0] = param;
                        button.onClick.Add(event_select);
                    }
                    if (days == DateTime.Now.Day && preMonth == DateTime.Now.Month && preYear == DateTime.Now.Year)
                    {
                        label.fontSize = 24;
                        label.text = "今天";
                    }
                    else
                    {
                        label.fontSize = 40;
                    }
                    CalendarData cd = day.GetComponent<CalendarData>();
                    cd.Value = MyUtilTools.numToString(preYear) + "-" + MyUtilTools.numToString(preMonth) + "-" + MyUtilTools.numToString(days);
                    cd.Index = dayCount;
                    _update_flags(day,preYear,preMonth,days);
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
                GameObject day = row_trans.FindChild("" + j).gameObject;
                UILabel label = day.GetComponent<UILabel>();
                label.text = MyUtilTools.numToString(dayIndex);
                label.color     = isNext ? Color.gray : Color.black;
                UIButton button = day.GetComponent<UIButton>();
                button.enabled  = !isNext;
                if (button.onClick.Count == 0)
                {
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
                if (_day == dayIndex && nextMonth == _month && nextYear == _year)
                {
                    select = day;
                    selectIndex = dayCount;
                }
                if (dayIndex == DateTime.Now.Day && nextMonth == DateTime.Now.Month && nextYear == DateTime.Now.Year)
                {
                    label.fontSize = 24;
                    label.text = "今天";
                }
                else
                {
                    label.fontSize = 40;
                }
                _update_flags(day,nextYear,nextMonth,dayIndex);
                dayIndex++;
                if (dayIndex > maxDays)
                {
                    isNext = true;
                    dayIndex = 1;
                    nextMonth++;
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
        showSelect(preSelect,select);
    }

    void backFromDetail()
    {
        needshow[0].transform.FindChild("list").gameObject.SetActive(true);
        needshow[0].transform.FindChild("detail").gameObject.SetActive(false);
    }

    void goToDetail(TimeLine timeLine)
    {
        needshow[0].transform.FindChild("list").gameObject.SetActive(false);
        Transform detail_trans = needshow[0].transform.FindChild("detail");
        detail_trans.gameObject.SetActive(true);
        callback = new EventDelegate(backFromDetail);
        if (detail_container == null)
        {
            detail_container = detail_trans.FindChild("body").FindChild("container");
        }
        MyUtilTools.clearChild(detail_container);
        detail_container.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        detail_container.parent.localPosition = new Vector3(0,0,0);
        GameObject newObj = new GameObject();
        GameObject title_obj = NGUITools.AddChild(detail_container.gameObject,newObj);
        title_obj.transform.localPosition = new Vector3(0,520,0);
        title_obj.name = "title";
        UILabel title_label = title_obj.AddComponent<UILabel>();
        title_label.trueTypeFont = labelFont;
        title_label.fontSize = 50;
        title_label.text   = timeLine.title;
        title_label.width  = 800;
        title_label.height = 60;
        title_label.spacingX = 5;
        title_label.spacingY = 5;
        title_label.maxLineCount = 100;
        title_label.color = Color.black;
        title_label.depth = 2;
        float starty = 450;
        for (int i = 0 ; i < timeLine.contents.Count; i++)
        {
            GameObject sun = null;
            TimeContent content = timeLine.contents[i];
            int height = 0;
            float offset = 0;
            if (content.type == 0)
            {
                sun = NGUITools.AddChild(detail_container.gameObject,title_obj);
                sun.name = "content" + i;
                UILabel sun_label = sun.GetComponent<UILabel>();
                sun_label.fontSize = 40;
                sun_label.width  = 750;
                sun_label.height = 10000;
                sun_label.text = content.str_value;
                int row = MyUtilTools.computeRow(sun_label);
                int total = row * (sun_label.fontSize + title_label.spacingY);
                sun_label.height = total;
                height = total;
                offset = total / 2;
            }
            else
            {
                sun = NGUITools.AddChild(detail_container.gameObject,newObj);
                sun.name = "content" + i;
                UITexture texture = sun.AddComponent<UITexture>();
                texture.width = 512;
                texture.height = 512;
                JustRun.Instance.loadPic(content.str_value,texture);
                height = texture.height + 20;
                offset = texture.height / 2;
            }
            starty -= height;
            sun.transform.localPosition = new Vector3(0,starty + offset,0);
        }
        Destroy(newObj);
    }

    void doNotify(TimeLine timeLine)
    {
        /*
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("TimeLineNotify");
        buffer.WriteByte(timeLine.type);
        buffer.WriteString(timeLine.title);
        buffer.WriteString(timeLine.time);
        NetUtil.getInstance.SendMessage(buffer);
        */
        #if UNITY_ANDROID
        AndroidJavaClass  ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject ajo = ajc.GetStatic<AndroidJavaObject>("currentActivity");
        ajo.Call("testNotification");
        #endif
    }

    void updateEvent(int year,int month,int day)
    {
        Transform trans = list_container.FindChild("events");
        MyUtilTools.clearChild(trans);
        GameObject events = trans.gameObject;
        Dictionary<byte, List<TimeLine>> maps = search(year,month,day);
        if (maps.Count > 0)
        {
            if (timeEvent == null)
            {
                timeEvent = Resources.Load<GameObject>("prefabs/time-list");
            }
            int count = 0;
            float staty = 0;
            for (byte i = 1; i < 5 ; i++ )
            {
                if (maps.ContainsKey(i))
                {
                    List<TimeLine> lis = maps[i];
                    foreach (TimeLine timeLine in lis)
                    {
                        GameObject time_event = NGUITools.AddChild(events, timeEvent);
                        GameObject time_obj = time_event.transform.FindChild("time").gameObject;
                        if (count == 0){
                            time_obj.GetComponent<UILabel>().text = timeLine.time;
                        }
                        else
                        {
                            time_obj.SetActive(false);
                        }
                        UILabel label_value = time_event.transform.FindChild("value").GetComponent<UILabel>();
                        MyUtilTools.insertStr(label_value,timeLine.title,400);
                        string title_str = "";
                        Color color = Color.black;
                        if (timeLine.type == 1)
                        {
                            title_str = "【申购】";
                            color = Color.red;
                            label_value.transform.localPosition = new Vector3(50,50,0);
                        }
                        else if (timeLine.type == 2)
                        {
                            title_str = "【托管预约】";
                            color = new Color(32f/255f,172f/255f,131f/255f);
                            label_value.transform.localPosition = new Vector3(100,50,0);
                        }
                        else if (timeLine.type == 3)
                        {
                            title_str = "【托管入库】";
                            color = new Color(121f / 255f, 170f / 255f, 217f / 255f);
                            label_value.transform.localPosition = new Vector3(100,50,0);
                        }
                        else if (timeLine.type == 4)
                        {
                            title_str = "【重要提示】";
                            color = new Color(1f, 96f / 255f,0);
                            label_value.transform.localPosition = new Vector3(100,50,0);
                        }
                        UILabel label_title = time_event.transform.FindChild("title").GetComponent<UILabel>();
                        label_title.text = title_str;
                        label_title.color = color;
                        UIButton detail_button = label_value.transform.GetComponent<UIButton>();
                        EventDelegate detail_event = new EventDelegate(this,"goToDetail");
                        detail_event.parameters[0] = new EventDelegate.Parameter();
                        detail_event.parameters[0].obj = timeLine;
                        detail_button.onClick.Add(detail_event);
                        UIButton notify_button = time_event.transform.FindChild("locker").GetComponent<UIButton>();
                        EventDelegate notify_event = new EventDelegate(this,"doNotify");
                        notify_event.parameters[0] = new EventDelegate.Parameter();
                        notify_event.parameters[0].obj = timeLine;
                        notify_button.onClick.Add(notify_event);
                        time_event.transform.localPosition = new Vector3(0,staty,0);
                        staty -= 280;
                        count++; 
                    }
                }
            }
        }
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
        GameObject select = list_container.FindChild("days").FindChild("detail").FindChild("row" + row).FindChild("" + col).gameObject;
        showSelect(select,obj);
        selectIndex = cd.Index;
        _updateEvent_CalendarData(cd);
    }

    void showSelect(GameObject pre , GameObject now)
    {
        if (pre != null)
        {
            pre.transform.FindChild("s-bg").gameObject.SetActive(false);
        }
        now.transform.FindChild("s-bg").gameObject.SetActive(true);
    }

    public void next()
    {
        _month++;
        if (_month > 12)
        {
            _year++;
            _month = 1;
        }
        getInfoFromNet();
    }

    public void previous()
    {
        _month--;
        if (_month < 1)
        {
            _year--;
            _month = 12;
        }
        getInfoFromNet();
    }

    void _updateEvent_CalendarData(CalendarData cd)
    {
        string[] ss = cd.Value.Split("-"[0]);
        int year = int.Parse(ss[0]);
        int month = int.Parse(ss[1]);
        int day = int.Parse(ss[2]);
        updateEvent(year, month, day);
    }

    public void openNotifation()
    {
        #if UNITY_ANDROID
        AndroidJavaClass ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject ajo = ajc.GetStatic<AndroidJavaObject>("currentActivity");
        ajo.Call("testNotification");
        #endif
    }

    public void back()
    {
        if (callback != null)
        {
            callback.Execute();
            callback = null;
            return;
        }
        backToCenter();
    }

    public void aasdadadasste(string str)
    {
        DialogUtil.tip(str);
    }
}
