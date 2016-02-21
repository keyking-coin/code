using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class AgencyEvent : CenterEvent
{
    GameObject list_container;

    public DealEvent dealEvent;

	// Use this for initialization
	void Start () {
        list_container = needshow[0].transform.FindChild("list").FindChild("body").FindChild("container").gameObject;
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("AgencyEnter");
        if (buffer != null)
        {
            click();
            readDatas(buffer);
        }
	}

    public void readDatas(ByteBuffer buffer)
    {
        MainData.instance.deserializeDeals(buffer);
        List<DealBody.Order> temp = new List<DealBody.Order>();
        foreach (DealBody item in MainData.instance.deal_all)
        {
            foreach (DealBody.Order order in item.orders)
            {
                if (order.helpflag)
                {
                    temp.Add(order);
                }
            }
        }
        temp.Sort();//排序
        list_container.transform.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        list_container.transform.parent.localPosition = new Vector3(0,0,0);
        dealEvent.refreshListOrder(list_container,temp,this,"gotoDealEvent_Detail");
    }

    public void enterIn()
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AgencyEnter");
        buffer.WriteString("null");
        NetUtil.getInstance.SendMessage(buffer);
    }

    void backFromDealEvent()
    {
        show();
        dealEvent.show(false);
    }

    void gotoDealEvent_Detail(DealBody.Order order)
    {
        dealEvent.detailBack = new EventDelegate(backFromDealEvent);
        show(false);
        dealEvent.show();
        dealEvent.order_detail(order);
    }
}
