using UnityEngine;
using System.Collections;

public class DealEditEvent : MonoBehaviour
{

    public EventDelegate callback = null;

    DealBody curItem;

	void Start () {
	
	}
	
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("DealEdit");
        if (buffer != null)
        {
            MainData.instance.deserializeDealModule(buffer);
            gameObject.SetActive(false);
            callback.Execute();
        }
	}

    public void sure()
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DealEdit");
        buffer.WriteLong(curItem.id);//编号
        Transform container = transform.FindChild("seller");
        UIPopupList pop_type = container.FindChild("type").GetComponent<UIPopupList>();
        buffer.WriteByte(pop_type.value.Equals("入库") ? (byte)0 : (byte)1);//交割方式
        if (curItem.seller)
        {
           string wjs = null;
           if (pop_type.value.Equals("入库"))
           {
               UIPopupList pop_select = container.FindChild("wjs-select").GetComponent<UIPopupList>();
               if (pop_select.value.Equals("其他文交所"))
               {
                   UIInput input = container.FindChild("wjs-select").GetComponentInChildren<UIInput>();
                   wjs = "1," + input.value;
               }
               else
               {
                   wjs = "0," + pop_select.value;
               }
           }
           else
           {
               GameObject address_obj = container.FindChild("address").gameObject;
               UIPopupList addressList = address_obj.GetComponent<UIPopupList>();
               wjs = addressList.value;
               if (wjs.Equals("其他"))
               {
                   UIInput input = address_obj.GetComponentInChildren<UIInput>();
                   if (MyUtilTools.stringIsNull(input.value))
                   {
                       DialogUtil.tip("请输入交易城市");
                       return;
                   }
                   wjs = "1," + input.value;
               }
               else
               {
                   wjs = "0," + addressList.value;
               }
           }
           buffer.WriteString(wjs);
           UIInput input_title = container.FindChild("title").GetComponentInChildren<UIInput>();
           string title = input_title.value;
           buffer.WriteString(title);

           UIInput input_price = container.FindChild("price").GetComponentInChildren<UIInput>();
           string price = input_price.value;
           buffer.WriteString(price);

           UIInput input_num = container.FindChild("num").GetComponentInChildren<UIInput>();
           string num = input_num.value;
           if (!MyUtilTools.stringIsNull(num))
           {
               buffer.WriteInt(int.Parse(num));
           }
           else
           {
               buffer.WriteInt(0);
           }
           UIInput input_minNum = container.FindChild("minNum").GetComponentInChildren<UIInput>();
           string minNum = input_minNum.value;
           buffer.WriteInt(int.Parse(minNum));

           UILabel label_time = container.FindChild("time").FindChild("show").GetComponentInChildren<UILabel>();
           string time = label_time.text;
           buffer.WriteString(time);

           UIInput input_other = container.FindChild("other").GetComponentInChildren<UIInput>();
           string other = input_other.value;
           buffer.WriteString(other);
        }
        else
        {
            container = gameObject.transform.FindChild("buyer");
            string wjs = null;
            if (pop_type.value.Equals("入库"))
            {
                UIPopupList pop_select = container.FindChild("wjs-select").GetComponent<UIPopupList>();
                if (pop_select.value.Equals("其他文交所"))
                {
                    UIInput input = container.FindChild("wjs-select").GetComponentInChildren<UIInput>();
                    wjs = "1," + input.value;
                }
                else
                {
                    wjs = "0," + pop_select.value;
                }
            }
            else
            {
                GameObject address_obj  = container.FindChild("address").gameObject;
                UIPopupList addressList = address_obj.GetComponent<UIPopupList>();
                wjs = addressList.value;
                if (wjs.Equals("其他"))
                {
                    UIInput input = address_obj.GetComponentInChildren<UIInput>();
                    if (MyUtilTools.stringIsNull(input.value))
                    {
                        DialogUtil.tip("请输入交易城市");
                        return;
                    }
                    wjs = "1," + input.value;
                }
                else
                {
                    wjs = "0," + addressList.value;
                }
            }
            buffer.WriteString(wjs);
            UIInput input_name = container.FindChild("name").GetComponentInChildren<UIInput>();
            string name = input_name.value;
            buffer.WriteString(name);
            UIInput input_price = container.FindChild("price").GetComponentInChildren<UIInput>();
            string price = input_price.value;
            buffer.WriteString(price);
            UIInput input_num = container.FindChild("num").GetComponentInChildren<UIInput>();
            string num = input_num.value;
            if (!MyUtilTools.stringIsNull(num))
            {
                buffer.WriteInt(int.Parse(num));
            }
            else
            {
                buffer.WriteInt(0);
            }
            buffer.WriteInt(0);
            UILabel label_time = container.FindChild("time").FindChild("show").GetComponentInChildren<UILabel>();
            string time = label_time.text;
            buffer.WriteString(time);
            UIInput input_other = container.FindChild("other").GetComponentInChildren<UIInput>();
            string other = input_other.value;
            buffer.WriteString(other);
        }
        NetUtil.getInstance.SendMessage(buffer);
    }

   

    public void cancle()
    {
        gameObject.SetActive(false);
        if (callback != null)
        {
            callback.Execute();
        }
    }

    public void show(DealBody item)
    {
        curItem = item;
        gameObject.SetActive(true);
        if (curItem.seller)
        {
            transform.FindChild("buyer").gameObject.SetActive(false);
            Transform container = transform.FindChild("seller");
            container.gameObject.SetActive(true);
            UIPopupList pop_type = container.FindChild("type").GetComponent<UIPopupList>();
            pop_type.value = item.typeStr;
            string[] ss = item.bourse.Split(new char[] { ',' });
            Transform wjs_trans = container.FindChild("wjs-select");
            Transform address_trans = container.FindChild("address");
            if (item.typeStr.Equals("入库"))
            {
                wjs_trans.gameObject.SetActive(true);
                address_trans.gameObject.SetActive(false);
                UIPopupList pop_select = wjs_trans.GetComponent<UIPopupList>();
                if (ss[0].Equals("0"))
                {
                    pop_select.value = ss[1];
                }
                else
                {
                    pop_select.value = "其他文交所";
                    UIInput input = wjs_trans.FindChild("inputer").GetComponent<UIInput>();
                    input.value = ss[1];
                }
            }
            else
            {
                wjs_trans.gameObject.SetActive(false);
                address_trans.gameObject.SetActive(true);
                UIPopupList pop_select = address_trans.GetComponent<UIPopupList>();
                if (ss[0].Equals("0"))
                {
                    pop_select.value = ss[1];
                }
                else
                {
                    pop_select.value = "其他";
                    UIInput input = address_trans.FindChild("inputer").GetComponent<UIInput>();
                    input.value = ss[1];
                }
            }
            UIInput title = container.FindChild("title").GetComponentInChildren<UIInput>();
            title.value = item.stampName;
            UIInput num = container.FindChild("num").GetComponentInChildren<UIInput>();
            num.value = item.curNum + "";
            UILabel label_time = container.FindChild("time").FindChild("show").GetComponentInChildren<UILabel>();
            label_time.text = item.validTime;
            UIInput price = container.FindChild("price").GetComponentInChildren<UIInput>();
            price.value = item.price + "";
            UIInput other = container.FindChild("other").GetComponentInChildren<UIInput>();
            other.value = item.context;
        }
        else
        {
            gameObject.transform.FindChild("seller").gameObject.SetActive(false);
            Transform container = gameObject.transform.FindChild("buyer");
            container.gameObject.SetActive(true);
            UIInput name = container.FindChild("name").GetComponentInChildren<UIInput>();
            name.value = item.stampName;
            UIInput num = container.FindChild("num").GetComponentInChildren<UIInput>();
            num.value = item.curNum + "";
            UILabel label_time = container.FindChild("time").FindChild("show").GetComponentInChildren<UILabel>();
            label_time.text = item.validTime;
            UIPopupList pop_type = container.FindChild("type").GetComponent<UIPopupList>();
            pop_type.value = item.typeStr;
            string[] ss = item.bourse.Split(new char[]{','});
            Transform wjs_trans = container.FindChild("wjs-select");
            Transform address_trans = container.FindChild("address");
            if (item.typeStr.Equals("入库"))
            {
                wjs_trans.gameObject.SetActive(true);
                address_trans.gameObject.SetActive(false);
                UIPopupList pop_select = wjs_trans.GetComponent<UIPopupList>();
                if (ss[0].Equals("0"))
                {
                    pop_select.value = ss[1];
                }
                else
                {
                    pop_select.value = "其他文交所";
                    UIInput input = wjs_trans.FindChild("inputer").GetComponent<UIInput>();
                    input.value = ss[1];
                }
            }
            else
            {
                wjs_trans.gameObject.SetActive(false);
                address_trans.gameObject.SetActive(true);
                UIPopupList pop_select = address_trans.GetComponent<UIPopupList>();
                if (ss[0].Equals("0"))
                {
                    pop_select.value = ss[1];
                }
                else
                {
                    pop_select.value = "其他";
                    UIInput input = address_trans.FindChild("inputer").GetComponent<UIInput>();
                    input.value = ss[1];
                }
            }
            UIInput other = container.FindChild("other").GetComponentInChildren<UIInput>();
            other.value = item.context;
        }
    }
}
