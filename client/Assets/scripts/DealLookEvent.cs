using UnityEngine;
using System.Collections;

public class DealLookEvent : MonoBehaviour
{

    public EventDelegate callback = null;

    MainData.BankAccount bacnkAccount = new MainData.BankAccount();

    string cur_tel_str;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {

	}

    void openMore()
    {
        GameObject.Find("base").transform.FindChild("popup-select").GetComponent<PopupListEvent>().OnlyPop("银行卡列表",bacnkAccount);
    }

    void telphone()
    {
        #if UNITY_ANDROID
        AndroidJavaClass ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject ajo = ajc.GetStatic<AndroidJavaObject>("currentActivity");
        ajo.Call("telphone",cur_tel_str);
        #endif
    }

    public void show(ByteBuffer buffer)
    {
        gameObject.SetActive(true);
        string icon_str = buffer.ReadString();
        string nikeName = buffer.ReadString();
        string wx_str = buffer.ReadString();
        string qq_str = buffer.ReadString();
        string address = buffer.ReadString();
        string name = buffer.ReadString();
        cur_tel_str = buffer.ReadString();
        bacnkAccount.deserialize(buffer);

        Transform container = transform.FindChild("scroll").FindChild("body").FindChild("container");
        UISprite icon = container.FindChild("icon").GetComponent<UISprite>();
        icon.spriteName = icon_str;
        UILabel label = container.FindChild("nikeName").GetComponent<UILabel>();
        label.text = nikeName;
        label = container.FindChild("weixin").FindChild("value").GetComponent<UILabel>();
        label.text = wx_str;
        label = container.FindChild("qq").FindChild("value").GetComponent<UILabel>();
        label.text = qq_str; 
        label = container.FindChild("address").FindChild("value").GetComponent<UILabel>();
        label.text = address;
        label = container.FindChild("name").FindChild("value").GetComponent<UILabel>();
        label.text = name;
        label = container.FindChild("tel").FindChild("value").GetComponent<UILabel>();
        label.text = cur_tel_str;
        Transform tel_trans = container.FindChild("tel").FindChild("call");
        if (cur_tel_str.Equals("保密"))
        {
            tel_trans.gameObject.SetActive(false);
        }
        else
        {
            tel_trans.gameObject.SetActive(true);
            UIButton tel_button = tel_trans.GetComponent<UIButton>();
            tel_button.onClick.Clear();
            tel_button.onClick.Add(new EventDelegate(telphone));
        }
        UILabel account_label = container.FindChild("bank").FindChild("value").GetComponent<UILabel>();
        if (bacnkAccount.names.Count == 0){
            account_label.text = "未绑定银行卡";
        }
        else
        {
            account_label.text = bacnkAccount.names[0] + ":" + bacnkAccount.accounts[0];
        }
        GameObject more = account_label.transform.parent.FindChild("more").gameObject;
        if (bacnkAccount.names.Count > 1)
        {
            more.SetActive(true);
            UIButton button = more.GetComponent<UIButton>();
            button.onClick.Clear();
            EventDelegate doOpen = new EventDelegate(openMore);
            button.onClick.Add(doOpen);
        }
        else
        {
            more.SetActive(false);
        }
        string curValue = buffer.ReadString();
        string maxValue = buffer.ReadString();
        string tempMaxValue = buffer.ReadString();
        string totalDealVale = buffer.ReadString();
        int hp = buffer.ReadInt();
        int zp = buffer.ReadInt();
        int cp = buffer.ReadInt();
        Transform credit = container.FindChild("credit");
        UILabel cur_value = credit.FindChild("cur-value").FindChild("Label").GetComponent<UILabel>();
        cur_value.text = curValue;
        UILabel max_value = credit.FindChild("max-value").FindChild("Label").GetComponent<UILabel>();
        max_value.text = maxValue;
        UILabel temp_value = credit.FindChild("temp-value").FindChild("Label").GetComponent<UILabel>();
        temp_value.text = tempMaxValue;
        UILabel deal_value = credit.FindChild("deal-value").FindChild("Label").GetComponent<UILabel>();
        deal_value.text = totalDealVale;
        UILabel hp_value = credit.FindChild("hp").FindChild("Label").GetComponent<UILabel>();
        hp_value.text = hp + "";
        UILabel zp_value = credit.FindChild("zp").FindChild("Label").GetComponent<UILabel>();
        zp_value.text = zp + "";
        UILabel cp_value = credit.FindChild("cp").FindChild("Label").GetComponent<UILabel>();
        cp_value.text = cp + "";
    }

    void insert(ByteBuffer buffer,string name)
    {
        int bad  = buffer.ReadInt();
        int med  = buffer.ReadInt();
        int good = buffer.ReadInt();
        int not  = buffer.ReadInt();
        int total = bad + med + good + not;

        Transform trans = transform.FindChild(name);
        UILabel label = trans.FindChild("bad").FindChild("value").GetComponent<UILabel>();
        label.text = bad == 0 ? "无" : (System.Convert.ToSingle(bad) / System.Convert.ToSingle(total) * 100 + "%(" + bad + ")");

        label = trans.FindChild("medium").FindChild("value").GetComponent<UILabel>();
        label.text = med == 0 ? "无" : (System.Convert.ToSingle(med) / System.Convert.ToSingle(total) * 100 + "%(" + med + ")");

        label = trans.FindChild("good").FindChild("value").GetComponent<UILabel>();
        label.text = good == 0 ? "无" : (System.Convert.ToSingle(good) / System.Convert.ToSingle(total) * 100 + "%(" + good + ")");

        label = trans.FindChild("not").FindChild("value").GetComponent<UILabel>();
        label.text = not == 0 ? "无" : (System.Convert.ToSingle(not) / System.Convert.ToSingle(total) * 100 + "%(" + not + ")");
    }

    public void cancle()
    {
        gameObject.SetActive(false);
        if (callback != null)
        {
            callback.Execute();
        }
    }
}
