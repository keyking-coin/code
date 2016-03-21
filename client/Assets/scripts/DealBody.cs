using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using LitJson;

public class DealBody : Object
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

        public float update(GameObject obj_item,bool fix = true)
        {
            obj_item.transform.FindChild("icon").GetComponent<UISprite>().spriteName = icon;
            UILabel user_name = obj_item.transform.FindChild("user").GetComponent<UILabel>();
            if (!tar.Equals("null"))
            {
                user_name.text = userName + " [0000ef]回复[-] " + tar;
            }
            else
            {
                user_name.text = userName;
            }
            obj_item.transform.FindChild("time").GetComponent<UILabel>().text = time;
            UILabel content_label = obj_item.transform.FindChild("content").GetComponent<UILabel>();
            content_label.text = context;
            int len = MyUtilTools.computeRow(content_label);
            content_label.height = len * (content_label.fontSize + content_label.spacingY);
            float a = (len * content_label.fontSize + (len - 1) * content_label.spacingY) / 2;
            content_label.transform.localPosition = new Vector3(60,-70-a,0);
            if (len % 2 != 0)
            {
                a += content_label.fontSize / 2;
            }
            obj_item.transform.FindChild("event").localPosition = new Vector3(0,-180-a,0);
            return fix ? (290 + a) : 200;
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

        public void insterToObj(GameObject obj, GameObject obj1,GameObject obj2,bool admin = false)
        {
            UILabel label = obj.transform.FindChild("value").GetComponent<UILabel>();
            bool isBuyer = obj.name.Equals("buyer-appraise");
            UIButton button = obj.transform.FindChild("action").GetComponent<UIButton>();
            if (isCompleted)
            {
                label.text = (isBuyer ? "买家" : "卖家") + "已评价";
                obj.transform.FindChild("icon-ok").gameObject.SetActive(true);
                obj.transform.FindChild("icon-no").gameObject.SetActive(false);
                obj.transform.FindChild("time").gameObject.SetActive(true);
                label = obj.transform.FindChild("time").GetComponent<UILabel>();
                label.text = time;
                button.enabled = true;
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
                button.gameObject.SetActive(true);
                AppraiseEvent aEvent = obj1.GetComponent<AppraiseEvent>();
                EventDelegate ed = null;
                if (admin)
                {
                    ed = new EventDelegate(aEvent, "openAppraise1");
                }
                else
                {
                    ed = new EventDelegate(aEvent, "openAppraise");
                }
                ed.parameters[0] = new EventDelegate.Parameter();
                ed.parameters[0].obj = obj2.transform.FindChild("appraise").gameObject;
                ed.parameters[1] = new EventDelegate.Parameter();
                ed.parameters[1].obj = this;
                ed.parameters[2] = new EventDelegate.Parameter();
                ed.parameters[2].obj = admin ? obj.transform.parent.parent.gameObject : obj;
                button.onClick.Clear();
                button.onClick.Add(ed);
            }
            else
            {
                button.gameObject.SetActive(false);
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
        public int revoke;//撤销
        public static int ORDER_REVOKE_BUYER     = 1;
        public static int ORDER_REVOKE_SELLER    = 1 << 1;
        public static int ORDER_REVOKE_ALL       = (ORDER_REVOKE_BUYER | ORDER_REVOKE_SELLER);

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
            order.revoke = data.ReadInt();
            return order;
        }

        public bool checkRevoke(int flag)
        {
            int result = revoke & flag;
            return result != 0;
        }

        public void insterToObj(GameObject obj,bool admin = false)
        {
            UILabel label = obj.transform.FindChild("title").GetComponent<UILabel>();
            string[] ss = item.bourse.Split(","[0]);
            string tail = helpflag ? "(中介)" : "" ;
            label.text = "(编号：[00ff00]" + id + ")[-][0000ff]" + ss[1] + "[-]的[ff0000]" + item.stampName + "[-]" + tail;
            label = obj.transform.FindChild("num").FindChild("value").GetComponent<UILabel>();
            label.text = num + " ( " + item.price + " 元/ " + item.monad + " )";
            label = obj.transform.FindChild("time").FindChild("value").GetComponent<UILabel>();
            label.text = times[0];
            bool ruku = item.typeStr.Equals("入库");
            if (!helpflag)
            {
                label = obj.transform.FindChild("fk").FindChild("value").GetComponent<UILabel>();
                label.text = "买家已付款";
                if (state >= 1)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.parent.FindChild("time").gameObject.SetActive(true);
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[1];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    if (!admin)
                    {
                        if ((item.seller && MainData.instance.user.id == buyId) ||
                                                (!item.seller && MainData.instance.user.id == item.uid) && state >= 0)
                        {//我是买家
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(true);
                        }
                        else
                        {
                            label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(false);
                        }
                    }
                }
                label = obj.transform.FindChild("fh").FindChild("value").GetComponent<UILabel>();
                label.text = "卖家已" + (ruku ? "入库" : "发货");
                if (state >= 2)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.parent.FindChild("time").gameObject.SetActive(true);
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[2];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    if (!admin)
                    {
                        if (((!item.seller && MainData.instance.user.id == buyId) ||
                        (item.seller && MainData.instance.user.id == item.uid)) && state >= 1)
                        {//我是卖家
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(true);
                        }
                        else
                        {
                            label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(false);
                        }
                    }
                }
                label = obj.transform.FindChild("qr").FindChild("value").GetComponent<UILabel>();
                label.text = "买家确认" + (ruku ? "货已入库" : "收货");
                if (state >= 3)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.parent.FindChild("time").gameObject.SetActive(true);
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[3];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    if (!admin)
                    {
                        if (((item.seller && MainData.instance.user.id == buyId) ||
                        (!item.seller && MainData.instance.user.id == item.uid)) && state >= 2)
                        {//我是买家
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(true);
                        }
                        else
                        {
                            label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(false);
                        }
                    }
                }
            }
            else//中介类型
            {
                label = obj.transform.FindChild("fk").FindChild("value").GetComponent<UILabel>();
                label.text = "买家已付款给中介";
                if (state >= 1)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.parent.FindChild("time").gameObject.SetActive(true);
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[1];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    if (!admin)
                    {
                        if ((item.seller && MainData.instance.user.id == buyId) ||
                        (!item.seller && MainData.instance.user.id == item.uid) && state >= 0)
                        {//我是买家
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(true);
                        }
                        else
                        {
                            label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(false);
                        }
                    }
                }
                label = obj.transform.FindChild("sk").FindChild("value").GetComponent<UILabel>();
                label.text = "中介已收款";
                if (state >= 2)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.parent.FindChild("time").gameObject.SetActive(true);
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
                label = obj.transform.FindChild("fh").FindChild("value").GetComponent<UILabel>();
                label.text = "卖家已" + (ruku ? "入库" : "发货");
                if (state >= 3)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.GetComponent<UIButton>().enabled = false;
                    label.transform.parent.FindChild("time").gameObject.SetActive(true);
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[3];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    if (!admin)
                    {
                        if (((!item.seller && MainData.instance.user.id == buyId) ||
                        (item.seller && MainData.instance.user.id == item.uid)) && state >= 2)
                        {//我是卖家
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(true);
                        }
                        else
                        {
                            label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(false);
                        }
                    }
                }
                label = obj.transform.FindChild("qr").FindChild("value").GetComponent<UILabel>();
                label.text = "买家确认" + (ruku ? "货已入库" : "收货");
                if (state >= 4)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.parent.FindChild("time").gameObject.SetActive(true);
                    label = label.transform.parent.FindChild("time").GetComponent<UILabel>();
                    label.text = times[4];
                }
                else
                {
                    label.transform.parent.FindChild("time").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(false);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(true);
                    if (!admin)
                    {
                        if (((item.seller && MainData.instance.user.id == buyId) ||
                        (!item.seller && MainData.instance.user.id == item.uid)) && state >= 3)
                        {//我是买家
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(true);
                        }
                        else
                        {
                            label.transform.parent.FindChild("value").localPosition = Vector3.zero;
                            label.transform.parent.FindChild("icon-no").FindChild("line").gameObject.SetActive(false);
                        }
                    }
                }
                label = obj.transform.FindChild("zfk").FindChild("value").GetComponent<UILabel>();
                label.text = "中介付款给卖家";
                if (state >= 5)
                {
                    label.transform.parent.FindChild("icon-ok").gameObject.SetActive(true);
                    label.transform.parent.FindChild("icon-no").gameObject.SetActive(false);
                    label.transform.parent.FindChild("time").gameObject.SetActive(true);
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
            if (!admin)
            {
                Transform revoke_trans = obj.transform.FindChild("revoke");
                if (state < 1)
                {//已进行的流程没法取消
                    if (((item.seller && MainData.instance.user.id == buyId) || (!item.seller && MainData.instance.user.id == item.uid)) && !checkRevoke(ORDER_REVOKE_BUYER))
                    {//我是买家
                        revoke_trans.gameObject.SetActive(true);
                    }
                    else if (((!item.seller && MainData.instance.user.id == buyId) || (item.seller && MainData.instance.user.id == item.uid)) && !checkRevoke(ORDER_REVOKE_SELLER))
                    {//我是卖家
                        revoke_trans.gameObject.SetActive(true);
                    }
                    else
                    {
                        revoke_trans.gameObject.SetActive(false);
                    }
                }
                else
                {
                    revoke_trans.gameObject.SetActive(false);
                }
            }
        }

        public int CompareTo(Order other)
        {
            //Debug.Log(id + " : " + times[0] + ";" + other.id + " : " + other.times[0]);
            System.DateTime mt = System.DateTime.Parse(times[0]);
            System.DateTime tt = System.DateTime.Parse(other.times[0]);
            return System.DateTime.Compare(tt,mt);
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
            times.Clear();
            for (byte i = 0; i <= order.state; i++)
            {
                times.Add(order.times[i]);
            }
            sellerAppraise.copy(order.sellerAppraise);
            buyerAppraise.copy(order.buyerAppraise);
            revoke = order.revoke;
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
    public bool revoke;
    public bool isLock;
    public bool refresh = true;//刷新标志
    public byte flag = JustRun.NULL_FLAG;
    public List<Revert> reverts = new List<Revert>();
    public List<Order> orders = new List<Order>();

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
        item.revoke = data.ReadByte() == 1;
        item.isLock = data.ReadByte() == 1;
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

    public void insterItem(GameObject obj_item,bool fix = true)
    {
        GameObject obj = obj_item.transform.FindChild("icon").gameObject;
        UISprite sprite = obj_item.transform.FindChild("icon").GetComponent<UISprite>();
        sprite.spriteName = icon;
        UILabel label = obj_item.transform.FindChild("user").GetComponent<UILabel>();
        label.text = userName + " ";
        label = obj_item.transform.FindChild("time").GetComponent<UILabel>();
        label.text = time;
        Transform content_trans = obj_item.transform.FindChild("content");
        label = content_trans.FindChild("id").FindChild("value").GetComponent<UILabel>();
        label.text = id + "";
        label = content_trans.FindChild("tip").FindChild("value").GetComponent<UILabel>();
        label.text = seller ? "出售" : "求购";
        label = content_trans.FindChild("type").FindChild("value").GetComponent<UILabel>();
        label.text = typeStr;
        label = content_trans.FindChild("help").FindChild("value").GetComponent<UILabel>();
        label.text = helpFlag ? "平台中介" : "买方先款";
        if (typeStr.Equals("入库"))
        {
            label = content_trans.FindChild("bourse").GetComponent<UILabel>();
            label.text = "文 交 所";
            label.spacingX = 4;
        }
        else
        {
            label = content_trans.FindChild("bourse").GetComponent<UILabel>();
            label.text = "交易城市";
            label.spacingX = 1;
        }
        label = content_trans.FindChild("bourse").FindChild("value").GetComponent<UILabel>();
        string[] ss = bourse.Split(","[0]);
        label.text = ss[1];
        label = content_trans.FindChild("title").FindChild("value").GetComponent<UILabel>();
        label.text = stampName;
        label = content_trans.FindChild("price").FindChild("value").GetComponent<UILabel>();
        label.text = price + "";
        label = content_trans.FindChild("monad").FindChild("value").GetComponent<UILabel>();
        label.text = monad;
        label = content_trans.FindChild("num").FindChild("value").GetComponent<UILabel>();
        label.text = curNum + "";
        label = content_trans.FindChild("valid").FindChild("value").GetComponent<UILabel>();
        label.text = validTime;
        Transform event_tran = obj_item.transform.FindChild("event");
        if (!context.Equals(""))
        {
            label = content_trans.FindChild("other").GetComponent<UILabel>();
            label.text = context;
            int len = MyUtilTools.computeRow(label);
            label.transform.localPosition = new Vector3(0,-420-len*label.fontSize/2 + 20,0);
            UITexture bg = content_trans.FindChild("rect-bg").GetComponent<UITexture>();
            bg.height = (int)(640 + len * label.fontSize);
            bg.transform.localPosition = new Vector3(0, -100 - len * label.fontSize / 2, 0);
            event_tran.localPosition = new Vector3(0, -750 - len * label.fontSize + 20 , 0);
        }
        else
        {
            event_tran.localPosition = new Vector3(0,-750,0);
            content_trans.FindChild("other").gameObject.SetActive(false);
        }
        System.DateTime vTime = System.DateTime.Parse(validTime);
        bool flag = revoke || vTime.CompareTo(System.DateTime.Now) < 0;
        GameObject revoke_obj = content_trans.FindChild("revoke").gameObject;
        revoke_obj.SetActive(flag);
        Transform reverts = obj_item.transform.FindChild("reverts");
        if (reverts != null)
        {
            reverts.localPosition = new Vector3(0, event_tran.localPosition.y-120,0);
        }
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

    /*
    public int CompareTo(DealBody other)
    {
        System.DateTime mt = System.DateTime.Parse(validTime);
        System.DateTime tt = System.DateTime.Parse(other.validTime);
        return System.DateTime.Compare(tt,mt);
    }*/

    public void showMustUseHelpTip()
    {
        DialogUtil.tip("买家强制使用中介服务");
    }
}
 
 
