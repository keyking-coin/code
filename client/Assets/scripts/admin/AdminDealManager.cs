using UnityEngine;
using System.Collections.Generic;

public class AdminDealManager : MonoBehaviour {

    GameObject pref_deal_info = null;

    int selectIndex = 0;

    List<DealBody.Order> orders = new List<DealBody.Order>();
    GameObject select_obj = null;

	// Use this for initialization
	void Start () {
        if (pref_deal_info == null)
        {
            pref_deal_info = Resources.Load<GameObject>("prefabs/deal-info");
        }
        Transform up = transform.FindChild("left").FindChild("up");
        up.FindChild("mm").GetComponent<UIButton>().tweenTarget = null;
        up.FindChild("cj").GetComponent<UIButton>().tweenTarget = null;
        select_obj = up.FindChild("mm").gameObject;
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("AdminDealSearch");
        if (buffer != null)
        {
            MainData.instance.deserializeDeals(buffer);
            refreshList();
        }
        buffer = MyUtilTools.tryToLogic("AdminDealMore");
        if (buffer != null)
        {
            MainData.instance.deserializeDeals(buffer);
            backHighSearch(transform.FindChild("right").gameObject,transform.FindChild("search-pop").gameObject);
            refreshList();
        }
        buffer = MyUtilTools.tryToLogic("AdminDealRevoke");
        if (buffer != null)
        {
            long dealId = buffer.ReadLong();
            for (int i = 0; i < MainData.instance.deal_all.Count; i++ )
            {
                DealBody deal = MainData.instance.deal_all[i];
                if (deal.id == dealId)
                {
                    MainData.instance.deal_all.RemoveAt(i);
                    break;
                }
            }
            MainData.instance.deserializeDeals(buffer);
            backHighSearch(transform.FindChild("right").gameObject, transform.FindChild("search-pop").gameObject);
            refreshList();
        }
	}

    void refreshList()
    {
        Transform body = transform.FindChild("left").FindChild("list").FindChild("body");
        body.localPosition = Vector3.zero;
        body.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        Transform container = body.FindChild("container");
        MyUtilTools.clearChild(container);
        float y = 780;
        if (select_obj.name.Equals("mm"))
        {
            for (int i = 0; i < MainData.instance.deal_all.Count; i++)
            {
                DealBody deal = MainData.instance.deal_all[i];
                GameObject deal_summary = NGUITools.AddChild(container.gameObject,pref_deal_info);
                deal_summary.name = "" + i;
                deal_summary.transform.FindChild("name").GetComponent<UILabel>().text = deal.stampName;
                deal_summary.transform.localPosition = new Vector3(0,y,0);
                Transform select_trans = deal_summary.transform.FindChild("select");
                select_trans.FindChild("show").gameObject.SetActive(i == selectIndex);
                UIButton button = select_trans.GetComponent<UIButton>();
                EventDelegate event_delegate = new EventDelegate(this, "select");
                event_delegate.parameters[0] = new EventDelegate.Parameter();
                event_delegate.parameters[0].obj = deal_summary;
                button.onClick.Add(event_delegate);
                y -= 80;
            }
        }
        else
        {
            orders.Clear();
            for (int i = 0; i < MainData.instance.deal_all.Count; i++)
            {
                DealBody deal = MainData.instance.deal_all[i];
                for (int j = 0 ; j < deal.orders.Count ; j++ )
                {
                    DealBody.Order order = deal.orders[j];
                    orders.Add(order);
                }
            }
            for (int i = 0; i < orders.Count; i++)
            {
                DealBody.Order order = orders[i];
                GameObject deal_summary = NGUITools.AddChild(container.gameObject, pref_deal_info);
                deal_summary.name = "" + i;
                deal_summary.transform.FindChild("name").GetComponent<UILabel>().text = order.item.stampName + "-" + order.id;
                deal_summary.transform.localPosition = new Vector3(0,y,0);
                Transform select_trans = deal_summary.transform.FindChild("select");
                select_trans.FindChild("show").gameObject.SetActive(i == selectIndex);
                UIButton button = select_trans.GetComponent<UIButton>();
                EventDelegate event_delegate = new EventDelegate(this,"select");
                event_delegate.parameters[0] = new EventDelegate.Parameter();
                event_delegate.parameters[0].obj = deal_summary;
                button.onClick.Add(event_delegate);
                y -= 80;
            }
        }
        refreshRight();
    }

