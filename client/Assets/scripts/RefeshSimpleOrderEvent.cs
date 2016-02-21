using UnityEngine;
using System.Collections;

public class RefeshSimpleOrderEvent : MonoBehaviour {

    GameObject pref_simple;

    public DealEvent dealEvent;

	// Use this for initialization
	void Start () {
        pref_simple = Resources.Load<GameObject>("prefabs/order-simple");
	}
	
	// Update is called once per frame
	void Update () {
        
        ByteBuffer buffer = MyUtilTools.tryToLogic("LookDealOrder");
        if (buffer != null)
        {
            MainData.instance.deserializeDeals(buffer);
            long orderId = buffer.ReadLong();
            DealBody deal = MainData.instance.deal_all[0];
            DealBody.Order order = deal.searchOrder(orderId);
            gotoDeal(order);
            return;
        }
        if (MainData.instance.user.simpleOrderModuleNeedRefresh)
        {
            refresh();
            MainData.instance.user.simpleOrderModuleNeedRefresh = false;
        }
	}

    void refresh()
    {
        MyUtilTools.clearChild(transform);
        float len = 140, start = 180;
        for (int i = 0; i < MainData.instance.user.recentOrders.Count; i++)
        {
            MainData.SimpleOrderModule module = MainData.instance.user.recentOrders[i];
            GameObject summary = NGUITools.AddChild(gameObject, pref_simple);
            summary.transform.localPosition = new Vector3(0,start,0);
            summary.name = "module_" + i;
            UILabel content = summary.transform.FindChild("title").GetComponent<UILabel>();
            content.text = module.content;
            UILabel time = summary.transform.FindChild("timeLable").GetComponent<UILabel>();
            time.text = module.time;
            UIButton button = summary.transform.FindChild("open").GetComponent<UIButton>();
            EventDelegate eventDelegate = new EventDelegate(this, "tryToLoadDeal");
            eventDelegate.parameters[0] = new EventDelegate.Parameter();
            eventDelegate.parameters[0].obj = module;
            button.onClick.Add(eventDelegate);
            start -= len;
        }
    }

    void tryToLoadDeal(MainData.SimpleOrderModule module)
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("LookDealOrder");
        buffer.WriteLong(module.dealId);
        buffer.WriteLong(module.orderId);//编号
        NetUtil.getInstance.SendMessage(buffer);
    }

    void backFromeDeal()
    {
        dealEvent.needshow[0].SetActive(false);
        dealEvent.needdisPear[0].SetActive(true);
        refresh();
    }

    void gotoDeal(DealBody.Order order)
    {
        dealEvent.needshow[0].SetActive(true);
        dealEvent.needdisPear[0].SetActive(false);
        dealEvent.order_detail(order);
        dealEvent.detailBack = new EventDelegate(backFromeDeal);
    }
}
