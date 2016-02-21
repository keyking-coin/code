﻿using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using LitJson;

public class DealBody : Object, System.IComparable<DealBody>
{
    public class Revert : Object
    {
        public long id;
        public long uid;
        public long dependentId = -1;
        public string icon;
        public string userName;
        public string time;
        public string context;
        public string tar = "null";

        public static Revert read(ByteBuffer data)
        {
            Revert revert = new Revert();
            revert.id = data.ReadLong();
            revert.dependentId = data.ReadLong();
            revert.uid = data.ReadLong();
            revert.userName = data.ReadString();
            revert.icon = data.ReadString();
            revert.time = data.ReadString();
            revert.context = data.ReadString();
            revert.tar = data.ReadString();
            return revert;
        }

        public void update(GameObject obj_item)
        {
            GameObject obj = obj_item.transform.FindChild("icon").gameObject;
            UISprite sprite = obj.GetComponent<UISprite>();
            sprite.spriteName = icon;
            obj = obj_item.transform.FindChild("name").gameObject;
            UILabel label = obj.GetComponent<UILabel>();
            label.text = userName + ":";
            if (!tar.Equals("null"))
            {
                label.text = userName + " 回复 " + tar + ":";
            }
            float name_len = MyUtilTools.computeLen(label);
            obj = obj_item.transform.FindChild("time").gameObject;
            label = obj.GetComponent<UILabel>();
            label.text = time;
            obj.transform.localPosition = new Vector3(name_len + 20,5,0);
            obj = obj_item.transform.FindChild("context").gameObject;
            label = obj.GetComponent<UILabel>();
            label.text = context;
        }

        public void copy(Revert revert)
        {
            dependentId = revert.dependentId;
            uid = revert.uid;
            userName = revert.userName;
            icon = revert.icon;
            time = revert.time;
            context = revert.context;
            tar = revert.tar;
        }
    }

    public class Appraise : Object
    {
        public bool isCompleted;
        public byte star;
        public string detail;
        public string time;

        public Order order;

        public static Appraise read(ByteBuffer data ,Order order)
        {
            Appraise appraise = new Appraise();
            appraise.isCompleted = data.ReadByte() == 1;
            appraise.star = data.ReadByte();
            appraise.detail = data.ReadString();
            appraise.time = data.ReadString();
            appraise.order = order;
            return appraise;
        }

        public bool couldAppraise()
        {
            return ((!order.helpflag && order.state >= 3) || (order.helpflag && order.state >= 5));
        }

        public void insterToObj(GameObject obj )
        {
            UILabel label = obj.transform.FindChild("value").GetComponent<UILabel>();
            bool isBuyer = obj.name.Equals("buyer-appraise");
            UIButton button = label.transform.GetComponent<UIButton>();
            if (isCompleted)
            {
                label.text = (isBuyer ? "买家" : "卖家") + "已评价";
                obj.transform.FindChild("icon-ok").gameObject.SetActive(true);
                obj.transform.FindChild("icon-no").gameObject.SetActive(false);
                label = obj.transform.FindChild("time").GetComponent<UILabel>();
                label.text = time;
            }
            else
            {
                label.text = (isBuyer ? "买家" : "卖家") + "未评价";
                obj.transform.FindChild("time").gameObject.SetActive(false);
                obj.transform.FindChild("icon-ok").gameObject.SetActive(false);
                obj.transform.FindChild("icon-no").gameObject.SetActive(true);
                obj.transform.FindChild("value").localPosition = Vector3.zero;
                bool flag1  = isBuyer ? ((order.item.seller && MainData.instance.user.id == order.buyId) || (!order.item.seller && MainData.instance.user.id == order.item.uid)) : ((order.item.seller && MainData.instance.user.id == order.item.uid) || (!order.item.seller && MainData.instance.user.id == order.buyId));
                bool flag = couldAppraise() ? flag1 : false;
                button.enabled = flag;
            }
            if (button.enabled)
            {
                AppraiseEvent aEvent = obj.transform.parent.GetComponent<AppraiseEvent>();
                EventDelegate ed = new EventDelegate(aEvent, "openAppraise");
                ed.parameters[0] = new EventDelegate.Parameter();
                ed.parameters[0].obj = label.transform.parent.parent.parent.parent.parent.FindChild("appraise").gameObject;
                ed.parameters[1] = new EventDelegate.Parameter();
                ed.parameters[1].obj = this;
                ed.parameters[2] = new EventDelegate.Parameter();
                ed.parameters[2].obj = obj;
                button.onClick.Clear();
                button.onClick.Add(ed);
            }
            else
            {
                obj.transform.FindChild("value").FindChild("line").gameObject.SetActive(false);
            }
        }

