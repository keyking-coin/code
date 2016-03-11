using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class SearchEvent : MonoBehaviour {

    public DealEvent dealEvent = null;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("EnterDeal");
        if (buffer != null)
        {
            MainData.instance.deserializeDeals(buffer);
			dealEvent.backSearch();
        }
	}

    public void search()
    {
        Transform transform = gameObject.transform;
        UIPopupList list = transform.FindChild("type").GetComponent<UIPopupList>();
        string typeStr = list.value.Equals("全部") ? "null" : list.value;
        string bourse = "null";
        if (typeStr.Equals("入库"))
        {
            Transform wjs_trans = transform.FindChild("wjs-select");
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
            Transform address_trans = transform.FindChild("address");
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
        UIInput input = transform.FindChild("title").GetComponent<UIInput>();
        string title = MyUtilTools.stringIsNull(input.value) ? "null" : input.value;
        input = transform.FindChild("seller").GetComponent<UIInput>();
        string seller = MyUtilTools.stringIsNull(input.value) ? "null" : input.value;
        input = transform.FindChild("buyer").GetComponent<UIInput>();
        string buyer = MyUtilTools.stringIsNull(input.value) ? "null" : input.value;
        list = transform.FindChild("validTime").GetComponent<UIPopupList>();
        string valid = list.value.Equals("不限有效期") ? "null" : list.value;
        string searchStr = "{\"type\":\"" + typeStr + "\"," +
                             "\"bourse\":\"" + bourse + "\"," +
                             "\"title\":\"" + title + "\"," +
                             "\"seller\":\"" + seller + "\"," +
                             "\"buyer\":\"" + buyer + "\"," +
                             "\"valid\":\"" + valid + "\"}";
        dealEvent.tryToSearch(searchStr);
    }

    public void back()
    {
        dealEvent.backSearch();
    }

    public void openAgencySearch()
    {

    }
}
