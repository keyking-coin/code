﻿using UnityEngine;
using System.Collections;

public class DealLookEvent : MonoBehaviour
{

    public EventDelegate callback = null;

    MainData.BankAccount bacnkAccount = new MainData.BankAccount();

    string cur_tel_str;

    static GameObject bank_account_pref = null;

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
        if (bank_account_pref == null)
        {
            bank_account_pref = Resources.Load<GameObject>("prefabs/look-bank-account");
        }
        gameObject.SetActive(true);
        string icon_str = buffer.ReadString();
        string nikeName = buffer.ReadString();
        string title = buffer.ReadString();
        string registTime = buffer.ReadString();
        string address = buffer.ReadString();
        string name = buffer.ReadString();
        cur_tel_str = buffer.ReadString();
        bacnkAccount.deserialize(buffer);
        buffer.ReadString();
        buffer.ReadString();
        buffer.ReadString();
        string totalDealVale = buffer.ReadString();
        int hp = buffer.ReadInt();
        int zp = buffer.ReadInt();
        int cp = buffer.ReadInt();

        Transform container = transform.FindChild("scroll").FindChild("body").FindChild("container");
        UISprite icon = container.FindChild("icon").GetComponent<UISprite>();
        icon.spriteName = icon_str;
        UILabel label = container.FindChild("nikeName").GetComponent<UILabel>();
        label.text = nikeName;
        label = container.FindChild("title").FindChild("value").GetComponent<UILabel>();
        label.text = title;
        label = container.FindChild("registTime").FindChild("value").GetComponent<UILabel>();
        string[] ss  = registTime.Split(" "[0]);
        string[] ssy = ss[0].Split("-"[0]);
        label.text = ssy[0] + "年" + ssy[1] + "月" + ssy[2] + "日";
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
        Transform bank_trans = container.FindChild("bank");
        UILabel account_label = bank_trans.FindChild("value").GetComponent<UILabel>();
        GameObject suns = bank_trans.FindChild("suns").gameObject;
        suns.SetActive(false);
        Transform other_trans = container.FindChild("other");
        if (bacnkAccount.names.Count == 0){
            account_label.text = "未绑定银行卡";
            bank_trans.FindChild("down").gameObject.SetActive(false);
            bank_trans.FindChild("up").gameObject.SetActive(false);
            other_trans.localPosition = new Vector3(0, 0, 0);
        }
        else
        {
            account_label.text = "已绑定" + bacnkAccount.names.Count + "张";
            bank_trans.FindChild("down").gameObject.SetActive(true);
            bank_trans.FindChild("up").gameObject.SetActive(false);
            other_trans.localPosition = new Vector3(0, 0, 0);
            DownOpenLink openLink = bank_trans.GetComponent<DownOpenLink>();
            MyUtilTools.clearChild(suns.transform);
            float y = 0;
            for (int i = 0; i < bacnkAccount.names.Count ; i++)
            {
                GameObject look_account = NGUITools.AddChild(suns,bank_account_pref);
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
            openLink.offset = -y - 30;
        }
        UILabel deal_value = other_trans.FindChild("deal-value").FindChild("Label").GetComponent<UILabel>();
        deal_value.text = totalDealVale;
        UILabel hp_value = other_trans.FindChild("hp").FindChild("Label").GetComponent<UILabel>();
        hp_value.text = hp + "";
        UILabel zp_value = other_trans.FindChild("zp").FindChild("Label").GetComponent<UILabel>();
        zp_value.text = zp + "";
        UILabel cp_value = other_trans.FindChild("cp").FindChild("Label").GetComponent<UILabel>();
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
