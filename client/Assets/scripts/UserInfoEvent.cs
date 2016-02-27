using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class UserInfoEvent : CenterEvent {

    GameObject title;

    GameObject pref_bank_account;

    EventDelegate callback = null;

    List<GameObject> curShow = new List<GameObject>();

    List<GameObject> preShow = new List<GameObject>();

	public DealEvent dealEvent = null;

    List<string> save_strs = new List<string>();

	public class UserInfoReceive : MonoBehaviour
	{ 
		public UserInfoEvent uEvent = null;

		void Start () 
		{

		}

		void Update ()
		{
            ByteBuffer buffer = MyUtilTools.tryToLogic("AddAccount");
			if (buffer != null)
			{
				MainData.instance.user.bacnkAccount.deserialize(buffer);
                DialogUtil.tip("添加成功",true,uEvent.callback);
			}
			buffer = MyUtilTools.tryToLogic("SellerCommit");
			if (buffer != null)
			{
				MainData.instance.user.seller.deserialize(buffer);
                DialogUtil.tip("申请成功", true, new EventDelegate(uEvent.backFromRZAndRefresh));
			}
            buffer = MyUtilTools.tryToLogic("UserUpdate");
			if (buffer != null)
			{
                MainData.instance.user.deserialize(buffer);
                //DialogUtil.tip("修改成功", true);
                uEvent.backToCenter();
            }
		}
	}

	// Use this for initialization
	void Start () {
		needshow[0].AddComponent<UserInfoReceive>().uEvent = this;
        title = needshow[0].transform.FindChild("title").gameObject;
	}

	// Update is called once per frame
	void Update () {

	}

	void backFromDealEvent()
	{
		show ();
		dealEvent.needshow [0].SetActive (false);
	}

    void clears()
    {
        curShow.Clear();
        preShow.Clear();
    }

    void changeShow(List<GameObject> curShow)
    {

    }

	void showBankAccount()
	{
		if (MainData.instance.user.bacnkAccount.names.Count == 0) 
		{
			return;
		}
		PopupListEvent listEvent = GameObject.Find ("base").transform.FindChild ("popup-select").GetComponent<PopupListEvent> ();
		listEvent.OnlyPop("银行卡列表",MainData.instance.user.bacnkAccount);
	}

    void refreshInfo()
    {
        save_strs.Clear();
        //填充数据
        Transform container = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container");
        container.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        container.parent.localPosition = new Vector3(0,0,0);
        UISprite icon = container.FindChild("icon").GetComponent<UISprite>();
        icon.spriteName = MainData.instance.user.face;
        save_strs.Add(MainData.instance.user.face);
        UILabel signature = container.FindChild("signature").GetComponent<UILabel>();
        UILabel signature_save = signature.transform.FindChild("save").GetComponent<UILabel>();
        MyUtilTools.insertStr(signature, MainData.instance.user.signature, signature.width / 2);
        signature_save.text = MainData.instance.user.signature;
        save_strs.Add(MainData.instance.user.signature);
        UILabel account = container.FindChild("account").GetComponent<UILabel>();
        account.text = MainData.instance.user.account;
        UILabel nikeName = container.FindChild("nikeName").GetComponent<UILabel>();
        nikeName.text = MainData.instance.user.nikeName;
        UILabel title = container.FindChild("title").FindChild("value").GetComponent<UILabel>();
        title.text = MainData.instance.user.title;
        UILabel registTime = container.FindChild("registTime").FindChild("value").GetComponent<UILabel>();
        string[] ss = MainData.instance.user.registTime.Split(" "[0]);
        string[] ssy = ss[0].Split("-"[0]);
        registTime.text = ssy[0] + "年" + ssy[1] + "月" + ssy[2] + "日";
        UILabel bank_list = container.FindChild("bank").FindChild("value").GetComponent<UILabel>();
        if (MainData.instance.user.bacnkAccount.names.Count > 0)
        {
            bank_list.text = MainData.instance.user.bacnkAccount.accounts[0];
        }
        else
        {
            bank_list.text = "未绑定";
        }
        UIButton button = bank_list.transform.GetComponent<UIButton>();
        button.onClick.Clear();
        button.onClick.Add(new EventDelegate(showBankAccount));

        UILabel address_label = container.FindChild("address").FindChild("value").GetComponent<UILabel>();
        MyUtilTools.insertStr(address_label,MainData.instance.user.address,address_label.width);
        UILabel address_save = container.FindChild("address").FindChild("save").GetComponent<UILabel>();
        address_save.text = MainData.instance.user.address;
        save_strs.Add(MainData.instance.user.address);
        UIInput name = container.FindChild("name").FindChild("inputer").GetComponent<UIInput>();
        name.value = MainData.instance.user.realyName;
        save_strs.Add(MainData.instance.user.realyName);
        if (!MyUtilTools.stringIsNull(MainData.instance.user.realyName))
        {
            name.transform.parent.FindChild("arraw").gameObject.SetActive(false);
        }
        UIInput indent = container.FindChild("indent").FindChild("inputer").GetComponent<UIInput>();
        indent.value = MainData.instance.user.indentity;
        save_strs.Add(MainData.instance.user.indentity);
        if (!MyUtilTools.stringIsNull(MainData.instance.user.indentity))
        {
            indent.transform.parent.FindChild("arraw").gameObject.SetActive(false);
        }
        UIToggle toggle = container.FindChild("push-flag").FindChild("toggle").GetComponent<UIToggle>();
        toggle.value = MainData.instance.user.pushFlag;
        //财富
        Transform credit_recharge = container.FindChild("recharge").FindChild("suns");
        UILabel cur_value = credit_recharge.FindChild("cur-value").FindChild("Label").GetComponent<UILabel>();
        cur_value.text = MainData.instance.user.recharge.curMoney + "邮游币";
        UILabel history_value = credit_recharge.FindChild("history-value").FindChild("Label").GetComponent<UILabel>();
        history_value.text = MainData.instance.user.recharge.historyMoney + "邮游币";
        //认证中心
        refreshRZ(container.FindChild("rzzx").FindChild("suns"));
    }

    public void openInfo()
    {
        if (!MainData.instance.user.login())
        {
            LoginEvent.tryToLogin();
            return;
        }
        click();
        needshow[0].transform.FindChild("selectIcon").gameObject.SetActive(false);
        refreshInfo();
    }

    void refreshRZ(Transform credit_rzzx, Texture texture = null)
	{
		Transform ok_trans = credit_rzzx.FindChild("ok");
		Transform not_trans = credit_rzzx.FindChild("not");
		if (MainData.instance.user.seller.have)
		{
			ok_trans.gameObject.SetActive(true);
			not_trans.gameObject.SetActive(false);
			UILabel id_label = ok_trans.FindChild("id").GetComponent<UILabel>();
			id_label.text = "认证编号: " + MainData.instance.user.seller.key + (MainData.instance.user.seller.pass ? " (已通过认证)" : " (等待评审)");
			UILabel time_label = ok_trans.FindChild("time").GetComponent<UILabel>();
			time_label.text = "认证时间: " + MainData.instance.user.seller.time;
			UILabel type_label = ok_trans.FindChild("type").GetComponent<UILabel>();
			type_label.text = "认证类型: " + (MainData.instance.user.seller.type == 0 ? "个人" : "公司");
			UITexture show_textture = ok_trans.FindChild("pic").FindChild("rect").FindChild("Texture").GetComponent<UITexture>();
			if (show_textture.mainTexture == null)
			{
                JustRun.Instance.loadPic(MainData.instance.user.seller.picName,show_textture,texture);
			}
		}
		else
		{
			ok_trans.gameObject.SetActive(false);
			not_trans.gameObject.SetActive(true);
		}
	}

	void backFromSelectIcon()
	{
		show(curShow,false);
		show(preShow,true);
		curShow[0].GetComponent<SelectIconEvent>().clear();
		clears();
		//CameraUtil.pop(7);
		title.GetComponentInChildren<UILabel>().text = "个人中心";
		title.GetComponent<JustChangeLayer>().change(5);
		//title.transform.FindChild ("ok").gameObject.SetActive(true);
		callback = null;
	}

    public void openSelectIcon(GameObject obj1, GameObject obj2)
    {
        clears();
        obj1.SetActive(false);
        preShow.Add(obj1);
        obj2.SetActive(true);
        obj2.GetComponent<SelectIconEvent>().init();
        curShow.Add(obj2);
        //CameraUtil.push(7,2);
		//obj2.GetComponent<SelectIconEvent>().change(1,10,obj2);
        UILabel label  = title.GetComponentInChildren<UILabel>();
        label.text = "修改头像";
        //JustChangeLayer layer = title.GetComponent<JustChangeLayer>();
        //layer.change(1,10,title);
		//title.transform.FindChild ("ok").gameObject.SetActive(false);
        callback = new EventDelegate(backFromSelectIcon);
    }

	public void openAddAccount(GameObject obj1,GameObject obj2)
	{
		obj1.SetActive(false);
		obj2.SetActive(true);
        UILabel label = title.GetComponentInChildren<UILabel>();
        label.text = "添加银行卡";
	}

    void refreshAccountList()
    {
        Transform container = needshow[0].transform.FindChild("account-body").FindChild("list").FindChild("body").FindChild("container");
        container.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        container.parent.localPosition = new Vector3(0,0,0);
        float startY = 430, len = 150;
        if (pref_bank_account == null)
        {
            pref_bank_account = Resources.Load<GameObject>("prefabs/Bank-Account");
        }
        MyUtilTools.clearChild(container,"addMore");
        for (int i = 0; i < MainData.instance.user.bacnkAccount.names.Count ; i++ )
        {
            GameObject bank = NGUITools.AddChild(container.gameObject,pref_bank_account);
            bank.transform.localPosition = new Vector3(0, startY, 0);
            bank.name = "bank" + i;
            UILabel label = bank.transform.FindChild("one").GetComponent<UILabel>();
            label.text = MainData.instance.user.bacnkAccount.names[i] + ":" + MainData.instance.user.bacnkAccount.accounts[i];
            label = bank.transform.FindChild("two").GetComponent<UILabel>();
            label.text = MainData.instance.user.bacnkAccount.openAddresses[i] + ":" + MainData.instance.user.bacnkAccount.openNames[i];
            startY -= len;
        }
        container.FindChild("addMore").transform.localPosition = new Vector3(0,startY == 430?0:startY,0);
    }

    void backFromShowAccounts()
    {
        Transform list_trans = needshow[0].transform.FindChild("account-body").FindChild("list");
        if (list_trans.gameObject.activeSelf)
        {
            show(curShow,false);
            show(preShow,true);
            clears();
            title.GetComponentInChildren<UILabel>().text = "个人中心";
            callback = null;
            UILabel bank_list = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container").FindChild("bank").FindChild("value").GetComponent<UILabel>();
            if (MainData.instance.user.bacnkAccount.names.Count > 0)
            {
                bank_list.text = MainData.instance.user.bacnkAccount.accounts[0];
            }
            else
            {
                bank_list.text = "未绑定银行卡";
            }
        }
        else
        {
            needshow[0].transform.FindChild("account-body").FindChild("add").gameObject.SetActive(false);
            list_trans.gameObject.SetActive(true);
            UILabel label = title.GetComponentInChildren<UILabel>();
            label.text = "我的银行卡";
            refreshAccountList();
        }
    }

    public void openShowAccounts(GameObject obj1,GameObject obj2)
    {
        clears();
        obj1.SetActive(false);
        preShow.Add(obj1);
        obj2.SetActive(true);
        curShow.Add(obj2);
        UILabel label = title.GetComponentInChildren<UILabel>();
        label.text = "我的银行卡";
        callback = new EventDelegate(backFromShowAccounts);
        refreshAccountList();
    }

	public void doAddAccount()
	{
        Transform container = needshow[0].transform.FindChild("account-body").FindChild("add");
        Transform name_tran = container.FindChild("name");
		UIPopupList list = name_tran.GetComponent<UIPopupList>();
		string name = null;
		if (list.value.Equals("其他银行"))
		{
			UIInput input = name_tran.FindChild("inputer").GetComponent<UIInput>();
			name = input.value;
			if (MyUtilTools.stringIsNull(name))
			{
				DialogUtil.tip(input.transform.FindChild("tips").GetComponent<UILabel>().text);
				return;
			}
		}
		else
		{
			name = list.value;
		}
		UIInput account_input = container.FindChild("account").GetComponent<UIInput>();
		string account = account_input.value;
		if (MyUtilTools.stringIsNull(account))
		{
			DialogUtil.tip(account_input.transform.FindChild("tips").GetComponent<UILabel>().text);
			return;
		}
        account_input = container.FindChild("openAddress").GetComponent<UIInput>();
        string openName = account_input.value;
        if (MyUtilTools.stringIsNull(openName))
        {
            DialogUtil.tip(account_input.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
        account_input = container.FindChild("openNames").GetComponent<UIInput>();
        string openPeopleName = account_input.value;
        if (MyUtilTools.stringIsNull(openPeopleName))
        {
            DialogUtil.tip(account_input.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
		ByteBuffer buffer = ByteBuffer.Allocate(1024);
		buffer.skip(4);
		buffer.WriteString("AddAccount");
		buffer.WriteLong(MainData.instance.user.id);//编号
		buffer.WriteString(name);
		buffer.WriteString(account);
        buffer.WriteString(openName);
        buffer.WriteString(openPeopleName);
		NetUtil.getInstance.SendMessage(buffer);
	}


    void backFromRZAndRefresh()
    {
        backFromRZ();
        Transform trans = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container").FindChild("rzzx").FindChild("suns");
        UITexture texture = needshow[0].transform.FindChild("rz-body").FindChild("pic").FindChild("rect").FindChild("Texture").GetComponent<UITexture>();
        refreshRZ(trans,texture.mainTexture);
    }

	void backFromRZ()
	{
		show(curShow,false);
		show(preShow,true);
		clears();
		//CameraUtil.pop(7);
		title.GetComponentInChildren<UILabel>().text = "个人中心";
		title.GetComponent<JustChangeLayer>().change(5);
		//title.transform.FindChild ("ok").gameObject.SetActive(true);
		callback = null;
	}

	public void openRZ(GameObject obj1, GameObject obj2)
	{
		clears();
		obj1.SetActive(false);
		preShow.Add(obj1);
		obj2.SetActive(true);
		curShow.Add(obj2);
		//obj2.GetComponent<JustChangeLayer>().change(1,10,obj2);
		//CameraUtil.push(7,2);
		UILabel label  = title.GetComponentInChildren<UILabel>();
		label.text = "用户认证";
		//JustChangeLayer layer = title.GetComponent<JustChangeLayer>();
		//layer.change(1,10,title);
		//title.transform.FindChild ("ok").gameObject.SetActive(false);
        callback = new EventDelegate(backFromRZ);
	}

    void backFromSignature()
    {
        show(curShow,false);
        show(preShow,true);
        clears();
        title.GetComponentInChildren<UILabel>().text = "个人中心";
        callback = null;
        UIInput input = needshow[0].transform.FindChild("signature-body").FindChild("inputer").GetComponent<UIInput>();
        if (!MyUtilTools.stringIsNull(input.value))
        {
            UILabel signature = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container").FindChild("signature").GetComponent<UILabel>();
            UILabel signature_save = signature.transform.FindChild("save").GetComponent<UILabel>();
            MyUtilTools.insertStr(signature,input.value,300);
            signature_save.text = input.value;
        }
    }

    public void openSignature(GameObject obj1 , GameObject obj2)
    {
        clears();
        obj1.SetActive(false);
        preShow.Add(obj1);
        obj2.SetActive(true);
        curShow.Add(obj2);
        obj2.transform.FindChild("inputer").GetComponent<UIInput>().value = save_strs[1];
        UILabel label = title.GetComponentInChildren<UILabel>();
        label.text = "修改签名";
        callback = new EventDelegate(backFromSignature);
    }

    void backFromAddress()
    {
        show(curShow,false);
        show(preShow,true);
        clears();
        title.GetComponentInChildren<UILabel>().text = "个人中心";
        callback = null;
        UIInput input = needshow[0].transform.FindChild("address-body").FindChild("inputer").GetComponent<UIInput>();
        if (!MyUtilTools.stringIsNull(input.value))
        {
            Transform address_tran = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container").FindChild("address");
            UILabel address        = address_tran.FindChild("value").GetComponent<UILabel>();
            UILabel address_save   = address_tran.FindChild("save").GetComponent<UILabel>();
            MyUtilTools.insertStr(address,input.value,300);
            address_save.text = input.value;
        }
    }

    public void openAddress(GameObject obj1, GameObject obj2)
    {
        clears();
        obj1.SetActive(false);
        preShow.Add(obj1);
        obj2.SetActive(true);
        curShow.Add(obj2);
        obj2.transform.FindChild("inputer").GetComponent<UIInput>().value = save_strs[2];
        UILabel label = title.GetComponentInChildren<UILabel>();
        label.text = "修改收货地址";
        callback = new EventDelegate(backFromAddress);
    }

    void sendRZ(SendMessageEntity entity)
    {
        NetUtil.getInstance.SendMessage(entity.buffer);
    }

    void uploadPicOk(SendMessageEntity entity)
    {
        LoadUtil.show(false);
        EventDelegate next = new EventDelegate(this,"sendRZ");
        next.parameters[0] = new EventDelegate.Parameter();
        next.parameters[0].obj = entity;
        DialogUtil.tip("图片上传成功点击确定继续认证",true,next);
    }

    void uploadPicFail()
    {
        LoadUtil.show(false);
        DialogUtil.tip("图片上传失败");
    }

	public void doRZ(GameObject container)
	{
		UIPopupList type_list = container.transform.FindChild ("type").GetComponent<UIPopupList>();
		UIInput key_input = container.transform.FindChild("key").GetComponent<UIInput>();
		if (MyUtilTools.stringIsNull(key_input.value))
		{
			UILabel label  = key_input.transform.FindChild("tips").GetComponent<UILabel>();
			DialogUtil.tip(label.text);
			return;
		}
        UITexture texture = container.transform.FindChild("front").FindChild("context").GetComponent<UITexture>();
		if (texture.mainTexture == null)
		{
			DialogUtil.tip("请拍摄证件正面");
			return;
		}
        SendMessageEntity entity = new SendMessageEntity();
        Texture2D t2d = (Texture2D)texture.mainTexture;
        EventDelegate ok = new EventDelegate(this, "uploadPicOk");
        ok.parameters[0] = new EventDelegate.Parameter();
        ok.parameters[0].obj = entity;
        string picName = MainData.instance.user.account + "-sellercommit.jpg";
        entity.buffer.skip(4);
        entity.buffer.WriteString("SellerCommit");
        entity.buffer.WriteLong(MainData.instance.user.id);
        entity.buffer.WriteByte((byte)(type_list.value.Equals("个人") ? 0 : 1));
        entity.buffer.WriteString(key_input.value);
        entity.buffer.WriteString(picName);
        LoadUtil.show(true,"上传图片中请稍后");
        JustRun.Instance.upLoadPic(picName, t2d.EncodeToJPG(),ok,new EventDelegate(uploadPicFail));
	}
	
	public void back()
    {
        if (callback != null)
        {
            callback.Execute();
            return;
        }
        bool needSave = false;
        Transform container = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container");
        UISprite icon = container.FindChild("icon").GetComponent<UISprite>();
        needSave = save_strs[0].Equals(icon.spriteName);
        UILabel signature_save = container.FindChild("signature").FindChild("save").GetComponent<UILabel>();
        needSave = needSave ? needSave : save_strs[1].Equals(signature_save.text);
        UILabel address_save = container.FindChild("address").FindChild("save").GetComponent<UILabel>();
        needSave = needSave ? needSave : save_strs[2].Equals(address_save.text);
        UIInput name = container.FindChild("name").FindChild("inputer").GetComponent<UIInput>();
        needSave = needSave ? needSave : save_strs[3].Equals(name.value);
        UIInput indent = container.FindChild("indent").FindChild("inputer").GetComponent<UIInput>();
        needSave = needSave ? needSave : save_strs[4].Equals(indent.value);
        UIToggle toggle = container.FindChild("push-flag").FindChild("toggle").GetComponent<UIToggle>();
        needSave = needSave ? needSave : toggle.value != MainData.instance.user.pushFlag;
        if (needSave)
        {
            change();
        }
        else
        {
            backToCenter();
        }
    }

	void tryToSearch(int type)
	{
		ByteBuffer buffer = ByteBuffer.Allocate(1024);
		buffer.skip(4);
		buffer.WriteString("DealSearch");
		buffer.WriteInt (type);
		buffer.WriteLong(MainData.instance.user.id);//编号
		NetUtil.getInstance.SendMessage(buffer);
	}

	void sells()
	{
		tryToSearch (0);
	}

	void buys()
	{
		tryToSearch (1);
	}

	void orders()
	{
		tryToSearch (2);
	}

	public void change()
	{
        Transform container = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container");
        UISprite icon = container.FindChild("icon").GetComponent<UISprite>();
        UILabel signature_save = container.FindChild("signature").FindChild("save").GetComponent<UILabel>();
        UILabel address_save = container.FindChild("address").FindChild("save").GetComponent<UILabel>();
        UIInput name = container.FindChild("name").FindChild("inputer").GetComponent<UIInput>();
        UIInput indent = container.FindChild("indent").FindChild("inputer").GetComponent<UIInput>();
        UIToggle toggle = container.FindChild("push-flag").FindChild("toggle").GetComponent<UIToggle>();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("UserUpdate");
        buffer.WriteLong(MainData.instance.user.id);//编号
        buffer.WriteString(icon.spriteName);
        buffer.WriteString(signature_save.text);
        buffer.WriteString(address_save.text);
        buffer.WriteByte((byte)(toggle.value ? 1 : 0));
        if (!MyUtilTools.stringIsNull(name.value))
        {
            buffer.WriteString(name.value);
        }
        else
        {
            buffer.WriteString("");
        }
        if (!MyUtilTools.stringIsNull(indent.value))
        {
            buffer.WriteString(indent.value);
        }
        else
        {
            buffer.WriteString("");
        }
        NetUtil.getInstance.SendMessage(buffer);
	}

    public void openInput(UIInput input)
    {
        UICamera.selectedObject = input.gameObject;
        UIInput.selection  = input;
        input.selectionEnd = input.value.Length - 1;
    }
}
