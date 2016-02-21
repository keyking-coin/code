using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class MyDealEvent :  CenterEvent {

    public DealEvent dealEvent;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("DealSearch");
        if (buffer != null)
        {
            int type = buffer.ReadInt();
            MainData.instance.deserializeDeals(buffer);
            if (MainData.instance.deal_all.Count == 0)
            {
                DialogUtil.tip("未找到相关数据");
            }
            else
            {
                dealEvent.initList();
                dealEvent.listBack = new EventDelegate(backFromDealEvent);
                show(false);
            }
        }
	}

    void backFromDealEvent()
    {
        show();
        dealEvent.needshow[0].SetActive(false);
    }

    public void search(GameObject obj)
    {
        if (!MainData.instance.user.login())
        {
            LoginEvent.tryToLogin();
            return;
        }
        int type = 0;
        if (obj.name.Equals("ddjy"))
        {
            type = 1;
        }
        else if (obj.name.Equals("zzjy"))
        {
            type = 2;
        }
        else if (obj.name.Equals("ddpj"))
        {
            type = 3;
        }
        else if (obj.name.Equals("ywcjy"))
        {
            type = 4;
        }
        else if (obj.name.Equals("wdct"))
        {
            type = 5;
        }
        else if (obj.name.Equals("wdscj"))
        {
            type = 6;
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DealSearch");
        buffer.WriteInt(type);
        buffer.WriteLong(MainData.instance.user.id);
        NetUtil.getInstance.SendMessage(buffer);
    }
}