        public void copy(Appraise appraise)
        {
            isCompleted = appraise.isCompleted;
            star = appraise.star;
            detail = appraise.detail;
            time = appraise.time;
        }
    }

    public class Order : Object , System.IComparable<Order>
    {
        public long id;
        public long dealId;
        public long buyId;
        public string buyerName;
        public int num;
        public float price;
        public byte state;
        public bool helpflag;
        public Appraise sellerAppraise;
        public Appraise buyerAppraise;
        public List<string> times = new List<string>();
        public DealBody item;
        public bool refresh = true;

        public static Order read(ByteBuffer data)
        {
            Order order = new Order();
            order.id = data.ReadLong();
            order.dealId = data.ReadLong();
            order.buyId = data.ReadLong();
            order.buyerName = data.ReadString();
            order.num = data.ReadInt();
            order.price = float.Parse(data.ReadString());
            order.state = data.ReadByte();
            order.helpflag = data.ReadByte() == 1;
            for (byte i = 0; i <= order.state ; i++)
            {
                string time = data.ReadString();
                order.times.Add(time);
            }
            order.sellerAppraise = Appraise.read(data,order);
            order.buyerAppraise = Appraise.read(data,order);
            return order;
        }

        public void insterToObj(GameObject obj)
        {
            UILabel label = obj.transform.FindChild("title").GetComponent<UILabel>();
            string[] ss = item.bourse.Split(","[0]);
            label.text = "[0000ff]" + ss[1] + "[-]的[ff0000]" + item.stampName + "[-] " + item.monad;
            label = obj.transform.FindChild("num").FindChild("value").GetComponent<UILabel>();
            label.text = num + " " + item.monad;
            label = obj.transform.FindChild("time").FindChild("value").GetComponent<UILabel>();
            string[] ts = times[0].Split(" "[0]);
            string[] ys = ts[0].Split("-"[0]);
            int year = int.Parse(ys[0]);
            int month = int.Parse(ys[1]);
            int day = int.Parse(ys[2]);
            int maxDays = System.DateTime.DaysInMonth(year, month);
            if (day == maxDays)
            {
                day = 1;
                if (month == 12)
                {
                    month = 1;
                    year++;
                }
                else
                {
                    month++;
                }
            }
            label.text = year + "-" + month + "-" + day + " " + ts[1];
            if (!helpflag)
            {
                label = obj.transform.FindChild("fk").FindChild("value").GetComponent<UILabel>();
                label.text = "买已付款";
                if (state >= 1)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.GetComponent<UIButton>().enabled = false;
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[1];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    if ((item.seller && MainData.instance.user.id == buyId) ||
                        (!item.seller && MainData.instance.user.id == item.uid))
                    {//我是买家
                        label.transform.GetComponent<UIButton>().enabled = true;
                    }
                    else
                    {
                        label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                        label.transform.GetComponent<UIButton>().enabled = false;
                        label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(false);
                    }
                }
                label = obj.transform.FindChild("fh").FindChild("value").GetComponent<UILabel>();
                label.text = "卖家已" + (item.typeStr.Equals("入库") ? "入库" : "发货");
                if (state >= 2)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.GetComponent<UIButton>().enabled = false;
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[2];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    if (((!item.seller && MainData.instance.user.id == buyId) ||
                        (item.seller && MainData.instance.user.id == item.uid)) && state >= 1)
                    {//我是卖家
                        label.transform.GetComponent<UIButton>().enabled = true;
                    }
                    else
                    {
                        label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                        label.transform.GetComponent<UIButton>().enabled = false;
                        label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(false);
                    }
                }
                label = obj.transform.FindChild("qr").FindChild("value").GetComponent<UILabel>();
                label.text = "买已" + (item.typeStr.Equals("入库") ? "确认账户" : "确认收货");
                if (state >= 3)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.GetComponent<UIButton>().enabled = false;
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[3];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    if (((item.seller && MainData.instance.user.id == buyId) ||
                        (!item.seller && MainData.instance.user.id == item.uid)) && state >= 2)
                    {//我是买家
                        label.transform.GetComponent<UIButton>().enabled = true;
                    }
                    else
                    {
                        label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                        label.transform.GetComponent<UIButton>().enabled = false;
                        label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(false);
                    }
                }
            }
            else
            {
                label = obj.transform.FindChild("fk1").FindChild("value").GetComponent<UILabel>();
                label.text = "买家已付款给中介";
                if (state >= 1)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[1];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                }
                label = obj.transform.FindChild("fh").FindChild("value").GetComponent<UILabel>();
                label.text = "卖家已" + (item.typeStr.Equals("入库") ? "入库" : "发货");
                if (state >= 2)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[2];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                }
                label = obj.transform.FindChild("qr1").FindChild("value").GetComponent<UILabel>();
                label.text = "买家已" + (item.typeStr.Equals("入库") ? "确认账户" : "确认收货");
                if (state >= 3)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.GetComponent<UIButton>().enabled = false;
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[3];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                }
                label = obj.transform.FindChild("fk2").FindChild("value").GetComponent<UILabel>();
                label.text = "中介已付款给卖家";
                if (state >= 4)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[4];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                }
                label = obj.transform.FindChild("qr2").FindChild("value").GetComponent<UILabel>();
                label.text = "卖家已确认收款";
                if (state >= 5)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[5];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                }
            }
        }

        public int CompareTo(Order other)
        {
            System.DateTime mt = System.DateTime.Parse(times[0]);
            System.DateTime tt = System.DateTime.Parse(other.times[0]);
            return System.DateTime.Compare(mt,tt);
        }

        public void copy(Order order)
        {
            id  = order.id;
            dealId = order.dealId;
            buyId = order.buyId;
            buyerName = order.buyerName;
            num = order.num;
            price = order.price;
            state = order.state;
            helpflag = order.helpflag;
            for (byte i = 0; i <= order.state; i++)
            {
                times.Add(order.times[i]);
            }
            sellerAppraise.copy(order.sellerAppraise);
            buyerAppraise.copy(order.buyerAppraise);
        }
    }

    public long id;
    public long uid;
    public string icon;
    public string userName;
    public string time;
    public string validTime = "永久";//有效时间
    public string typeStr;
    public string bourse;
    public string stampName;
    public string monad;//单位
    public int curNum;
    public float price;
    public string context;
    public bool seller;
    public bool helpFlag;
    public bool refresh = true;//刷新标志
    public byte flag = JustRun.NULL_FLAG;

    public List<Revert> reverts = new List<Revert>();
    public List<Order> orders = new List<Order>();

    static string[] keyNames = {"方式: ","文交所: ","藏品名称：","单位: ","剩余数量: ","有效时间: ","单价: "};

    public static DealBody read(ByteBuffer data)
    {

        DealBody item  = new DealBody();
        item.id = data.ReadLong();
        item.seller = data.ReadByte() == 1;
        item.uid = data.ReadLong();
        item.userName = data.ReadString();
        item.icon = data.ReadString();
        item.time = data.ReadString();
        item.validTime = data.ReadString();
        item.typeStr = data.ReadByte() == 0 ? "入库" : "现货";
        item.bourse = data.ReadString();
        item.stampName = data.ReadString();
        item.monad = data.ReadString();
        item.curNum = data.ReadInt();
        item.price = float.Parse(data.ReadString());
        item.context = data.ReadString();
        item.helpFlag = data.ReadByte() == 1;
        int revertLen = data.ReadInt();
        for (int j = 0; j < revertLen; j++)
        {
            Revert revert = Revert.read(data);
            item.reverts.Add(revert);
        }
        int orderLen = data.ReadInt();
        for (int j = 0; j < orderLen; j++)
        {
            Order order = Order.read(data);
            order.item = item;
            item.orders.Add(order);
        }
        return item;
    }

    public float insterItem(GameObject obj_item)
    {
        GameObject obj = obj_item.transform.FindChild("icon").gameObject;
        UISprite sprite = obj.GetComponent<UISprite>();
        sprite.spriteName = icon;
        obj = obj_item.transform.FindChild("name").gameObject;
        UILabel label = obj.GetComponent<UILabel>();
        label.text = userName + ":";
        float name_len = MyUtilTools.computeLen(label);
        obj = obj_item.transform.FindChild("time").gameObject;
        label = obj.GetComponent<UILabel>();
        label.text = time;
        obj.transform.localPosition = new Vector3(name_len + 20, 5, 0);
        obj = obj_item.transform.FindChild("context").gameObject;
        label = obj.GetComponent<UILabel>();
        string[] ss = bourse.Split(","[0]);
        string str ;
        if (seller){
            str = "编号：" + id + "\n" +
                  keyNames[0] + typeStr + "\n" +
                  (typeStr.Equals("入库") ? keyNames[1] : "交易城市：") + ss[1] + "\n" +
                  keyNames[2] + stampName + "\n" +
                  keyNames[3] + monad + "\n" +
                  keyNames[4] + curNum + "\n" +
                  keyNames[6] + price + "\n" +
                  keyNames[5] + validTime +
                  (MyUtilTools.stringIsNull(context) ? "" : "\n" + context);
        }
        else
        {
            str = "编号：" + id + "\n" +
                "求购：" + typeStr + "\n" +
                (typeStr.Equals("入库") ? keyNames[1] : "交易城市：") + ss[1] + "\n" +
                keyNames[2] + stampName + "\n" +
                keyNames[3] + monad + "\n" +
                keyNames[4] + curNum + "\n" +
                keyNames[5] + validTime  +
                (MyUtilTools.stringIsNull(context) ? "" : "\n" + context);
        }
        label.text = str;
        int len = MyUtilTools.computeRow(label);
        label.height = len * (label.fontSize + label.spacingY);
        float a = len * label.fontSize / 2 + 50;
        obj.transform.localPosition = new Vector3(95,-a,0);
        return len * label.fontSize + 50;
    }

    public Order searchOrder(long orderId)
    {
        for (int i = 0; i < orders.Count; i++ )
        {
            Order order = orders[i];
            if (order.id == orderId)
            {
                return order;
            }
        }
        return null;
    }

    public void copy(DealBody item)
    {
        id  = item.id;
        uid = item.uid;
        icon = item.icon;
        userName = item.userName;
        time = item.time;
        validTime = item.validTime;
        typeStr = item.typeStr;
        bourse = item.bourse;
        stampName = item.stampName;
        monad = item.monad;
        curNum = item.curNum;
        price = item.price;
        context = item.context;
        seller = item.seller;
        refresh = item.refresh;
        for (int i = 0 ; i < reverts.Count ;  )
        {
            Revert dr = reverts[i];
            bool delFlag = true;
            foreach (Revert revert in item.reverts)
            {
                if (dr.id == revert.id)
                {
                    dr.copy(revert);
                    item.reverts.Remove(revert);
                    delFlag = false;
                    break;
                }
            }
            if (delFlag)
            {
                reverts.RemoveAt(i);
            }
            else
            {
                i++;
            }
        }
        foreach (Revert revert in item.reverts)
        {
            reverts.Add(revert);
        }

        for (int i = 0; i < orders.Count; )
        {
            Order dor = orders[i];
            bool delFlag = true;
            foreach (Order order in item.orders)
            {
                if (dor.id == order.id)
                {
                    dor.copy(order);
                    item.orders.Remove(order);
                    delFlag = false;
                    break;
                }
            }
            if (delFlag)
            {
                orders.RemoveAt(i);
            }
            else
            {
                i++;
            }
        }
        foreach (Order order in item.orders)
        {
            orders.Add(order);
        }
    }

    public int CompareTo(DealBody other)
    {
        System.DateTime mt = System.DateTime.Parse(validTime);
        System.DateTime tt = System.DateTime.Parse(other.validTime);
        return System.DateTime.Compare(mt,tt);
    }

    public void showMustUseHelpTip()
    {
        DialogUtil.tip("买家强制使用中介服务");
    }
}
 
 
