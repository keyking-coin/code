using UnityEngine;
//using System.Collections;
using System.Collections.Generic;

public class SellEvent : CenterEvent {
	
	public class SellLogic : MonoBehaviour{
		
		public SellEvent sell;
		
		// Use this for initialization
		void Start () {
			
		}
		
		// Update is called once per frame
		void Update () {
			ByteBuffer buffer = MyUtilTools.tryToLogic("Issue");
			if (buffer != null)
			{
				sell.backToCenter();
			}
		}
	}
	
	// Use this for initialization
	void Start () {
		SellLogic logic = needshow[0].AddComponent<SellLogic>();
		logic.sell = this;
	}
	
	// Update is called once per frame
	void Update () {
		
	}
	
	public void issue(GameObject container)
	{
		_issue(container,false);
	}
	
	public void pushIssue(GameObject container)
	{
		_issue(container,true);
	}
	
	private void _issue(GameObject container , bool push)
	{
		if (!MainData.instance.user.login())
		{
			LoginEvent.tryToLogin();
			return;
		}
		if (push && !MainData.instance.user.recharge.haveMoney(10))
		{
			DialogUtil.tip("邮游币不足");
			return;
		}
        Transform trans = container.transform;
        UIPopupList typeList = trans.FindChild("type").GetComponent<UIPopupList>();
        string addressStr = "";
        if (typeList.value.Equals("现货"))
        {
            Transform address_tran = trans.FindChild("address");
            UIPopupList addressList = address_tran.GetComponent<UIPopupList>();
            if (addressList.value.Equals("其他"))
            {
                UIInput input = address_tran.FindChild("inputer").GetComponent<UIInput>();
                if (MyUtilTools.stringIsNull(input.value))
                {
                    UILabel label = input.transform.FindChild("tips").GetComponent<UILabel>();
                    DialogUtil.tip(label.text);
                    return;
                }
                addressStr = "1," + input.value;
            }
            else
            {
                addressStr = "0," + addressList.value;
            }
        }
        else
        {
            Transform wjs_tran = trans.FindChild("wjs-select");
            UIPopupList wjsList = wjs_tran.GetComponent<UIPopupList>();
            if (wjsList.value.Equals("其他文交所"))
            {
                UIInput input = wjs_tran.FindChild("inputer").GetComponent<UIInput>();
                if (MyUtilTools.stringIsNull(input.value))
                {
                    UILabel label = input.transform.FindChild("tips").GetComponent<UILabel>();
                    DialogUtil.tip(label.text);
                    return;
                }
                addressStr = "1," + input.value;
            }
            else
            {
                addressStr = "0," + wjsList.value;
            }
        }
        UIInput input_name = trans.FindChild("name").GetComponent<UIInput>();
        string name = input_name.value;
        if (MyUtilTools.stringIsNull(name))
        {
            UILabel label = input_name.transform.FindChild("tips").GetComponent<UILabel>();
            DialogUtil.tip(label.text);
            return;
        }
        UIInput input_num = trans.FindChild("num").GetComponent<UIInput>();
        string num = input_num.value;
        if (MyUtilTools.stringIsNull(num))
        {
            UILabel label = input_num.transform.FindChild("tips").GetComponent<UILabel>();
            DialogUtil.tip(label.text);
            return;
        }
        UIInput input_price = trans.FindChild("price").GetComponent<UIInput>();
        string price = input_price.value;
        if (MyUtilTools.stringIsNull(price))
        {
            UILabel label = input_price.transform.FindChild("tips").GetComponent<UILabel>();
            DialogUtil.tip(label.text);
            return;
        }
        UIPopupList danweiList = trans.FindChild("danwei").GetComponent<UIPopupList>();
        int n_num = int.Parse(num);
        Transform time_trans = trans.FindChild("time");
        UIInput year = time_trans.FindChild("year").GetComponent<UIInput>();
        UIInput month = time_trans.FindChild("month").GetComponent<UIInput>();
        UIInput day = time_trans.FindChild("day").GetComponent<UIInput>();
        UIInput hour = time_trans.FindChild("hour").GetComponent<UIInput>();
        UIInput minute = time_trans.FindChild("minute").GetComponent<UIInput>();
        string time = year.value + "-" + month.value + "-" + day.value + " " + hour.value + ":" + minute.value + ":00";
        UIInput input_other = trans.FindChild("other").GetComponent<UIInput>();
        string other = input_other.value;
        UIToggle toggle = trans.FindChild("flag").GetComponent<UIToggle>();
		ByteBuffer buffer = ByteBuffer.Allocate(1024);
		buffer.skip(4);
		buffer.WriteString("Issue");
		buffer.WriteLong(MainData.instance.user.id);
        buffer.WriteByte((byte)(push?1:0));
		buffer.WriteByte((byte)1);
        buffer.WriteByte((byte)(typeList.value.Equals("入库") ?0:1));
        buffer.WriteString(addressStr);
        buffer.WriteString(name);
		buffer.WriteString(price);
        buffer.WriteInt(n_num);
        buffer.WriteString(danweiList.value);
		buffer.WriteString(time);
		buffer.WriteString(other);
        buffer.WriteByte((byte)(toggle.value ? 1 : 0));
		NetUtil.getInstance.SendMessage(buffer);
	}
}