using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class RegistEvent : MonoBehaviour {

    Dictionary<string,UIInput> inputs = new Dictionary<string,UIInput>();

    UILabel tokenLabe;

    long startTime = 0;

    UIButton tokenButton;

    bool isOpen = false;

	// Use this for initialization
	void Start () {
        string[] keyNames = new string[] {"account", "token", "pwd","rpwd","nikeName","weixin","name","address"};
        Transform trans1 = transform.FindChild("container");
        Transform trans2 = trans1.FindChild("more").FindChild("suns");
        for (int i = 0 ; i < keyNames.Length ; i++ )
        {
            init(i < 5 ? trans1 : trans2, keyNames[i]);
        }
        Transform sendTranform = trans1.FindChild("token").FindChild("send");
        tokenLabe   = sendTranform.GetComponent<UILabel>();
        tokenButton = sendTranform.GetComponent<UIButton>();
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("Regist");
		if (buffer != null) {
            //注册成功
            DialogUtil.tip(buffer.ReadString());
            clearInputs();
		}
        buffer = MyUtilTools.tryToLogic("Token");
        if (buffer != null)
        {
            startTime = System.DateTime.Now.Ticks / 10000000;
            tokenButton.SetState(UIButtonColor.State.Disabled, true);
        }
        if (startTime > 0)
        {
            long now = System.DateTime.Now.Ticks / 10000000;
            int fix = (int)(now - startTime);
            if (fix >= 60)
            {
                init();
            }
            else
            {
                tokenLabe.text = "重发(" + MyUtilTools.numToString(60 - fix) + ")秒";
            }
        }
	}

    void init()
    {
        startTime = 0;
        tokenLabe.text = "发送短信";
        float r = (float)(43 / 255);
        float g = (float)(130 / 255);
        tokenLabe.color = new Color(r, g, 1f, 1);
        tokenButton.SetState(UIButtonColor.State.Normal, true);
    }

    private void init(Transform trans,string name)
    {
        UIInput input = trans.FindChild(name).GetComponent<UIInput>();
        inputs.Add(name,input);
	}

	public void regist(){
		UIInput account = inputs["account"];
		if (MyUtilTools.stringIsNull(account.value)){
            UILabel label  = account.transform.FindChild("tips").GetComponent<UILabel>();
			DialogUtil.tip(label.text);
			return;
		}
        UIInput token = inputs["token"];
		if (MyUtilTools.stringIsNull(token.value)){
            UILabel label  = token.transform.FindChild("tips").GetComponent<UILabel>();
			DialogUtil.tip(label.text);
			return;
		}
        UIInput pwd = inputs["pwd"];
		if (MyUtilTools.stringIsNull(pwd.value)){
            UILabel label  = pwd.transform.FindChild("tips").GetComponent<UILabel>();
			DialogUtil.tip(label.text);
			return;
		}
        if (pwd.value.Length < 6 || pwd.value.Length > 32){
			DialogUtil.tip("密码长度6~32");
			return;
		}
        UIInput rpwd = inputs["rpwd"];
		if (MyUtilTools.stringIsNull(rpwd.value)){
            UILabel label  = rpwd.transform.FindChild("tips").GetComponent<UILabel>();
			DialogUtil.tip(label.text);
			return;
		}
		if (rpwd.value.Length < 6 || rpwd.value.Length > 32){
			DialogUtil.tip("密码长度6~32");
			return;
		}
		if (!pwd.value.Equals(rpwd.value)){
			DialogUtil.tip("两次输入不匹配");
			return;
		}
        UIInput nikeName = inputs["nikeName"];
        if (MyUtilTools.stringIsNull(nikeName.value))
        {
            UILabel label = nikeName.transform.FindChild("tips").GetComponent<UILabel>();
            DialogUtil.tip(label.text);
            return;
        }
        if (nikeName.value.Length > 12){
			DialogUtil.tip("昵称过长");
			return;
		}
        UIInput weixin = inputs["weixin"];
        UIInput name = inputs["name"];
        UIInput address = inputs["address"];
        if (isOpen)
        {
            if (MyUtilTools.stringIsNull(weixin.value))
            {
                UILabel label = weixin.transform.FindChild("tips").GetComponent<UILabel>();
                DialogUtil.tip(label.text);
                return;
            }
            if (MyUtilTools.stringIsNull(name.value))
            {
                UILabel label = name.transform.FindChild("tips").GetComponent<UILabel>();
                DialogUtil.tip(label.text);
                return;
            }
            if (MyUtilTools.stringIsNull(address.value))
            {
                UILabel label = address.transform.FindChild("tips").GetComponent<UILabel>();
                DialogUtil.tip(label.text);
                return;
            }
        }
		ByteBuffer buffer = ByteBuffer.Allocate(1024);
		buffer.skip(4);
		buffer.WriteString("Regist");
		buffer.WriteString (account.value);
        buffer.WriteString(token.value);
        buffer.WriteString(pwd.value);
        buffer.WriteString(nikeName.value);
        buffer.WriteString(weixin.value);
        buffer.WriteString(name.value);
        buffer.WriteString(address.value);
		NetUtil.getInstance.SendMessage (buffer);
	}

	private void clearInputs(){
		foreach (UIInput label in inputs.Values){
			label.value = "";
		}
	}

    public void send()
    {
        UIInput input = inputs["account"];
        if (MyUtilTools.stringIsNull(input.value))
        {
            DialogUtil.tip("请输入手机号码");
            return;
        }
        if (startTime == 0)
        {
            ByteBuffer buffer = ByteBuffer.Allocate(1024);
            buffer.skip(4);
            buffer.WriteString("Token");
            buffer.WriteString(input.value);
            buffer.WriteString("1");
            NetUtil.getInstance.SendMessage(buffer);
        }
    }

    public void goToNext(GameObject to,GameObject from)
    {
        to.SetActive(true);
        from.SetActive(false);
        transform.FindChild("container").FindChild("more").FindChild("suns").gameObject.SetActive(false);
    }

    public void open(GameObject next , GameObject suns,UILabel showTip)
    {
        isOpen = !isOpen;
        GameObject obj = next;
        while (obj != null)
        {
            float x = obj.transform.localPosition.x;
            float y = obj.transform.localPosition.y + (isOpen ? -1 : 1) * 300;
            obj.transform.localPosition = new Vector3(x, y, 0);
            DownOpenLink link = obj.GetComponent<DownOpenLink>();
            obj = link == null ? null : link.next;
        }
        suns.SetActive(isOpen);
        showTip.enabled = !isOpen;
    }
}
 
 
