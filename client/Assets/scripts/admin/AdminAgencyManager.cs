using UnityEngine;
using System.Collections.Generic;

public class AdminAgencyManager : MonoBehaviour {

    public static List<DealBody.Order> orders = new List<DealBody.Order>();

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
	}

    void refresh()
    {
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
            for (int i = 0; i < deal.orders.Count; i++)
            {
                DealBody.Order order = deal.orders[i];
                if (order.id == orderId)
                {
                    orders.Add(order);
                    break;
                }
            }
        }
        else if (flag == JustRun.DEL_FLAG)
        {
            for (int i = 0; i < deal.orders.Count ; i++)
            {
                DealBody.Order order = deal.orders[i];
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

    }

    public void lookBuyer()
    {

    }

    public void revoke()
    {

    }

    public void zjsk()
    {

    }

    public void zjfk()
    {

    }
}
