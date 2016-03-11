using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class DealLookEvent : MonoBehaviour
{

    public EventDelegate callback = null;

    string cur_tel_str;

    static GameObject bank_account_pref = null;

    static GameObject address_account_pref = null;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {

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
        if (bank_account_pref == null)
        {
            bank_account_pref = Resources.Load<GameObject>("prefabs/look-bank-account");
        }
        if (address_account_pref == null)
        {
            address_account_pref = Resources.Load<GameObject>("prefabs/look-address");
        }
        gameObject.SetActive(true);
        string icon_str = buffer.ReadString();
        string nikeName = buffer.ReadString();
        string signature = buffer.ReadString();
        string title = buffer.ReadString();
        string registTime = buffer.ReadString();
        List<string> addresses = new List<string>();
        int size = buffer.ReadInt();
        for (int i = 0; i < size; i++ )
        {
            string address = buffer.ReadString();
            addresses.Add(address);
        }
        string name = buffer.ReadString();
        cur_tel_str = buffer.ReadString();
        MainData.BankAccount bacnkAccount  = new MainData.BankAccount(); 
        bacnkAccount.deserialize(buffer);
        buffer.ReadString();
        buffer.ReadString();
        buffer.ReadString();
        string totalDealVale = buffer.ReadString();
        int hp = buffer.ReadInt();
        int zp = buffer.ReadInt();
        int cp = buffer.ReadInt();
        Transform body = transform.FindChild("scroll").FindChild("body");
        body.localPosition = Vector3.zero;
        body.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        Transform container = body.FindChild("container");
        UISprite icon = container.FindChild("icon").GetComponent<UISprite>();
        icon.spriteName = icon_str;
        UILabel label = container.FindChild("nikeName").GetComponent<UILabel>();
        label.text = nikeName;
        label = container.FindChild("signature").FindChild("value").GetComponent<UILabel>();
        label.text = signature;
        label = container.FindChild("title").FindChild("value").GetComponent<UILabel>();
        label.text = title;
        label = container.FindChild("registTime").FindChild("value").GetComponent<UILabel>();
        string[] ss  = registTime.Split(" "[0]);
        string[] ssy = ss[0].Split("-"[0]);
        label.text = ssy[0] + "年" + ssy[1] + "月" + ssy[2] + "日";
        Transform address_trans = container.FindChild("address");
        label = address_trans.FindChild("value").GetComponent<UILabel>();
        DownOpenLink adress_open_link = address_trans.GetComponent<DownOpenLink>();
        GameObject address_suns = address_trans.FindChild("suns").gameObject;
        address_suns.SetActive(false);
        if (addresses.Count == 0)
        {
            label.text = "保密";
            address_trans.FindChild("down").gameObject.SetActive(false);
            address_trans.FindChild("up").gameObject.SetActive(false);
        }
        else
        {
            label.text = "已设置" + addresses.Count + "个地址";
            address_trans.FindChild("down").gameObject.SetActive(true);
            address_trans.FindChild("up").gameObject.SetActive(false);
            MyUtilTools.clearChild(address_suns.transform);
            float y = 0;
            for (int i = 0; i < addresses.Count; i++)
            {
                GameObject look_address = NGUITools.AddChild(address_suns,address_account_pref);
                look_address.name = "look_address" + i;
                UILabel bank_value = look_address.transform.FindChild("value").GetComponent<UILabel>();
                bank_value.text = addresses[i];
                look_address.transform.localPosition = new Vector3(0,y,0);
                y -= 100;
            }
            adress_open_link.offset = -y - 10;
        }
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
        Transform bank_trans = container.FindChild("bank");
        UILabel account_label = bank_trans.FindChild("value").GetComponent<UILabel>();
        GameObject bank_suns = bank_trans.FindChild("suns").gameObject;
        bank_suns.SetActive(false);
        DownOpenLink bank_open_link = bank_trans.GetComponent<DownOpenLink>();
        if (bacnkAccount.names.Count == 0){
            account_label.text = "未绑定银行卡";
            bank_trans.FindChild("down").gameObject.SetActive(false);
            bank_trans.FindChild("up").gameObject.SetActive(false);
        }
        else
        {
            account_label.text = "已绑定" + bacnkAccount.names.Count + "张";
            bank_trans.FindChild("down").gameObject.SetActive(true);
            bank_trans.FindChild("up").gameObject.SetActive(false);
            MyUtilTools.clearChild(bank_suns.transform);
            float y = 0;
            for (int i = 0; i < bacnkAccount.names.Count ; i++)
            {
                GameObject look_account = NGUITools.AddChild(bank_suns,bank_account_pref);
                look_account.name = "look_account" + i;
                UILabel bank_value = look_account.transform.FindChild("name").FindChild("value").GetComponent<UILabel>();
                bank_value.text = bacnkAccount.names[i];
                bank_value = look_account.transform.FindChild("account").FindChild("value").GetComponent<UILabel>();
                bank_value.text = bacnkAccount.accounts[i];
                bank_value = look_account.transform.FindChild("openAddress").FindChild("value").GetComponent<UILabel>();
                bank_value.text = bacnkAccount.openAddresses[i];
                bank_value = look_account.transform.FindChild("openNames").FindChild("value").GetComponent<UILabel>();
                bank_value.text = bacnkAccount.openNames[i];
                look_account.transform.localPosition = new Vector3(0,y,0);
                y -= 420;
            }
            bank_open_link.offset = -y - 30;
        }
        Transform deal_trans = container.FindChild("deal-info");
        UILabel deal_value = deal_trans.FindChild("deal-value").FindChild("Label").GetComponent<UILabel>();
        deal_value.text = totalDealVale;
        UILabel hp_value = deal_trans.FindChild("hp").FindChild("Label").GetComponent<UILabel>();
        hp_value.text = hp + "";
        UILabel zp_value = deal_trans.FindChild("zp").FindChild("Label").GetComponent<UILabel>();
        zp_value.text = zp + "";
        UILabel cp_value = deal_trans.FindChild("cp").FindChild("Label").GetComponent<UILabel>();
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
        Transform container = transform.FindChild("scroll").FindChild("body").FindChild("container");
        Transform address_trans = container.FindChild("address");
        DownOpenLink adress_open_link = address_trans.GetComponent<DownOpenLink>();
        GameObject up_obj   = address_trans.FindChild("up").gameObject;
        GameObject down_obj = address_trans.FindChild("down").gameObject;
        if (up_obj.activeSelf)
        {
            adress_open_link.open(up_obj,down_obj);
        }
        Transform bank_trans = container.FindChild("bank");
        DownOpenLink bank_open_link = bank_trans.GetComponent<DownOpenLink>();
        up_obj   = bank_trans.FindChild("up").gameObject;
        down_obj = bank_trans.FindChild("down").gameObject;
        if (up_obj.activeSelf)
        {
            bank_open_link.open(up_obj, down_obj);
        }
        if (callback != null)
        {
            callback.Execute();
        }
    }
}
