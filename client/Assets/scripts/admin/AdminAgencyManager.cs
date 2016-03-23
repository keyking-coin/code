using UnityEngine;
using System.Collections.Generic;

public class AdminAgencyManager : MonoBehaviour {
    static List<DealBody.Order> orders = new List<DealBody.Order>();
    Transform mjfk = null;
    Transform mjqr = null;
    DownOpenLink mjfk_link = null;
    DownOpenLink mjqr_link = null;

    int selectIndex = 0;
	// Use this for initialization
	void Start () {
        if (AdminDealManager.pref_deal_info == null)
        {
            AdminDealManager.pref_deal_info = Resources.Load<GameObject>("prefabs/deal-info");
        }
        Transform container = transform.FindChild("left").FindChild("list").FindChild("body").FindChild("container");
        mjfk = container.transform.FindChild("mjfk");
        mjqr = container.transform.FindChild("mjqr");
        mjfk_link = mjfk.GetComponent<DownOpenLink>();
        mjqr_link = mjqr.GetComponent<DownOpenLink>();

	}
	
	// Update is called once per frame
	void Update () {
        int count_mjfk = 0, count_mjqr = 0;
        foreach (DealBody.Order order in orders)
        {
            if (order.state == 1)
            {
                count_mjfk++;
            }
            else if (order.state == 4)
            {
                count_mjqr++;
            }
        }
        bool flag = count_mjfk != mjfk.FindChild("suns").childCount || count_mjqr != mjqr.FindChild("suns").childCount;
        if (flag)
        {
            refresh();
        }
        ByteBuffer buffer = MyUtilTools.tryToLogic("AdminLookUser");
        if (buffer != null)
        {
            MainData.UserData user = new MainData.UserData();
            user.deserialize(buffer);
            refreshUser(user);
        }
        buffer = MyUtilTools.tryToLogic("AdminOrderRevoke");
        if (buffer != null)
        {
            DialogUtil.tip("撤销成功",true);
        }
        buffer = MyUtilTools.tryToLogic("AdminDealOrderUpdate");
        if (buffer != null)
        {
            DialogUtil.tip("操作成功",true);
        }
        Transform right = transform.FindChild("right");
        if (orders.Count == 0)
        {
            right.FindChild("order-help").gameObject.SetActive(false);
            right.FindChild("appraise").gameObject.SetActive(false);
            right.FindChild("user-look").gameObject.SetActive(false);
            return;
        }
	}

    void refresh()
    {
        selectIndex = 0;
        mjfk_link.closeLink();
        mjqr_link.closeLink();
        mjfk.localPosition = new Vector3(0,820,0);
        mjqr.localPosition = new Vector3(0,730,0);
        Transform suns1 = mjfk.FindChild("suns");
        Transform suns2 = mjqr.FindChild("suns");
        MyUtilTools.clearChild(suns1);
        MyUtilTools.clearChild(suns2);
        float y1 = -80 , y2 = -80 ;
        for (int i = 0; i < orders.Count; i++ )
        {
            DealBody.Order order = orders[i];
            if (order.state == 1)
            {
                GameObject order_summary = NGUITools.AddChild(suns1.gameObject, AdminDealManager.pref_deal_info);
                order_summary.name = "" + i;
                order_summary.transform.FindChild("name").GetComponent<UILabel>().text = order.item.stampName + "-" + order.id;
                order_summary.transform.localPosition = new Vector3(0,y1, 0);
                Transform select_trans = order_summary.transform.FindChild("select");
                select_trans.FindChild("show").gameObject.SetActive(i == selectIndex);
                UIButton button = select_trans.GetComponent<UIButton>();
                EventDelegate event_delegate = new EventDelegate(this,"select");
                event_delegate.parameters[0] = new EventDelegate.Parameter();
                event_delegate.parameters[0].obj = order_summary;
                button.onClick.Add(event_delegate);
                y1 -= 80;
                mjfk_link.offset += 80;
            }
            else if (order.state == 4)
            {
                GameObject order_summary = NGUITools.AddChild(suns2.gameObject, AdminDealManager.pref_deal_info);
                order_summary.name = "" + i;
                order_summary.transform.FindChild("name").GetComponent<UILabel>().text = order.item.stampName + "-" + order.id;
                order_summary.transform.localPosition = new Vector3(0,y2,0);
                Transform select_trans = order_summary.transform.FindChild("select");
                select_trans.FindChild("show").gameObject.SetActive(i == selectIndex);
                UIButton button = select_trans.GetComponent<UIButton>();
                EventDelegate event_delegate = new EventDelegate(this,"select");
                event_delegate.parameters[0] = new EventDelegate.Parameter();
                event_delegate.parameters[0].obj = order_summary;
                button.onClick.Add(event_delegate);
                y2 -= 80;
                mjqr_link.offset += 80;
            }
        }
        refreshRight();
    }

