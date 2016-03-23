using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class UserInfoEvent : CenterEvent {

    GameObject title;

    GameObject pref_bank_account;

    GameObject pref_address_list;

    EventDelegate callback = null;

    List<GameObject> curShow = new List<GameObject>();

    List<GameObject> preShow = new List<GameObject>();

	public DealEvent dealEvent = null;

    string[] BANK_NAMES_DATAS = new string[] {
        "招商银行:zhaoshang",         "福建信业银行:fjxingye",    "广东发展银行:gzfazhan",
        "广州商业银行:gzshangye",     "华夏银行:huaxia",          "交通银行:jiaotong",
        "农村信用社:ncxinyongshe",    "上海浦东发展银行:shpufa",  "上海银行:shyinhang",
        "深圳市商业银行:szshangye",   "中国工商银行:zggongshang", "中国光大银行:zgguangda",
        "中国建设银行:zgjianshe",     "中国民生银行:zgmingsheng", "中国农业银行:zgnongye",
        "中国邮政储蓄银行:zgyouzhen", "中国银行:zhongguo",        "中信实业银行:zxshiye"
    };

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
                uEvent.backToCenter();
            }
            buffer = MyUtilTools.tryToLogic("AddressChange");
            if (buffer != null)
            {
                byte type = buffer.ReadByte();
                MainData.instance.user.deserialize(buffer);
                DialogUtil.tip(type == 1 ? "删除成功" : "添加成功",true,uEvent.callback);
            }
		}
	}

    public class UserAccountDel : MonoBehaviour
    {
        int index;

        UserInfoEvent infoEvent = null;

        int count = 0;

        bool isPressed = false;

        GameObject delete_obj;

        int popCount = 0;

        public void init(int index,UserInfoEvent infoEvent){
            this.index = index;
            this.infoEvent = infoEvent;
        }

        void Start(){
            delete_obj = transform.FindChild("delete").gameObject;
            UIButton button = delete_obj.GetComponent<UIButton>();
            button.onClick.Clear();
            button.onClick.Add(new EventDelegate(doDelete));
        }

        void Update()
        {
            ByteBuffer buffer = MyUtilTools.tryToLogic("BankAccountDel");
            if (buffer != null)
            {
                DialogUtil.tip("删除成功", true, new EventDelegate(infoEvent.refreshAccountList));
            }
            if (isPressed)
            {
                count++;
                if (count > 50)
                {
                    openPop();
                    count = 0;
                }
            }
            else
            {
                if (delete_obj.activeSelf)
                {
                    popCount--;
                    if (popCount <= 0)
                    {
                        delete_obj.SetActive(false);
                    }
                }
            }
        }

        void comfirmDelete()
        {
            ConfirmUtil.TryToDispear();
            ByteBuffer buffer = ByteBuffer.Allocate(1024);
            buffer.skip(4);
            buffer.WriteString("BankAccountDel");
            buffer.WriteLong(MainData.instance.user.id);
            buffer.WriteString(MainData.instance.user.bacnkAccount.accounts[index]);
            NetUtil.getInstance.SendMessage(buffer);
        }

        void doDelete()
        {
            delete_obj.SetActive(false);
            ConfirmUtil.confirm("确定删除此条银行卡记录?",comfirmDelete);
        }

        void openPop()
        {
            if (delete_obj.activeSelf)
            {
                return;
            }
            delete_obj.SetActive(true);
            popCount = 500;
        }

        void OnPress(bool pressed)
        {
            isPressed = pressed;
        }
    }

    public class UserAddressDel : MonoBehaviour
    {
        int index;

        UserInfoEvent infoEvent = null;

        int count = 0;

        bool isPressed = false;

        GameObject delete_obj;

        int popCount = 0;

        public void init(int index, UserInfoEvent infoEvent)
        {
            this.index = index;
            this.infoEvent = infoEvent;
        }

        void Start()
        {
            delete_obj = transform.FindChild("delete").gameObject;
            UIButton button = delete_obj.GetComponent<UIButton>();
            button.onClick.Clear();
            button.onClick.Add(new EventDelegate(doDelete));
        }

        void Update()
        {
            ByteBuffer buffer = MyUtilTools.tryToLogic("AddressChange");
            if (buffer != null)
            {
                byte type = buffer.ReadByte();
                MainData.instance.user.deserialize(buffer);
                DialogUtil.tip(type == 1 ? "删除成功" : "添加成功",true,new EventDelegate(infoEvent.refreshAddressList));
            }
            if (isPressed)
            {
                count++;
                if (count > 50)
                {
                    openPop();
                    count = 0;
                }
            }
            else
            {
                if (delete_obj.activeSelf)
                {
                    popCount--;
                    if (popCount <= 0)
                    {
                        delete_obj.SetActive(false);
                    }
                }
            }
        }

        void comfirmDelete()
        {
            ConfirmUtil.TryToDispear();
            ByteBuffer buffer = ByteBuffer.Allocate(1024);
            buffer.skip(4);
            buffer.WriteString("AddressChange");
            buffer.WriteByte(1);
            buffer.WriteLong(MainData.instance.user.id);
            buffer.WriteString(MainData.instance.user.addresses[index]);
            NetUtil.getInstance.SendMessage(buffer);
        }

        void doDelete()
        {
            delete_obj.SetActive(false);
            ConfirmUtil.confirm("确定删除此条收货地址?", comfirmDelete);
        }

        void openPop()
        {
            if (delete_obj.activeSelf)
            {
                return;
            }
            delete_obj.SetActive(true);
            popCount = 500;
        }

        void OnPress(bool pressed)
        {
            isPressed = pressed;
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

    public void refreshInfo()
    {
        //填充数据
        Transform container = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container");
        container.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        container.parent.localPosition = new Vector3(0,0,0);
        UISprite icon = container.FindChild("icon").GetComponent<UISprite>();
        icon.spriteName = MainData.instance.user.face;
        UILabel signature = container.FindChild("signature").GetComponent<UILabel>();
        UILabel signature_save = signature.transform.FindChild("save").GetComponent<UILabel>();
        MyUtilTools.insertStr(signature, MainData.instance.user.signature, signature.width / 2);
        signature_save.text = MainData.instance.user.signature;
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
        if (MainData.instance.user.addresses.Count > 0)
        {
            MyUtilTools.insertStr(address_label,MainData.instance.user.addresses[0],300);
        }
        else
        {
            address_label.text = "未设置收货地址";
        }
        UIInput name = container.FindChild("name").FindChild("inputer").GetComponent<UIInput>();
        name.value = MainData.instance.user.realyName;
        if (!MyUtilTools.stringIsNull(MainData.instance.user.realyName))
        {
            name.transform.parent.FindChild("arraw").gameObject.SetActive(false);
        }
        UIInput indent = container.FindChild("indent").FindChild("inputer").GetComponent<UIInput>();
        indent.value = MainData.instance.user.indentity;
        if (!MyUtilTools.stringIsNull(MainData.instance.user.indentity))
        {
            indent.transform.parent.FindChild("arraw").gameObject.SetActive(false);
        }
        UIToggle toggle = container.FindChild("push-flag").FindChild("toggle").GetComponent<UIToggle>();
        toggle.value = MainData.instance.user.pushFlag;
        //信用
        refreshCredit(container.FindChild("credit").FindChild("suns"));
        //财富
        Transform credit_recharge = container.FindChild("recharge").FindChild("suns");
        UILabel cur_value = credit_recharge.FindChild("cur-value").FindChild("Label").GetComponent<UILabel>();
        cur_value.text = MainData.instance.user.recharge.curMoney + "邮游币";
        UILabel history_value = credit_recharge.FindChild("history-value").FindChild("Label").GetComponent<UILabel>();
        history_value.text = MainData.instance.user.recharge.historyMoney + "邮游币";
        //认证中心
        refreshRZ(container.FindChild("rzzx").FindChild("suns"));
    }

    void refreshCredit(Transform container){
        UILabel cur_value = container.FindChild("cur-value").FindChild("Label").GetComponent<UILabel>();
        cur_value.text = MainData.instance.user.credit.curValue + "";
        UILabel max_value = container.FindChild("max-value").FindChild("Label").GetComponent<UILabel>();
        max_value.text = MainData.instance.user.credit.maxValue + "";
        UILabel temp_value = container.FindChild("temp-value").FindChild("Label").GetComponent<UILabel>();
        temp_value.text = MainData.instance.user.credit.tempMaxValue + "";
        UILabel deal_value = container.FindChild("deal-value").FindChild("Label").GetComponent<UILabel>();
        deal_value.text = MainData.instance.user.credit.totalDealValue + "";
        UILabel hp_value = container.FindChild("hp").FindChild("Label").GetComponent<UILabel>();
        hp_value.text = MainData.instance.user.credit.hp + "";
        UILabel zp_value = container.FindChild("zp").FindChild("Label").GetComponent<UILabel>();
        zp_value.text = MainData.instance.user.credit.zp + "";
        UILabel cp_value = container.FindChild("cp").FindChild("Label").GetComponent<UILabel>();
        cp_value.text = MainData.instance.user.credit.cp + "";
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
        float startY = 430, len = 160;
        if (pref_bank_account == null)
        {
            pref_bank_account = Resources.Load<GameObject>("prefabs/Bank-Account");
        }
        MyUtilTools.clearChild(container, new string[]{"addMore"});
        for (int i = 0; i < MainData.instance.user.bacnkAccount.names.Count ; i++ )
        {
            GameObject bank = NGUITools.AddChild(container.gameObject,pref_bank_account);
            bank.AddComponent<UserAccountDel>().init(i,this);
            bank.transform.localPosition = new Vector3(0,startY,0);
            bank.name = "bank" + i;
            Transform icon_trans = bank.transform.FindChild("icon");
            UISprite icon = icon_trans.GetComponent<UISprite>();
            string iconName = null;
            GameObject iconName_obj = icon_trans.FindChild("name").gameObject;
            for (int j = 0; i < BANK_NAMES_DATAS.Length; j++ )
            {
                if (BANK_NAMES_DATAS[j].Contains(MainData.instance.user.bacnkAccount.names[i]))
                {
                    iconName = BANK_NAMES_DATAS[j].Split(":"[0])[1];
                    break;
                }
            }
            if (iconName != null)
            {
                icon.spriteName = iconName;
                iconName_obj.SetActive(false);
            }
            else
            {
                icon.spriteName = null;
                iconName_obj.SetActive(true);
                iconName_obj.GetComponent<UILabel>().text = MainData.instance.user.bacnkAccount.names[i];
            }
            UILabel label = icon_trans.FindChild("value").GetComponent<UILabel>();
            label.text = MainData.instance.user.bacnkAccount.accounts[i];
            label = bank.transform.FindChild("openAddress").FindChild("value").GetComponent<UILabel>();
            label.text = MainData.instance.user.bacnkAccount.openAddresses[i];
            label = bank.transform.FindChild("openPeople").FindChild("value").GetComponent<UILabel>();
            label.text = MainData.instance.user.bacnkAccount.openNames[i];
            startY -= len;
        }
        container.FindChild("addMore").transform.localPosition = new Vector3(0,startY == 430 ? 0 : startY,0);
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
		title.GetComponentInChildren<UILabel>().text = "个人中心";
		callback = null;
	}

	public void openRZ(GameObject obj1, GameObject obj2)
	{
		clears();
		obj1.SetActive(false);
		preShow.Add(obj1);
		obj2.SetActive(true);
		curShow.Add(obj2);
		UILabel label  = title.GetComponentInChildren<UILabel>();
		label.text = "用户认证";
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
        UILabel signature_save = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container").FindChild("signature").FindChild("save").GetComponent<UILabel>();
        obj2.transform.FindChild("inputer").GetComponent<UIInput>().value = signature_save.text;
        UILabel label = title.GetComponentInChildren<UILabel>();
        label.text = "修改签名";
        callback = new EventDelegate(backFromSignature);
    }

    void refreshAddressList()
    {
        Transform container = needshow[0].transform.FindChild("address-body").FindChild("list").FindChild("body").FindChild("container");
        container.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        container.parent.localPosition = new Vector3(0, 0, 0);
        float startY = 430, len = 110;
        if (pref_address_list == null)
        {
            pref_address_list = Resources.Load<GameObject>("prefabs/address-list");
        }
        MyUtilTools.clearChild(container,new string[]{"addMore"});
        for (int i = 0; i < MainData.instance.user.addresses.Count; i++)
        {
            GameObject address = NGUITools.AddChild(container.gameObject,pref_address_list);
            address.AddComponent<UserAddressDel>().init(i,this);
            address.transform.localPosition = new Vector3(0, startY, 0);
            address.name = "address_" + i;
            UILabel label = address.transform.FindChild("value").GetComponent<UILabel>();
            label.text = MainData.instance.user.addresses[i];
            startY -= len;
        }
        container.FindChild("addMore").transform.localPosition = new Vector3(0,startY == 430 ? 0 : startY,0);
    }

    void backFromShowAddresses()
    {
        Transform address_trans = needshow[0].transform.FindChild("address-body");
        Transform list_trans = address_trans.FindChild("list");
        if (list_trans.gameObject.activeSelf)
        {
            show(curShow,false);
            show(preShow,true);
            clears();
            title.GetComponentInChildren<UILabel>().text = "个人中心";
            callback = null;
            UILabel address_label = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container").FindChild("address").FindChild("value").GetComponent<UILabel>();
            if (MainData.instance.user.addresses.Count > 0)
            {
                MyUtilTools.insertStr(address_label,MainData.instance.user.addresses[0],300);
            }
            else
            {
                address_label.text = "未绑定收货地址";
            }
        }
        else
        {
            address_trans.FindChild("add").gameObject.SetActive(false);
            list_trans.gameObject.SetActive(true);
            UILabel label = title.GetComponentInChildren<UILabel>();
            label.text = "我的收货地址";
            refreshAddressList();
        }
    }

    public void openShowAddresses(GameObject obj1, GameObject obj2)
    {
        clears();
        obj1.SetActive(false);
        preShow.Add(obj1);
        obj2.SetActive(true);
        curShow.Add(obj2);
        UILabel label = title.GetComponentInChildren<UILabel>();
        label.text = "我的收货地址";
        callback = new EventDelegate(backFromShowAddresses);
        refreshAddressList();
    }

    public void doAddAddress()
    {
        Transform container = needshow[0].transform.FindChild("address-body").FindChild("add");
        UIInput input = container.FindChild("inputer").GetComponent<UIInput>();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AddressChange");
        buffer.WriteByte(0);
        buffer.WriteLong(MainData.instance.user.id);//编号
        buffer.WriteString(input.value);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void openAddAddresses(GameObject obj1, GameObject obj2)
    {
        obj1.SetActive(false);
        obj2.SetActive(true);
        UILabel label = title.GetComponentInChildren<UILabel>();
        label.text = "添加收货地址";
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
        System.DateTime tody = System.DateTime.Now;
        string dateStr = tody.Year + "-" + tody.Month + "-" + tody.Day + "-" + tody.Hour + "-" + tody.Minute + "-" + tody.Second + ".jpg";
        string picName = MainData.instance.user.account + "-sellercommit-" + dateStr;
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
        needSave = MainData.instance.user.face.Equals(icon.spriteName);
        UILabel signature_save = container.FindChild("signature").FindChild("save").GetComponent<UILabel>();
        needSave = needSave ? needSave : MainData.instance.user.signature.Equals(signature_save.text);
        UIInput name = container.FindChild("name").FindChild("inputer").GetComponent<UIInput>();
        needSave = needSave ? needSave : MainData.instance.user.realyName.Equals(name.value);
        UIInput indent = container.FindChild("indent").FindChild("inputer").GetComponent<UIInput>();
        needSave = needSave ? needSave : MainData.instance.user.indentity.Equals(indent.value);
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
        UIInput name = container.FindChild("name").FindChild("inputer").GetComponent<UIInput>();
        UIInput indent = container.FindChild("indent").FindChild("inputer").GetComponent<UIInput>();
        UIToggle toggle = container.FindChild("push-flag").FindChild("toggle").GetComponent<UIToggle>();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("UserUpdate");
        buffer.WriteLong(MainData.instance.user.id);//编号
        buffer.WriteString(icon.spriteName);
        buffer.WriteString(signature_save.text);
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
