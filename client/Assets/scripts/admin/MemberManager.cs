using UnityEngine;
using System.Collections.Generic;

public class MemberManager : MonoBehaviour {

    List<MainData.UserData> users = new List<MainData.UserData>();

    int selectIndex = 0;

    public static GameObject pref_user_info = null;

	// Use this for initialization
	void Start () {
        if (pref_user_info == null)
        {
            pref_user_info = Resources.Load<GameObject>("prefabs/user-info");
        }
	}

    void refresh()
    {
        Transform body      = transform.FindChild("left").FindChild("list").FindChild("body");
        body.localPosition = Vector3.zero;
        body.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        Transform container = body.FindChild("container");
        MyUtilTools.clearChild(container);
        float y = 800f;
        for (int i = 0; i < users.Count; i++ )
        {
            MainData.UserData user = users[i];
            GameObject user_summary = NGUITools.AddChild(container.gameObject,pref_user_info);
            user_summary.transform.localPosition = new Vector3(-180,y,0);
            user_summary.name = "" + i;
            user_summary.transform.FindChild("icon").GetComponent<UISprite>().spriteName = user.face;
            user_summary.transform.FindChild("name").GetComponent<UILabel>().text = user.nikeName;
            Transform select_trans = user_summary.transform.FindChild("select");
            select_trans.FindChild("show").gameObject.SetActive(i == selectIndex);
            UIButton button = select_trans.GetComponent<UIButton>();
            EventDelegate event_delegate = new EventDelegate(this, "select");
            event_delegate.parameters[0] = new EventDelegate.Parameter();
            event_delegate.parameters[0].obj = user_summary;
            button.onClick.Add(event_delegate);
            y -= 80;
        }
        refreshRight();
    }

    void select(GameObject obj)
    {
        int index = int.Parse(obj.name);
        if (selectIndex == index)
        {
            return;
        }
        Transform container = obj.transform.parent;
        Transform preTrans = container.FindChild(selectIndex + "");
        if (preTrans != null)
        {
            preTrans.FindChild("select").FindChild("show").gameObject.SetActive(false);
        }
        obj.transform.FindChild("select").FindChild("show").gameObject.SetActive(true);
        selectIndex = index;
        refreshRight();
    }