    void select(GameObject obj)
    {
        int index = int.Parse(obj.name);
        if (selectIndex == index)
        {
            return;
        }
        Transform container = obj.transform.parent;
        Transform preTrans = container.FindChild(selectIndex + "");
        if (preTrans != null)
        {
            preTrans.FindChild("select").FindChild("show").gameObject.SetActive(false);
        }
        obj.transform.FindChild("select").FindChild("show").gameObject.SetActive(true);
        selectIndex = index;
        refreshRight();
    }

    void refreshRight()
    {
        Transform right = transform.FindChild("right");
        if (orders.Count == 0)
        {
            right.FindChild("order-help").gameObject.SetActive(false);
            right.FindChild("appraise").gameObject.SetActive(false);
        }
        else
        {
            DealBody.Order order = orders[selectIndex];
            GameObject order_obj = right.FindChild("order-help").gameObject;
            order_obj.SetActive(true);
            right.FindChild("appraise").gameObject.SetActive(false);
            Transform body = order_obj.transform.FindChild("body");
            order.insterToObj(body.gameObject,true);
            body.FindChild("sk").FindChild("action").gameObject.SetActive(order.state == 1);
            body.FindChild("zfk").FindChild("action").gameObject.SetActive(order.state == 4);
            GameObject buy_obj = body.FindChild("buyer-appraise").gameObject;
            GameObject sell_obj = body.FindChild("seller-appraise").gameObject;
            order.buyerAppraise.insterToObj(buy_obj,body.transform.parent.gameObject,body.transform.parent.parent.gameObject,true);
            order.sellerAppraise.insterToObj(sell_obj,body.transform.parent.gameObject,body.transform.parent.parent.gameObject,true);
        }
    }
    public static void deserializeAll(ByteBuffer data)
    {
        orders.Clear();
        int size = data.ReadInt();
        for (int i = 0; i < size; i++ )
        {
            DealBody deal = DealBody.read(data);
            for (int j = 0; j < deal.orders.Count; j++)
            {
                DealBody.Order order = deal.orders[j];
                if (order.helpflag && (order.state == 1 || order.state == 4))
                {
                    orders.Add(order);
                }
            }
        }
    }
    public static void deserializeModuleOne(ByteBuffer data)
    {
        byte flag = data.ReadByte();
        long orderId = data.ReadLong();
        DealBody deal = DealBody.read(data);
        if (flag == JustRun.ADD_FLAG)
        {
            bool insert = true;
            for (int i = 0; i < orders.Count; i++)
            {
                DealBody.Order order = orders[i];
                if (order.id == orderId)
                {
                    insert = false;
                    break;
                }
            }
            if (insert)
            {
                orders.Add(deal.searchOrder(orderId));
            }
        }
        else if (flag == JustRun.DEL_FLAG)
        {
            for (int i = 0; i < orders.Count; i++)
            {
                DealBody.Order order = orders[i];
                if (order.id == orderId)
                {
                    orders.Remove(order);
                    break;
                }
            }
        }
    }