    public void backHighSearch(GameObject show, GameObject dispear)
    {
        CameraUtil.pop(4);
        show.SetActive(true);
        dispear.SetActive(false);
    }

    public void openHighSearch(GameObject show,GameObject dispear)
    {
        CameraUtil.push(4,3);
        show.GetComponent<JustChangeLayer>().change(11); ;
        show.SetActive(true);
        dispear.SetActive(false);
    }

    public void search()
    {
        UIInput input = transform.FindChild("left").FindChild("search").FindChild("inputer").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(input.value))
        {
            DialogUtil.tip(input.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminDealSearch");
        buffer.WriteLong(long.Parse(input.value));
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void searchHight()
    {
        Transform  container = transform.FindChild("search-pop");
        UIPopupList list = container.FindChild("type").GetComponent<UIPopupList>();
        string typeStr = list.value.Equals("全部") ? "null" : list.value;
        string bourse = "null";
        if (typeStr.Equals("入库"))
        {
            Transform wjs_trans = container.FindChild("wjs-select");
            list = wjs_trans.GetComponent<UIPopupList>();
            if (list.value.Equals("其他文交所"))
            {
                UIInput bourse_input = wjs_trans.FindChild("inputer").GetComponent<UIInput>();
                bourse = MyUtilTools.stringIsNull(bourse_input.value) ? "null" : bourse_input.value;
            }
            else if (!list.value.Equals("所有文交所"))
            {
                bourse = list.value;
            }
        }
        else if (typeStr.Equals("现货"))
        {
            Transform address_trans = container.FindChild("address");
            list = address_trans.GetComponent<UIPopupList>();
            if (list.value.Equals("其他"))
            {
                UIInput address_input = address_trans.FindChild("inputer").GetComponent<UIInput>();
                bourse = MyUtilTools.stringIsNull(address_input.value) ? "null" : address_input.value;
            }
            else if (!list.value.Equals("不限"))
            {
                bourse = list.value;
            }
        }
        UIInput input = container.FindChild("title").GetComponent<UIInput>();
        string title = MyUtilTools.stringIsNull(input.value) ? "null" : input.value;
        input = container.FindChild("seller").GetComponent<UIInput>();
        string seller = MyUtilTools.stringIsNull(input.value) ? "null" : input.value;
        input = container.FindChild("buyer").GetComponent<UIInput>();
        string buyer = MyUtilTools.stringIsNull(input.value) ? "null" : input.value;
        list = container.FindChild("validTime").GetComponent<UIPopupList>();
        string valid = list.value.Equals("不限有效期") ? "null" : list.value;
        string searchStr = "{\"type\":\"" + typeStr + "\"," +
                             "\"bourse\":\"" + bourse + "\"," +
                             "\"title\":\"" + title + "\"," +
                             "\"seller\":\"" + seller + "\"," +
                             "\"buyer\":\"" + buyer + "\"," +
                             "\"valid\":\"" + valid + "\"}";
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminDealMore");
        buffer.WriteString(searchStr);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void selectTab(GameObject obj1,GameObject obj2)
    {
        obj1.GetComponent<UITexture>().color = new Color(82f/255f, 227f/255f, 125f/255f);
        obj2.GetComponent<UITexture>().color = Color.gray;
        select_obj = obj1;
        refreshList();
    }

    void select(GameObject obj)
    {
        int index = int.Parse(obj.name);
        if (selectIndex == index)
        {
            return;
        }
        Transform container = transform.FindChild("left").FindChild("list").FindChild("body").FindChild("container");
        Transform preTrans = container.FindChild(selectIndex + "");
        if (preTrans != null)
        {
            preTrans.FindChild("select").FindChild("show").gameObject.SetActive(false);
        }
        Transform curTrans = container.FindChild(index + "");
        if (curTrans != null)
        {
            curTrans.FindChild("select").FindChild("show").gameObject.SetActive(true);
        }
        selectIndex = index;
        refreshRight();
    }

    void refreshRight()
    {
        Transform right = transform.FindChild("right");
        if (select_obj.name.Equals("mm"))
        {
            GameObject deal_obj = right.FindChild("deal-detail").gameObject;
            if (MainData.instance.deal_all.Count == 0)
            {
                deal_obj.SetActive(false);
            }
            else
            {
                deal_obj.SetActive(true);
                DealBody deal = MainData.instance.deal_all[selectIndex];
                deal.insterItem(deal_obj);
                Transform event_tran = deal_obj.transform.FindChild("event");
                if (deal.isLock)
                {
                    event_tran.FindChild("ulock").gameObject.SetActive(true);
                    event_tran.FindChild("lock").gameObject.SetActive(false);
                }
                else
                {
                    event_tran.FindChild("lock").gameObject.SetActive(true);
                    event_tran.FindChild("ulock").gameObject.SetActive(false);
                }
            }
            right.FindChild("order-help").gameObject.SetActive(false);
            right.FindChild("order-normal").gameObject.SetActive(false);
            right.FindChild("appraise").gameObject.SetActive(false);
            
        }
        else
        {
            right.FindChild("deal-detail").gameObject.SetActive(false);
            right.FindChild("appraise").gameObject.SetActive(false);
            if (orders.Count == 0)
            {
                right.FindChild("order-help").gameObject.SetActive(false);
                right.FindChild("order-normal").gameObject.SetActive(false);
            }
            else
            {
                DealBody.Order order = orders[selectIndex];
                if (order.helpflag)
                {
                    GameObject order_obj = right.FindChild("order-help").gameObject;
                    order_obj.SetActive(true);
                    right.FindChild("order-normal").gameObject.SetActive(false);
                    Transform body = order_obj.transform.FindChild("body");
                    order.insterToObj(body.gameObject,true);
                    GameObject buy_obj = body.FindChild("buyer-appraise").gameObject;
                    GameObject sell_obj = body.FindChild("seller-appraise").gameObject;
                    order.buyerAppraise.insterToObj(buy_obj, body.transform.parent.gameObject,body.transform.parent.parent.gameObject,true);
                    order.sellerAppraise.insterToObj(sell_obj, body.transform.parent.gameObject, body.transform.parent.parent.gameObject,true);
                }
                else
                {
                    right.FindChild("order-help").gameObject.SetActive(false);
                    GameObject order_obj = right.FindChild("order-normal").gameObject;
                    order_obj.SetActive(true);
                    Transform body = order_obj.transform.FindChild("body");
                    order.insterToObj(body.gameObject,true);
                    GameObject buy_obj = body.FindChild("buyer-appraise").gameObject;
                    GameObject sell_obj = body.FindChild("seller-appraise").gameObject;
                    order.buyerAppraise.insterToObj(buy_obj, body.transform.parent.gameObject, body.transform.parent.parent.gameObject,true);
                    order.sellerAppraise.insterToObj(sell_obj, body.transform.parent.gameObject, body.transform.parent.parent.gameObject,true);
                }
            }
        }
    }

    public void revokeOrder()
    {

    }

    public void lockDeal()
    {

    }

    public void ulockDeal()
    {

    }

    public void revokeDeal()
    {
        DealBody deal = MainData.instance.deal_all[selectIndex];
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminDealRevoke");
        buffer.WriteLong(deal.id);
        NetUtil.getInstance.SendMessage(buffer);
    }
}