    void refreshRight()
    {
        Transform container = transform.FindChild("right");
        if (selectIndex > users.Count)
        {
            return;
        }
        MainData.UserData user = users[selectIndex];
        container.FindChild("account").FindChild("value").GetComponent<UILabel>().text = user.account;
        container.FindChild("nickName").FindChild("inputer").GetComponent<UIInput>().value = user.nikeName;
        container.FindChild("name").FindChild("inputer").GetComponent<UIInput>().value = user.realyName;
        container.FindChild("ident").FindChild("inputer").GetComponent<UIInput>().value = user.indentity;
        UIPopupList type = container.FindChild("type").FindChild("value").GetComponent<UIPopupList>();
        UIPopupList title = container.FindChild("title").FindChild("value").GetComponent<UIPopupList>();
        if (user.permission == 1)
        {
            type.value = "买家";
            title.items.Clear();
            title.items.Add("普通买家");
            title.items.Add("高级买家");
        }
        else if (user.permission == 2)
        {
            type.value = "卖家";
            title.items.Clear();
            title.items.Add("普通营销员");
            title.items.Add("高级营销员");
            title.items.Add("金牌营销员");
        }
        title.value = user.title;
        container.FindChild("deposit").FindChild("inputer").GetComponent<UIInput>().value = user.seller.deposit + "";
        container.FindChild("deal").FindChild("inputer").GetComponent<UIInput>().value = user.credit.totalDealValue + "";
        container.FindChild("credit-c").FindChild("inputer").GetComponent<UIInput>().value = user.credit.maxValue + "";
        container.FindChild("credit-t").FindChild("inputer").GetComponent<UIInput>().value = user.credit.tempMaxValue + "";
        container.FindChild("hp").FindChild("inputer").GetComponent<UIInput>().value = user.credit.hp + "";
        container.FindChild("zp").FindChild("inputer").GetComponent<UIInput>().value = user.credit.zp + "";
        container.FindChild("cp").FindChild("inputer").GetComponent<UIInput>().value = user.credit.cp + "";
        container.FindChild("regist").FindChild("value").GetComponent<UILabel>().text = user.registTime;
        container.FindChild("time").FindChild("value").GetComponent<UILabel>().text = user.endTime;
        container.FindChild("wg").FindChild("inputer").GetComponent<UIInput>().value = user.breach + "";
        Transform fh_body = container.FindChild("fh").FindChild("body");
        UIInput reason_input = fh_body.FindChild("inputer").GetComponent<UIInput>();
        reason_input.value = user.forbid.reason + "";
        string nowDateStr = System.DateTime.Now.Year + "-" + System.DateTime.Now.Month + "-" + System.DateTime.Now.Day + " 23:59:59";
        if (user.forbid.endTime.Equals("forever"))
        {
            reason_input.value = "永久封号";
            fh_body.FindChild("time").FindChild("value").GetComponent<UILabel>().text = nowDateStr;
        }
        else if (user.forbid.endTime.Equals("null"))
        {
            reason_input.value = "未被封号";
            fh_body.FindChild("time").FindChild("value").GetComponent<UILabel>().text = nowDateStr;
        }
        else
        {
            reason_input.value = user.forbid.reason + "";
            fh_body.FindChild("time").FindChild("value").GetComponent<UILabel>().text = user.forbid.endTime;
        }
        if (user.addresses.Count > 0)
        {
            container.FindChild("address").FindChild("value").GetComponent<UILabel>().text = user.addresses[0];
        }
        else
        {
            container.FindChild("address").FindChild("value").GetComponent<UILabel>().text = "未绑定地址";
        }
        if (user.bacnkAccount.names.Count > 0)
        {
            container.FindChild("bank").FindChild("value").GetComponent<UILabel>().text = user.bacnkAccount.names[0] + " " + user.bacnkAccount.accounts[0];
        }
        else
        {
            container.FindChild("bank").FindChild("value").GetComponent<UILabel>().text = "未绑定银行卡";
        }
        container.FindChild("other").FindChild("inputer").GetComponent<UIInput>().value = user.other;
    }

	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("AdminMemberSearch");
        if (buffer != null)
        {
            int size = buffer.ReadInt();
            if (size > 0)
            {
                selectIndex = 0;
                users.Clear();
                for (int i = 0; i < size; i++)
                {
                    MainData.UserData user = new MainData.UserData();
                    user.deserialize(buffer);
                    users.Add(user);
                }
                refresh();
            }
            else
            {
                DialogUtil.tip("查找不到相关数据");
            }
        }
        buffer = MyUtilTools.tryToLogic("AdminUserResetPwd");
        if (buffer != null)
        {
            DialogUtil.tip(buffer.ReadString(),true);
        }
	}

    public void search()
    {
        UIInput input = transform.FindChild("left").FindChild("search").FindChild("inputer").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(input.value))
        {
            DialogUtil.tip(input.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminMemberSearch");
        buffer.WriteString(input.value);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void commit()
    {
        Transform container = transform.FindChild("right");
        UILabel account = container.FindChild("account").FindChild("value").GetComponent<UILabel>();
        UIInput nickName = container.FindChild("nickName").FindChild("inputer").GetComponent<UIInput>();
        UIInput name = container.FindChild("name").FindChild("inputer").GetComponent<UIInput>();
        UIInput ident = container.FindChild("ident").FindChild("inputer").GetComponent<UIInput>();
        UIPopupList type = container.FindChild("type").FindChild("value").GetComponent<UIPopupList>();
        UIPopupList title = container.FindChild("title").FindChild("value").GetComponent<UIPopupList>();
        UIInput deposit = container.FindChild("deposit").FindChild("inputer").GetComponent<UIInput>();
        UIInput deal = container.FindChild("deal").FindChild("inputer").GetComponent<UIInput>();
        UIInput credit_c =  container.FindChild("credit-c").FindChild("inputer").GetComponent<UIInput>();
        UIInput credit_t =  container.FindChild("credit-t").FindChild("inputer").GetComponent<UIInput>();
        UIInput hp = container.FindChild("hp").FindChild("inputer").GetComponent<UIInput>();
        UIInput zp = container.FindChild("zp").FindChild("inputer").GetComponent<UIInput>();
        UIInput cp = container.FindChild("cp").FindChild("inputer").GetComponent<UIInput>();
        UILabel regist = container.FindChild("regist").FindChild("value").GetComponent<UILabel>();
        UILabel time = container.FindChild("time").FindChild("value").GetComponent<UILabel>();
        UIInput wg = container.FindChild("wg").FindChild("inputer").GetComponent<UIInput>();
        Transform fh_body = container.FindChild("fh").FindChild("body");
        UIInput fh_reason = fh_body.FindChild("inputer").GetComponent<UIInput>();
        UILabel fh_time = fh_body.FindChild("time").FindChild("value").GetComponent<UILabel>();
        UIInput other = container.FindChild("other").FindChild("inputer").GetComponent<UIInput>();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminUserResetPwd");
        buffer.WriteLong(MainData.instance.user.id);
        buffer.WriteString(account.text);
        buffer.WriteString(nickName.value);
        buffer.WriteString(name.value);
        buffer.WriteString(ident.value);
        buffer.WriteString(type.value);
        buffer.WriteString(title.value);
        buffer.WriteString(deposit.value);
        buffer.WriteString(deal.value);
        buffer.WriteString(credit_c.value);
        buffer.WriteString(credit_t.value);
        buffer.WriteString(hp.value);
        buffer.WriteString(zp.value);
        buffer.WriteString(cp.value);
        buffer.WriteString(regist.text);
        buffer.WriteString(time.text);
        buffer.WriteString(wg.value);
        buffer.WriteString(fh_reason.value);
        buffer.WriteString(fh_time.text);
        buffer.WriteString(other.value);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void resetPwd()
    {
        UIInput account = transform.FindChild("right").FindChild("account").FindChild("value").GetComponent<UIInput>();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminUserResetPwd");
        buffer.WriteLong(MainData.instance.user.id);
        buffer.WriteString(account.value);
        NetUtil.getInstance.SendMessage(buffer);
    }
}