    public void lookSeller()
    {
        DealBody.Order order = orders[selectIndex];
        long sellerId = order.item.seller ? order.item.uid : order.buyId;
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminLookUser");
        buffer.WriteLong(sellerId);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void lookBuyer()
    {
        DealBody.Order order = orders[selectIndex];
        long buyerId = order.item.seller ? order.buyId : order.item.uid;
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminLookUser");
        buffer.WriteLong(buyerId);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void _revoke()
    {
        ConfirmUtil.TryToDispear();
        DealBody.Order order = orders[selectIndex];
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminOrderRevoke");
        buffer.WriteLong(order.item.id);
        buffer.WriteLong(order.id);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void revoke()
    {
        ConfirmUtil.confirm("确定撤销?", _revoke);
    }

    void _zjsk()
    {
        ConfirmUtil.TryToDispear();
        DealBody.Order order = orders[selectIndex];
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminDealOrderUpdate");
        buffer.WriteLong(order.item.id);
        buffer.WriteLong(order.id);
        buffer.WriteByte((byte)2);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void zjsk()
    {
        ConfirmUtil.confirm("确定买家已打款?", _zjsk);
    }

    void _zjfk()
    {
        ConfirmUtil.TryToDispear();
        DealBody.Order order = orders[selectIndex];
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminDealOrderUpdate");
        buffer.WriteLong(order.item.id);
        buffer.WriteLong(order.id);
        buffer.WriteByte((byte)5);
        NetUtil.getInstance.SendMessage(buffer);
    }
    public void zjfk()
    {
        ConfirmUtil.confirm("确定已给卖家打款?", _zjfk);
    }

    void refreshUser(MainData.UserData user)
    {
        Transform right = transform.FindChild("right");
        Transform container = right.FindChild("user-look");
        right.FindChild("order-help").gameObject.SetActive(false);
        container.gameObject.SetActive(true);
        container.FindChild("account").FindChild("value").GetComponent<UILabel>().text = user.account;
        container.FindChild("nickName").FindChild("value").GetComponent<UILabel>().text = user.nikeName;
        container.FindChild("name").FindChild("value").GetComponent<UILabel>().text = user.realyName;
        container.FindChild("ident").FindChild("value").GetComponent<UILabel>().text = user.indentity;
        container.FindChild("type").FindChild("value").GetComponent<UILabel>().text = user.permission == 1 ? "买家" : "卖家";
        container.FindChild("title").FindChild("value").GetComponent<UILabel>().text = user.title;
        container.FindChild("deposit").FindChild("value").GetComponent<UILabel>().text = user.deposit + "";
        container.FindChild("deal").FindChild("value").GetComponent<UILabel>().text = user.credit.totalDealValue + "";
        container.FindChild("credit-c").FindChild("value").GetComponent<UILabel>().text = user.credit.maxValue + "";
        container.FindChild("credit-t").FindChild("value").GetComponent<UILabel>().text = user.credit.tempMaxValue + "";
        container.FindChild("hp").FindChild("value").GetComponent<UILabel>().text = user.credit.hp + "";
        container.FindChild("zp").FindChild("value").GetComponent<UILabel>().text = user.credit.zp + "";
        container.FindChild("cp").FindChild("value").GetComponent<UILabel>().text = user.credit.cp + "";
        container.FindChild("regist").FindChild("value").GetComponent<UILabel>().text = user.registTime;
        container.FindChild("time").FindChild("value").GetComponent<UILabel>().text = user.endTime;
        container.FindChild("wg").FindChild("value").GetComponent<UILabel>().text = user.breach + "";
        Transform fh_body = container.FindChild("fh").FindChild("body");
        UILabel reason = fh_body.FindChild("value").GetComponent<UILabel>();
        if (user.forbid.endTime.Equals("forever"))
        {
            reason.text = "永久封号";
            fh_body.FindChild("time").FindChild("value").GetComponent<UILabel>().text = "永久";
        }
        else if (user.forbid.endTime.Equals("null"))
        {
            reason.text = "未被封号";
            fh_body.FindChild("time").FindChild("value").GetComponent<UILabel>().text = "无";
        }
        else
        {
            reason.text = user.forbid.reason + "";
            fh_body.FindChild("time").FindChild("value").GetComponent<UILabel>().text = user.forbid.endTime;
        }
        if (user.addresses.Count > 0)
        {
            container.FindChild("address").FindChild("value").GetComponent<UILabel>().text = user.addresses[0];
        }
        else
        {
            container.FindChild("address").FindChild("value").GetComponent<UILabel>().text = "未绑定地址";
        }
        if (user.bacnkAccount.names.Count > 0)
        {
            container.FindChild("bank").FindChild("value").GetComponent<UILabel>().text = user.bacnkAccount.names[0] + " " + user.bacnkAccount.accounts[0];
        }
        else
        {
            container.FindChild("bank").FindChild("value").GetComponent<UILabel>().text = "未绑定银行卡";
        }
        container.FindChild("other").FindChild("value").GetComponent<UILabel>().text = MyUtilTools.stringIsNull(user.other) ? "没有备注" : user.other;
    }

    public void backFromUserLook()
    {
       Transform right  =  transform.FindChild("right");
       right.FindChild("order-help").gameObject.SetActive(true);
       right.FindChild("user-look").gameObject.SetActive(false);
    }
}
